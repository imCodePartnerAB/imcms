package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface EmptyTypedDocumentCreatingService<D extends Document> {

    D createEmpty(Meta.DocumentType type);

}
