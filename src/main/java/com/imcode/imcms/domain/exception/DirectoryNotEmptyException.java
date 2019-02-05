package com.imcode.imcms.domain.exception;

public class DirectoryNotEmptyException extends RuntimeException {
    private static final long serialVersionUID = -5922068522431691835L;

    public DirectoryNotEmptyException(String message) {
        super(message);
    }
}
