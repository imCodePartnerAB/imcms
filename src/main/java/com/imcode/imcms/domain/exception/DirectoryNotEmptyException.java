package com.imcode.imcms.domain.exception;

public class DirectoryNotEmptyException extends RuntimeException {
    public DirectoryNotEmptyException(String message) {
        super(message);
    }
}
