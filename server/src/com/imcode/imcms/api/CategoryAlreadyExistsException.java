package com.imcode.imcms.api;

public class CategoryAlreadyExistsException extends SaveException {
    public CategoryAlreadyExistsException( String message ) {
        super( message );
    }
}
