package imcode.server.document;

import imcode.server.IMCService;
import imcode.server.IMCConstants;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class DocumentPermissionSetMapper {

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
        DatabaseAccessor.RolePermissionTuple[] sprocResult = DatabaseAccessor.sprocGetUserRolesDocPermissions( service, document.getMetaId() );
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
        DocumentPermissionSetDomainObject result = new DocumentPermissionSetDomainObject( document, permissionType );
        DatabaseAccessor.PermissionTuple[] permissionMapping = DatabaseAccessor.sprocGetPermissionSet( service, document.getMetaId(),  permissionType, langPrefix );
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
                        DatabaseAccessor.DocumentIdEditablePermissionsTuple[] names = DatabaseAccessor.sprocGetDocTypesWithPermissions( service, document.getMetaId(), permissionType, langPrefix );
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
                        DatabaseAccessor.GroupPermissionTuple[] names = DatabaseAccessor.sprocGetTemplateGroupsWithPermissions( service, document.getMetaId(), permissionType );
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
