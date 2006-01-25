package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.SingleConnectionDatabase;
import com.imcode.db.commands.TransactionDatabaseCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.BrowserDocumentDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentStoringVisitor extends DocumentVisitor {

    protected Database database ;
    protected ImcmsServices services;

    private static final int FILE_BUFFER_LENGTH = 2048;
    private static final int DB_FIELD_MAX_LENGTH__FILENAME = 255;

    public DocumentStoringVisitor(Database database, ImcmsServices services) {
        this.database = database ;
        this.services = services ;
    }

    protected void saveFileDocumentFile( int fileDocumentId, FileDocumentDomainObject.FileDocumentFile fileDocumentFile,
                                       String fileId ) {
        try {
            InputStreamSource inputStreamSource = fileDocumentFile.getInputStreamSource();
            InputStream in;
            try {
                in = inputStreamSource.getInputStream();
            } catch (FileNotFoundException e) {
                throw new UnhandledException("The file for filedocument " + fileDocumentId
                        + " has disappeared.", e);
            }
            if (null == in) {
                return;
            }

            File file = getFileForFileDocumentFile( fileDocumentId, fileId );

            boolean sameFileOnDisk = file.exists() && inputStreamSource.equals(new FileInputStreamSource(file)) ;
            if ( sameFileOnDisk ) {
                return;
            }

            byte[] buffer = new byte[FILE_BUFFER_LENGTH];
            final OutputStream out = new FileOutputStream(file);
            try {
                for (int bytesRead; -1 != (bytesRead = in.read(buffer));) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
                in.close();
            }
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    public static File getFileForFileDocumentFile( int fileDocumentId, String fileId ) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        String filename = "" + fileDocumentId ;
        if (StringUtils.isNotBlank( fileId )) {
            filename += "."+FileUtility.escapeFilename(fileId) ;
        }
        File file = new File(filePath, filename);
        return file;
    }

    static String makeSqlInsertString(String tableName, String[] columnNames) {
        return "INSERT INTO " + tableName + " (" + StringUtils.join(columnNames, ",") + ")"
                + "VALUES(?" + StringUtils.repeat(",?", columnNames.length - 1) + ")";
    }

    void updateTextDocumentTexts(TextDocumentDomainObject textDocument) {
        Map texts = textDocument.getTexts();
        String sqlDeleteTexts = "DELETE FROM texts WHERE meta_id = ?";
        final Object[] parameters = new String[]{"" + textDocument.getId()};
        ((Integer)database.execute( new SqlUpdateCommand( sqlDeleteTexts, parameters ) )).intValue();
        for (Iterator iterator = texts.keySet().iterator(); iterator.hasNext();) {
            Integer textIndex = (Integer) iterator.next();
            TextDomainObject text = (TextDomainObject) texts.get(textIndex);
            sqlInsertText(textDocument, textIndex, text);
        }
    }

    void updateTextDocumentImages(TextDocumentDomainObject textDocument) {
        Map images = textDocument.getImages();
        String sqlDeleteImages = "DELETE FROM images WHERE meta_id = ?";
        final Object[] parameters = new String[]{"" + textDocument.getId()};
        ((Integer)database.execute( new SqlUpdateCommand( sqlDeleteImages, parameters ) )).intValue();
        for (Iterator iterator = images.keySet().iterator(); iterator.hasNext();) {
            Integer imageIndex = (Integer) iterator.next();
            ImageDomainObject image = (ImageDomainObject) images.get(imageIndex);
            saveDocumentImage(textDocument.getId(), imageIndex.intValue(), image);
        }
    }

    void updateTextDocumentIncludes(TextDocumentDomainObject textDocument) {
        Map includes = textDocument.getIncludes();
        String sqlDeleteDocumentIncludes = "DELETE FROM includes WHERE meta_id = ?";
        final Object[] parameters = new String[]{"" + textDocument.getId()};
        ((Integer)database.execute( new SqlUpdateCommand( sqlDeleteDocumentIncludes, parameters ) )).intValue();
        for (Iterator iterator = includes.keySet().iterator(); iterator.hasNext();) {
            Integer includeIndex = (Integer) iterator.next();
            Integer includedDocumentId = (Integer) includes.get(includeIndex);
            sqlInsertTextDocumentInclude(textDocument, includeIndex, includedDocumentId);
        }
    }

    private void sqlInsertText(TextDocumentDomainObject textDocument, Integer textIndex, TextDomainObject text) {
        final Object[] parameters = new String[]{
            "" + textDocument.getId(), "" + textIndex, text.getText(), "" + text.getType()
        };
        ((Integer)database.execute( new SqlUpdateCommand( "INSERT INTO texts (meta_id, name, text, type) VALUES(?,?,?,?)", parameters ) )).intValue();
    }

    private void sqlInsertTextDocumentInclude(TextDocumentDomainObject textDocument, Integer includeIndex,
                                              Integer includedDocumentId) {
        final Object[] parameters = new String[]{
            "" + textDocument.getId(), "" + includeIndex, "" + includedDocumentId
        };
        ((Integer)database.execute( new SqlUpdateCommand( "INSERT INTO includes (meta_id, include_id, included_meta_id) VALUES(?,?,?)", parameters ) )).intValue();
    }

    public static void saveDocumentImage(int meta_id, int img_no, ImageDomainObject image) {
        String sqlStr = "update images\n"
                + "set imgurl  = ?, \n"
                + "width       = ?, \n"
                + "height      = ?, \n"
                + "border      = ?, \n"
                + "v_space     = ?, \n"
                + "h_space     = ?, \n"
                + "image_name  = ?, \n"
                + "target      = ?, \n"
                + "align       = ?, \n"
                + "alt_text    = ?, \n"
                + "low_scr     = ?, \n"
                + "linkurl     = ?, \n"
                + "type        = ?  \n"
                + "where meta_id = ? \n"
                + "and name = ? \n";

        int rowUpdateCount = sqlImageUpdateQuery(sqlStr, image, meta_id, img_no);
        if (0 == rowUpdateCount) {
            sqlStr = "insert into images (imgurl, width, height, border, v_space, h_space, image_name, target, align, alt_text, low_scr, linkurl, type, meta_id, name)"
                    + " values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)";

            sqlImageUpdateQuery(sqlStr, image, meta_id, img_no);
        }
    }

    private static int sqlImageUpdateQuery(String sqlStr, ImageDomainObject image, int meta_id, int img_no) {
        ImageSource imageSource = image.getSource();
        final Object[] parameters = new String[] {
            imageSource.toStorageString(),
            "" + image.getWidth(),
            "" + image.getHeight(),
            "" + image.getBorder(),
            "" + image.getVerticalSpace(),
            "" + image.getHorizontalSpace(),
            image.getName(),
            image.getTarget(),
            image.getAlign(),
            image.getAlternateText(),
            image.getLowResolutionUrl(),
            image.getLinkUrl(),
            "" + imageSource.getTypeId(),
            "" + meta_id,
            "" + img_no,
        };
        return ( (Integer) Imcms.getServices().getDatabase().execute(new SqlUpdateCommand(sqlStr, parameters)) ).intValue();
    }

    public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
        Map fileDocumentFiles = fileDocument.getFiles();

        String sqlDelete = "DELETE FROM fileupload_docs WHERE meta_id = ?";
        final Object[] parameters1 = new String[]{"" + fileDocument.getId()};
        ((Integer)database.execute( new SqlUpdateCommand( sqlDelete, parameters1 ) )).intValue();

        for ( Iterator iterator = fileDocumentFiles.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String fileId = (String)entry.getKey();
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = (FileDocumentDomainObject.FileDocumentFile)entry.getValue();

            String filename = fileDocumentFile.getFilename();
            if ( filename.length() > DB_FIELD_MAX_LENGTH__FILENAME ) {
                filename = truncateFilename( filename, DB_FIELD_MAX_LENGTH__FILENAME );
            }
            String sqlInsert = "INSERT INTO fileupload_docs (meta_id, variant_name, filename, mime, created_as_image, default_variant) VALUES(?,?,?,?,?,?)";
            boolean isDefaultFile = fileId.equals( fileDocument.getDefaultFileId());
            final Object[] parameters = new String[]{""+ fileDocument.getId(), fileId, filename, fileDocumentFile.getMimeType(), fileDocumentFile.isCreatedAsImage() ? "1" : "0", isDefaultFile ? "1" : "0"};
            ((Integer)database.execute( new SqlUpdateCommand( sqlInsert, parameters ) )).intValue();
            saveFileDocumentFile( fileDocument.getId(), fileDocumentFile, fileId );
        }
        DocumentMapper.deleteOtherFileDocumentFiles( fileDocument ) ;
    }

    private String truncateFilename(String filename, int length) {
        String truncatedFilename = StringUtils.left(filename, length);
        String extensions = getExtensionsFromFilename(filename);
        if (extensions.length() > length) {
            return truncatedFilename;
        }
        String basename = StringUtils.chomp(filename, extensions);
        String truncatedBasename = StringUtils.substring(basename, 0, length - extensions.length());
        truncatedFilename = truncatedBasename + extensions;
        return truncatedFilename;
    }

    private String getExtensionsFromFilename(String filename) {
        String extensions = "";
        Matcher matcher = Pattern.compile("(?:\\.\\w+)+$").matcher(filename);
        if (matcher.find()) {
            extensions = matcher.group();
        }
        return extensions;
    }

    public void visitBrowserDocument( BrowserDocumentDomainObject browserDocument ) {
        deleteBrowserDocument( browserDocument );
        saveNewBrowserDocument( browserDocument );
    }

    private void deleteBrowserDocument( BrowserDocumentDomainObject browserDocument ) {
        String sqlStr = "DELETE FROM browser_docs WHERE meta_id = ?";
        final Object[] parameters = new String[]{"" + browserDocument.getId()};
        ((Integer)database.execute( new SqlUpdateCommand( sqlStr, parameters ) )).intValue();
    }

    public void saveNewBrowserDocument( BrowserDocumentDomainObject document ) {
        String[] browserDocumentColumns = {"meta_id", "to_meta_id", "browser_id"};

        String sqlBrowserDocsInsertStr = makeSqlInsertString( "browser_docs", browserDocumentColumns );

        Map browserDocumentMap = document.getBrowserDocumentIdMap();
        for ( Iterator iterator = browserDocumentMap.keySet().iterator(); iterator.hasNext(); ) {
            BrowserDocumentDomainObject.Browser browser = (BrowserDocumentDomainObject.Browser)iterator.next();
            Integer metaIdForBrowser = (Integer)browserDocumentMap.get( browser );
            final Object[] parameters = new String[]{
                "" + document.getId(), "" + metaIdForBrowser, "" + browser.getId()
            };
            ((Integer)database.execute( new SqlUpdateCommand( sqlBrowserDocsInsertStr, parameters ) )).intValue();
        }
    }

    protected void updateTextDocumentMenus(final TextDocumentDomainObject textDocument) {
        database.execute( new TransactionDatabaseCommand() {
            public Object executeInTransaction( DatabaseConnection connection ) {
                MenuSaver menuSaver = new MenuSaver(new SingleConnectionDatabase(connection)) ;
                menuSaver.updateTextDocumentMenus(textDocument, services );
                return null ;
            }
        } );
    }
}
