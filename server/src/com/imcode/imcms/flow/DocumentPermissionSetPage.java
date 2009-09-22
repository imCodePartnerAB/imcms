package com.imcode.imcms.flow;

import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.IntegerSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

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
    private DocumentDomainObject document;

    public DocumentPermissionSetPage(DocumentDomainObject document,
                                     DocumentPermissionSetDomainObject documentPermissionSet, boolean forNew,
                                     DispatchCommand okDispatchCommand, DispatchCommand cancelDispatchCommand) {
        super(okDispatchCommand, cancelDispatchCommand);
        this.document = document ;
        this.documentPermissionSet = documentPermissionSet;
        this.forNew = forNew;
    }

    public DocumentPermissionSetDomainObject getDocumentPermissionSet() {
        return documentPermissionSet;
    }

    public DocumentDomainObject getDocument() {
        return document;
    }

    protected void updateFromRequest( HttpServletRequest request ) {
        documentPermissionSet.setEditPermissions( null != request.getParameter( REQUEST_PARAMETER__EDIT_PERMISSIONS ));
        documentPermissionSet.setEditDocumentInformation( null != request.getParameter( REQUEST_PARAMETER__EDIT_DOCUMENT_INFORMATION ));

        if (document instanceof TextDocumentDomainObject) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet ;
            textDocumentPermissionSet.setEditTexts( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_TEXTS ));
            textDocumentPermissionSet.setEditImages( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_IMAGES ));
            textDocumentPermissionSet.setEditIncludes( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_INCLUDES) );
            textDocumentPermissionSet.setEditMenus( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_MENUS ) );
            textDocumentPermissionSet.setEditTemplates( Utility.parameterIsSet( request, REQUEST_PARAMETER__EDIT_TEMPLATES ) );
            textDocumentPermissionSet.setAllowedDocumentTypeIds( new IntegerSet(Utility.getParameterInts(request, REQUEST_PARAMETER__ALLOWED_DOCUMENT_TYPE_IDS)) );
            int[] allowedTemplateGroupIds = Utility.getParameterInts( request, REQUEST_PARAMETER__ALLOWED_TEMPLATE_GROUP_IDS ) ;
            textDocumentPermissionSet.setAllowedTemplateGroupIds( new IntegerSet(allowedTemplateGroupIds));
            String defaultTemplateIdParameter = request.getParameter(REQUEST_PARAMETER__DEFAULT_TEMPLATE_ID);
            String defaultTemplateId = StringUtils.isNotBlank(defaultTemplateIdParameter) ? defaultTemplateIdParameter : null;
            TextDocumentDomainObject textDocument = (TextDocumentDomainObject) document;
            if (DocumentPermissionSetTypeDomainObject.RESTRICTED_1.equals(textDocumentPermissionSet.getType())) {
                textDocument.setDefaultTemplateIdForRestricted1( defaultTemplateId );
            } else if (DocumentPermissionSetTypeDomainObject.RESTRICTED_2.equals(textDocumentPermissionSet.getType())) {
                textDocument.setDefaultTemplateIdForRestricted2( defaultTemplateId );
            }
        } else {
            documentPermissionSet.setEdit( null != request.getParameter( REQUEST_PARAMETER__EDIT ));
        }
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        return EditDocumentPageFlow.URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2()
               + URL_I15D_PAGE__DOCUMENT_PERMISSION_SET;
    }

    public boolean isForNew() {
        return forNew;
    }

}
