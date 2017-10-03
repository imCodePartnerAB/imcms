package com.imcode.imcms.domain.service.exception;

/**
 * Is thrown when trying to do smth with non-existing document.
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 03.10.17.
 */
public class DocumentNotExistException extends Exception {
    private static final long serialVersionUID = 8436506557570083168L;

    public DocumentNotExistException() {
        super("Document does not exist!");
    }
}
