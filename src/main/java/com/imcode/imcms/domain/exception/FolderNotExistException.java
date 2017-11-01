package com.imcode.imcms.domain.exception;

public class FolderNotExistException extends RuntimeException {
    private static final long serialVersionUID = 3599379164719922771L;

    public FolderNotExistException(String message) {
        super(message);
    }
}
