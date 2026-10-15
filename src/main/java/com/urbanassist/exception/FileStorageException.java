package com.urbanassist.exception;

/**
 * FileStorageException — Thrown when file upload or retrieval fails.
 *
 * Examples of when this is thrown:
 *   - The uploads directory does not exist or is not writable.
 *   - A file exceeds the maximum allowed size.
 *   - The file could not be saved to disk.
 *
 * The GlobalExceptionHandler catches this and returns HTTP 500 Internal Server Error.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
