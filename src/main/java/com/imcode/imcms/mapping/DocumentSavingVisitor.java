package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.UrlReference;

/**
 * Updates existing document fields.
 * <p/>
 * Not a public API. Must not be used directly.
 *
 * @see com.imcode.imcms.mapping.DocumentSaver
 */
public class DocumentSavingVisitor extends DocumentStoringVisitor {

    /** Current version of a document. */
    private DocumentDomainObject oldDocument;

    /** An user performing save operation. */
    private UserDomainObject savingUser;

    public DocumentSavingVisitor(DocumentDomainObject documentInDatabase,
                                 ImcmsServices services, UserDomainObject user) {
        super(services);
        oldDocument = documentInDatabase;
        savingUser = user;
    }

    // runs inside transaction   
    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        HtmlReference htmlReference = new HtmlReference();

        htmlReference.setDocRef(document.getRef());
        htmlReference.setHtml(document.getHtml());

        metaDao.deleteHtmlReference(document.getRef());
        metaDao.saveHtmlReference(htmlReference);
    }

    // runs inside transaction   
    public void visitUrlDocument(UrlDocumentDomainObject document) {
        UrlReference reference = new UrlReference();
        reference.setDocRef(document.getRef());
        reference.setUrl(document.getUrl());
        reference.setUrlTarget("");
        reference.setUrlText("");
        reference.setUrlLanguagePrefix("");
        reference.setUrlFrameName("");

        metaDao.deleteUrlReference(document.getRef());
        metaDao.saveUrlReference(reference);
    }

    // runs inside transaction 
    public void visitTextDocument(final TextDocumentDomainObject textDocument) {
        // NB! Content loops must be created before texts and images they possibly contain.
        updateTextDocumentContentLoops(textDocument, savingUser);
        updateTextDocumentTemplateNames(textDocument, savingUser);
        updateTextDocumentTexts(textDocument, savingUser);
        updateTextDocumentImages(textDocument, savingUser);
        updateTextDocumentIncludes(textDocument);

        boolean menusChanged = !textDocument.getMenus().equals(((TextDocumentDomainObject) oldDocument).getMenus());

        if (menusChanged) {
            updateTextDocumentMenus(textDocument, savingUser);
        }
    }

    public UserDomainObject getSavingUser() {
        return savingUser;
    }

    public void setSavingUser(UserDomainObject savingUser) {
        this.savingUser = savingUser;
    }
}
