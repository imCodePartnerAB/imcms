package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.user.UserDomainObject;

public interface DocumentMenuService {
    boolean hasUserAccessToDoc(int docId, UserDomainObject user);

    String getDocumentTitle(int documentId, Language language);

    String getDocumentTarget(int documentId);

    String getDocumentLink(int documentId);

    Meta.DocumentType getDocumentType(int documentId);

    Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId);
}
