package com.imcode.imcms.storage.exception;

public class FolderNotEmptyException extends RuntimeException{

    public FolderNotEmptyException() {}

    public FolderNotEmptyException(String message) {
        super(message);
    }

    public FolderNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
