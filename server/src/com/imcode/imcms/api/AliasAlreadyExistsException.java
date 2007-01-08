package com.imcode.imcms.api;

public class AliasAlreadyExistsException extends SaveException {
    public AliasAlreadyExistsException() {
        super("Alias already exists.");
    }
}
