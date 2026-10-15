package com.urbanassist.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JwtTokenProvider — Generates, parses, and validates JWT tokens.
 * Supports environment variable JWT_SECRET override with a secure development key fallback.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey key;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:UrbanAssistDevDefaultSecretKey2026MustBeAtLeast32BytesLongForSecurity!}") String jwtSecret,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationMs) {

        String secretToUse = jwtSecret != null ? jwtSecret.trim() : "";
        if (secretToUse.length() < 32) {
            logger.warn("JWT secret is under 32 characters. Padding to 32 characters for HMAC-SHA256 compliance.");
            secretToUse = String.format("%-32s", secretToUse).replace(' ', '0');
        }

        if ("UrbanAssistDevDefaultSecretKey2026MustBeAtLeast32BytesLongForSecurity!".equals(secretToUse)) {
            logger.info("Using default development JWT secret key. Provide 'JWT_SECRET' environment variable for production.");
        } else {
            logger.info("Custom JWT secret key successfully loaded from environment variable.");
        }

        this.key = Keys.hmacShaKeyFor(secretToUse.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
