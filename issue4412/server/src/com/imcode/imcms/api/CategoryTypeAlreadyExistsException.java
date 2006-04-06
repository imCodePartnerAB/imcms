package com.imcode.imcms.api;

public class CategoryTypeAlreadyExistsException extends AlreadyExistsException {
    public CategoryTypeAlreadyExistsException( String message ) {
        super( message );
    }
}
