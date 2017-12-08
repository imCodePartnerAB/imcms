package com.imcode.imcms.domain.service;

import imcode.server.user.UserDomainObject;

public interface DocumentMenuService {
    boolean hasUserAccessToDoc(int docId, UserDomainObject user);

    String getDocumentTitle(int documentId);

    String getDocumentTarget(int documentId);

    String getDocumentLink(int documentId);
}
