package com.imcode.imcms.api;

public class AlreadyExistsException extends SaveException {

    public AlreadyExistsException( String message ) {
        super( message );
    }
}
