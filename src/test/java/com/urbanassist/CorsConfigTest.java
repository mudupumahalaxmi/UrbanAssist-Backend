package com.urbanassist;

import com.urbanassist.config.CorsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CorsConfigTest — Unit tests verifying CORS configuration and preflight matching
 * for the exact production Vercel frontend origin and development origins.
 * Runs independently without requiring a database connection.
 */
class CorsConfigTest {

    private static final String EXACT_VERCEL_ORIGIN = "https://urbanassist-frontend-ql7fr0ef9-urban-assist.vercel.app";

    @Test
    void testExactVercelProductionOriginAllowedWhenConfigured() {
        CorsConfig corsConfig = new CorsConfig(EXACT_VERCEL_ORIGIN);
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");
        request.setMethod("OPTIONS");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config, "CORS configuration must not be null for /api/auth/login");

        String matchedOrigin = config.checkOrigin(EXACT_VERCEL_ORIGIN);
        assertEquals(EXACT_VERCEL_ORIGIN, matchedOrigin, "Exact Vercel origin must match allowed origin pattern");

        assertTrue(config.getAllowCredentials(), "Credentials (Authorization header) must be allowed");
        assertTrue(config.getAllowedMethods().contains("OPTIONS"), "OPTIONS method must be permitted for preflight");
        assertTrue(config.getAllowedMethods().contains("POST"), "POST method must be permitted for login");
        assertTrue(config.getAllowedHeaders().contains("Authorization"), "Authorization header must be permitted");
        assertTrue(config.getAllowedHeaders().contains("Content-Type"), "Content-Type header must be permitted");
    }

    @Test
    void testTrailingSlashSanitizedInConfiguredOrigin() {
        // Even if user entered a trailing slash in the Render dashboard, it should be sanitized
        CorsConfig corsConfig = new CorsConfig(EXACT_VERCEL_ORIGIN + "/");
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        String matchedOrigin = config.checkOrigin(EXACT_VERCEL_ORIGIN);
        assertEquals(EXACT_VERCEL_ORIGIN, matchedOrigin, "Sanitized origin without trailing slash must match browser Origin header");
    }

    @Test
    void testDefaultVercelFallbackWhenUnset() {
        // When CORS_ALLOWED_ORIGINS is not set, default fallback allows https://*.vercel.app
        CorsConfig corsConfig = new CorsConfig("");
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        String matchedPreviewOrigin = config.checkOrigin(EXACT_VERCEL_ORIGIN);
        assertEquals(EXACT_VERCEL_ORIGIN, matchedPreviewOrigin, "Default pattern must match preview *.vercel.app deployment");

        String matchedProdOrigin = config.checkOrigin("https://urbanassist-frontend.vercel.app");
        assertEquals("https://urbanassist-frontend.vercel.app", matchedProdOrigin, "Default pattern must match production *.vercel.app deployment");
    }

    @Test
    void testLocalhostAllowedForDevelopment() {
        CorsConfig corsConfig = new CorsConfig(EXACT_VERCEL_ORIGIN);
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        assertEquals("http://localhost:5173", config.checkOrigin("http://localhost:5173"), "Localhost dev server must be allowed");
        assertEquals("http://127.0.0.1:5173", config.checkOrigin("http://127.0.0.1:5173"), "127.0.0.1 dev server must be allowed");
    }

    @Test
    void testArbitraryUntrustedOriginRejected() {
        CorsConfig corsConfig = new CorsConfig(EXACT_VERCEL_ORIGIN);
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        assertNull(config.checkOrigin("https://malicious-attacker.com"), "Untrusted origins must be rejected");
        assertNull(config.checkOrigin("http://evil-site.org"), "Untrusted origins must be rejected");
    }
}
