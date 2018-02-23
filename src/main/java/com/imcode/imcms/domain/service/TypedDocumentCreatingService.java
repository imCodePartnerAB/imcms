package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface TypedDocumentCreatingService<D extends Document> {

    D createNewDocument(Meta.DocumentType type, Integer parentDocId);

}
