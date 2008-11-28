package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.dao.ImageDao;
import com.imcode.imcms.dao.MenuDao;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.mapping.orm.FileReference;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;

public class DocumentStoringVisitor extends DocumentVisitor {
	
	private final ResultSetHandler singleStringHandler = new ResultSetHandler() {
		public Object handle(ResultSet rs) throws SQLException {
			return rs.next() ? rs.getString(1) : null;
		}
	};
	
	/**
	 * Hibernate template.
	 */
	//protected HibernateTemplate hibernateTemplate;

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

            FileInputStreamSource fileInputStreamSource = new FileInputStreamSource(file);
            boolean sameFileOnDisk = file.exists() && inputStreamSource.equals(fileInputStreamSource) ;
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
            fileDocumentFile.setInputStreamSource(fileInputStreamSource);
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
        return new File(filePath, filename);
    }

    static String makeSqlInsertString(String tableName, String[] columnNames) {
        return "INSERT INTO " + tableName + " (" + StringUtils.join(columnNames, ",") + ")"
                + "VALUES(?" + StringUtils.repeat(",?", columnNames.length - 1) + ")";
    }
   

    // TODO i18n: refactor
    void updateTextDocumentTexts(TextDocumentDomainObject textDocument, TextDocumentDomainObject oldTextDocument, UserDomainObject user) {
        TextDao textDao = (TextDao)Imcms.getServices().getSpringBean("textDao");
        Integer metaId = textDocument.getId();

        for (I18nLanguage language: I18nSupport.getLanguages()) {
    	     
            Map<Integer, TextDomainObject> texts = textDocument.getTextsMap(language);

            for (Map.Entry<Integer, TextDomainObject> entry: texts.entrySet()) {
                Integer index = entry.getKey();
                TextDomainObject text = entry.getValue();

                if (text.isModified()) {
                    sqlInsertTextHistory(language, textDocument, index, text, user);
                   
                    textDao.saveText(metaId, text);
                }
            }
        }
    }
    
    
    private String getLastHistoryTextValue(I18nLanguage language, int metaId, int name, int type) {
    	String sql = "SELECT text FROM texts_history WHERE counter = (" +
    			"SELECT MAX(counter) FROM texts_history WHERE meta_id = ? AND name = ? AND type = ? and language_id = ?)";
    	
    	Object[] parameters = new Object[] {metaId, name, type, language.getId()};
    	
    	return (String)database.execute(new SqlQueryCommand(sql, parameters, singleStringHandler));
    }

    void updateTextDocumentImages(TextDocumentDomainObject textDocument, TextDocumentDomainObject oldTextDocument, UserDomainObject user) {
        ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
        
        imageDao.saveImagesMap(textDocument.getId(), textDocument.getAllImages());
    }

    private void sqlInsertImageHistory(TextDocumentDomainObject textDocument, Integer imageIndex, UserDomainObject user) {
        SimpleDateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING);
        String[] columnNames = new String[] {"imgurl", "width", "height", "border", "v_space", "h_space", "image_name", "target", "align", "alt_text", "low_scr", "linkurl", "type", "meta_id", "name", "modified_datetime", "user_id" };
        ImageDomainObject image = textDocument.getImage(imageIndex.intValue());
        final String[] parameters = getSqlImageParameters(image, textDocument.getId(), imageIndex.intValue());
        List <String> param =  new ArrayList <String>( Arrays.asList(parameters) ) ;
        param.add(dateFormat.format(new Date()));
        param.add(""+user.getId());
        database.execute(new SqlUpdateCommand(makeSqlInsertString("images_history", columnNames), param.toArray(new String[param.size()])));
    }

    
    // TODO: transactional - new or can participate
    void updateTextDocumentIncludes(TextDocumentDomainObject textDocument) {
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
    	Set<Include> includes = new HashSet<Include>();
    	Integer metaId = textDocument.getId();
    	
    	for (Map.Entry<Integer, Integer> entry: textDocument.getIncludesMap().entrySet()) {
    		Include include = new Include();
    		include.setMetaId(metaId);
    		include.setIndex(entry.getKey());
    		include.setIncludedMetaId(entry.getValue());
    		
    		includes.add(include);
    	}
    	
    	dao.saveIncludes(metaId, includes);
    }
    
    // TODO: transactional - new or can participate
    void updateTextDocumentTemplateNames(TextDocumentDomainObject textDocument, TextDocumentDomainObject oldTextDocument, UserDomainObject user) {
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
    	TemplateNames templateNames = textDocument.getTemplateNames();
    	Integer metaId = textDocument.getId();
    	    	
    	templateNames.setMetaId(metaId);
    	dao.saveTemplateNames(metaId, templateNames);    	
    }    

    
    private void sqlInsertTextHistory(I18nLanguage language, TextDocumentDomainObject textDocument, Integer textIndex, TextDomainObject text, UserDomainObject user) {
        SimpleDateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING);
        final Object[] parameters = new String[]{
            "" + textDocument.getId(), "" + textIndex, text.getText(), "" + text.getType(), dateFormat.format(new Date()), 
            ""+user.getId(), "" + language.getId()
        };
        database.execute(new SqlUpdateCommand("INSERT INTO texts_history (meta_id, name, text, type, modified_datetime, user_id, language_id) VALUES(?,?,?,?,?,?,?)", parameters));
    }

    private static String[] getSqlImageParameters(ImageDomainObject image, int meta_id, int img_no) {
        ImageSource imageSource = image.getSource();
        return new String[] {
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
    }

    public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
    	/*
        Map fileDocumentFiles = fileDocument.getFiles();

        String sqlDelete = "DELETE FROM fileupload_docs WHERE meta_id = ?";
        final Object[] parameters1 = new String[]{"" + fileDocument.getId()};
        database.execute(new SqlUpdateCommand(sqlDelete, parameters1));

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
            database.execute(new SqlUpdateCommand(sqlInsert, parameters));
            saveFileDocumentFile( fileDocument.getId(), fileDocumentFile, fileId );
        }
        DocumentMapper.deleteOtherFileDocumentFiles( fileDocument ) ;
        */
    	
    	MetaDao dao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    	
        Map fileDocumentFiles = fileDocument.getFiles();

        // DELETE
        dao.deleteFileReferences(fileDocument.getId());

        
        // Save point...
        for ( Iterator iterator = fileDocumentFiles.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String fileId = (String)entry.getKey();
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = (FileDocumentDomainObject.FileDocumentFile)entry.getValue();

            String filename = fileDocumentFile.getFilename();
            if ( filename.length() > DB_FIELD_MAX_LENGTH__FILENAME ) {
                filename = truncateFilename( filename, DB_FIELD_MAX_LENGTH__FILENAME );
            }

            boolean isDefaultFile = fileId.equals( fileDocument.getDefaultFileId());
            FileReference fileRef = new FileReference();
            fileRef.setMetaId(fileDocument.getId());
            fileRef.setFileId(fileId);
            fileRef.setFilename(filename);
            fileRef.setDefaultFileId(isDefaultFile);
            fileRef.setMimeType(fileDocumentFile.getMimeType());
            fileRef.setCreatedAsImage(fileDocumentFile.isCreatedAsImage());
            
            
            dao.saveFileReference(fileRef);
            
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

    protected void updateTextDocumentMenus(final TextDocumentDomainObject textDocument, final TextDocumentDomainObject oldTextDocument, final UserDomainObject savingUser) {
    	MenuDao dao = (MenuDao)Imcms.getServices().getSpringBean("menuDao");

    	dao.saveDocumentMenus(textDocument.getId(), textDocument.getMenus());
    	
    	/*
        database.execute( new TransactionDatabaseCommand() {
            public Object executeInTransaction( DatabaseConnection connection ) {
                MenuSaver menuSaver = new MenuSaver(new SingleConnectionDatabase(connection)) ;
                menuSaver.updateTextDocumentMenus(textDocument, services, oldTextDocument, savingUser);
                return null ;
            }
        } );
        */
    }
}
