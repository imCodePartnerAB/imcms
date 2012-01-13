package com.imcode.imcms.api;

/**
 * General exception produced when duplicate entries of some sort are not acceptable in the cms.
 * @since 2.0
 */
public class AlreadyExistsException extends SaveException {

    public AlreadyExistsException(String message, Throwable cause) {
        super( message, cause );
    }
}
