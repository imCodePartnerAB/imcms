package com.imcode.imcms.api.exception;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
    public CategoryAlreadyExistsException(String message) {
        super(message, null);
    }
}
