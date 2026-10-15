package com.urbanassist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CorsConfig — Configures Cross-Origin Resource Sharing (CORS).
 *
 * WHY THIS IS NEEDED:
 *   Browsers block JavaScript requests from one origin (e.g., http://localhost:5173
 *   where React runs) to a different origin (e.g., http://localhost:8080 where
 *   Spring Boot runs). This is a browser security feature called the
 *   "Same-Origin Policy".
 *
 *   CORS headers tell the browser: "It's OK, this backend trusts requests
 *   from the React frontend."
 *
 * WHAT EACH SETTING DOES:
 *   - allowedOrigins:  Which frontend URLs can call this API.
 *   - allowedMethods:  Which HTTP methods are permitted (GET, POST, PUT, DELETE).
 *   - allowedHeaders:  Which request headers the frontend can send (Authorization
 *                      for JWT, Content-Type for JSON).
 *   - allowCredentials: Whether cookies/auth headers are allowed.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow the Vite React dev server on all local ports (5173, 5174, etc.)
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "http://127.0.0.1:*"
        ));

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
