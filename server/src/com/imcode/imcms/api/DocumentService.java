package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.document.CategoryDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.user.UserAndRoleMapper;

public class DocumentService {
    private SecurityChecker securityChecker;
    private DocumentMapper documentMapper;
    private DocumentPermissionSetMapper documentPermissionSetMapper;
    private UserAndRoleMapper userAndRoleMapper;

    public DocumentService(SecurityChecker securityChecker, DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper, UserAndRoleMapper userAndRoleMapper) {
        this.securityChecker = securityChecker;
        this.documentMapper = documentMapper;
        this.documentPermissionSetMapper = documentPermissionSetMapper;
        this.userAndRoleMapper = userAndRoleMapper;
    }

    /**
     *
     * @param documentId The id number of the document requested, also somtimes known as "meta_id"
     * @return The document
     * @throws com.imcode.imcms.api.NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument(int documentId) throws NoPermissionException {
        imcode.server.document.DocumentDomainObject doc = documentMapper.getDocument(documentId);
        TextDocument result = new TextDocument(doc, securityChecker, this, documentMapper, documentPermissionSetMapper, userAndRoleMapper);
        securityChecker.hasDocumentPermission(result);
        return result;
    }

    public TextDocument createNewTextDocument(int parentId, int parentMenuNumber) throws NoPermissionException {
        securityChecker.hasEditPermission(parentId);
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        DocumentDomainObject newDoc = documentMapper.createNewTextDocument(user, parentId, parentMenuNumber);
        TextDocument result = new TextDocument(newDoc, securityChecker, this, documentMapper, documentPermissionSetMapper, userAndRoleMapper);
        return result;
    }

    public void saveChanges(TextDocument document) throws NoPermissionException {
        securityChecker.hasEditPermission(document);
        documentMapper.saveDocument(document.getInternal());
    }

    public Category getCategory(String categoryTypeName, String categoryName) {
        // Allow everyone to get a Category. No security check.
        final CategoryDomainObject category = documentMapper.getCategory(categoryTypeName, categoryName);
        if (null != category) {
            return new Category(category);
        } else {
            return null ;
        }
    }

}