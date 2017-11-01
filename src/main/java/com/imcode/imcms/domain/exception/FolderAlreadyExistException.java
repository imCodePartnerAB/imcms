package com.imcode.imcms.domain.exception;

public class FolderAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = 7273885263728725934L;

    public FolderAlreadyExistException(String message) {
        super(message);
    }
}
