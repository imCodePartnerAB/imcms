package com.imcode.imcms.api.exception;

public class CategoryTypeAlreadyExistsException extends AlreadyExistsException {
    public CategoryTypeAlreadyExistsException(String message) {
        super(message, null);
    }
}
