package com.imcode.imcms.storage.exception;

import com.imcode.imcms.storage.StoragePath;

import java.io.IOException;

public class StorageFileNotFoundException extends RuntimeException {

    public StorageFileNotFoundException() {}

    public StorageFileNotFoundException(StoragePath path) {
        super("File not found in storage: " + path);
    }

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
