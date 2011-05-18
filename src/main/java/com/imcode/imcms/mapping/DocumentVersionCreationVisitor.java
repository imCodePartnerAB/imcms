package com.imcode.imcms.mapping;

import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.UrlReference;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Not a public API. Must not be used directly.
 *
 * @see DocumentSaver
 */
public class DocumentVersionCreationVisitor extends DocumentSavingVisitor {

    public DocumentVersionCreationVisitor(ImcmsServices services, UserDomainObject user) {
        super(null, services, user);
    }


    /**
     * Visits non shared nodes. 
     */
    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        // NB! Content loops must be created before texts and images they possibly contain.
        updateTextDocumentContentLoops(textDocument, null, null);
        updateTextDocumentTexts( textDocument, null, getSavingUser());
        updateTextDocumentImages( textDocument, null, getSavingUser());
        updateTextDocumentMenus( textDocument, null, getSavingUser());
    }
}