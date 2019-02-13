package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updates existing document content.
 *
 * @see com.imcode.imcms.mapping.DocumentSaver
 */
@Component
@Transactional
public class DocumentSavingVisitor extends DocumentStoringVisitor {

    public DocumentSavingVisitor(DocRepository docRepository,
                                 VersionRepository versionRepository,
                                 LanguageRepository languageRepository,
                                 TextDocumentContentSaver textDocumentContentSaver) {

        super(docRepository, versionRepository, languageRepository, textDocumentContentSaver);
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
        DocumentUrlJPA documentUrlJPA = docRepository.getUrlDocContent(document.getRef());
        documentUrlJPA.setUrl(document.getUrl());

        docRepository.saveUrlDocContent(documentUrlJPA);
    }

    // runs inside transaction
    public void visitTextDocument(TextDocumentDomainObject document) {
        textDocumentContentSaver.updateContent(document, Imcms.getUser());
    }

}
