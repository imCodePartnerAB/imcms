package imcode.server.document;

import imcode.server.*;
import imcode.server.util.DateHelper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.Parser;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentMapper {
    /**
     * Stored procedure names used in this class
     */
    private static final String SPROC_GET_TEST_DOC_DATA = "GetTextDocData";
    private static final String SPROC_SECTION_GET_INHERIT_ID = "SectionGetInheritId";
    private static final String SPROC_GET_FILE_NAME = "GetFileName";
    private static final String SPROC_GET_DOCUMENT_INFO = "GetDocumentInfo";
    private static final String SPROC_GET_USER_PERMISSION_SET = "GetUserPermissionSet";
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String SPROC_INSERT_TEXT = "InsertText";
    private static final String SPROC_UPDATE_PARENTS_DATE_MODIFIED = "UpdateParentsDateModified";
    private static final String SPROC_GET_DOC_TYPES_FOR_USER = "GetDocTypesForUser";
    private static final String SPROC_SECTION_ADD_CROSSREF = "SectionAddCrossref";
    private static final String SPROC_INHERIT_PERMISSONS = "InheritPermissions";
    private static final String SPROC_CLASSIFICATION_FIX = "Classification_Fix";

    private IMCService service;
    private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
    private static Logger log = Logger.getLogger( DocumentMapper.class );
    /**
     *
     * @param metaId
     * @param permissionSetId
     * @param langPrefix
     * @return Map of type (permissionId<Integer>, hasPermission<Boolean>)
     */
    /*    private Map sprocGetDocTypesWithPermissions( int metaId, int permissionSetId, String langPrefix ) {
            String[] params = new String[]{ String.valueOf(metaId), String.valueOf(permissionSetId), langPrefix };
            String[] result = service.sqlProcedure( SPROC_GET_DOC_TYPES_WITH_PERMISSIONS, params );
        }
    */
    //    private static final String SPROC_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS = "GetTemplateGroupsWithPermissions";
    //    private static final String SPROC_GET_ROLES_DOC_PERMISSONS = "getrolesdocpermissions";

    private static String[] sprocGetUserPermissionSet( IMCService service, int metaId, int userId ) {
        String[] sqlParams = {String.valueOf( metaId ), String.valueOf( userId )};
        String[] sqlResult = service.sqlProcedure( SPROC_GET_USER_PERMISSION_SET, sqlParams );
        return sqlResult;
    }

    /** @return the filename for a fileupload-internalDocument, or null if the internalDocument isn't a fileupload-docuemnt. **/
    private static String sprocGetFilename( IMCService service, int meta_id ) {
        String[] params = new String[]{String.valueOf( meta_id )};
        return service.sqlProcedureStr( SPROC_GET_FILE_NAME, params );
    }

    private static String[] sprocGetDocumentInfo( IMCService service, int metaId ) {
        String[] params = new String[]{String.valueOf( metaId )};
        String[] result = service.sqlProcedure( SPROC_GET_DOCUMENT_INFO, params );
        return result;
    }

    public static class SectionData {
        public int sectionId;
        public String sectionName;
    }

    public static String[] sprocSectionGetInheritId( IMCServiceInterface service, int meta_id ) {
        String[] section_data = service.sqlProcedure( SPROC_SECTION_GET_INHERIT_ID, new String[]{String.valueOf( meta_id )} );
        return section_data;
    }

    private String[] sprocGetTestDocData( int metaId ) {
        String[] textdoc_data = service.sqlProcedure( SPROC_GET_TEST_DOC_DATA, new String[]{String.valueOf( metaId )} );
        return textdoc_data;
    }

    private String[] sprocGetText( int meta_id, int no ) {
        String[] params = new String[]{"" + meta_id, "" + no};
        String[] results = service.sqlProcedure( SPROC_GET_TEXT, params, false );
        return results;
    }

    private static HashSet sprocGetDocTypesForUser( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id, String lang_prefix ) {
        String[] user_dt = imcref.sqlProcedure( SPROC_GET_DOC_TYPES_FOR_USER + " " + parent_meta_id + "," + user.getUserId() + ",'" + lang_prefix + "'" );
        HashSet user_doc_types = new HashSet();
        for( int i = 0; i < user_dt.length; i += 2 ) {
            user_doc_types.add( user_dt[i] );
        }
        return user_doc_types;
    }

    private static void sprocUpdateInsertText( IMCService service, int meta_id, int txt_no, IMCText text, String textstring ) {
        String[] params = new String[]{"" + meta_id, "" + txt_no, "" + text.getType(), textstring};
        service.sqlUpdateProcedure( SPROC_INSERT_TEXT, params );
    }

    public static void sprocUpdateInheritPermissions( IMCServiceInterface imcref, int meta_id, int parent_meta_id, int doc_type ) {
        imcref.sqlUpdateProcedure( SPROC_INHERIT_PERMISSONS + " " + meta_id + "," + parent_meta_id + "," + doc_type );
    }

    public static void sprocUpdateSaveClassification( IMCServiceInterface imcref, int meta_id, String classification ) {
        imcref.sqlUpdateProcedure( SPROC_CLASSIFICATION_FIX + " " + meta_id + ",'" + classification + "'" );
    }

    public static void sprocUpdateSectionAddCrossref( IMCServiceInterface imcref, int meta_id, int section_id ) {
        imcref.sqlUpdateProcedure( SPROC_SECTION_ADD_CROSSREF + " " + meta_id + ", " + section_id );
    }

    private void sqlUpdateDockType( IMCService service, int metaId, int docType ) {
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

    public void sqlUpdateModifiedDatesOnDocumentAndItsParent( int meta_id, Date dateTime ) {
        String modifiedDateTimeStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateTime );
        String sqlStr = "update meta set date_modified ='" + modifiedDateTimeStr + "' where meta_id = " + meta_id;
        service.sqlUpdateQuery( sqlStr );
        // Update the date_modified for all parents.
        String[] params = new String[]{String.valueOf( meta_id )};
        service.sqlUpdateProcedure( SPROC_UPDATE_PARENTS_DATE_MODIFIED, params );
    }

    private void sqlUpdateOneField( DocumentDomainObject document, String fieldName, String valueStr ) {
        String whereString = " where meta_id = " + document.getMetaId();
        String updateStr = "update meta set ";
        String sqlStr = updateStr + fieldName + " = " + valueStr + whereString;
        service.sqlUpdateQuery( sqlStr );
    }

    public void sqlUpdateSaveTextAttribute( DocumentDomainObject document ) {
        String fieldName = "meta_text";
        String valueStr = "'" + document.getText() + "'";
        sqlUpdateOneField( document, fieldName, valueStr );
    }

    public void sqlUpdateSaveHeadlineAttribute( DocumentDomainObject document ) {
        String fieldName = "meta_headline";
        String valueStr = "'" + document.getHeadline() + "'";
        sqlUpdateOneField( document, fieldName, valueStr );
    }

    public void sqlUpdateSaveImageAttribute( DocumentDomainObject document ) {
        String fieldName = "meta_image";
        String valueStr = "'" + document.getImage() + "'";
        sqlUpdateOneField( document, fieldName, valueStr );
    }

    public static void sqlUpdateModifiedDate( IMCServiceInterface imcref, int parent_meta_id, Date dateModified ) {
        String sqlStr = "update meta\n";
        String dateModifiedStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( dateModified );
        sqlStr += "set date_modified = '" + dateModifiedStr + "'\n";
        sqlStr += "where meta_id = " + parent_meta_id;
        imcref.sqlUpdateQuery( sqlStr );
    }

    private void sqlUpdateCreatedDate( IMCService service, int metaId, Date dateTime ) {
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
    private static void sqlUpdateTouchDocument( IMCService service, int meta_id ) {
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat( DateHelper.DATE_TIME_SECONDS_FORMAT_STRING );
        service.sqlUpdateQuery( "update meta set date_modified = '" + dateformat.format( date ) + "' where meta_id = " + meta_id );
    }

    public DocumentMapper( IMCService service, ImcmsAuthenticatorAndUserMapper imcmsAAUM ) {
        this.service = service;
        this.imcmsAAUM = imcmsAAUM;
    }

    public DocumentDomainObject getDocument( int metaId ) {
        DocumentDomainObject document = null;
        try {
            String[] result = sprocGetDocumentInfo( service, metaId );

            //lets start and do some controlls of the resulted data
            if( result == null || result.length < 25 ) {
                throw new IndexOutOfBoundsException( "No such internalDocument: " + metaId );
            }

            DateFormat dateform = new SimpleDateFormat( DateHelper.DATE_TIME_SECONDS_FORMAT_STRING );
            //ok lets set all the internalDocument stuff
            try {
                document = new DocumentDomainObject();
                document.setMetaId( Integer.parseInt( result[0] ) );
                document.setDocumentType( Integer.parseInt( result[2] ) );
            } catch( NumberFormatException nfe ) {
                throw new SQLException( "SQL: GetDocumentInfo " + metaId + " returned corrupt data! '" + result[0] + "' '" + result[2] + "'" );
            }
            document.setHeadline( result[3] );
            document.setText( result[4] );
            document.setImage( result[5] );
            document.setTarget( result[21] );

            document.setArchived( result[12] == "0" ? false : true );

            String[] section_data = sprocSectionGetInheritId( service, metaId );

            String result11 = null;
            if( section_data.length == 2 ) {
                result11 = section_data[1];
            }
            String sectionName = result11;
            document.setSection( sectionName );

            try {
                document.setCreatedDatetime( dateform.parse( result[16] ) );
            } catch( NullPointerException npe ) {
                document.setCreatedDatetime( null );
            } catch( ParseException pe ) {
                document.setCreatedDatetime( null );
            }
            try {
                document.setModifiedDatetime( dateform.parse( result[17] ) );
            } catch( NullPointerException npe ) {
                document.setModifiedDatetime( null );
            } catch( ParseException pe ) {
                document.setModifiedDatetime( null );
            }
            try {
                document.setActivatedDatetime( dateform.parse( result[23] ) );
            } catch( NullPointerException npe ) {
                document.setActivatedDatetime( null );
            } catch( ParseException pe ) {
                document.setActivatedDatetime( null );
            }
            try {
                document.setArchivedDatetime( dateform.parse( result[24] ) );
            } catch( NullPointerException npe ) {
                document.setArchivedDatetime( null );
            } catch( ParseException pe ) {
                document.setArchivedDatetime( null );
            }
            if( document.getDocumentType() == DocumentDomainObject.DOCTYPE_FILE ) {
                document.setFilename( sprocGetFilename( service, metaId ) );
            }
            if( document.getDocumentType() == DocumentDomainObject.DOCTYPE_TEXT ) {
                String[] textdoc_data = sprocGetTestDocData( metaId );

                if( textdoc_data.length >= 4 ) {
                    document.setTemplate( new Template( Integer.parseInt( textdoc_data[0] ), textdoc_data[1] ) );
                    document.setMenuSortOrder( Integer.parseInt( textdoc_data[2] ) );
                    document.setTemplateGroupId( Integer.parseInt( textdoc_data[3] ) );
                }
            }
        } catch( SQLException ex ) {
            log.error( ex );
            throw new IndexOutOfBoundsException();
        }
        return document;

    }

    public boolean hasAdminPermissions( DocumentDomainObject document, UserDomainObject user ) {

        boolean result = false;

        boolean userHasSuperAdminRole = imcmsAAUM.hasSuperAdminRole( user );

        if( userHasSuperAdminRole ) {
            result = true;
        } else {

            String[] sqlResult = sprocGetUserPermissionSet( service, document.getMetaId(), user.getUserId() );
            Vector perms = new Vector( Arrays.asList( sqlResult ) );

            if( perms.size() > 0 ) {
                int userPermissionSetId = Integer.parseInt( (String)perms.elementAt( 0 ) );
                switch( userPermissionSetId ) {
                    case IMCConstants.DOC_PERM_SET_FULL:
                    case IMCConstants.DOC_PERM_SET_RESTRICTED_1:
                    case IMCConstants.DOC_PERM_SET_RESTRICTED_2:
                        result = true;
                        break;
                    default:
                        result = false;
                }
            }
        }

        return result;
    }

    public IMCText getTextField( DocumentDomainObject document, int textFieldIndexInDocument ) {
        return service.getText( document.getMetaId(), textFieldIndexInDocument );
    }

    public IMCText getText( int meta_id, int no ) {
        try {
            String[] results = sprocGetText( meta_id, no );

            if( results == null || results.length == 0 ) {
                /* There was no text. Return null. */
                return null;
            }

            /* Return the text */
            String text = results[0];
            int type = Integer.parseInt( results[1] );

            return new IMCText( text, type );

        } catch( NumberFormatException ex ) {
            /* There was no text, but we shouldn't come here unless the db returned something wrong. */
            log.error( "SProc 'sprocGetText()' returned an invalid text-type.", ex );
            return null;
        }
    }

    public void saveText( IMCText text, int meta_id, int txt_no, UserDomainObject user, String text_type ) {
        String textstring = text.getText();

        // update text
        sprocUpdateInsertText( service, meta_id, txt_no, text, textstring );

        // update the date
        sqlUpdateTouchDocument( service, meta_id );

        service.updateLogs( "Text " + txt_no + " in  " + "[" + meta_id + "] modified by user: [" + user.getFullName() + "]" );

        if( !("").equals( text_type ) ) {

            if( text_type.startsWith( "poll" ) ) {
                PollHandlingSystem poll = service.getPollHandlingSystem();
                poll.savePollparameter( text_type, meta_id, txt_no, textstring );
            }
        }
    }

    public static void copyTemplateData( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id, String meta_id ) {
        //ok now lets see what to do with the templates
        String[] temp = sqlSelectTemplateInfoFromTextDocs( imcref, parent_meta_id );
        String sqlStr;

        //lets get the users greatest permission_set for this dokument
        final int perm_set = imcref.getUserHighestPermissionSet( Integer.parseInt( meta_id ), user.getUserId() );
        //ok now we have to setup the template too use

        if( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_1 ) {
            //ok restricted_1 permission lets see if we have a default template fore this one
            //and if so lets put it as the orinary template instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[3] );
                if( tempInt >= 0 )
                    temp[0] = String.valueOf( tempInt );
            } catch( NumberFormatException nfe ) {

                //there wasn't a number but we dont care, we just catch the exeption and moves on.
            }
        } else if( perm_set == IMCConstants.DOC_PERM_SET_RESTRICTED_2 ) { //ok we have a restricted_2 permission lets see if we have default template fore this one
            //and if soo lets put it as ordinary instead of the parents
            try {
                int tempInt = Integer.parseInt( temp[4] );
                if( tempInt >= 0 )
                    temp[0] = String.valueOf( tempInt );
            } catch( NumberFormatException nfe ) {
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
        String sqlStr2 = ")\nvalues (" + doc_type + ",0,''," + (null == activated_datetime ? "NULL" : "'" + activated_datetime + "'") + "," + (null == archived_datetime ? "NULL" : "'" + archived_datetime + "'");
        Enumeration propkeys = metaprops.propertyNames();
        while( propkeys.hasMoreElements() ) {
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
        sqlUpdateModifiedDate( service, newMetaId , nowDateTime );
        sqlUpdateDockType( service, newMetaId, docType );

        // inherit all the different data thats not in meta from parent.
        sprocUpdateInheritPermissions( service, newMetaId, parentId, docType );
        inheritClassifications( parentId, newMetaId );
        inheritSection( parentId, newMetaId );

        // update parents modfied date because it has gotten an new link
        sqlSelectAddSortorderToParentsChildList( service, String.valueOf( parentId ), String.valueOf( newMetaId ), String.valueOf( parentMenuNumber ) );
        sqlUpdateModifiedDate( service, parentId , nowDateTime );

        // Now the TEXT document specific stuff
        DocumentMapper.copyTemplateData( service, user, String.valueOf( parentId ), String.valueOf( newMetaId ) );
        DocumentMapper.sqlUpdateActivateTheTextField( service, newMetaId );

        return getDocument( newMetaId );
    }

    private void inheritSection( int from_parentId, int to_metaId ) {
        String[] sectionData = sprocSectionGetInheritId( service, from_parentId );
        sprocUpdateSectionAddCrossref( service, to_metaId, Integer.parseInt(sectionData[0]) );
    }

    private void inheritClassifications( int from_parentId, int to_newMetaId ) {
        String classifications = getClassificationsAsOneString( service, from_parentId );
        sprocUpdateSaveClassification( service, to_newMetaId, classifications );
    }

    private int sqlCreateNewRowInMetaCopyParentData( IMCService service, int parentId ) {
        // todo: All this chould propably be done in a singel sql query but i dont have time to think how that should be done right now. Hasse
        final String columnsToBeCopied = "description,doc_type,meta_headline,meta_text,meta_image,owner_id,permissions,shared,expand,show_meta,help_text_id,archive,status_id,lang_prefix,classification,date_created,date_modified,sort_position,menu_position,disable_search,target,frame_name,activate,activated_datetime,archived_datetime";
        String sqlStatmentGetAllParentData = "select " + columnsToBeCopied + " from meta where meta_id = " + parentId;

        String[] parentDataRow = service.sqlQuery( sqlStatmentGetAllParentData );
        String values =
            "'" + parentDataRow[0] + "'," +
            parentDataRow[1] + "," +
            "'" + parentDataRow[2] + "'," +
            "'" + parentDataRow[3] + "'," +
            "'" + parentDataRow[4] + "'," +
            parentDataRow[5] + "," +
            parentDataRow[6] + "," +
            parentDataRow[7] + "," +
            parentDataRow[8] + "," +
            parentDataRow[9] + "," +
            parentDataRow[10] + "," +
            parentDataRow[11] + "," +
            parentDataRow[12] + "," +
            "'" + parentDataRow[13] + "'," +
            "'" + parentDataRow[14] + "'," +
            "'" + parentDataRow[15] + "'," +
            "'" + parentDataRow[16] + "'," +
            parentDataRow[17] + "," +
            parentDataRow[18] + "," +
            parentDataRow[19] + "," +
            "'" + parentDataRow[20] + "'," +
            "'" + parentDataRow[21] + "'," +
            parentDataRow[22];

        // The above query returns "" of some reason instead of NULL, and I don't have time to think of why, Hasse
        // Todo: think throu how to deal with sql that is not stored procedures.
        if( parentDataRow[23].equals("") ) {
            values+=",NULL";
        } else {
            values +=  ",'" + parentDataRow[23] + "'";
        }

        // Same reson here.
        if( parentDataRow[24].equals("") ) {
            values+=",NULL";
        } else {
            values +=  ",'" + parentDataRow[24] + "'";
        }

        String sqlStatmentInsertAllButMetaId = "insert into meta ("+columnsToBeCopied+") " + " values (" + values.toString() + ")";
        service.sqlUpdateQuery(sqlStatmentInsertAllButMetaId);
        String meta_id = service.sqlQueryStr( "select @@IDENTITY" );
        return Integer.parseInt(meta_id);
    }

    public static boolean checkUsersRights( IMCServiceInterface imcref, UserDomainObject user, String parent_meta_id, String lang_prefix, String doc_type ) {
        HashSet user_doc_types = sprocGetDocTypesForUser( imcref, user, parent_meta_id, lang_prefix );
        boolean userHasRights = user_doc_types.contains( doc_type );
        return userHasRights;
    }

    public static String getClassificationsAsOneString( IMCServiceInterface imcref, int meta_id ) {
        String[] classifications = sqlSelectGetClassificationStrings( imcref, meta_id );
        String classification = "";
        if( classifications.length > 0 ) {
            classification += classifications[0];
            for( int i = 1; i < classifications.length; ++i ) {
                classification += "; " + classifications[i];
            }
        }
        return classification;
    }
}

