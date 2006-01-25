package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;
import imcode.util.Utility;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.UnhandledException;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

class DocumentInitializingVisitor extends DocumentVisitor {

    private DocumentMapper documentMapper;
    private final Database database;
    private static final String SQL__SELECT_FILE_DOCUMENT_FILES = "SELECT variant_name, filename, mime, created_as_image, default_variant FROM fileupload_docs WHERE meta_id = ? ORDER BY default_variant DESC, variant_name";

    private TextDocumentInitializer textDocumentInitializer;
    
    DocumentInitializingVisitor(DocumentGetter documentGetter, Collection documentIds,
                                DocumentMapper documentMapper) {
        this.database = documentMapper.getDatabase();
        this.documentMapper = documentMapper;
        textDocumentInitializer = new TextDocumentInitializer(database, documentGetter, documentIds);
    }

    public void visitBrowserDocument(BrowserDocumentDomainObject document) {
        String[][] sqlResult;
        try {
            String[] parameters = new String[] { "" + document.getId() };
            String sqlStr = "SELECT to_meta_id, browser_id FROM browser_docs WHERE meta_id = ?";
            sqlResult = (String[][]) database.execute(new SqlQueryCommand(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
        for ( int i = 0; i < sqlResult.length; i++ ) {
            String[] sqlRow = sqlResult[i];
            int toMetaId = Integer.parseInt(sqlRow[0]);
            int browserId = Integer.parseInt(sqlRow[1]);
            BrowserDocumentDomainObject.Browser browser = documentMapper.getBrowserById(browserId);
            document.setBrowserDocumentId(browser, toMetaId);
        }
    }

    public void visitFileDocument(final FileDocumentDomainObject document) {
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
    }

    public void visitHtmlDocument(HtmlDocumentDomainObject htmlDocument) {
        String html;
        try {
            String[] parameters = new String[] { "" + htmlDocument.getId() };
            String sqlStr = "SELECT frame_set FROM frameset_docs WHERE meta_id = ?";
            html = (String) database.execute(new SqlQueryCommand(sqlStr, parameters, Utility.SINGLE_STRING_HANDLER));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
        htmlDocument.setHtml(html);
    }

    public void visitUrlDocument(UrlDocumentDomainObject document) {
        String url;
        try {
            String[] parameters = new String[] { "" + document.getId() };
            url = (String) database.execute(new SqlQueryCommand("SELECT url_ref FROM url_docs WHERE meta_id = ?", parameters, Utility.SINGLE_STRING_HANDLER));
        } catch ( DatabaseException e ) {
            throw new UnhandledException(e);
        }
        document.setUrl(url);
    }

    public void visitTextDocument(final TextDocumentDomainObject document) {
        textDocumentInitializer.initialize(document) ;
    }


}
