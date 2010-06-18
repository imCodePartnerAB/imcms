package com.imcode.imcms.addon.imagearchive.service.exception;

public class CategoryExistsException extends RuntimeException {
    private static final long serialVersionUID = 1740190300693433518L;
    

    public CategoryExistsException() {
    }

    public CategoryExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryExistsException(String message) {
        super(message);
    }

    public CategoryExistsException(Throwable cause) {
        super(cause);
    }
}
