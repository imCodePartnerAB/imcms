package com.imcode.imcms;

import com.imcode.imcms.api.DocumentVersion;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;

/**
 * Cleans doc's identity data.
 * When new doc is created by cloning an existing one its identity and version fields should be cleared.
 */
public class DocIdentityCleanerVisitor extends DocumentVisitor {

    @Override
    public void visitTextDocument(TextDocumentDomainObject doc) {
        visitOtherDocument(doc);



        doc.getTemplateNames().setDocId(null);
    }


    @Override
    protected void visitOtherDocument(DocumentDomainObject doc) {
        doc.getMeta().setId(null);
        doc.setVersionNo(DocumentVersion.WORKING_VERSION_NO);
    }

    @Override
    public void visitFileDocument(FileDocumentDomainObject doc) {
        visitOtherDocument(doc);
        //for (FileDocumentDomainObject.FileDocumentFile file: doc.getFiles().values()) {
        //    file.
        //}
    }
}
