package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;

public interface DocumentService extends DeleterByDocumentId {

    DocumentDTO getOrEmpty(Integer docId, DocumentType type);

    DocumentDTO get(int docId);

    int save(DocumentDTO saveMe);

    void delete(DocumentDTO deleteMe);

    boolean publishDocument(int docId, int userId);

}
