package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface DocumentSaver<D extends Document> {

    /**
     * Saves document to DB.
     *
     * @param saveMe document to be saved
     * @return saved document (with id if doc was new)
     */
    D save(D saveMe);

}
