package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.mapping.jpa.doc.content.UrlDocContent;
import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates or updates document content.
 */
//todo: init using spring
public class DocumentCreatingVisitor extends DocumentStoringVisitor {

    private UserDomainObject currentUser;

    public DocumentCreatingVisitor(ImcmsServices services, UserDomainObject currentUser) {
        super(services);
        this.currentUser = currentUser;
    }

    @Transactional
    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        HtmlDocContent reference = new HtmlDocContent();
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setDocVersion(docVersion);
        reference.setHtml(document.getHtml());

        DocRepository repository = services.getManagedBean(DocRepository.class);

        repository.saveHtmlReference(reference);
    }

    @Transactional
    public void visitUrlDocument(UrlDocumentDomainObject document) {
        UrlDocContent reference = new UrlDocContent();
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setDocVersion(docVersion);
        reference.setUrlTarget("");
        reference.setUrlText("");
        reference.setUrlLanguagePrefix("");
        reference.setUrlFrameName("");
        reference.setUrl(document.getUrl());

        DocRepository repository = services.getManagedBean(DocRepository.class);

        repository.saveUrlReference(reference);
    }

    @Transactional
    public void visitTextDocument(final TextDocumentDomainObject textDocument) {
        updateTextDocumentContentLoops(textDocument, currentUser);
        updateTextDocumentTemplateNames(textDocument, currentUser);
        updateTextDocumentTexts(textDocument, currentUser);
        updateTextDocumentImages(textDocument, currentUser);
        updateTextDocumentIncludes(textDocument);
        updateTextDocumentMenus(textDocument, currentUser);
    }
}
