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


    // runs inside transaction
    public void visitTextDocument( final TextDocumentDomainObject textDocument ) {
        updateTextDocumentTexts( textDocument, null, getSavingUser());
        updateTextDocumentImages( textDocument, null, getSavingUser());
        updateTextDocumentContentLoops(textDocument, null, null);
        updateTextDocumentMenus( textDocument, null, getSavingUser());
    }
}