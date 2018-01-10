package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.01.18.
 */
public interface TypedDocumentService<Document extends DocumentDTO> extends EmptyTypedDocumentCreatingService<Document>,
        BasicDocumentService<Document>,
        DocumentSaver<UberDocumentDTO> {
}
