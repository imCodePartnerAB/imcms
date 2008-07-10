package com.imcode.imcms.api;

/**
 * @since 2.0
 */
public class AlreadyExistsException extends SaveException {

    public AlreadyExistsException(String message, Throwable cause) {
        super( message, cause );
    }
}
