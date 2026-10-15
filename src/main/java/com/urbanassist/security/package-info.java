/**
 * com.urbanassist.security — JWT authentication and authorization components.
 *
 * FILES COMING IN PHASE 2:
 *   - JwtTokenProvider.java        — Generates and validates JWT tokens.
 *   - JwtAuthenticationFilter.java — Intercepts HTTP requests to extract and
 *                                    validate the Bearer token from the
 *                                    Authorization header.
 *   - JwtAuthenticationEntryPoint.java — Returns HTTP 401 when a request
 *                                        arrives without a valid token.
 *   - CustomUserDetailsService.java — Loads user data from the database
 *                                     for Spring Security authentication.
 */
package com.urbanassist.security;
