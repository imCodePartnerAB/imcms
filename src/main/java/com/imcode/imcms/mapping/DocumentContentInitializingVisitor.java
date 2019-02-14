package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlDocContent;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;

/**
 * Initializes a document fields depending on document's type.
 * <p/>
 * Document's fields are queried from a database.
 */
@Service
public class DocumentContentInitializingVisitor extends DocumentVisitor {

    private final TextDocumentContentInitializer textDocumentContentInitializer;

    private final DocRepository docRepository;

    public DocumentContentInitializingVisitor(TextDocumentContentInitializer textDocumentContentInitializer, DocRepository docRepository) {
        this.textDocumentContentInitializer = textDocumentContentInitializer;
        this.docRepository = docRepository;
    }

    /**
     * Initializes file document.
     * <p/>
     * Undocumented behavior:
     * ?? If file is missing in FS this is not an error.
     * ?? If file can not be found by original filename tries to find the same file but with "_se" suffix.
     */
    public void visitFileDocument(FileDocumentDomainObject doc) {
        Collection<DocumentFileJPA> fileDocItems = docRepository.getFileDocContent(doc.getRef());

        for (DocumentFileJPA item : fileDocItems) {
            String fileId = item.getFileId();
            FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();

            file.setMimeType(item.getMimeType());
            file.setCreatedAsImage(item.isCreatedAsImage());

            final File fileForFileDocument = DocumentStoringVisitor.getFileForFileDocumentFile(item);
            file.setInputStreamSource(new FileInputStreamSource(fileForFileDocument));
            file.setFilename(item.getFilename());

            doc.addFile(fileId, file);

            if (item.isDefaultFile()) {
                doc.setDefaultFileId(fileId);
            }

        }
    }

    public void visitHtmlDocument(HtmlDocumentDomainObject doc) {
        HtmlDocContent html = docRepository.getHtmlDocContent(doc.getRef());
        doc.setHtml(html.getHtml());
    }

    public void visitUrlDocument(UrlDocumentDomainObject doc) {
        DocumentUrlJPA reference = docRepository.getUrlDocContent(doc.getRef());
        doc.setUrl(reference.getUrl());
    }

    public void visitTextDocument(TextDocumentDomainObject document) {
        textDocumentContentInitializer.initialize(document);
    }
}