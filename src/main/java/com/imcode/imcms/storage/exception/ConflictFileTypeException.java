package com.imcode.imcms.storage.exception;

public class ConflictFileTypeException extends RuntimeException {

    public ConflictFileTypeException() {}

    public ConflictFileTypeException(String message) {
        super(message);
    }

    public ConflictFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
