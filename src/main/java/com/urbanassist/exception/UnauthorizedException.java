package com.urbanassist.exception;

/**
 * UnauthorizedException — Thrown when authentication or authorization fails.
 *
 * Examples of when this is thrown:
 *   - JWT token is expired or malformed.
 *   - A CITIZEN tries to access a STAFF-only endpoint.
 *   - Login credentials are incorrect.
 *
 * The GlobalExceptionHandler catches this and returns HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
