package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.DocumentGetter;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.user.UserDomainObject;

public interface DocumentMapper extends DocumentGetter {
    DocumentDomainObject getDocument(int documentId);

    void saveNewDocument(DocumentDomainObject document, UserDomainObject user)
            throws MaxCategoryDomainObjectsOfTypeExceededException, NoPermissionToAddDocumentToMenuException;

    void saveDocument(DocumentDomainObject document,
                      UserDomainObject user) throws MaxCategoryDomainObjectsOfTypeExceededException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException;

    void invalidateDocument(DocumentDomainObject document);
}