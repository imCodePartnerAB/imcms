package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.orm.FileDocItem;
import com.imcode.imcms.mapping.orm.HtmlDocContent;
import com.imcode.imcms.mapping.orm.UrlDocContent;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;

import java.io.File;
import java.util.Collection;

import com.imcode.imcms.mapping.dao.DocDao;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

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
    private DocDao metaDao;

    /**
     * Initializes file document.
     * <p/>
     * Undocumented behavior:
     * ?? If file is missing in FS this is not an error.
     * ?? If file can not be found by original filename tries to find the same file but with "_se" suffix.
     */
    public void visitFileDocument(final FileDocumentDomainObject doc) {
        Collection<FileDocItem> fileDocItems = metaDao.getFileDocItems(doc.getRef());

        for (FileDocItem item : fileDocItems) {
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
        HtmlDocContent html = metaDao.getHtmlDocContent(doc.getRef());
        doc.setHtml(html.getHtml());
    }

    public void visitUrlDocument(UrlDocumentDomainObject doc) {
        UrlDocContent reference = metaDao.getUrlDocContent(doc.getRef());
        doc.setUrl(reference.getUrl());
    }

    public void visitTextDocument(final TextDocumentDomainObject document) {
        textDocumentInitializer.initialize(document);
    }

    public DocDao getMetaDao() {
        return metaDao;
    }

    public void setMetaDao(DocDao metaDao) {
        this.metaDao = metaDao;
    }

    public TextDocumentInitializer getTextDocumentInitializer() {
        return textDocumentInitializer;
    }

    public void setTextDocumentInitializer(TextDocumentInitializer textDocumentInitializer) {
        this.textDocumentInitializer = textDocumentInitializer;
    }
}