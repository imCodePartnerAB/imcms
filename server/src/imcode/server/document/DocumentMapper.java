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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DocumentMapper {
    private IMCService service;
    private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
    private static Logger log = Logger.getLogger( DocumentMapper.class );
    private static final String DATE_FORMATING_STRING = "yyyy-MM-dd HH:mm:ss";

    /**
     * Stored procedure names used in this class
     */
    private final static String SPROC_GET_USER_ROLES_DOC_PERMISSONS = "GetUserRolesDocPermissions";
    private static final String SPROC_GET_TEST_DOC_DATA = "GetTextDocData";
    private static final String SPROC_SECTION_GET_INHERIT_ID = "SectionGetInheritId";
    private static final String SPROC_GET_FILE_NAME = "GetFileName";
    private static final String SPROC_GET_DOCUMENT_INFO = "GetDocumentInfo";
    private static final String SPROC_GET_USER_PERMISSION_SET = "GetUserPermissionSet";
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String SPROC_INSERT_TEXT = "InsertText";
    private static final String SPROC_GET_PERMISSION_SET = "GetPermissionSet";
    //    private static final String SPROC_GET_DOC_TYPES_WITH_PERMISSIONS = "GetDocTypesWithPermissions";

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

    private static class PermissionTuple {
        int permissionId;
        boolean hasPermission;

        public PermissionTuple( int permissionId, boolean hasPermission ) {
            this.permissionId = permissionId;
            this.hasPermission = hasPermission;
        }
    }

    /**
     * @param metaId
     * @param permissionSetId
     * @param langPrefix
     * @return PermissionTuple[]
     */
    private PermissionTuple[] sprocGetPermissionSet( int metaId, int permissionSetId, String langPrefix ) {
        String[] sqlParams = {String.valueOf( metaId ), String.valueOf( permissionSetId ), langPrefix};
        String[] sqlResult = service.sqlProcedure( SPROC_GET_PERMISSION_SET, sqlParams );
        PermissionTuple[] result = new PermissionTuple[sqlResult.length / 3];
        for( int i = 0, r = 0; i < sqlResult.length; i = i + 3, r++ ) {
            int permissionId = Integer.parseInt( sqlResult[i] );
            String permissionDescriptionStr = sqlResult[i + 1];
            boolean hasPermission = Integer.parseInt( sqlResult[i + 2] ) == 1;
            result[r] = new PermissionTuple(permissionId,hasPermission);
        }
        return result;
    }

    private String[] sprocGetUserPermissionSet( int metaId, int userId ) {
        String[] sqlParams = {String.valueOf( metaId ), String.valueOf( userId )};
        String[] sqlResult = service.sqlProcedure( SPROC_GET_USER_PERMISSION_SET, sqlParams );
        return sqlResult;
    }

    /** @return the filename for a fileupload-internalDocument, or null if the internalDocument isn't a fileupload-docuemnt. **/
    private String sprocGetFilename( int meta_id ) {
        String[] params = new String[]{String.valueOf( meta_id )};
        return service.sqlProcedureStr( SPROC_GET_FILE_NAME, params );
    }

    private String[] sprocGetUserRolesDocPermissions( int metaId ) {
        String[] params = {String.valueOf( metaId ), null};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_USER_ROLES_DOC_PERMISSONS, params );
        return sprocResult;
    }

    private String[] sprocGetDocumentInfo( int metaId ) {
        String[] params = new String[]{String.valueOf( metaId )};
        String[] result = service.sqlProcedure( SPROC_GET_DOCUMENT_INFO, params );
        return result;
    }

    /** @return the section for a internalDocument, or null if there was none **/
    private String sprocSectionGetInheritId( int meta_id ) {
        String[] section_data = service.sqlProcedure( SPROC_SECTION_GET_INHERIT_ID, new String[]{String.valueOf( meta_id )} );

        if( section_data.length < 2 ) {
            return null;
        }
        return section_data[1];
    }

    private void sprocInsertText( int meta_id, int txt_no, IMCText text, String textstring ) {
        String[] params = new String[]{"" + meta_id, "" + txt_no, "" + text.getType(), textstring};
        service.sqlUpdateProcedure( SPROC_INSERT_TEXT, params );
    }

    /**
     Set the modified datetime of a internalDocument to now
     @param meta_id The id of the internalDocument
     **/
    private void sqlTouchDocument( int meta_id ) {
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
            String[] result = sprocGetDocumentInfo( metaId );

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

            document.setSection( sprocSectionGetInheritId( metaId ) );

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
                document.setFilename( sprocGetFilename( metaId ) );
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

    public Map getAllRolesMappedToPermissions( DocumentDomainObject document ) {
        Map result = new HashMap();
        String[] sprocResult = sprocGetUserRolesDocPermissions( document.getMetaId() );
        int columnsResult = 4;
        for( int i = 0; i < sprocResult.length; i += columnsResult ) {
            // String roleId = sprocResult[i];
            String roleName = sprocResult[i + 1];
            String userPermissionSetId = sprocResult[i + 2];
            DocumentPermissionSetDomainObject docPermSetDO = new DocumentPermissionSetDomainObject( Integer.parseInt( userPermissionSetId ) );
            if( docPermSetDO.getPermissionType() == IMCConstants.DOC_PERM_SET_RESTRICTED_1 || docPermSetDO.getPermissionType() == IMCConstants.DOC_PERM_SET_RESTRICTED_2 ) {
                PermissionTuple[] permissionMapping = sprocGetPermissionSet( document.getMetaId(), docPermSetDO.getPermissionType(), "en" );
                mapPermissions( docPermSetDO, permissionMapping );
            }
            result.put( roleName, docPermSetDO );
        }
        return result;
    }

    public boolean hasAdminPermissions( DocumentDomainObject document, UserDomainObject user ) {

        boolean result = false;

        boolean userHasSuperAdminRole = imcmsAAUM.hasSuperAdminRole( user );

        if( userHasSuperAdminRole ) {
            result = true;
        } else {

            String[] sqlResult = sprocGetUserPermissionSet( document.getMetaId(), user.getUserId() );
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
        sprocInsertText( meta_id, txt_no, text, textstring );

        // update the date
        sqlTouchDocument( meta_id );

        service.updateLogs( "Text " + txt_no + " in  " + "[" + meta_id + "] modified by user: [" + user.getFullName() + "]" );

        if( !("").equals( text_type ) ) {

            if( text_type.startsWith( "poll" ) ) {
                PollHandlingSystem poll = service.getPollHandlingSystem();
                poll.savePollparameter( text_type, meta_id, txt_no, textstring );
            }
        }
    }

    private final static int EDIT_HEADLINE_PERMISSON_ID = 1;
    private final static int EDIT_DOCINFO_PERMISSON_ID = 2;
    private final static int EDIT_PERMISSIONS_PERMISSON_ID = 4;
    private final static int EDIT_TEXTS_PERMISSON_ID = 65536;
    private final static int EDIT_PICTURES_PERMISSON_ID = 131072;
    private final static int EDIT_MENUES_PERMISSON_ID = 262144;
    private final static int EDIT_TEMPLATE_PERMISSON_ID = 524288;
    private final static int EDIT_INCLUDE_PERMISSON_ID = 1048576;
    /*
        private final static Integer[] permissionIds = new Integer[]{ new Integer( EDIT_HEADLINE_PERMISSON_ID ),
                                                              new Integer( EDIT_DOCINFO_PERMISSON_ID ),
                                                              new Integer( EDIT_PERMISSIONS_PERMISSON_ID ),
                                                              new Integer( EDIT_TEXTS_PERMISSON_ID ),
                                                              new Integer( EDIT_PICTURES_PERMISSON_ID ),
                                                              new Integer( EDIT_MENUES_PERMISSON_ID ),
                                                              new Integer( EDIT_TEMPLATE_PERMISSON_ID ),
                                                              new Integer( EDIT_INCLUDE_PERMISSON_ID) };   */

    static void mapPermissions( DocumentPermissionSetDomainObject docPermSetDO, PermissionTuple[] permissionMapping ) {

        for( int i = 0; i < permissionMapping.length; i++ ) {
            switch( permissionMapping[i].permissionId ) {
                case EDIT_HEADLINE_PERMISSON_ID:
                    docPermSetDO.setEditHeadline( permissionMapping[i].hasPermission );
                    break;
                case EDIT_DOCINFO_PERMISSON_ID:
                    docPermSetDO.setEditDocumentInformation( permissionMapping[i].hasPermission );
                    break;
                case EDIT_PERMISSIONS_PERMISSON_ID:
                    docPermSetDO.setEditPermissions( permissionMapping[i].hasPermission );
                    break;
                case EDIT_TEXTS_PERMISSON_ID:
                    docPermSetDO.setEditTexts( permissionMapping[i].hasPermission );
                    break;
                case EDIT_PICTURES_PERMISSON_ID:
                    docPermSetDO.setEditPictures( permissionMapping[i].hasPermission );
                    break;
                case EDIT_MENUES_PERMISSON_ID:
                    docPermSetDO.setEditMenues( permissionMapping[i].hasPermission );
                    break;
                case EDIT_TEMPLATE_PERMISSON_ID:
                    docPermSetDO.setEditTemplates( permissionMapping[i].hasPermission );
                    break;
                case EDIT_INCLUDE_PERMISSON_ID:
                    docPermSetDO.setEditIncludes( permissionMapping[i].hasPermission );
                    break;
                default:
                    log.warn( "Missing permission_id in mapPermissions()" );
                    break;
            }
        }
    }
}

