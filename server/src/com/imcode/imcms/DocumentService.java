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

    public Document getDocument( int metaId ) throws NoPermissionException {
        securityChecker.hasDocumentRights( metaId );
        imcode.server.document.DocumentDomainObject doc = documentMapper.getDocument( metaId );
        Document result = new TextDocument( securityChecker, doc, documentMapper, documentPermissionSetMapper );

        return result;
    }

}