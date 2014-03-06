package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.mapping.jpa.doc.content.UrlDocContent;
import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Creates new document content.
 */
public class DocumentCreatingVisitor extends DocumentStoringVisitor {

    private UserDomainObject currentUser;

    public DocumentCreatingVisitor(ImcmsServices services, UserDomainObject currentUser) {
        super(services);
        this.currentUser = currentUser;
    }

    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        HtmlDocContent reference = new HtmlDocContent();
        Version version = versionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setVersion(version);
        reference.setHtml(document.getHtml());

        DocRepository repository = services.getManagedBean(DocRepository.class);

        repository.saveHtmlDocContent(reference);
    }

    public void visitUrlDocument(UrlDocumentDomainObject document) {
        UrlDocContent reference = new UrlDocContent();
        Version version = versionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setVersion(version);
        reference.setUrlTarget("");
        reference.setUrlText("");
        reference.setUrlLanguagePrefix("");
        reference.setUrlFrameName("");
        reference.setUrl(document.getUrl());

        DocRepository repository = services.getManagedBean(DocRepository.class);

        repository.saveUrlDocContent(reference);
    }

    public void visitTextDocument(TextDocumentDomainObject document) {
        textDocumentContentSaver.createContent(document, currentUser);
    }
}
