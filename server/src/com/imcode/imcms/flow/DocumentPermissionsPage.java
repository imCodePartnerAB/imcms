package com.imcode.imcms.flow;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class DocumentPermissionsPage extends OkCancelPage {

    public static final String REQUEST_PARAMETER__ROLES_WITHOUT_PERMISSIONS = "roles_no_rights";
    public static final String REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION = "role_";
    public static final String REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID = "default_template";
    public static final String REQUEST_PARAMETER__ADD_ROLES = "add_roles";
    public static final String REQUEST_PARAMETER__RESTRICTED_ONE_MORE_PRIVILEGED_THAN_RESTRICTED_TWO = "restricted_one_more_privileged_than_restricted_two";

    private static final String URL_I15D_PAGE__DOCUMENT_PERMISSIONS = "/jsp/docadmin/document_permissions.jsp";

    private DocumentDomainObject document;

    public DocumentPermissionsPage( DocumentDomainObject document, DispatchCommand okCommand,
                                    DispatchCommand cancelCommand ) {
        super(okCommand,cancelCommand) ;
        this.document = document;
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return EditDocumentPageFlow.URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2()
               + URL_I15D_PAGE__DOCUMENT_PERMISSIONS;
    }

    protected void dispatchOther( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        DocumentPermissionSetDomainObject documentPermissionSet = null;
        boolean forNew = false;
        UserDomainObject user = Utility.getLoggedOnUser( request ) ;
        if ( Utility.parameterIsSet( request, "define_set_1" ) && user.canDefineRestrictedOneFor( document ) ) {
            documentPermissionSet = document.getPermissionSets().getRestricted1();
        } else if ( Utility.parameterIsSet( request, "define_set_2" ) && user.canDefineRestrictedTwoFor( document ) ) {
            documentPermissionSet = document.getPermissionSets().getRestricted2();
        } else if ( Utility.parameterIsSet( request, "define_new_set_1" ) && user.canDefineRestrictedOneFor( document ) ) {
            documentPermissionSet = document.getPermissionSetsForNewDocuments().getRestricted1();
            forNew = true;
        } else if ( Utility.parameterIsSet( request, "define_new_set_2" ) && user.canDefineRestrictedTwoFor( document ) ) {
            documentPermissionSet = document.getPermissionSetsForNewDocuments().getRestricted2();
            forNew = true;
        }

        if ( null != documentPermissionSet ) {
            DispatchCommand returnCommand = new DispatchCommand() {
                public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                    forward( request, response );
                }
            };
            DocumentPermissionSetPage documentPermissionSetPage = new DocumentPermissionSetPage( this.document, documentPermissionSet, forNew, returnCommand, returnCommand );
            documentPermissionSetPage.forward(request, response);
        } else {
            forward( request, response );
        }
    }

    protected void updateFromRequest( HttpServletRequest request ) {
        document.setRoleIdsMappedToDocumentPermissionSetTypes( getRoleIdsMappedToDocumentPermissionSetTypesFromRequest( request ) );
        document.setRestrictedOneMorePrivilegedThanRestrictedTwo( Utility.parameterIsSet( request, REQUEST_PARAMETER__RESTRICTED_ONE_MORE_PRIVILEGED_THAN_RESTRICTED_TWO ) );
        document.setLinkableByOtherUsers( Utility.parameterIsSet( request, EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS ) );
        document.setLinkedForUnauthorizedUsers( Utility.parameterIsSet( request, EditDocumentInformationPageFlow.REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS ) );

        if ( document instanceof TextDocumentDomainObject ) {
            String defaultTemplateIdStr = request.getParameter( REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID );
            String templateName = StringUtils.isNotBlank(defaultTemplateIdStr) ? defaultTemplateIdStr : null;
            ( (TextDocumentDomainObject)document ).setDefaultTemplateId( templateName );
        }

    }

    private RoleIdToDocumentPermissionSetTypeMappings getRoleIdsMappedToDocumentPermissionSetTypesFromRequest( HttpServletRequest request ) {
        Map parameterMap = request.getParameterMap();
        Collection rolesWithPermissions = CollectionUtils.collect( CollectionUtils.select( parameterMap.keySet(), new RolePermissionParameterPredicate() ), new RolePermissionParameterToRoleIdTransformer() );
        RoleIdToDocumentPermissionSetTypeMappings result = new RoleIdToDocumentPermissionSetTypeMappings();
        for ( Iterator iterator = rolesWithPermissions.iterator(); iterator.hasNext(); ) {
            RoleId roleId = (RoleId) iterator.next();
            int permissionSetTypeId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION
                                                                          + roleId.intValue() ) );
            DocumentPermissionSetTypeDomainObject documentPermissionSetType = DocumentPermissionSetTypeDomainObject.fromInt(permissionSetTypeId);
            result.setPermissionSetTypeForRole( roleId, documentPermissionSetType );
        }
        String[] rolesToGiveReadPermission = request.getParameterValues( REQUEST_PARAMETER__ROLES_WITHOUT_PERMISSIONS );
        if ( null != request.getParameter( REQUEST_PARAMETER__ADD_ROLES ) && null != rolesToGiveReadPermission ) {
            for ( int i = 0; i < rolesToGiveReadPermission.length; i++ ) {
                RoleId roleId = new RoleId( Integer.parseInt( rolesToGiveReadPermission[i] ));
                result.setPermissionSetTypeForRole( roleId, DocumentPermissionSetTypeDomainObject.READ );
            }
        }
        return result;
    }

    public DocumentDomainObject getDocument() {
        return document;
    }

    private static class RolePermissionParameterPredicate implements Predicate {

        public boolean evaluate( Object object ) {
            return ( (String)object ).startsWith( REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION );
        }
    }

    private static class RolePermissionParameterToRoleIdTransformer implements Transformer {

        public Object transform( Object input ) {
            int roleId = Integer.parseInt( ( (String)input ).substring( REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION.length() ) );
            return new RoleId( roleId);
        }
    }
}
