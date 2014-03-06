package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlContent;
import com.imcode.imcms.mapping.jpa.doc.content.UrlContent;
import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.transaction.annotation.Transactional;

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
        HtmlContent reference = new HtmlContent();
        Version version = docVersionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setVersion(version);
        reference.setHtml(document.getHtml());

        DocRepository repository = services.getManagedBean(DocRepository.class);

        repository.saveHtmlReference(reference);
    }

    public void visitUrlDocument(UrlDocumentDomainObject document) {
        UrlContent reference = new UrlContent();
        Version version = docVersionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setVersion(version);
        reference.setUrlTarget("");
        reference.setUrlText("");
        reference.setUrlLanguagePrefix("");
        reference.setUrlFrameName("");
        reference.setUrl(document.getUrl());

        DocRepository repository = services.getManagedBean(DocRepository.class);

        repository.saveUrlReference(reference);
    }

    public void visitTextDocument(TextDocumentDomainObject document) {
        textDocumentContentSaver.saveContent(document, currentUser);
    }
}
