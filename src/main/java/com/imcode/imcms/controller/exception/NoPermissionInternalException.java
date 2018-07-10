package com.imcode.imcms.controller.exception;

public class NoPermissionInternalException extends RuntimeException {

    private static final long serialVersionUID = 7901776412931941743L;

    public NoPermissionInternalException(String message) {
        super(message);
    }
}
