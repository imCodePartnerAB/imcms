package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.IMCConstants;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class DocumentPermissionSetMapper {
    /**
     * Stored procedure names used in this class
     */
    private static final String SPROC_GET_PERMISSION_SET = "GetPermissionSet";
    private final static String SPROC_GET_USER_ROLES_DOC_PERMISSONS = "GetUserRolesDocPermissions";
    private static final String SPROC_GET_DOC_TYPES_WITH_PERMISSIONS = "GetDocTypesWithPermissions";

    private static String[] sprocGetUserRolesDocPermissions( IMCService service, int metaId ) {
        String[] params = {String.valueOf( metaId ), null};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_USER_ROLES_DOC_PERMISSONS, params );
        return sprocResult;
    }

    private static class PermissionTuple {
        int permissionId;
        boolean hasPermission;
    }

    /**
     * @param metaId
     * @param permissionSetId
     * @param langPrefix
     * @return PermissionTuple[]
     */
    private static DocumentPermissionSetMapper.PermissionTuple[] sprocGetPermissionSet( IMCService service, int metaId, int permissionSetId, String langPrefix ) {
        String[] sqlParams = {String.valueOf( metaId ), String.valueOf( permissionSetId ), langPrefix};
        String[] sqlResult = service.sqlProcedure( DocumentPermissionSetMapper.SPROC_GET_PERMISSION_SET, sqlParams );
        DocumentPermissionSetMapper.PermissionTuple[] result = new DocumentPermissionSetMapper.PermissionTuple[sqlResult.length / 3];
        for( int i = 0, r = 0; i < sqlResult.length; i = i + 3, r++ ) {
            int permissionId = Integer.parseInt( sqlResult[i] );
            //String permissionDescriptionStr = sqlResult[i + 1];
            boolean hasPermission = Integer.parseInt( sqlResult[i + 2] ) == 1;
            result[r] = new DocumentPermissionSetMapper.PermissionTuple();
            result[r].permissionId = permissionId;
            result[r].hasPermission = hasPermission;
        }
        return result;
    }

    private IMCService service;
    private static Logger log = Logger.getLogger( DocumentPermissionSetMapper.class );

    private final static int EDIT_HEADLINE_PERMISSON_ID = 1;
    private final static int EDIT_DOCINFO_PERMISSON_ID = 2;
    private final static int EDIT_PERMISSIONS_PERMISSON_ID = 4;
    private final static int EDIT_TEXTS_PERMISSON_ID = 65536;
    private final static int EDIT_PICTURES_PERMISSON_ID = 131072;
    private final static int EDIT_MENUES_PERMISSON_ID = 262144;
    private final static int EDIT_TEMPLATE_PERMISSON_ID = 524288;
    private final static int EDIT_INCLUDE_PERMISSON_ID = 1048576;

    public DocumentPermissionSetMapper( IMCService service ) {
        this.service = service;
    }

    public Map getAllRolesMappedToPermissions( DocumentDomainObject document ) {
        Map result = new HashMap();
        String[] sprocResult = sprocGetUserRolesDocPermissions( service, document.getMetaId() );
        int columnsResult = 4;
        for( int i = 0; i < sprocResult.length; i += columnsResult ) {
            // String roleId = sprocResult[i];
            String roleName = sprocResult[i + 1];
            String userPermissionSetId = sprocResult[i + 2];
            DocumentPermissionSetDomainObject docPermSetDO = new DocumentPermissionSetDomainObject( Integer.parseInt( userPermissionSetId ) );
            if( docPermSetDO.getPermissionType() == IMCConstants.DOC_PERM_SET_RESTRICTED_1 || docPermSetDO.getPermissionType() == IMCConstants.DOC_PERM_SET_RESTRICTED_2 ) {
                DocumentPermissionSetMapper.PermissionTuple[] permissionMapping = sprocGetPermissionSet( service, document.getMetaId(), docPermSetDO.getPermissionType(), "en" );
                mapPermissions( docPermSetDO, permissionMapping );
            }
            result.put( roleName, docPermSetDO );
        }
        return result;
    }

   static void mapPermissions( DocumentPermissionSetDomainObject docPermSetDO, DocumentPermissionSetMapper.PermissionTuple[] permissionMapping ) {

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
