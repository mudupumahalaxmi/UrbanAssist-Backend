package com.urbanassist.exception;

/**
 * ResourceNotFoundException — Thrown when a requested entity does not exist.
 *
 * Examples of when this is thrown:
 *   - GET /api/complaints/999 but complaint #999 doesn't exist.
 *   - GET /api/departments/50 but department #50 doesn't exist.
 *
 * The GlobalExceptionHandler catches this and returns HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience constructor for common "Entity with id X not found" messages.
     *
     * Usage: throw new ResourceNotFoundException("Complaint", "id", 999);
     * Result message: "Complaint not found with id: 999"
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
}
