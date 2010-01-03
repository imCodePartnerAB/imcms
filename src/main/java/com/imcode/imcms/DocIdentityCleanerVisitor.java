package com.imcode.imcms;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.DocumentVersion;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;

/**
 * Cleans doc's identity data.
 */
public class DocIdentityCleanerVisitor extends DocumentVisitor {

    @Override
    public void visitTextDocument(TextDocumentDomainObject doc) {
        visitOtherDocument(doc);

        for (TextDomainObject text: doc.getTexts().values()) {
            text.setId(null);
            text.setDocId(null);
        }

        for (ImageDomainObject image: doc.getImages().values()) {
            image.setId(null);
            image.setDocId(null);
        }

    	for (MenuDomainObject menu: doc.getMenus().values()) {
    		menu.setId(null);
    		menu.setMetaId(null);
    	}

    	for (ContentLoop loop: doc.getContentLoops().values()) {
    		loop.setId(null);
    		loop.setDocId(null);
    		loop.setModified(true);

    		for (Content content: loop.getContents()) {
    			content.setId(null);
    		}
    	}

        doc.getTemplateNames().setId(null);
        doc.getTemplateNames().setMetaId(null);
    }


    @Override
    protected void visitOtherDocument(DocumentDomainObject doc) {
		doc.getMeta().setId(null);
        doc.getLabels().setId(null);

        DocumentVersion version = doc.getVersion();

        version.setId(null);
        version.setNo(0);
        version.setDocId(null);
    }
    
}
