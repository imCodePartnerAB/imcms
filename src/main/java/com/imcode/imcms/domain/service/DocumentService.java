package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;

public interface DocumentService<Document extends DocumentDTO>
        extends EmptyDocumentCreatingService<Document>, BasicDocumentService<Document>, DocumentSaver<Document> {
}
