package imcode.server.document;

import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.IMCText;
import imcode.server.Template;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.User;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DocumentMapper {
    private IMCServiceInterface service;
    private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
    private Logger log = Logger.getLogger( DocumentMapper.class );

    private final static String SPROC_GET_USER_ROLES_DOC_PERMISSONS = "GetUserRolesDocPermissions";
    private static final String SPROC_GET_TEST_DOC_DATA = "GetTextDocData";
    private static final String SPROC_SECTION_GET_INHERIT_ID = "SectionGetInheritId";
    private static final String SPROC_GET_FILE_NAME = "GetFileName ";
    private static final String SPROC_GET_DOCUMENT_INFO = "GetDocumentInfo ";
    private static final String SPROC_GET_USER_PERMISSION_SET = "GetUserPermissionSet";

    public DocumentMapper( IMCServiceInterface service, ImcmsAuthenticatorAndUserMapper imcmsAAUM ) {
        this.service = service;
        this.imcmsAAUM = imcmsAAUM;
    }

    public Document getDocument( int metaId ) {
        Document document = null;
        try {
            String[] result = service.sqlProcedure( SPROC_GET_DOCUMENT_INFO + metaId );

            //lets start and do some controlls of the resulted data
            if( result == null || result.length < 25 ) {
                throw new IndexOutOfBoundsException( "No such document: " + metaId );
            }

            DateFormat dateform = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            //ok lets set all the document stuff
            try {
                document = new Document();
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

            document.setSection( getSection( metaId ) );

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
                document.setFilename( getFilename( metaId ) );
            }
            if( document.getDocumentType() == IMCConstants.DOCTYPE_TEXT ) {
                String[] textdoc_data = service.sqlProcedure( SPROC_GET_TEST_DOC_DATA, new String[]{String.valueOf( metaId )} );

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

    /** @return the section for a document, or null if there was none **/
    private String getSection( int meta_id ) {
        String[] section_data = service.sqlProcedure( SPROC_SECTION_GET_INHERIT_ID, new String[]{String.valueOf( meta_id )} );

        if( section_data.length < 2 ) {
            return null;
        }
        return section_data[1];
    }

    /** @return the filename for a fileupload-document, or null if the document isn't a fileupload-docuemnt. **/
    private String getFilename( int meta_id ) {
        return service.sqlProcedureStr( SPROC_GET_FILE_NAME + meta_id );
    }

    public Map getAllRolesMappedToPermissions( Document document ) {
        Map result = new HashMap();
        String[] params = {String.valueOf( document.getMetaId() ), null};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_USER_ROLES_DOC_PERMISSONS, params );
        int columnsResult = 4;
        for( int i = 0; i < sprocResult.length; i += columnsResult ) {
            String roleId = sprocResult[i];
            String roleName = sprocResult[i + 1];
            String setId = sprocResult[i + 2];
            result.put( roleName, Integer.valueOf( setId ) );
        }
        return result;
    }

    public boolean hasAdminPermissions( Document document, User user ) {

        boolean result = false;

        boolean userHasSuperAdminRole = imcmsAAUM.hasSuperAdminRole( user );

        if( userHasSuperAdminRole ) {
            result = true;
        } else {

            String[] sqlParams = {String.valueOf( document.getMetaId() ), String.valueOf( user.getUserId() )};
            String[] sqlResult = service.sqlProcedure( SPROC_GET_USER_PERMISSION_SET, sqlParams );

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

    public IMCText getTextField( Document document, int textFieldIndexInDocument ) {
        return service.getText( document.getMetaId(), textFieldIndexInDocument );
    }

}
