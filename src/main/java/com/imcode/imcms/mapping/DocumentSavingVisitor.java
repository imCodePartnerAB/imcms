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
 *
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

    // todo: check and (if needs) prepare like #visitUrlDocument()
    // runs inside transaction
    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        Version version = versionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());
        HtmlDocContent htmlReference = new HtmlDocContent();

        htmlReference.setHtml(document.getHtml());
        htmlReference.setVersion(version);

        docRepository.deleteHtmlDocContent(document.getRef());
        docRepository.saveHtmlDocContent(htmlReference);
    }

    // runs inside transaction
    public void visitUrlDocument(UrlDocumentDomainObject document) {
        UrlDocContent urlDocContent = docRepository.getUrlDocContent(document.getRef());
        urlDocContent.setUrl(document.getUrl());

        docRepository.saveUrlDocContent(urlDocContent);
    }

    // runs inside transaction
    public void visitTextDocument(TextDocumentDomainObject document) {
        textDocumentContentSaver.updateContent(document, savingUser);
    }

    public UserDomainObject getSavingUser() {
        return savingUser;
    }

    public void setSavingUser(UserDomainObject savingUser) {
        this.savingUser = savingUser;
    }
}
