package com.imcode.imcms.api;

import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

public class DocumentService {
    private SecurityChecker securityChecker;
    private DocumentMapper documentMapper;
    private DocumentPermissionSetMapper documentPermissionSetMapper;

    public DocumentService( SecurityChecker securityChecker, DocumentMapper documentMapper, DocumentPermissionSetMapper documentPermissionSetMapper ) {
        this.securityChecker = securityChecker;
        this.documentMapper = documentMapper;
        this.documentPermissionSetMapper = documentPermissionSetMapper;
    }

    /**
     *
     * @param documentId The id number of the document requested, also somtimes known as "meta_id"
     * @return The document
     * @throws com.imcode.imcms.api.NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public TextDocument getTextDocument( int documentId ) throws NoPermissionException {
        securityChecker.hasDocumentRights( documentId );
        imcode.server.document.DocumentDomainObject doc = documentMapper.getDocument( documentId );
        TextDocument result = new TextDocument( securityChecker, this, doc, documentMapper, documentPermissionSetMapper );
        return result;
    }

    public TextDocument createNewTextDocument( int parentId, int parentMenuNumber ) {
        UserDomainObject user = securityChecker.getCurrentLoggedInUser();
        DocumentDomainObject newDoc = documentMapper.createNewTextDocument( user, parentId, parentMenuNumber );
        TextDocument result = new TextDocument( securityChecker, this, newDoc, documentMapper, documentPermissionSetMapper );
        return result;
    }

    public void saveChanges( TextDocument document ) {
        documentMapper.saveTextDocument( document.getInternal() );
    }

}