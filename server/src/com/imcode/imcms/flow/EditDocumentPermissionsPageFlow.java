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

public class EditDocumentPermissionsPageFlow extends EditDocumentPageFlow {

    public static final String PAGE__DOCUMENT_PERMISSIONS = "document_permissions";
    public static final String REQUEST_PARAMETER__ROLES_WITHOUT_PERMISSIONS = "roles_no_rights";
    public static final String REQUEST_PARAMETER_PREFIX__ROLE_PERMISSION = "role_";
    public static final String REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID = "default_template";
    public static final String REQUEST_PARAMETER__ADD_ROLES = "add_roles";

    public EditDocumentPermissionsPageFlow( DocumentDomainObject document, DispatchCommand returnCommand,
                                            SaveDocumentCommand saveDocumentCommand ) {
        super( document, returnCommand, saveDocumentCommand );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        setDocumentFromRequest( document, request );
        dispatchToFirstPage( request, response );
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        setDocumentFromRequest( document, request );
    }

    private void setDocumentFromRequest( DocumentDomainObject document, HttpServletRequest request ) {
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
        document.setRolesMappedToPermissionSetIds( rolesMappedToPermissionSetIds );
        String[] rolesToGiveReadPermission = request.getParameterValues( REQUEST_PARAMETER__ROLES_WITHOUT_PERMISSIONS );
        if ( null != request.getParameter( REQUEST_PARAMETER__ADD_ROLES ) && null != rolesToGiveReadPermission ) {
            for ( int i = 0; i < rolesToGiveReadPermission.length; i++ ) {
                int roleId = Integer.parseInt( rolesToGiveReadPermission[i] );
                RoleDomainObject role = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getRoleById( roleId );
                document.setPermissionSetIdForRole( role, DocumentPermissionSetDomainObject.TYPE_ID__READ );
            }
        }

        boolean linkableByOtherUsers = null
                                       != request.getParameter( EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS );
        document.setLinkableByOtherUsers( linkableByOtherUsers );

        boolean visibleInMenusForUnauthorizedUsers = null
                                                     != request.getParameter( EditDocumentInformationPageFlow.REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS );
        document.setVisibleInMenusForUnauthorizedUsers( visibleInMenusForUnauthorizedUsers );

        if ( document instanceof TextDocumentDomainObject ) {
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document;
            String defaultTemplateIdStr = request.getParameter( REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID );
            TemplateDomainObject defaultTemplate = null;
            if ( null != defaultTemplateIdStr ) {
                try {
                    int templateId = Integer.parseInt( defaultTemplateIdStr );
                    defaultTemplate = Imcms.getServices().getTemplateMapper().getTemplateById( templateId );
                } catch ( NumberFormatException ignored ) {}
            }
            textDocument.setDefaultTemplate( defaultTemplate );
        }
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        DocumentPermissionsPage documentPermissionsPage = new DocumentPermissionsPage( getDocument() );
        documentPermissionsPage.forward( request, response );
    }

    public static class DocumentPermissionsPage {

        private DocumentDomainObject document;
        private static final String REQUEST_ATTRIBUTE__DOCUMENT_PERMISSIONS_PAGE = "documentPermissionsPage";
        private static final String URL_I15D_PAGE__DOCUMENT_PERMISSIONS = "/jsp/docadmin/document_permissions.jsp";

        public DocumentPermissionsPage( DocumentDomainObject document ) {
            this.document = document;
        }

        public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__DOCUMENT_PERMISSIONS_PAGE, this );
            UserDomainObject user = Utility.getLoggedOnUser( request );
            request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2()
                                          + URL_I15D_PAGE__DOCUMENT_PERMISSIONS ).forward( request, response );
        }

        public static DocumentPermissionsPage fromRequest( HttpServletRequest request ) {
            return (DocumentPermissionsPage)request.getAttribute( REQUEST_ATTRIBUTE__DOCUMENT_PERMISSIONS_PAGE );
        }

        public DocumentDomainObject getDocument() {
            return document;
        }
    }
}
