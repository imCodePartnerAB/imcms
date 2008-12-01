package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;

import java.io.File;
import java.util.Collection;

import com.imcode.db.Database;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.orm.FileReference;
import com.imcode.imcms.mapping.orm.HtmlReference;
import com.imcode.imcms.mapping.orm.UrlReference;

class DocumentInitializingVisitor extends DocumentVisitor {

    private final Database database;

    private TextDocumentInitializer textDocumentInitializer;
    
    DocumentInitializingVisitor(DocumentGetter documentGetter, Collection documentIds,
                                DocumentMapper documentMapper) {
        this.database = documentMapper.getDatabase();
        textDocumentInitializer = new TextDocumentInitializer(database, documentGetter, documentIds);
    }

    public void visitFileDocument(final FileDocumentDomainObject document) {    	
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
    	Collection<FileReference> fileReferences = dao.getFileReferences(document.getId());
    	
    	for (FileReference fileRef: fileReferences) {
            String fileId = fileRef.getFileId();           
            FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();
            
            file.setFilename(fileRef.getFilename());
            file.setMimeType(fileRef.getMimeType());
            file.setCreatedAsImage(fileRef.getCreatedAsImage());
            
            File fileForFileDocument = DocumentStoringVisitor.getFileForFileDocumentFile(document.getId(), fileId);
            if ( !fileForFileDocument.exists() ) {
                File oldlyNamedFileForFileDocument = new File(fileForFileDocument.getParentFile(),
                                                              fileForFileDocument.getName()
                                                              + "_se");
                if ( oldlyNamedFileForFileDocument.exists() ) {
                    fileForFileDocument = oldlyNamedFileForFileDocument;
                }
            }
            
            file.setInputStreamSource(new FileInputStreamSource(fileForFileDocument));
            
            document.addFile(fileId, file);
            
            if (fileRef.isDefaultFileId()) {
                document.setDefaultFileId(fileId);
            }
    		
    	}
    }

    public void visitHtmlDocument(HtmlDocumentDomainObject document) {
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
    	HtmlReference html = dao.getHtmlReference(document.getId());
    	document.setHtml(html.getHtml());    	
    }

    public void visitUrlDocument(UrlDocumentDomainObject document) {
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
    	UrlReference reference = dao.getUrlReference(document.getId());
    	document.setUrl(reference.getUrl());    	
    }

    public void visitTextDocument(final TextDocumentDomainObject document) {
        textDocumentInitializer.initialize(document) ;
    }
}
