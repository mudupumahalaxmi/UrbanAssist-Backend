package com.urbanassist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CorsConfig — Configures Cross-Origin Resource Sharing (CORS).
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:}")
    private String corsAllowedOrigins;

    public CorsConfig() {
    }

    public CorsConfig(String corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Base development origins
        List<String> allowedOrigins = new ArrayList<>(List.of(
            "http://localhost:*",
            "http://127.0.0.1:*"
        ));

        // Add explicitly configured production origins from CORS_ALLOWED_ORIGINS
        if (StringUtils.hasText(corsAllowedOrigins)) {
            Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .map(origin -> origin.replaceAll("/+$", ""))
                .filter(StringUtils::hasText)
                .forEach(allowedOrigins::add);
        }

        // Ensure Vercel domain pattern is always permitted for production and preview builds
        if (!allowedOrigins.contains("https://*.vercel.app")) {
            allowedOrigins.add("https://*.vercel.app");
        }

        configuration.setAllowedOriginPatterns(allowedOrigins);

        // Allow standard REST methods
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Allow common headers (Authorization carries the JWT token)
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));

        // Allow the browser to send credentials (JWT in Authorization header)
        configuration.setAllowCredentials(true);

        // Cache the preflight response for 1 hour (reduces OPTIONS requests)
        configuration.setMaxAge(3600L);

        // Apply this CORS configuration to all API endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
