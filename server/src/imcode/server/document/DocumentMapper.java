package imcode.server.document;

import imcode.server.*;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.poll.PollHandlingSystem;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;

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
    public static final String SPROC_UPDATE_PARENTS_DATE_MODIFIED = "UpdateParentsDateModified";

    private IMCService service;
    private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
    private static Logger log = Logger.getLogger( DocumentMapper.class );
    private static final String DATE_FORMATING_STRING = "yyyy-MM-dd HH:mm:ss";

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

    private static String[] sprocGetDocumentInfo( IMCService service,  int metaId ) {
        String[] params = new String[]{String.valueOf( metaId )};
        String[] result = service.sqlProcedure( SPROC_GET_DOCUMENT_INFO, params );
        return result;
    }

    /** @return the section for a internalDocument, or null if there was none **/
    private static String sprocSectionGetInheritId( IMCService service,  int meta_id ) {
        String[] section_data = service.sqlProcedure( SPROC_SECTION_GET_INHERIT_ID, new String[]{String.valueOf( meta_id )} );

        if( section_data.length < 2 ) {
            return null;
        }
        return section_data[1];
    }

    private static void sprocInsertText( IMCService service, int meta_id, int txt_no, IMCText text, String textstring ) {
        String[] params = new String[]{"" + meta_id, "" + txt_no, "" + text.getType(), textstring};
        service.sqlUpdateProcedure( SPROC_INSERT_TEXT, params );
    }

    /**
     Set the modified datetime of a internalDocument to now
     @param meta_id The id of the internalDocument
     **/
    private static void sqlTouchDocument( IMCService service, int meta_id ) {
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat( DATE_FORMATING_STRING );
        service.sqlUpdateQuery( "update meta set date_modified = '" + dateformat.format( date ) + "' where meta_id = " + meta_id );
    }

    public DocumentMapper( IMCService service, ImcmsAuthenticatorAndUserMapper imcmsAAUM ) {
        this.service = service;
        this.imcmsAAUM = imcmsAAUM;
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

    public DocumentDomainObject getDocument( int metaId ) {
        DocumentDomainObject document = null;
        try {
            String[] result = sprocGetDocumentInfo( service, metaId );

            //lets start and do some controlls of the resulted data
            if( result == null || result.length < 25 ) {
                throw new IndexOutOfBoundsException( "No such internalDocument: " + metaId );
            }

            DateFormat dateform = new SimpleDateFormat( DATE_FORMATING_STRING );
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

            document.setSection( sprocSectionGetInheritId( service, metaId ) );

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
            if( document.getDocumentType() == IMCConstants.DOCTYPE_FILE ) {
                document.setFilename( sprocGetFilename( service, metaId ) );
            }
            if( document.getDocumentType() == IMCConstants.DOCTYPE_TEXT ) {
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
        sprocInsertText( service, meta_id, txt_no, text, textstring );

        // update the date
        sqlTouchDocument( service, meta_id );

        service.updateLogs( "Text " + txt_no + " in  " + "[" + meta_id + "] modified by user: [" + user.getFullName() + "]" );

        if( !("").equals( text_type ) ) {

            if( text_type.startsWith( "poll" ) ) {
                PollHandlingSystem poll = service.getPollHandlingSystem();
                poll.savePollparameter( text_type, meta_id, txt_no, textstring );
            }
        }
    }

    public void saveHeadline( DocumentDomainObject document ) {
        String sqlStr = "update meta set meta_headline = '" + document.getHeadline() + "'where meta_id = " + document.getMetaId();
        service.sqlUpdateQuery( sqlStr );
    }

    public void sqlUpdateModifiedDatesOnDocumentAndItsParent( int meta_id, Date date, Date time ) {
        String modifiedDateStr = IMCConstants.DATE_FORMAT.format( date );
        String modifiedTimeStr = IMCConstants.DATE_FORMAT.format( time );
        String modifiedTimeDateStr = modifiedDateStr  + " " + modifiedTimeStr;
        String sqlStr = "update meta set date_modified ='"+ modifiedTimeDateStr + "' where meta_id = "+meta_id ;
        service.sqlUpdateQuery(sqlStr) ;
        // Update the date_modified for all parents.
        String[] params = new String[]{ String.valueOf(meta_id)};
        service.sqlUpdateProcedure( SPROC_UPDATE_PARENTS_DATE_MODIFIED, params) ;
    }
}

