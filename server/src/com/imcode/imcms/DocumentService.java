package com.imcode.imcms;

import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;

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
     * @throws NoPermissionException If the current user dosen't have the rights to read this document.
     */
    public Document getDocument( int documentId ) throws NoPermissionException {
        securityChecker.hasDocumentRights( documentId );
        imcode.server.document.DocumentDomainObject doc = documentMapper.getDocument( documentId );
        Document result = new TextDocument( securityChecker, doc, documentMapper, documentPermissionSetMapper );

        return result;
    }

}