package com.imcode.imcms.mapping;

import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;

public class DocumentDeletingVisitor extends DocumentVisitor {

    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        DocumentMapper.deleteAllFileDocumentFiles(fileDocument);
    }
}
