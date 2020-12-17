package com.imcode.imcms.api.exception;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(Integer roleId) {
        super("Role does not exist with id: " + roleId);
    }
}
