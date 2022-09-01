package com.imcode.imcms.storage.exception;

/**
 * Is thrown when a file with the same name already exists.
 */
public class SuchStorageFileExistsException extends RuntimeException{

    public SuchStorageFileExistsException() {}

    public SuchStorageFileExistsException(String message) {
        super(message);
    }
}
