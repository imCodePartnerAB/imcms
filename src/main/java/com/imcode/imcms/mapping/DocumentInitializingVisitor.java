package com.imcode.imcms.mapping;

import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.imcode.db.Database;
import com.imcode.imcms.api.orm.OrmFileDocument;
import com.imcode.imcms.api.orm.OrmHtmlDocument;
import com.imcode.imcms.api.orm.OrmUrlDocument;

class DocumentInitializingVisitor extends DocumentVisitor {

    private DocumentMapper documentMapper;
    private final Database database;
    //private static final String SQL__SELECT_FILE_DOCUMENT_FILES = "SELECT variant_name, filename, mime, created_as_image, default_variant FROM fileupload_docs WHERE meta_id = ? ORDER BY default_variant DESC, variant_name";

    private TextDocumentInitializer textDocumentInitializer;
    
    DocumentInitializingVisitor(DocumentGetter documentGetter, Collection documentIds,
                                DocumentMapper documentMapper) {
        this.database = documentMapper.getDatabase();
        this.documentMapper = documentMapper;
        textDocumentInitializer = new TextDocumentInitializer(database, documentGetter, documentIds);
    }

    public void visitFileDocument(final FileDocumentDomainObject document) {
    	/*
        Object[] parameters = new String[] {
                "" + document.getId() };
        database.execute(new SqlQueryCommand(SQL__SELECT_FILE_DOCUMENT_FILES, parameters, new ResultSetHandler() {
            public Object handle(ResultSet resultSet) throws SQLException {
                while ( resultSet.next() ) {
                    String fileId = resultSet.getString(1);
                    FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();
                    file.setFilename(resultSet.getString(2));
                    file.setMimeType(resultSet.getString(3));
                    file.setCreatedAsImage(0 != resultSet.getInt(4));
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
                    boolean isDefaultFile = 0 != resultSet.getInt(5);
                    if ( isDefaultFile ) {
                        document.setDefaultFileId(fileId);
                    }
                }
                return null;
            }
        }));
        */
    	
    	
    	/*
    	OrmFileDocument orm = (OrmFileDocument)document.getMeta().getOrmDocument();
    	
    	for (Map.Entry<String, OrmFileDocument.FileRef> entry: orm.getFileRefsMap().entrySet()) {
            String fileId = entry.getKey();
            OrmFileDocument.FileRef fileRef = entry.getValue();
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
    	*/
    }

    public void visitHtmlDocument(HtmlDocumentDomainObject htmlDocument) {
    	/*
        String html;
        try {
            String[] parameters = new String[] { "" + htmlDocument.getId() };
            String sqlStr = "SELECT frame_set FROM frameset_docs WHERE meta_id = ?";
            html = (String) database.execute(new SqlQueryCommand(sqlStr, parameters, Utility.SINGLE_STRING_HANDLER));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
        htmlDocument.setHtml(html);
        */
    	//OrmHtmlDocument orm = (OrmHtmlDocument)htmlDocument.getMeta().getOrmDocument();
    	//htmlDocument.setHtml(orm.getHtml());    	
    }

    public void visitUrlDocument(UrlDocumentDomainObject document) {
    	/*
        String url;
        try {
            String[] parameters = new String[] { "" + document.getId() };
            url = (String) database.execute(new SqlQueryCommand("SELECT url_ref FROM url_docs WHERE meta_id = ?", parameters, Utility.SINGLE_STRING_HANDLER));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
        document.setUrl(url);
        */
    	//OrmUrlDocument orm = (OrmUrlDocument)document.getMeta().getOrmDocument();
    	//document.setUrl(orm.getUrl());
    }

    public void visitTextDocument(final TextDocumentDomainObject document) {
        textDocumentInitializer.initialize(document) ;
    }
}
