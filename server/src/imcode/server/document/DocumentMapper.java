package imcode.server.document;

import imcode.server.IMCConstants;
import imcode.server.IMCService;
import imcode.server.IMCServiceInterface;
import imcode.server.Table;
import imcode.server.db.DBConnect;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.server.util.DateHelper;
import imcode.util.Parser;
import imcode.util.poll.PollHandlingSystem;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.imcode.imcms.api.TextDocument;

public class DocumentMapper {

    private Logger log = Logger.getLogger( DocumentMapper.class );
    protected IMCServiceInterface service;
    protected ImcmsAuthenticatorAndUserMapper imcmsAAUM;

    public DocumentMapper( IMCService service, ImcmsAuthenticatorAndUserMapper imcmsAAUM ) {
        this.service = service;
        this.imcmsAAUM = imcmsAAUM;
    }

    /**
     * Stored procedure names used in this class
     */
    // todo make sure all these is only used in one sprocMethod
    private static final String SPROC_SECTION_GET_INHERIT_ID = "SectionGetInheritId";
    private static final String SPROC_GET_FILE_NAME = "GetFileName";
    private static final String SPROC_GET_DOCUMENT_INFO = "GetDocumentInfo";
    private static final String SPROC_GET_USER_PERMISSION_SET = "GetUserPermissionSet";
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String sprocGetIncludes = "GetIncludes";
    private static final String SPROC_INSERT_TEXT = "InsertText";
    private static final String SPROC_UPDATE_PARENTS_DATE_MODIFIED = "UpdateParentsDateModified";
    private static final String SPROC_GET_DOC_TYPES_FOR_USER = "GetDocTypesForUser";
    private static final String SPROC_SECTION_ADD_CROSSREF = "SectionAddCrossref";
    private static final String SPROC_INHERIT_PERMISSONS = "InheritPermissions";
    private static final String SPROC_CLASSIFICATION_FIX = "Classification_Fix";

    // todo make sure all sproc and sql mehtods are private
    private static String[] sprocGetUserPermissionSet( IMCServiceInterface service, int metaId, int userId ) {
        String[] sqlParams = {String.valueOf( metaId ), String.valueOf( userId )};
        String[] sqlResult = service.sqlProcedure( SPROC_GET_USER_PERMISSION_SET, sqlParams );
        return sqlResult;
    }

    /** @return the filename for a fileupload-internalDocument, or null if the internalDocument isn't a fileupload-docuemnt. **/
    private static String sprocGetFilename( IMCServiceInterface service, int meta_id ) {
        String[] params = new String[]{String.valueOf( meta_id )};
        return service.sqlProcedureStr( SPROC_GET_FILE_NAME, params );
    }

    private static Map sprocGetDocumentInfo( IMCServiceInterface service, int metaId ) {
        String[] params = new String[]{String.valueOf( metaId )};
        String[] result = service.sqlProcedure( SPROC_GET_DOCUMENT_INFO, params );

        if ( 0 == result.length ) {
            return null;
        }

        Map map = new HashMap();

        map.put( "meta_id", result[0] );
        map.put( "description", result[1] );
        map.put( "doc_type", result[2] );
        map.put( "meta_headline", result[3] );
        map.put( "meta_text", result[4] );
        map.put( "meta_image", result[5] );
        map.put( "owner_id", result[6] );
        map.put( "permissions", result[7] );
        map.put( "shared", result[8] );
        map.put( "expand", result[9] );
        map.put( "show_meta", result[10] );
        map.put( "help_text_id", result[11] );
        map.put( "archive", result[12] );
        map.put( "status_id", result[13] );
        map.put( "lang_prefix", result[14] );
        map.put( "classification", result[15] );
        map.put( "date_created", result[16] );
        map.put( "date_modified", result[17] );
        map.put( "sort_position", result[18] );
        map.put( "menu_position", result[19] );
        map.put( "disable_search", result[20] );
        map.put( "target", result[21] );
        map.put( "frame_name", result[22] );
        map.put( "activated_datetime", result[23] );
        map.put( "archived_datetime", result[24] );

        return map;
    }

    public static class SectionData {

        public int sectionId;
        public String sectionName;
    }

    public static String[] sprocSectionGetInheritId( IMCServiceInterface service, int meta_id ) {
        String[] section_data = service.sqlProcedure( SPROC_SECTION_GET_INHERIT_ID, new String[]{String.valueOf( meta_id )} );
        return section_data;
    }

    private String[] sprocGetText( int meta_id, int no ) {
        String[] params = new String[]{"" + meta_id, "" + no};
        String[] results = service.sqlProcedure( SPROC_GET_TEXT, params, false );
        return results;
    }

    private static HashSet sprocGetDocTypesForUser( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id, String lang_prefix ) {
        String[] user_dt = imcref.sqlProcedure( SPROC_GET_DOC_TYPES_FOR_USER + " " + parent_meta_id + "," + user.getUserId() + ",'" + lang_prefix + "'" );
        HashSet user_doc_types = new HashSet();
        for ( int i = 0; i < user_dt.length; i += 2 ) {
            user_doc_types.add( user_dt[i] );
        }
        return user_doc_types;
    }

    private static void sprocUpdateInsertText( IMCServiceInterface service, int meta_id, int txt_no, TextDocumentTextDomainObject text, String textstring ) {
        String[] params = new String[]{"" + meta_id, "" + txt_no, "" + text.getType(), textstring};
        service.sqlUpdateProcedure( SPROC_INSERT_TEXT, params );
    }

    public static void sprocUpdateInheritPermissions( IMCServiceInterface imcref, int meta_id, int parent_meta_id, int doc_type ) {
        imcref.sqlUpdateProcedure( SPROC_INHERIT_PERMISSONS + " " + meta_id + "," + parent_meta_id + "," + doc_type );
    }

    public static void sprocClassification_Fix( IMCServiceInterface imcref, int meta_id, String classification ) {
        sprocSaveClassification( imcref, meta_id, classification );
    }

    public static void sprocSaveClassification( IMCServiceInterface imcref, int meta_id, String classification ) {
        imcref.sqlUpdateProcedure( SPROC_CLASSIFICATION_FIX + " " + meta_id + ",'" + classification + "'" );
    }

    public static void sprocSectionAddCrossref( IMCServiceInterface imcref, int meta_id, int section_id ) {
        imcref.sqlUpdateProcedure( SPROC_SECTION_ADD_CROSSREF + " " + meta_id + ", " + section_id );
    }

    public static String[] sprocGetIncludes( IMCServiceInterface imcref, int meta_id ) {
        String[] included_docs = imcref.sqlProcedure( sprocGetIncludes, new String[]{String.valueOf( meta_id )} );
        return included_docs;
    }

    public static Vector sprocGetDocTypeForUser( DBConnect dbc, UserDomainObject user, int meta_id, String lang_prefix ) {
        String sqlStr = SPROC_GET_DOC_TYPES_FOR_USER;
        String[] sqlAry2 = {String.valueOf( meta_id ), String.valueOf( user.getUserId() ), lang_prefix};
        dbc.setProcedure( sqlStr, sqlAry2 );
        Vector doc_types_vec = dbc.executeProcedure();
        return doc_types_vec;
    }

    public static void sprocUpdateParentsDateModified( IMCServiceInterface imcref, int meta_id ) {
        imcref.sqlUpdateProcedure( SPROC_UPDATE_PARENTS_DATE_MODIFIED + " " + meta_id );
    }


    public void sqlUpdateModifiedDatesOnDocumentAndItsParent( int meta_id, Date dateTime ) {
        String modifiedDateTimeStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateTime );
        String sqlStr = "update meta set date_modified ='" + modifiedDateTimeStr + "' where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
        // Update the date_modified for all parents.
        sprocUpdateParentsDateModified( service, meta_id );
    }

    public static void sqlUpdateMetaDateCreated( IMCServiceInterface imcref, String meta_id, String created_datetime ) {
        String sqlStr;
        sqlStr = "update meta set date_created ='" + created_datetime + "' where meta_id = " + meta_id;
        imcref.sqlUpdateQuery( sqlStr );
    }

    private void sqlUpdateDocType( IMCServiceInterface service, int metaId, int docType ) {
        String sql = "update meta set doc_type = " + docType + " where meta_id = " + metaId;
        service.sqlUpdateQuery( sql );
    }

    private static void sqlInsertIntoTemplateInfoIntoTextDocs( IMCServiceInterface imcref, String meta_id, String[] temp ) {
        String sqlStr;
        sqlStr = "insert into text_docs (meta_id,template_id,sort_order,group_id,default_template_1,default_template_2) values (" + meta_id + "," + temp[0] + "," + temp[1] + "," + temp[2] + "," + temp[3] + "," + temp[4] + ")";
        imcref.sqlUpdateQuery( sqlStr );
    }

    private static String[] sqlSelectTemplateInfoFromTextDocs( IMCServiceInterface imcref, String parent_meta_id ) {
        String sqlStr = "select template_id, sort_order,group_id,default_template_1,default_template_2 from text_docs where meta_id = " + parent_meta_id;
        String temp[] = imcref.sqlQuery( sqlStr );
        return temp;
    }

    public static void sqlSelectAddSortorderToParentsChildList( IMCServiceInterface imcref, String parent_meta_id, String meta_id, String doc_menu_no ) {
        String sqlStr = "declare @new_sort int\n" + "select @new_sort = max(manual_sort_order)+10 from childs where meta_id = " + parent_meta_id + " and menu_sort = " + doc_menu_no + "\n" + "if @new_sort is null begin set @new_sort = 500 end\n" + "insert into childs (meta_id, to_meta_id, menu_sort, manual_sort_order) values (" + parent_meta_id + "," + meta_id + "," + doc_menu_no + ",@new_sort)\n";
        imcref.sqlUpdateQuery( sqlStr );
    }

    private static String[] sqlSelectGetClassificationStrings( IMCServiceInterface imcref, int meta_id ) {
        String sqlStr;
        sqlStr = "select code from classification c join meta_classification mc on mc.class_id = c.class_id where mc.meta_id = " + meta_id;
        String[] classifications = imcref.sqlQuery( sqlStr );
        return classifications;
    }

    public static void sqlUpdateModifiedDate( IMCServiceInterface service, int meta_id, Date date ) {
        String dateModifiedStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( date );
        String sqlStr = "update meta set date_modified = '" + dateModifiedStr + "' where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
    }

    private void sqlUpdateCreatedDate( IMCServiceInterface service, int metaId, Date dateTime ) {
        String dateTimeStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateTime );
        String sql = "update meta set date_created = '" + dateTimeStr + "' where meta_id = " + metaId;
        service.sqlUpdateQuery( sql );
    }

    public static void sqlUpdateActivateTheTextField( IMCServiceInterface imcref, int meta_id ) {
        String sqlStr = "update meta set activate = 1 where meta_id = " + meta_id;
        imcref.sqlUpdateQuery( sqlStr );
    }

    public static void sqlInsertIntoTexts( IMCServiceInterface imcref, String meta_id, String mHeadline, String mText ) {
        String sqlStr = "insert into texts (meta_id,name,text,type) values (" + meta_id + ", 1, '" + mHeadline + "', 1)";
        imcref.sqlUpdateQuery( sqlStr );
        sqlStr = "insert into texts (meta_id,name,text,type) values (" + meta_id + ", 2, '" + mText + "', 1)";
        imcref.sqlUpdateQuery( sqlStr );
    }

    /**
     Set the modified datetime of a internalDocument to now
     @param meta_id The id of the internalDocument
     **/
    private static void sqlUpdateTouchDocument( IMCServiceInterface service, int meta_id ) {
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat( DateHelper.DATE_TIME_SECONDS_FORMAT_STRING );
        service.sqlUpdateQuery( "update meta set date_modified = '" + dateformat.format( date ) + "' where meta_id = " + meta_id );
    }

    public Map getIncludedDocuments( DocumentDomainObject textDocument ) {
        Map result = new HashMap();
        String[] includedMetaIds = sprocGetIncludes( service, textDocument.getMetaId() );
        for ( int i = 0; i < includedMetaIds.length; i += 2 ) {
            int include_id = Integer.parseInt( includedMetaIds[i] );
            int included_meta_id = Integer.parseInt( includedMetaIds[i + 1] );
            result.put( new Integer( include_id ), new Integer( included_meta_id ) );
        }
        return result;
    }

    public DocumentDomainObject getDocument( int metaId ) {
        DocumentDomainObject document = null;
        Map documentData = sprocGetDocumentInfo( service, metaId );
        if ( documentData == null ) {
            throw new IndexOutOfBoundsException( "No such internalDocument: " + metaId );
        }
        DateFormat dateform = new SimpleDateFormat( DateHelper.DATE_TIME_SECONDS_FORMAT_STRING );
        document = new DocumentDomainObject();
        document.setMetaId( Integer.parseInt( (String)documentData.get( "meta_id" ) ) );
        document.setDocumentType( Integer.parseInt( (String)documentData.get( "doc_type" ) ) );
        document.setCreator( imcmsAAUM.getUser( Integer.parseInt( (String)documentData.get( "owner_id" ) ) ) );
        document.setHeadline( (String)documentData.get( "meta_headline" ) );
        document.setText( (String)documentData.get( "meta_text" ) );
        document.setImage( (String)documentData.get( "meta_image" ) );
        document.setTarget( (String)documentData.get( "target" ) );
        document.setArchived( "0".equals( documentData.get( "archive" ) ) ? false : true );
        String[] section_data = sprocSectionGetInheritId( service, metaId );
        String sectionName = null;
        if ( section_data.length == 2 ) {
            sectionName = section_data[1];
        }
        document.setSection( sectionName );
        try {
            document.setCreatedDatetime( dateform.parse( (String)documentData.get( "date_created" ) ) );
        } catch ( NullPointerException npe ) {
            document.setCreatedDatetime( null );
        } catch ( ParseException pe ) {
            document.setCreatedDatetime( null );
        }
        try {
            document.setModifiedDatetime( dateform.parse( (String)documentData.get( "date_modified" ) ) );
        } catch ( NullPointerException npe ) {
            document.setModifiedDatetime( null );
        } catch ( ParseException pe ) {
            document.setModifiedDatetime( null );
        }
        try {
            document.setActivatedDatetime( dateform.parse( (String)documentData.get( "activated_datetime" ) ) );
        } catch ( NullPointerException npe ) {
            document.setActivatedDatetime( null );
        } catch ( ParseException pe ) {
            document.setActivatedDatetime( null );
        }
        try {
            document.setArchivedDatetime( dateform.parse( (String)documentData.get( "archived_datetime" ) ) );
        } catch ( NullPointerException npe ) {
            document.setArchivedDatetime( null );
        } catch ( ParseException pe ) {
            document.setArchivedDatetime( null );
        }
        if ( document.getDocumentType() == DocumentDomainObject.DOCTYPE_FILE ) {
            document.setFilename( sprocGetFilename( service, metaId ) );
        }
        if ( document.getDocumentType() == DocumentDomainObject.DOCTYPE_TEXT ) {
            initTextDoc( service, document );
        }
        return document;
    }

    public boolean hasEditPermission( int documentId, UserDomainObject user ) {

        int[] wantedPermissionSetIds = {IMCConstants.DOC_PERM_SET_FULL,
                                        IMCConstants.DOC_PERM_SET_RESTRICTED_1,
                                        IMCConstants.DOC_PERM_SET_RESTRICTED_2};

        return userIsSuperAdminOrHasPermissionSetId( documentId, user, wantedPermissionSetIds );
    }

    public boolean hasDocumentPermission( int documentId, UserDomainObject user ) {

        int[] wantedPermissionSetIds = {IMCConstants.DOC_PERM_SET_FULL,
                                        IMCConstants.DOC_PERM_SET_RESTRICTED_1,
                                        IMCConstants.DOC_PERM_SET_RESTRICTED_2,
                                        IMCConstants.DOC_PERM_SET_READ};

        return userIsSuperAdminOrHasPermissionSetId( documentId, user, wantedPermissionSetIds );

    }

    private boolean userIsSuperAdminOrHasPermissionSetId( int documentId, UserDomainObject user, int[] wantedPermissionSetIds ) {
        boolean result = false;

        boolean userHasSuperAdminRole = imcmsAAUM.hasSuperAdminRole( user );

        if ( userHasSuperAdminRole ) {
            result = true;
        } else {

            String[] perms = sprocGetUserPermissionSet( service, documentId, user.getUserId() );

            if ( perms.length > 0 ) {
                int userPermissionSetId = Integer.parseInt( perms[0] );

                result = arrayContains( wantedPermissionSetIds, userPermissionSetId );
            }
        }
        return result;
    }

    private boolean arrayContains( int[] array, int wantedValue ) {
        boolean result = false;
        for ( int i = 0; i < array.length; ++i ) {
            if ( wantedValue == array[i] ) {
                result = true;
                break;
            }
        }
        return result;
    }

    public TextDocumentTextDomainObject getTextField( DocumentDomainObject document, int textFieldIndexInDocument ) {
        return service.getText( document.getMetaId(), textFieldIndexInDocument );
    }

    public TextDocumentTextDomainObject getText( int meta_id, int no ) {
        try {
            String[] results = sprocGetText( meta_id, no );

            if ( results == null || results.length == 0 ) {
                /* There was no text. Return null. */
                return null;
            }

            /* Return the text */
            String text = results[0];
            int type = Integer.parseInt( results[1] );

            return new TextDocumentTextDomainObject( text, type );

        } catch ( NumberFormatException ex ) {
            /* There was no text, but we shouldn't come here unless the db returned something wrong. */
            log.error( "SProc 'sprocGetText()' returned an invalid text-type.", ex );
            return null;
        }
    }

    public void saveText( TextDocumentTextDomainObject text, int meta_id, int txt_no, UserDomainObject user, String text_type ) {
        String textstring = text.getText();

        // update text
        sprocUpdateInsertText( service, meta_id, txt_no, text, textstring );

        // update the date
        sqlUpdateTouchDocument( service, meta_id );

        service.updateLogs( "Text " + txt_no + " in  " + "[" + meta_id + "] modified by user: [" + user.getFullName() + "]" );

        if ( !( "" ).equals( text_type ) ) {

            if ( text_type.startsWith( "poll" ) ) {
                PollHandlingSystem poll = service.getPollHandlingSystem();
                poll.savePollparameter( text_type, meta_id, txt_no, textstring );
            }
        }
    }

    public static void copyTemplateData( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id, String meta_id ) {
        //ok now lets see what to do with the templates
        String[] temp = sqlSelectTemplateInfoFromTextDocs( imcref, parent_meta_id );

        //lets get the users greatest permission_set for this dokument
        final int perm_set = imcref.getUserHighestPermissionSet( Integer.parseInt( meta_id ), user.getUserId() );
        //ok now we have to setup the template too use

        if ( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_1 ) {
            //ok restricted_1 permission lets see if we have a default template fore this one
            //and if so lets put it as the orinary template instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[3] );
                if ( tempInt >= 0 )
                    temp[0] = String.valueOf( tempInt );
            } catch ( NumberFormatException nfe ) {

                //there wasn't a number but we dont care, we just catch the exeption and moves on.
            }
        } else if ( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_2 ) { //ok we have a restricted_2 permission lets see if we have default template fore this one
            //and if soo lets put it as ordinary instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[4] );
                if ( tempInt >= 0 )
                    temp[0] = String.valueOf( tempInt );
            } catch ( NumberFormatException nfe ) {
                //there wasn't a number but we dont care, we just catch the exeption and moves on.
            }
        }
        //ok were set, lets update db
        sqlInsertIntoTemplateInfoIntoTextDocs( imcref, meta_id, temp );
    }

    /**
     * @deprecated Use {@link #createNewTextDocument} instead.
     */
    public static synchronized String sqlInsertIntoMeta( IMCServiceInterface imcref, String doc_type, String activated_datetime, String archived_datetime, Properties metaprops ) {
        // Lets build the sql statement to add a new meta id
        String sqlStr = "insert into meta (doc_type,activate,classification,activated_datetime,archived_datetime";
        String sqlStr2 = ")\nvalues (" + doc_type + ",0,''," + ( null == activated_datetime ? "NULL" : "'" + activated_datetime + "'" ) + "," + ( null == archived_datetime ? "NULL" : "'" + archived_datetime + "'" );
        Enumeration propkeys = metaprops.propertyNames();
        while ( propkeys.hasMoreElements() ) {
            String temp = (String)propkeys.nextElement();
            String val = metaprops.getProperty( temp );
            String[] vp = {"'", "''"};
            sqlStr += "," + temp;
            sqlStr2 += ",'" + Parser.parseDoc( val, vp ) + "'";
        }
        sqlStr += sqlStr2 + ")";
        imcref.sqlUpdateQuery( sqlStr );
        String meta_id = imcref.sqlQueryStr( "select @@IDENTITY" );
        return meta_id;
    }

    /**
     * Inspired by the SaveNewMeta servlet... I went throu the code and tried to extract the nessesary parts. Hasse
     * todo: make the SaveNewMeta to use this method instead.
     * @param parentId
     * @param parentMenuNumber
     * @return
     */
    public synchronized DocumentDomainObject createNewTextDocument( UserDomainObject user, int parentId, int parentMenuNumber ) {
        // general for all types of documents
        int docType = DocumentDomainObject.DOCTYPE_TEXT;
        Date nowDateTime = new Date();

        int newMetaId = sqlCreateNewRowInMetaCopyParentData( service, parentId );
        // fix the data that is unique for this document
        sqlUpdateCreatedDate( service, newMetaId, nowDateTime );
        sqlUpdateModifiedDate( service, newMetaId, nowDateTime );
        sqlUpdateDocType( service, newMetaId, docType );

        // inherit all the different data that's not in meta from parent.
        sprocUpdateInheritPermissions( service, newMetaId, parentId, docType );
        inheritClassifications( parentId, newMetaId );
        inheritSection( parentId, newMetaId );

        // update parents, why? what is this? /Hasse
        sqlSelectAddSortorderToParentsChildList( service, String.valueOf( parentId ), String.valueOf( newMetaId ), String.valueOf( parentMenuNumber ) );
        // update parents modfied date because it has gotten an new link
        sqlUpdateModifiedDate( service, parentId, nowDateTime );

        // Text document
        DocumentMapper.copyTemplateData( service, user, String.valueOf( parentId ), String.valueOf( newMetaId ) );
        DocumentMapper.sqlUpdateActivateTheTextField( service, newMetaId );

        return getDocument( newMetaId );
    }

    public void saveTextDocument( DocumentDomainObject document ) {
        Date now = new Date();
        document.setModifiedDatetime( now );

        // Bokstavsorning på attributen, så man lättare kan hitta om något läggs till eller tagits bort
        // använder attributen direkt, för att inte förvirras av de get-metoder som saknar motsvarande
        // underliggande data.
        // Denna långa uppraddning gör jag för att IntelliJ kan visa vilka variabler jag missar att använda.
        // Denna kommentar blev på svenska för jag kom inte på vad bokstavsordning är på engelska.../Hasse
        Date activatedDatetime = document.activatedDatetime;
        Date archivedDatetime = document.archivedDatetime;
        Date createdDatetime = document.createdDatetime;
        // String filename = document.filename; // only in file docks, not implemented yet.
        String headline = document.headline;
        String image = document.image;
        int menuSortOrder = document.menuSortOrder;
        Date modifiedDatetime = document.modifiedDatetime;
        String section = document.section;
        String target = document.target;
        TemplateDomainObject template = document.template;
        int templateGroupId = document.templateGroupId;
        String text = document.text;
        boolean archived = document.archived;

        sqlUpdateMeta( service, document.getMetaId(), activatedDatetime, archivedDatetime, createdDatetime, headline, image, modifiedDatetime, target, text, archived );
        updateSection( service, document, section );

        // Restricted One and Two



        // TEXT_DOC
        updateTextDoc( service, document.getMetaId(), template, menuSortOrder, templateGroupId );


        // todo: Mark parent as modified
        /*
                int parentId = ???;
                sqlUpdateModifiedDate( service, parentId , now );
        */
    }

    // todo make Section into an DomainObject
    private static void updateSection( IMCServiceInterface service, DocumentDomainObject document, String section ) {
        if (null != section) {
            String sqlQuery = "select section_id from sections where section_name = ?";
            String[] queryResult = service.sqlQuery( sqlQuery, new String[] { section } );
            if (queryResult.length > 0) {
                int sectionId = Integer.parseInt( queryResult[0] );
                sprocSectionAddCrossref( service, document.getMetaId(), sectionId );
            } else {
                throw new IllegalArgumentException("No such section '"+section+"'") ;
            }
        } else {
            removeSectionCrossref( service, document );
        }
    }

    private static void removeSectionCrossref( IMCServiceInterface service, DocumentDomainObject document ) {
        sprocSectionAddCrossref(service,document.getMetaId(), -1);
    }

    private static void updateTextDoc( IMCServiceInterface service, int meta_id, TemplateDomainObject template, int menuSortOrder, int templateGroupId ) {
        int templateId = template.getId();
        String sqlStr = "update text_docs set "+
            "template_id = ?, " +
            "sort_order = ?, " +
            "group_id = ? "+
            "where meta_id = ?";
        service.sqlUpdateQuery( sqlStr, new String[] { ""+templateId, ""+menuSortOrder, ""+templateGroupId, ""+meta_id } );
    }

    private static void sqlUpdateMeta( IMCServiceInterface service, int meta_id, Date activatedDatetime, Date archivedDateTime, Date createdDatetime, String headline, String image, Date modifiedDateTime, String target, String text, boolean isArchived ) {

        StringBuffer sqlStr = new StringBuffer( "update meta set " );

        sqlStr.append( makeDateSQL( "activated_datetime", activatedDatetime ) );
        sqlStr.append( makeDateSQL( "archived_datetime", archivedDateTime ) );
        sqlStr.append( makeDateSQL( "date_created", createdDatetime ) );
        sqlStr.append( makeStringSQL( "meta_headline", headline ) );
        sqlStr.append( makeStringSQL( "meta_image", image ) );
        sqlStr.append( makeDateSQL( "date_modified", modifiedDateTime ) );
        sqlStr.append( makeStringSQL( "target", target ) );
        sqlStr.append( makeStringSQL( "meta_text", text ) );
        String str = makeBooleanSQL( "archive", isArchived );

        sqlStr.append( str );
        // todo: Remove from the meta table all collumns that are not used.
        // Candidates: All not used above.

        sqlStr.append( " where meta_id = " + meta_id );
        service.sqlUpdateQuery( sqlStr.toString() );
    }

    private static String makeBooleanSQL( String columnName, boolean field_isArchived ) {
        String str = columnName + " = " + ( field_isArchived ? 1 : 0 );
        return str;
    }


    private static String makeDateSQL( String columnName, Date date ) {
        if ( null != date ) {
            String dateStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( date );
            return makeStringSQL( columnName, dateStr );
        } else {
            return makeStringSQL( columnName, null );
        }
    }

    private static String makeStringSQL( String columnName, Object value ) {
        String s = ( value != null ? "'" + value + "'" : "NULL" );
        String str = columnName + " = " + s + ", ";
        return str;
    }

    private void inheritSection( int from_parentId, int to_metaId ) {
        String[] sectionIdStrAry = sprocSectionGetInheritId( service, from_parentId );
        int sectionId = sectionIdStrAry.length > 0 ? Integer.parseInt( sectionIdStrAry[0] ) : -1 ;
        sprocSectionAddCrossref( service, to_metaId, sectionId );
    }

    private void inheritClassifications( int from_parentId, int to_newMetaId ) {
        String classifications = getClassificationsAsOneString( service, from_parentId );
        sprocSaveClassification( service, to_newMetaId, classifications );
    }

    private int sqlCreateNewRowInMetaCopyParentData( IMCServiceInterface service, int parentId ) {
        // todo: All this chould propably be done in a singel sql query but i dont have time to think how that should be done right now. Hasse
        final String columnsToBeCopied = "description,doc_type,meta_headline,meta_text,meta_image,owner_id,permissions,shared,expand,show_meta,help_text_id,archive,status_id,lang_prefix,classification,date_created,date_modified,sort_position,menu_position,disable_search,target,frame_name,activate,activated_datetime,archived_datetime";
        String sqlStatmentGetAllParentData = "select " + columnsToBeCopied + " from meta where meta_id = " + parentId;

        String[] parentDataRow = service.sqlQuery( sqlStatmentGetAllParentData );
        String values = "'" + parentDataRow[0] + "'," + parentDataRow[1] + "," + "'" + parentDataRow[2] + "'," + "'" + parentDataRow[3] + "'," + "'" + parentDataRow[4] + "'," + parentDataRow[5] + "," + parentDataRow[6] + "," + parentDataRow[7] + "," + parentDataRow[8] + "," + parentDataRow[9] + "," + parentDataRow[10] + "," + parentDataRow[11] + "," + parentDataRow[12] + "," + "'" + parentDataRow[13] + "'," + "'" + parentDataRow[14] + "'," + "'" + parentDataRow[15] + "'," + "'" + parentDataRow[16] + "'," + parentDataRow[17] + "," + parentDataRow[18] + "," + parentDataRow[19] + "," + "'" + parentDataRow[20] + "'," + "'" + parentDataRow[21] + "'," + parentDataRow[22];

        // The above query returns "" of some reason instead of NULL, and I don't have time to think of why, Hasse
        // Todo: think throu how to deal with sql that is not stored procedures.
        if ( parentDataRow[23].equals( "" ) ) {
            values += ",NULL";
        } else {
            values += ",'" + parentDataRow[23] + "'";
        }

        // Same reson here.
        if ( parentDataRow[24].equals( "" ) ) {
            values += ",NULL";
        } else {
            values += ",'" + parentDataRow[24] + "'";
        }

        String sqlStatmentInsertAllButMetaId = "insert into meta (" + columnsToBeCopied + ") " + " values (" + values.toString() + ")";
        service.sqlUpdateQuery( sqlStatmentInsertAllButMetaId );
        String meta_id = service.sqlQueryStr( "select @@IDENTITY" );
        return Integer.parseInt( meta_id );
    }

    public static boolean checkUsersRights( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id, String lang_prefix, String doc_type ) {
        HashSet user_doc_types = sprocGetDocTypesForUser( imcref, user, parent_meta_id, lang_prefix );
        boolean userHasRights = user_doc_types.contains( doc_type );
        return userHasRights;
    }

    public static String getClassificationsAsOneString( IMCServiceInterface imcref, int meta_id ) {
        String[] classifications = sqlSelectGetClassificationStrings( imcref, meta_id );
        String classification = "";
        if ( classifications.length > 0 ) {
            classification += classifications[0];
            for ( int i = 1; i < classifications.length; ++i ) {
                classification += "; " + classifications[i];
            }
        }
        return classification;
    }

    /**
     * Save template -> text_docs, sort
     */
    public static void saveTextDoc( IMCServiceInterface service, int meta_id, Table doc ) {
        String sqlStr = "";
        sqlStr = "update text_docs\n";
        sqlStr += "set template_id= " + doc.getString( "template" );
        sqlStr += ", group_id= " + doc.getString( "group_id" );
        sqlStr += " where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
    }

    private static void initTextDoc( IMCServiceInterface service, DocumentDomainObject inout_document ) {
        // all from the table text_doc
        String[] textdoc_data1 = service.sqlProcedure( "GetTextDocData", new String[]{String.valueOf( inout_document.getMetaId() )} );
        String[] textdoc_data = textdoc_data1;
        if ( textdoc_data.length >= 4 ) {
            int template_id = Integer.parseInt( textdoc_data[0] );
            //String simple_name = textdoc_data[1];
            int sort_order = Integer.parseInt( textdoc_data[2] );
            int group_id = Integer.parseInt( textdoc_data[3] );
            TemplateDomainObject template = TemplateMapper.getTemplate( service, template_id );
            inout_document.setTemplate( template );
            inout_document.setMenuSortOrder( sort_order );
            inout_document.setTemplateGroupId( group_id );
        }
    }

    public void setInclude( int includingMetaId, int includeIndex, int includedMetaId ) {
        sprocSetInclude( service, includingMetaId, includeIndex, includedMetaId );
    }

    public static void sprocSetInclude( IMCServiceInterface imcref, int including_meta_id, int include_id, int included_meta_id ) {
        imcref.sqlUpdateProcedure( "SetInclude " + including_meta_id + "," + include_id + "," + included_meta_id );
    }

    public void removeInclusion( int includingMetaId, int includeIndex ) {
        sprocDeleteInclude( service, includingMetaId, includeIndex );
    }

    public static void sprocDeleteInclude( IMCServiceInterface imcref, int including_meta_id, int include_id ) {
        imcref.sqlUpdateProcedure( "DeleteInclude " + including_meta_id + "," + include_id );
    }

    public String[] getMenuLinks( int documentId, int menuIndex) {
        int sortOrder = getSortOrderOfDocument( documentId ) ;
        String orderBy = getSortOrderAsSqlOrderBy( sortOrder );
        String sqlStr = "select to_meta_id from childs,meta where childs.meta_id = meta.meta_id and childs.meta_id = ? and menu_sort = ? order by "+orderBy;
        return service.sqlQuery(sqlStr,
                               new String[] { ""+documentId, ""+menuIndex} ) ;
    }

    private String getSortOrderAsSqlOrderBy( int sortOrder ) {
        String orderBy = "meta_headline" ;
        switch (sortOrder) {
            case TextDocument.Menu.SORT_BY_MANUAL_ORDER_DESCENDING:
                orderBy = "manual_sort_order desc" ;
                break;

            case TextDocument.Menu.SORT_BY_MODIFIED_DATETIME_DESCENDING:
                orderBy = "date_modified desc" ;
                break;

            case TextDocument.Menu.SORT_BY_HEADLINE:
                orderBy = "meta_headline" ;
                break;
        }
        return orderBy;
    }

    private int getSortOrderOfDocument( int documentId ) {
        return Integer.parseInt(sqlSelectTemplateInfoFromTextDocs(service,String.valueOf( documentId ))[1]);
    }

    public boolean hasSharePermission( UserDomainObject user, int documentId ) {
        return service.checkUserDocSharePermission(user,documentId) ;
    }

    public void addDocumentToMenu( UserDomainObject user, int menuDocumentId, int menuIndex, int toBeAddedId ) {
        addDocumentToMenu(service, user, menuDocumentId, menuIndex, toBeAddedId );
    }

    public void removeDocumentFromMenu(UserDomainObject user, int menuDocumentId, int menuIndex, int toBeRemovedId ) {
        removeDocumentFromMenu(service, user, menuDocumentId,menuIndex,toBeRemovedId);
    }

    private static void removeDocumentFromMenu( IMCServiceInterface service, UserDomainObject user, int menuDocumentId, int menuIndex, int toBeRemovedId ) {
        String sqlStr = "delete from childs\n"+
        "where to_meta_id = ?\n"+
        "and meta_id = ?\n"+
        "and menu_sort = ?" ;

        int updatedRows = service.sqlUpdateQuery(sqlStr, new String[] { ""+toBeRemovedId, ""+menuDocumentId, ""+menuIndex });

        if ( 1 == updatedRows ) {	// if existing doc is added to the menu
            service.updateLogs( "Link from [" + menuDocumentId + "] in menu ["+menuIndex+"] to ["+toBeRemovedId+"] removed by user: [" + user.getFullName() + "]" );
        }
    }

    /**
     * Delete childs from a menu.
     */
    public static void deleteChilds( IMCServiceInterface service, int meta_id, int menu, UserDomainObject user, String childsThisMenu[] ) {
        StringBuffer childStr = new StringBuffer("[");
        // create a db connection an get meta data

        for ( int i = 0; i < childsThisMenu.length; i++ ) {
            int childId = Integer.parseInt(childsThisMenu[i]) ;

            removeDocumentFromMenu( service, user, meta_id, menu, childId );

            childStr.append(childsThisMenu[i]);
            if ( i < childsThisMenu.length - 1 )
                childStr.append(", ");
        }
        childStr.append("]");

    }

    public static void addDocumentToMenu( IMCServiceInterface service, UserDomainObject user, int menuDocumentId, int menuIndex, int toBeAddedId ) {
        int updatedRows = service.sqlUpdateProcedure("AddExistingDocToMenu", new String[] {""+menuDocumentId,""+toBeAddedId,""+menuIndex} ) ;

        if ( 1 == updatedRows ) {	// if existing doc is added to the menu
            service.updateLogs( "Link from [" + menuDocumentId + "] in menu ["+menuIndex+"] to ["+toBeAddedId+"] added by user: [" + user.getFullName() + "]" );
        }
    }


}

