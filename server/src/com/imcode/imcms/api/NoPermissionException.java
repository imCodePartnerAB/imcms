package com.imcode.imcms.api;

/**
 * This exception is thrown from almost all methods in the service classes. It is thrown when the current logged
 * in user dosen't have the apropiate rights to do that operation. See the message for futher information.
 */
public class NoPermissionException extends Exception {

    public NoPermissionException( String message ) {
        super( message );
    }
}
