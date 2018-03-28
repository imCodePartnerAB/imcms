package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.model.Document;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface TypedDocumentService<D extends Document> extends TypedDocumentCreatingService<D>,
        BasicDocumentService<D>,
        DocumentSaver<UberDocumentDTO> {
}
