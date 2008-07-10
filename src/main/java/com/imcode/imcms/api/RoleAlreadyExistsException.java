package com.imcode.imcms.api;

public class RoleAlreadyExistsException extends AlreadyExistsException {

    public RoleAlreadyExistsException(String message, Throwable cause) {
        super(message, cause) ;
    }
}
