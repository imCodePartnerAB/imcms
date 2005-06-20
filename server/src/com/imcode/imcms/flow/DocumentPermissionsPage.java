package com.imcode.imcms.flow;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        putInSessionAndForwardToPath( EditDocumentPageFlow.URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2()
                                      + URL_I15D_PAGE__DOCUMENT_PERMISSIONS,request, response );
    }

    protected void dispatchOther( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        DocumentPermissionSetDomainObject documentPermissionSet = null;
        boolean forNew = false;
        UserDomainObject user = Utility.getLoggedOnUser( request ) ;
        if ( Utility.parameterIsSet( request, "define_set_1" ) && user.canDefineRestrictedOneFor( document ) ) {
            documentPermissionSet = document.getPermissionSetForRestrictedOne();
        } else if ( Utility.parameterIsSet( request, "define_set_2" ) && user.canDefineRestrictedTwoFor( document ) ) {
            documentPermissionSet = document.getPermissionSetForRestrictedTwo();
        } else if ( Utility.parameterIsSet( request, "define_new_set_1" ) && user.canDefineRestrictedOneFor( document ) ) {
            documentPermissionSet = document.getPermissionSetForRestrictedOneForNewDocuments();
            forNew = true;
        } else if ( Utility.parameterIsSet( request, "define_new_set_2" ) && user.canDefineRestrictedTwoFor( document ) ) {
            documentPermissionSet = document.getPermissionSetForRestrictedTwoForNewDocuments();
            forNew = true;
        }

        if ( null != documentPermissionSet ) {
            DispatchCommand returnCommand = new DispatchCommand() {
                public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                    forward( request, response );
                }
            };
            DocumentPermissionSetPage documentPermissionSetPage = new DocumentPermissionSetPage( documentPermissionSet, forNew, returnCommand, returnCommand );
            documentPermissionSetPage.forward(request, response);
        } else {
            forward( request, response );
        }
    }

    protected void updateFromRequest( HttpServletRequest request ) {
        document.setRolesMappedToPermissionSetIds( getRolesMappedToPermissionIdsFromRequest( request ) );
        document.setRestrictedOneMorePrivilegedThanRestrictedTwo( Utility.parameterIsSet( request, REQUEST_PARAMETER__RESTRICTED_ONE_MORE_PRIVILEGED_THAN_RESTRICTED_TWO ) );
        document.setLinkableByOtherUsers( Utility.parameterIsSet( request, EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS ) );
        document.setVisibleInMenusForUnauthorizedUsers( Utility.parameterIsSet( request, EditDocumentInformationPageFlow.REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS ) );

        if ( document instanceof TextDocumentDomainObject ) {
            String defaultTemplateIdStr = request.getParameter( REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID );
            TemplateDomainObject defaultTemplate = null;
            if ( null != defaultTemplateIdStr ) {
                try {
                    int templateId = Integer.parseInt( defaultTemplateIdStr );
                    defaultTemplate = Imcms.getServices().getTemplateMapper().getTemplateById( templateId );
                } catch ( NumberFormatException ignored ) {}
            }
            ( (TextDocumentDomainObject)document ).setDefaultTemplate( defaultTemplate );
        }

    }

    private Map getRolesMappedToPermissionIdsFromRequest( HttpServletRequest request ) {
        Map parameterMap = request.getParameterMap();
        Collection rolesWithPermissions = CollectionUtils.collect( CollectionUtils.select( parameterMap.keySet(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (String)object ).startsWith( REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION );
            }
        } ), new Transformer() {
            public Object transform( Object input ) {
                int roleId = Integer.parseInt( ( (String)input ).substring( REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION.length() ) );
                return Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getRoleById( roleId );
            }
        } );
        Map rolesMappedToPermissionSetIds = new HashMap();
        for ( Iterator iterator = rolesWithPermissions.iterator(); iterator.hasNext(); ) {
            RoleDomainObject role = (RoleDomainObject)iterator.next();
            int permissionSetId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION
                                                                          + role.getId() ) );
            rolesMappedToPermissionSetIds.put( role, new Integer( permissionSetId ) );
        }
        String[] rolesToGiveReadPermission = request.getParameterValues( REQUEST_PARAMETER__ROLES_WITHOUT_PERMISSIONS );
        if ( null != request.getParameter( REQUEST_PARAMETER__ADD_ROLES ) && null != rolesToGiveReadPermission ) {
            for ( int i = 0; i < rolesToGiveReadPermission.length; i++ ) {
                int roleId = Integer.parseInt( rolesToGiveReadPermission[i] );
                RoleDomainObject role = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getRoleById( roleId );
                rolesMappedToPermissionSetIds.put( role, new Integer( DocumentPermissionSetDomainObject.TYPE_ID__READ ) );
            }
        }
        return rolesMappedToPermissionSetIds;
    }

    public DocumentDomainObject getDocument() {
        return document;
    }
}
