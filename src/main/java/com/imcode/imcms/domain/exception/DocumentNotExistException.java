package com.imcode.imcms.domain.exception;

/**
 * Is thrown when trying to do smth with non-existing document.
 * <p>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 03.10.17.
 */
public class DocumentNotExistException extends RuntimeException {
    private static final long serialVersionUID = 8436506557570083168L;

    public DocumentNotExistException() {
        super("Document does not exist!");
    }

    public DocumentNotExistException(String docId) {
        super(String.format("Document with id = %s does not exist!", docId));
    }

    public DocumentNotExistException(int docId) {
        this(String.valueOf(docId));
    }

}
