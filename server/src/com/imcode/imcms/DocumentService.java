package com.imcode.imcms;

import imcode.server.document.DocumentMapper ;

public class DocumentService {
    private SecurityChecker securityChecker;
    private DocumentMapper documentMapper;

    public DocumentService( SecurityChecker securityChecker, DocumentMapper documentMapper ) {
        this.securityChecker = securityChecker;
        this.documentMapper = documentMapper;
    }

    public Document getDocument( int metaId ) throws NoPermissionException {
        imcode.server.document.DocumentDomainObject doc = documentMapper.getDocument( metaId );
        Document result = new TextDocumentBean( securityChecker, doc, documentMapper );

        return result;
    }

}