package com.imcode.imcms.api;

/**
 * Usually thrown when saving a {@link Role}. Signals that there's already a role with the name that the role being save has.
 */
public class RoleAlreadyExistsException extends AlreadyExistsException {

    public RoleAlreadyExistsException(String message, Throwable cause) {
        super(message, cause) ;
    }
}
