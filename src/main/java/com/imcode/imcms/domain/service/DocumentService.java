package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import imcode.server.user.UserDomainObject;

public interface DocumentService extends DeleterByDocumentId {

    DocumentDTO get(Integer docId);

    int save(DocumentDTO saveMe);

    void delete(DocumentDTO deleteMe);

    boolean publishDocument(int docId, int userId);

}
