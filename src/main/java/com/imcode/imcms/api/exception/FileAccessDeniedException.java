package com.imcode.imcms.api.exception;

public class FileAccessDeniedException extends RuntimeException {

    public FileAccessDeniedException(String message) {
        super(message, null);
    }
}
