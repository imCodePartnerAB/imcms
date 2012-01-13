package com.imcode.imcms.api;

/**
 * Base exception, thrown when saving failed.
 * @since 2.0
 */
public class SaveException extends Exception {

    public SaveException(String message, Throwable cause) {
        super(message, cause) ;
    }

    public SaveException( Throwable cause ) {
        super(cause) ;
    }
}
