package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.content.FileItem;
import com.imcode.imcms.mapping.jpa.doc.content.HtmlContent;
import com.imcode.imcms.mapping.jpa.doc.content.UrlContent;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.Collection;

/**
 * Initializes a document fields depending on document's type.
 * <p/>
 * Document's fields are queried from a database.
 */
@Service
public class DocumentInitializingVisitor extends DocumentVisitor {

    @Inject
    private TextDocumentInitializer textDocumentInitializer;

    @Inject
    private DocRepository metaRepository;

    /**
     * Initializes file document.
     * <p/>
     * Undocumented behavior:
     * ?? If file is missing in FS this is not an error.
     * ?? If file can not be found by original filename tries to find the same file but with "_se" suffix.
     */
    public void visitFileDocument(final FileDocumentDomainObject doc) {
        Collection<FileItem> fileDocItems = metaRepository.getFileDocItems(doc.getRef());

        for (FileItem item : fileDocItems) {
            String fileId = item.getFileId();
            FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();

            file.setFilename(item.getFilename());
            file.setMimeType(item.getMimeType());
            file.setCreatedAsImage(item.getCreatedAsImage());

            File fileForFileDocument = DocumentStoringVisitor.getFileForFileDocumentFile(doc.getVersionRef(), fileId);
            if (!fileForFileDocument.exists()) {
                File oldlyNamedFileForFileDocument = new File(fileForFileDocument.getParentFile(),
                        fileForFileDocument.getName()
                                + "_se");
                if (oldlyNamedFileForFileDocument.exists()) {
                    fileForFileDocument = oldlyNamedFileForFileDocument;
                }
            }

            file.setInputStreamSource(new FileInputStreamSource(fileForFileDocument));

            doc.addFile(fileId, file);

            if (item.isDefaultFileId()) {
                doc.setDefaultFileId(fileId);
            }

        }
    }


    public void visitHtmlDocument(HtmlDocumentDomainObject doc) {
        HtmlContent html = metaRepository.getHtmlDocContent(doc.getRef());
        doc.setHtml(html.getHtml());
    }

    public void visitUrlDocument(UrlDocumentDomainObject doc) {
        UrlContent reference = metaRepository.getUrlDocContent(doc.getRef());
        doc.setUrl(reference.getUrl());
    }

    public void visitTextDocument(final TextDocumentDomainObject document) {
        textDocumentInitializer.initialize(document);
    }

    public DocRepository getMetaRepository() {
        return metaRepository;
    }

    public void setMetaRepository(DocRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    public TextDocumentInitializer getTextDocumentInitializer() {
        return textDocumentInitializer;
    }

    public void setTextDocumentInitializer(TextDocumentInitializer textDocumentInitializer) {
        this.textDocumentInitializer = textDocumentInitializer;
    }
}