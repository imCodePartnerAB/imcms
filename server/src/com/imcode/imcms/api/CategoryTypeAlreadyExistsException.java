package com.imcode.imcms.api;

public class CategoryTypeAlreadyExistsException extends SaveException {
    public CategoryTypeAlreadyExistsException( String message ) {
        super( message );
    }
}
