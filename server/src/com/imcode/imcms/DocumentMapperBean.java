package com.imcode.imcms;

import imcode.server.document.DocumentMapper ;
import imcode.server.document.Document;

public class DocumentMapperBean {
    private SecurityChecker securityChecker;
    private DocumentMapper documentMapper;

    public DocumentMapperBean( SecurityChecker securityChecker, DocumentMapper documentMapper ) {
        this.securityChecker = securityChecker;
        this.documentMapper = documentMapper;
    }

    public TextDocumentBean getDocument( int metaId ) throws NoPermissionException {
        Document doc = documentMapper.getDocument( metaId );
        TextDocumentBean result = new TextDocumentBean( securityChecker, doc, documentMapper );

        return result;
    }

}