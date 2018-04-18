package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;

/**
 * Central {@link com.imcode.imcms.domain.service.DocumentService} instance
 * that delegates calls to corresponding service by document's type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
public interface DelegatingByTypeDocumentService extends
        TypedDocumentCreatingService<Document>,
        BasicDocumentService<Document>,
        DocumentSaver<Document> {
}
