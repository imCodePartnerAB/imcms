package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.mapping.jpa.doc.content.UrlDocContent;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.stereotype.Component;

/**
 * Creates new document content.
 */
@Component
public class DocumentCreatingVisitor extends DocumentStoringVisitor {

    public DocumentCreatingVisitor(ImcmsServices services) {
        super(services);
    }

    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        HtmlDocContent reference = new HtmlDocContent();
        Version version = versionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setVersion(version);
        reference.setHtml(document.getHtml());

        docRepository.saveHtmlDocContent(reference);
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

        docRepository.saveUrlDocContent(reference);
    }

    public void visitTextDocument(TextDocumentDomainObject document) {
        textDocumentContentSaver.createContent(document, Imcms.getUser());
    }
}
