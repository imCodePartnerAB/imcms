package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.IMCConstants;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class DocumentPermissionSetMapper {
    /**
     * Stored procedure names used in this class
     */
    private static final String SPROC_GET_PERMISSION_SET = "GetPermissionSet";
    private final static String SPROC_GET_USER_ROLES_DOC_PERMISSONS = "GetUserRolesDocPermissions";
    private static final String SPROC_GET_DOC_TYPES_WITH_PERMISSIONS = "GetDocTypesWithPermissions";
    private static final String SPROC_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS = "getTemplateGroupsWithPermissions";

    static class RolePermissionTuple {
        String roleName;
        int permissionId;
    }

    private static RolePermissionTuple[] sprocGetUserRolesDocPermissions( IMCService service, int metaId ) {
        String[] params = {String.valueOf( metaId ), null};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_USER_ROLES_DOC_PERMISSONS, params );
        int noOfColumns = 4;
        RolePermissionTuple[] result = new RolePermissionTuple[sprocResult.length / noOfColumns];
        for( int i = 0, k = 0; i < sprocResult.length; i = i + noOfColumns, k++ ) {
            //String roleId = sprocResult[i];
            String roleName = sprocResult[i + 1];
            String rolePermissionSetId = sprocResult[i + 2];
            result[k] = new RolePermissionTuple();
            result[k].roleName = roleName;
            result[k].permissionId = Integer.parseInt( rolePermissionSetId );
        }
        return result;
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
    private static PermissionTuple[] sprocGetPermissionSet( IMCService service, int metaId, int permissionSetId, String langPrefix ) {
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

    private static class DocumentIdEditablePermissionsTuple {
        boolean hasRights;
        String documentTypeName;
    }

    private static DocumentIdEditablePermissionsTuple[] sprocGetDocTypesWithPermissions( IMCService service, int metaId, int permissionType, String langPrefix ) {
        String[] params = new String[]{String.valueOf( metaId ), String.valueOf( permissionType ), langPrefix};
        String[] sprocResult = service.sqlProcedure( SPROC_GET_DOC_TYPES_WITH_PERMISSIONS, params );
        int numberOfColumns = 3;
        DocumentIdEditablePermissionsTuple[] result = new DocumentIdEditablePermissionsTuple[ sprocResult.length/numberOfColumns ];
        for( int i = 0, k = 0; i < sprocResult.length; i = i+numberOfColumns, k++ ){
            //int documentType = Integer.parseInt(sprocResult[i]);
            String documentTypeName = sprocResult[i+1];
            boolean permission = -1 != Integer.parseInt(sprocResult[i+2]);
            result[k] = new DocumentIdEditablePermissionsTuple();
            result[k].hasRights = permission;
            result[k].documentTypeName = documentTypeName;
        }
        return result;
    }

    private static class GroupPermissionTuple {
        String groupName;
        boolean hasPermission;
    }

    private static GroupPermissionTuple[] sprocGetTemplateGroupsWithPermissions( IMCService service, int metaId, int permissionType ) {
        String[] params = new String[]{ String.valueOf(metaId), String.valueOf( permissionType ) };
        String[] sprocResult = service.sqlProcedure( SPROC_GET_TEMPLATE_GROUPS_WITH_PERMISSIONS, params );
        int numberOfColumns = 3;
        GroupPermissionTuple[] result = new GroupPermissionTuple[sprocResult.length/numberOfColumns];
        for( int i = 0, k=0; i < sprocResult.length; i=i+numberOfColumns, k++) {
            //String groupId = sprocResult[i];
            String groupName = sprocResult[i+1];
            boolean hasPermission = -1 != Integer.parseInt(sprocResult[i+2]);
            result[k] = new GroupPermissionTuple();
            result[k].groupName = groupName;
            result[k].hasPermission = hasPermission;
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
        RolePermissionTuple[] sprocResult = sprocGetUserRolesDocPermissions( service, document.getMetaId() );
        for( int i = 0; i < sprocResult.length; i++ ) {
            String roleName = sprocResult[i].roleName;
            int permissionType = sprocResult[i].permissionId;
            String langPrefix = "en";
            switch( permissionType ) {
                case IMCConstants.DOC_PERM_SET_FULL:
                    result.put( roleName, createFullPermissionSet( document ) );
                    break;
                case IMCConstants.DOC_PERM_SET_RESTRICTED_1:
                case IMCConstants.DOC_PERM_SET_RESTRICTED_2:
                    result.put( roleName, createRestricedPermissionSet( document, permissionType, langPrefix )  );
                    break;
                case IMCConstants.DOC_PERM_SET_READ:
                    result.put( roleName, createReadPermissionSet( document )  );
                    break;
                default:
                    log.warn( "A missing mapping in DocumentPermissionSetMapper" );
                    break;
            }
        }
        return result;
    }

    private DocumentPermissionSetDomainObject createFullPermissionSet(  DocumentDomainObject document ) {
        DocumentPermissionSetDomainObject result =  null;
        result = new DocumentPermissionSetDomainObject( document, DocumentPermissionSetDomainObject.FULL_ID );
        result.setEditDocumentInformation( true );
        result.setEditHeadline( true );
        result.setEditIncludes( true );
        result.setEditMenues( true );
        result.setEditPermissions( true );
        result.setEditPictures( true );
        result.setEditTemplates( true );
        result.setEditTexts( true );
        return result;
    }

    private DocumentPermissionSetDomainObject createRestricedPermissionSet( DocumentDomainObject document, int permissionType, String langPrefix ) {
        DocumentPermissionSetMapper.PermissionTuple[] permissionMapping = sprocGetPermissionSet( service, document.getMetaId(),  permissionType, langPrefix );
        DocumentPermissionSetDomainObject result = new DocumentPermissionSetDomainObject( document, permissionType );
        for( int i = 0; i < permissionMapping.length; i++ ) {
            switch( permissionMapping[i].permissionId ) {
                case EDIT_HEADLINE_PERMISSON_ID:
                    result.setEditHeadline( permissionMapping[i].hasPermission );
                    break;
                case EDIT_DOCINFO_PERMISSON_ID:
                    result.setEditDocumentInformation( permissionMapping[i].hasPermission );
                    break;
                case EDIT_PERMISSIONS_PERMISSON_ID:
                    result.setEditPermissions( permissionMapping[i].hasPermission );
                    break;
                case EDIT_TEXTS_PERMISSON_ID:
                    result.setEditTexts( permissionMapping[i].hasPermission );
                    break;
                case EDIT_PICTURES_PERMISSON_ID:
                    result.setEditPictures( permissionMapping[i].hasPermission );
                    break;
                case EDIT_MENUES_PERMISSON_ID:
                    if( permissionMapping[i].hasPermission ) {
                        DocumentIdEditablePermissionsTuple[] names = sprocGetDocTypesWithPermissions( service, document.getMetaId(), permissionType, langPrefix );
                        ArrayList namesStr = new ArrayList();
                        for( int k = 0; k < names.length; k++ ) {
                            if( names[k].hasRights ) {
                                namesStr.add( names[k].documentTypeName );
                            }
                        }
                        result.setEditMenues( true );
                        String[] namesStrArr = (String[])namesStr.toArray( new String[ namesStr.size()] );
                        result.setEditableMenuNames( namesStrArr );
                    } else {
                        result.setEditMenues( false );
                        result.setEditableMenuNames( null );
                    }
                    break;
                case EDIT_TEMPLATE_PERMISSON_ID:
                    if( permissionMapping[i].hasPermission ) {
                        GroupPermissionTuple[] names = sprocGetTemplateGroupsWithPermissions( service, document.getMetaId(), permissionType );
                        ArrayList namesStr = new ArrayList();
                        for( int k = 0; k < names.length; k++ ) {
                            if( names[k].hasPermission ) {
                                namesStr.add( names[k].groupName );
                            }
                        }
                        result.setEditTemplates( true );
                        String[] namesStrArr = (String[])namesStr.toArray( new String[ namesStr.size()] );
                        result.setEditableTemplateGroupNames( namesStrArr );
                    } else {
                        result.setEditMenues( false );
                        result.setEditableMenuNames( null );
                    }
                    break;
                case EDIT_INCLUDE_PERMISSON_ID:
                    result.setEditIncludes( permissionMapping[i].hasPermission );
                    break;
                default:
                    log.warn( "Missing permission_id in createRestrictedPermissionSet()" );
                    break;
            }
        }
        return result;
    }

    private DocumentPermissionSetDomainObject createReadPermissionSet( DocumentDomainObject document) {
        DocumentPermissionSetDomainObject result =  null;
        result = new DocumentPermissionSetDomainObject( document, DocumentPermissionSetDomainObject.READ_ID );
        return result;
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedOne( DocumentDomainObject document ) {
        return createRestricedPermissionSet( document, DocumentPermissionSetDomainObject.RESTRICTED_1_ID, "en" );
    }

    public DocumentPermissionSetDomainObject getPermissionSetRestrictedTwo( DocumentDomainObject document ) {
        return createRestricedPermissionSet( document, DocumentPermissionSetDomainObject.RESTRICTED_2_ID, "en" );
    }
}
