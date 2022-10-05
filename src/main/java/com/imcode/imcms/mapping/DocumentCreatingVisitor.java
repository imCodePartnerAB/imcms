package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.stereotype.Component;

/**
 * Creates new document content.
 */
@Component
public class DocumentCreatingVisitor extends DocumentVisitor {

    private final DocRepository docRepository;
    private final VersionRepository versionRepository;
    private final TextDocumentContentSaver textDocumentContentSaver;

    public DocumentCreatingVisitor(DocRepository docRepository,
                                   VersionRepository versionRepository,
                                   TextDocumentContentSaver textDocumentContentSaver) {
        this.docRepository = docRepository;
        this.versionRepository = versionRepository;
        this.textDocumentContentSaver = textDocumentContentSaver;
    }

    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
        HtmlDocContent reference = new HtmlDocContent();
        Version version = versionRepository.findByDocIdAndNo(document.getId(), document.getVersionNo());

        reference.setVersion(version);
        reference.setHtml(document.getHtml());

        docRepository.saveHtmlDocContent(reference);
    }

    public void visitUrlDocument(UrlDocumentDomainObject document) {
        DocumentUrlJPA reference = new DocumentUrlJPA();
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
