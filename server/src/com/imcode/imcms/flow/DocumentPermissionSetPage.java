package com.imcode.imcms.flow;

import imcode.server.document.*;
import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DocumentPermissionSetPage extends OkCancelPage {

    private static final String URL_I15D_PAGE__DOCUMENT_PERMISSION_SET = "/jsp/docadmin/document_permission_set.jsp";

    private DocumentPermissionSetDomainObject documentPermissionSet;
    private boolean forNew;

    public static final String REQUEST_PARAMETER__EDIT_PERMISSIONS = "editPermissions";
    public static final String REQUEST_PARAMETER__EDIT = "edit";
    public static final String REQUEST_PARAMETER__EDIT_DOCUMENT_INFORMATION = "editDocumentInformation";
    public static final String REQUEST_PARAMETER__EDIT_TEXTS = "editTexts";
    public static final String REQUEST_PARAMETER__EDIT_IMAGES = "editImages";
    public static final String REQUEST_PARAMETER__EDIT_INCLUDES = "editIncludes";
    public static final String REQUEST_PARAMETER__EDIT_MENUS = "editMenus";
    public static final String REQUEST_PARAMETER__ALLOWED_DOCUMENT_TYPE_IDS = "allowedDocumentTypeIds";
    public static final String REQUEST_PARAMETER__ALLOWED_TEMPLATE_GROUP_IDS = "allowedTemplateGroupIds";
    public static final String REQUEST_PARAMETER__EDIT_TEMPLATES = "editTemplates";
    public static final String REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID = "defaultTemplateId";

    public DocumentPermissionSetPage( DocumentPermissionSetDomainObject documentPermissionSet, boolean forNew,
                                      DispatchCommand okDispatchCommand, DispatchCommand cancelDispatchCommand ) {
        super(okDispatchCommand, cancelDispatchCommand);
        this.documentPermissionSet = documentPermissionSet;
        this.forNew = forNew;
    }

    public DocumentPermissionSetDomainObject getDocumentPermissionSet() {
        return documentPermissionSet;
    }

    protected void updateFromRequest( HttpServletRequest request ) {
        documentPermissionSet.setEditPermissions( null != request.getParameter( REQUEST_PARAMETER__EDIT_PERMISSIONS ));
        documentPermissionSet.setEdit( null != request.getParameter( REQUEST_PARAMETER__EDIT ));
        documentPermissionSet.setEditDocumentInformation( null != request.getParameter( REQUEST_PARAMETER__EDIT_DOCUMENT_INFORMATION ));
        if (documentPermissionSet instanceof TextDocumentPermissionSetDomainObject) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet ;
            textDocumentPermissionSet.setEditTexts( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_TEXTS ));
            textDocumentPermissionSet.setEditImages( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_IMAGES ));
            textDocumentPermissionSet.setEditIncludes( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_INCLUDES) );
            textDocumentPermissionSet.setEditMenus( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_MENUS ) );
            textDocumentPermissionSet.setEditTemplates( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_TEMPLATES ) );
            textDocumentPermissionSet.setAllowedDocumentTypeIds( Utility.getParameterInts( request, REQUEST_PARAMETER__ALLOWED_DOCUMENT_TYPE_IDS ) );
            int[] allowedTemplateGroupIds = Utility.getParameterInts( request, REQUEST_PARAMETER__ALLOWED_TEMPLATE_GROUP_IDS ) ;
            TemplateGroupDomainObject[] allowedTemplateGroups = new TemplateGroupDomainObject[allowedTemplateGroupIds.length];
            TemplateMapper templateMapper = Imcms.getServices().getTemplateMapper() ;
            for ( int i = 0; i < allowedTemplateGroupIds.length; i++ ) {
                allowedTemplateGroups[i] = templateMapper.getTemplateGroupById( allowedTemplateGroupIds[i] ) ;
            }
            textDocumentPermissionSet.setAllowedTemplateGroups( allowedTemplateGroups );
            try {
                int defaultTemplateId = Integer.parseInt(request.getParameter( REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID )) ;
                TemplateDomainObject defaultTemplate = templateMapper.getTemplateById( defaultTemplateId );
                textDocumentPermissionSet.setDefaultTemplate(defaultTemplate) ;
            } catch( NumberFormatException ignored ) {}
        }
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        putInSessionAndForwardToPath( EditDocumentPageFlow.URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2()
                                      + URL_I15D_PAGE__DOCUMENT_PERMISSION_SET, request, response );
    }

    public boolean isForNew() {
        return forNew;
    }
}
