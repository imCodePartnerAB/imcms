package com.imcode.imcms.domain.exception;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.07.18.
 */
public class ExternalIdentifierNotEnabledException extends RuntimeException {
    private static final long serialVersionUID = 6792272920243591023L;

    public ExternalIdentifierNotEnabledException(String identifierId) {
        super("External identifier with " + identifierId + " ID not enabled.");
    }
}
