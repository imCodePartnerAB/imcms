package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;

public interface DocumentService<Document extends DocumentDTO> extends DeleterByDocumentId {

    Document createEmpty(DocumentType type);

    Document get(int docId);

    int save(Document saveMe);

    boolean publishDocument(int docId, int userId);

}
