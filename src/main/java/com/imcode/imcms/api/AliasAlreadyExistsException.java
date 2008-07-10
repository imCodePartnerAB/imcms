package com.imcode.imcms.api;

public class AliasAlreadyExistsException extends SaveException {
    public AliasAlreadyExistsException(Throwable cause) {
        super("Alias already exists.", cause);
    }
}
