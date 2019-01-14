package com.imcode.imcms.api;

public class DocumentsExistsException extends AlreadyExistsException {
    public DocumentsExistsException(String message) {
        super(message, null);
    }
}
