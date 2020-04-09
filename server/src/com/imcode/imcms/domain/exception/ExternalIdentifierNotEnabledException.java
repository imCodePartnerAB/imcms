package com.imcode.imcms.domain.exception;

public class ExternalIdentifierNotEnabledException extends RuntimeException {

    private static final long serialVersionUID = 6792272920243591023L;

    public ExternalIdentifierNotEnabledException(String identifierId) {
        super("External identifier with " + identifierId + " ID not enabled.");
    }
}
