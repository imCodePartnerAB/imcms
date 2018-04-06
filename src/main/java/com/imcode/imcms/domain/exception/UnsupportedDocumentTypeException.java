package com.imcode.imcms.domain.exception;

import com.imcode.imcms.persistence.entity.Meta;

/**
 * Is thrown when unknown or null document type is requested.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
public class UnsupportedDocumentTypeException extends RuntimeException {
    private static final long serialVersionUID = 1387095604813340247L;
    private final Meta.DocumentType type;

    public UnsupportedDocumentTypeException(Meta.DocumentType type) {
        super("Can not create document of type " + type);
        this.type = type;
    }

    public Meta.DocumentType getType() {
        return type;
    }
}
