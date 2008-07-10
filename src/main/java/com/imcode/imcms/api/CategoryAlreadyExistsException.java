package com.imcode.imcms.api;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
    public CategoryAlreadyExistsException( String message ) {
        super( message, null );
    }
}
