package com.imcode.imcms.mapping;

import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;

import java.io.File;
import java.util.Collection;

import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.orm.FileReference;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.UrlReference;

/**
 * Initializes a document fields depending on document's type.
 * 
 * Document's fields are queried from a database.
 */
public class DocumentInitializingVisitor extends DocumentVisitor {

    private TextDocumentInitializer textDocumentInitializer;
    
    private MetaDao metaDao;

    /**
     * Initializes file document.
     *
     * Undocumented behavior:
     *   ?? If file is missing in FS this is not an error.
     *   ?? If file can not be found by original filename tries to find the same file but with "_se" suffix.
     */
    public void visitFileDocument(final FileDocumentDomainObject doc) {
    	Collection<FileReference> fileReferences = metaDao.getFileReferences(doc.getRef());
    	
    	for (FileReference fileRef: fileReferences) {
            String fileId = fileRef.getFileId();           
            FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();
            
            file.setFilename(fileRef.getFilename());
            file.setMimeType(fileRef.getMimeType());
            file.setCreatedAsImage(fileRef.getCreatedAsImage());
            
            File fileForFileDocument = DocumentStoringVisitor.getFileForFileDocumentFile(doc.getRef(), fileId);
            if ( !fileForFileDocument.exists() ) {
                File oldlyNamedFileForFileDocument = new File(fileForFileDocument.getParentFile(),
                                                              fileForFileDocument.getName()
                                                              + "_se");
                if ( oldlyNamedFileForFileDocument.exists() ) {
                    fileForFileDocument = oldlyNamedFileForFileDocument;
                }
            }
            
            file.setInputStreamSource(new FileInputStreamSource(fileForFileDocument));
            
            doc.addFile(fileId, file);
            
            if (fileRef.isDefaultFileId()) {
                doc.setDefaultFileId(fileId);
            }
    		
    	}
    }
    

    public void visitHtmlDocument(HtmlDocumentDomainObject doc) {
    	HtmlReference html = metaDao.getHtmlReference(doc.getRef());
    	doc.setHtml(html.getHtml());
    }

    public void visitUrlDocument(UrlDocumentDomainObject doc) {
    	UrlReference reference = metaDao.getUrlReference(doc.getRef());
    	doc.setUrl(reference.getUrl());
    }

    
    /**
     * 
     */
    public void visitTextDocument(final TextDocumentDomainObject document) {
        textDocumentInitializer.initialize(document) ;
    }

	public MetaDao getMetaDao() {
		return metaDao;
	}

	public void setMetaDao(MetaDao metaDao) {
		this.metaDao = metaDao;
	}

	public TextDocumentInitializer getTextDocumentInitializer() {
		return textDocumentInitializer;
	}

	public void setTextDocumentInitializer(TextDocumentInitializer textDocumentInitializer) {
		this.textDocumentInitializer = textDocumentInitializer;
	}
}