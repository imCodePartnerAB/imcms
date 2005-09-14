package com.imcode.imcms.mapping;

import imcode.server.ImcmsServices;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.DatabaseConnectionUtils;
import imcode.server.db.commands.QueryDatabaseCommand;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.document.*;
import imcode.server.document.textdocument.*;
import imcode.util.io.FileInputStreamSource;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class DocumentInitializingVisitor extends DocumentVisitor {

    private final static Logger log = Logger.getLogger( DocumentInitializingVisitor.class );

    private final static String IMAGE_SQL_COLUMNS = "name,image_name,imgurl,width,height,border,v_space,h_space,target,align,alt_text,low_scr,linkurl,type";

    private ImcmsServices service;
    private DatabaseConnection connection ;
    private static final String SQL__SELECT_FILE_DOCUMENT_FILES = "SELECT variant_name, filename, mime, created_as_image, default_variant FROM fileupload_docs WHERE meta_id = ? ORDER BY default_variant DESC, variant_name";

    DocumentInitializingVisitor(DatabaseConnection connection, ImcmsServices services) {
        this.service = services;
        this.connection = connection;
    }

    public void visitBrowserDocument( BrowserDocumentDomainObject document ) {
        String sqlStr = "SELECT to_meta_id, browser_id FROM browser_docs WHERE meta_id = ?";
        String[][] sqlResult ;
        try {
            String[] parameters = new String[]{"" + document.getId()};
            sqlResult = DatabaseConnectionUtils.execute2dStringArrayQuery(connection, sqlStr, parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        for ( int i = 0; i < sqlResult.length; i++ ) {
            String[] sqlRow = sqlResult[i];
            int toMetaId = Integer.parseInt( sqlRow[0] );
            int browserId = Integer.parseInt( sqlRow[1] );
            BrowserDocumentDomainObject.Browser browser = service.getDefaultDocumentMapper().getBrowserById( browserId );
            document.setBrowserDocumentId( browser, toMetaId );
        }
    }

    public void visitFileDocument( final FileDocumentDomainObject document ) {
        new QueryDatabaseCommand(SQL__SELECT_FILE_DOCUMENT_FILES, new String[] { "" + document.getId() }, new ResultSetHandler() {
            public Object handle(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    String fileId = resultSet.getString(1);
                    FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();
                    file.setFilename( resultSet.getString(2) );
                    file.setMimeType( resultSet.getString(3) );
                    file.setCreatedAsImage( 0 != resultSet.getInt(4) );
                    File fileForFileDocument = DocumentStoringVisitor.getFileForFileDocumentFile( document.getId(), fileId );
                    if ( !fileForFileDocument.exists() ) {
                        File oldlyNamedFileForFileDocument = new File( fileForFileDocument.getParentFile(), fileForFileDocument.getName()
                                                                                                            + "_se" );
                        if ( oldlyNamedFileForFileDocument.exists() ) {
                            fileForFileDocument = oldlyNamedFileForFileDocument;
                        }
                    }
                    file.setInputStreamSource( new FileInputStreamSource( fileForFileDocument ) );
                    document.addFile( fileId, file );
                    boolean isDefaultFile = 0 != resultSet.getInt(5);
                    if ( isDefaultFile ) {
                        document.setDefaultFileId( fileId );
                    }
                }
                return null ;
            }
        }).executeOn(connection);
    }

    public void visitHtmlDocument( HtmlDocumentDomainObject htmlDocument ) {
        String sqlStr = "SELECT frame_set FROM frameset_docs WHERE meta_id = ?";
        String html ;
        try {
            String[] parameters = new String[]{"" + htmlDocument.getId()};
            html = DatabaseConnectionUtils.executeStringQuery(connection, sqlStr, parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        htmlDocument.setHtml( html );
    }

    public void visitUrlDocument( UrlDocumentDomainObject document ) {
        String url ;
        try {
            String[] parameters = new String[]{"" + document.getId()};
            url = DatabaseConnectionUtils.executeStringQuery(connection, "SELECT url_ref FROM url_docs WHERE meta_id = ?", parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        document.setUrl( url );
    }

    public void visitTextDocument( TextDocumentDomainObject document ) {
        String[] sqlResult ;
        try {
            final String[] parameters = new String[]{String.valueOf( document.getId() )};
            sqlResult = DatabaseConnectionUtils.executeStringArrayQuery(connection, "SELECT template_id, group_id, default_template_1, default_template_2, default_template FROM text_docs WHERE meta_id = ?", parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        if ( sqlResult.length > 0 ) {
            int template_id = Integer.parseInt( sqlResult[0] );
            int group_id = Integer.parseInt( sqlResult[1] );
            int defaultTemplateIdForRestrictedPermissionSetOne = Integer.parseInt( sqlResult[2] );
            int defaultTemplateIdForRestrictedPermissionSetTwo = Integer.parseInt( sqlResult[3] );

            TemplateMapper templateMapper = service.getTemplateMapper();
            TemplateDomainObject template = templateMapper.getTemplateById( template_id );

            TemplateDomainObject defaultTemplate = null;
            try {
                int defaultTemplateId = Integer.parseInt( sqlResult[4] );
                defaultTemplate = templateMapper.getTemplateById( defaultTemplateId );
            } catch ( NumberFormatException ignored ) { /* ignored */ }

            TemplateDomainObject defaultTemplateForRestrictedOne = templateMapper.getTemplateById( defaultTemplateIdForRestrictedPermissionSetOne ) ;
            TemplateDomainObject defaultTemplateForRestrictedTwo = templateMapper.getTemplateById( defaultTemplateIdForRestrictedPermissionSetTwo );
            document.setTemplate( template );
            document.setTemplateGroupId( group_id );
            ((TextDocumentPermissionSetDomainObject)document.getPermissionSetForRestrictedOneForNewDocuments()).setDefaultTemplate( defaultTemplateForRestrictedOne );
            ((TextDocumentPermissionSetDomainObject)document.getPermissionSetForRestrictedTwoForNewDocuments()).setDefaultTemplate( defaultTemplateForRestrictedTwo );
            document.setDefaultTemplate( defaultTemplate );
        }

        setDocumentTexts( document );
        setDocumentImages( document );
        setDocumentIncludes( document );
        setDocumentMenus( document );
    }

    private void setDocumentMenus( TextDocumentDomainObject document ) {
        String sqlSelectDocumentMenus = "SELECT menus.menu_id, menu_index, sort_order, to_meta_id, manual_sort_order, tree_sort_index FROM menus,childs WHERE menus.menu_id = childs.menu_id AND meta_id = ? ORDER BY menu_index";
        String[][] sqlRows ;
        try {
            String[] parameters = new String[]{"" + document.getId()};
            sqlRows = DatabaseConnectionUtils.execute2dStringArrayQuery(connection, sqlSelectDocumentMenus, parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        MenuDomainObject menu = null;
        int previousMenuIndex = 0;
        for ( int i = 0; i < sqlRows.length; i++ ) {
            String[] sqlRow = sqlRows[i];
            int menuId = Integer.parseInt( sqlRow[0] );
            int menuIndex = Integer.parseInt( sqlRow[1] );
            int sortOrder = Integer.parseInt( sqlRow[2] );
            int childId = Integer.parseInt( sqlRow[3] );
            int manualSortKey = Integer.parseInt( sqlRow[4] );
            String treeSortKey = sqlRow[5];
            if ( null == menu || menuIndex != previousMenuIndex ) {
                previousMenuIndex = menuIndex;
                menu = new MenuDomainObject( menuId, sortOrder );
                document.setMenu( menuIndex, menu );
            }
            // FIXME: DocumentReference here dangerous? Use DocumentGetter/DocumentId?
            final DocumentReference documentReference = service.getDefaultDocumentMapper().getDocumentReference( childId );
            menu.addMenuItem( new MenuItemDomainObject( documentReference, new Integer( manualSortKey ), treeSortKey ) );
        }
    }

    private void setDocumentIncludes( TextDocumentDomainObject document ) {
        String sqlSelectDocumentIncludes = "SELECT include_id, included_meta_id FROM includes WHERE meta_id = ?";
        String[][] documentIncludesSqlResult ;
        try {
            String[] parameters = new String[]{
                "" + document.getId()
            };
            documentIncludesSqlResult = DatabaseConnectionUtils.execute2dStringArrayQuery(connection, sqlSelectDocumentIncludes, parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        for ( int i = 0; i < documentIncludesSqlResult.length; i++ ) {
            String[] documentIncludeSqlRow = documentIncludesSqlResult[i];
            int includeIndex = Integer.parseInt( documentIncludeSqlRow[0] );
            int includedDocumentId = Integer.parseInt( documentIncludeSqlRow[1] );
            document.setInclude( includeIndex, includedDocumentId );
        }
    }

    private void setDocumentImages( TextDocumentDomainObject document ) {
        document.setImages( getDocumentImages( document ) );
    }

    private void setDocumentTexts( TextDocumentDomainObject document ) {
        String sqlSelectTexts = "SELECT name, text, type FROM texts WHERE meta_id = ?";
        String[][] sqlTextsResult ;
        try {
            String[] parameters = new String[]{"" + document.getId()};
            sqlTextsResult = DatabaseConnectionUtils.execute2dStringArrayQuery(connection, sqlSelectTexts, parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        for ( int i = 0; i < sqlTextsResult.length; i++ ) {
            String[] sqlTextsRow = sqlTextsResult[i];
            int textIndex = Integer.parseInt( sqlTextsRow[0] );
            String text = sqlTextsRow[1];
            int textType = Integer.parseInt( sqlTextsRow[2] );
            document.setText( textIndex, new TextDomainObject( text, textType ) );
        }
    }

    private Map getDocumentImages( DocumentDomainObject document ) {
        String[][] imageRows ;
        try {
            String[] parameters = new String[]{"" + document.getId()};
            imageRows = DatabaseConnectionUtils.execute2dStringArrayQuery(connection,
                                                                          "select " + IMAGE_SQL_COLUMNS + " from images\n"
                                                                          + "where meta_id = ?", parameters);
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
        Map imageMap = new HashMap();
        for ( int i = 0; i < imageRows.length; i++ ) {
            String[] imageRow = imageRows[i];
            Integer imageIndex = Integer.valueOf( imageRow[0] );
            ImageDomainObject image = createImageFromSqlResultRow( imageRow );
            imageMap.put( imageIndex, image );
        }
        return imageMap;
    }

    private ImageDomainObject createImageFromSqlResultRow( String[] sqlResult ) {
        ImageDomainObject image = new ImageDomainObject();

        int imageType = Integer.parseInt( sqlResult[13] );
        String imageSource = sqlResult[2];

        image.setName( sqlResult[1] );
        if ( StringUtils.isNotBlank( imageSource ) ) {
            if ( ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT == imageType ) {
                try {
                    int fileDocumentId = Integer.parseInt( imageSource );
                    DefaultDocumentMapper documentMapper = service.getDefaultDocumentMapper();
                    DocumentDomainObject document = documentMapper.getDocument( fileDocumentId );
                    if (null != document) {
                        image.setSource( new FileDocumentImageSource( documentMapper.getDocumentReference( document ) ) );
                    }
                } catch ( NumberFormatException nfe ) {
                    log.warn( "Non-numeric document-id \"" + imageSource + "\" for image in database." );
                } catch ( ClassCastException cce ) {
                    log.warn( "Non-file-document-id \"" + imageSource + "\" for image in database." );
                }
            } else if ( ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH == imageType ) {
                image.setSource( new ImagesPathRelativePathImageSource( imageSource ) );
            }
        }

        image.setWidth( Integer.parseInt( sqlResult[3] ) );
        image.setHeight( Integer.parseInt( sqlResult[4] ) );
        image.setBorder( Integer.parseInt( sqlResult[5] ) );
        image.setVerticalSpace( Integer.parseInt( sqlResult[6] ) );
        image.setHorizontalSpace( Integer.parseInt( sqlResult[7] ) );
        image.setTarget( sqlResult[8] );
        image.setAlign( sqlResult[9] );
        image.setAlternateText( sqlResult[10] );
        image.setLowResolutionUrl( sqlResult[11] );
        image.setLinkUrl( sqlResult[12] );
        return image;
    }

}
