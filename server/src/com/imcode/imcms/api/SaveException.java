package com.imcode.imcms.api;

public class SaveException extends Exception {

    public SaveException( String message ) {
        super(message) ;
    }

    public SaveException( Throwable cause ) {
        super(cause) ;
    }
}
