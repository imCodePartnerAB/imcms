package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import imcode.server.user.UserDomainObject;

public interface DocumentService extends DeleterByDocumentId {

    DocumentDTO get(Integer docId);

    int save(DocumentDTO saveMe);

    String getDocumentTitle(int documentId);

    String getDocumentTarget(int documentId);

    String getDocumentLink(int documentId);

    void delete(DocumentDTO deleteMe);

    boolean hasUserAccessToDoc(int docId, UserDomainObject user);

}
