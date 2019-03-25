package com.imcode.imcms.api.exception;

public class CategoryTypeHasCategoryException extends RuntimeException {
    public CategoryTypeHasCategoryException(String message) {
        super(message, null);
    }
}
