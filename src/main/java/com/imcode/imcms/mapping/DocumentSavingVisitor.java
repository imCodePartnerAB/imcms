package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.mapping.jpa.doc.content.UrlDocContent;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Updates existing document content.

 * @see com.imcode.imcms.mapping.DocumentSaver
 */
public class DocumentSavingVisitor extends DocumentStoringVisitor {

    /**
     * Current version of a document.
     */
    private DocumentDomainObject oldDocument;

    /**
     * An user performing save operation.
     */
    private UserDomainObject savingUser;

    public DocumentSavingVisitor(DocumentDomainObject documentInDatabase,
                                 ImcmsServices services, UserDomainObject user) {
        super(services);
        oldDocument = documentInDatabase;
        savingUser = user;
    }

    // runs inside transaction   
    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        Version version = docVersionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());
        HtmlDocContent htmlReference = new HtmlDocContent();

        htmlReference.setHtml(document.getHtml());
        htmlReference.setVersion(version);

        docRepository.deleteHtmlReference(document.getRef());
        docRepository.saveHtmlReference(htmlReference);
    }

    // runs inside transaction   
    public void visitUrlDocument(UrlDocumentDomainObject document) {
        Version version = docVersionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());
        UrlDocContent reference = new UrlDocContent();

        reference.setVersion(version);

        reference.setUrl(document.getUrl());
        reference.setUrlTarget("");
        reference.setUrlText("");
        reference.setUrlLanguagePrefix("");
        reference.setUrlFrameName("");

        docRepository.deleteUrlReference(document.getRef());
        docRepository.saveUrlReference(reference);
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
