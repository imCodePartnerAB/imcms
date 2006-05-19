package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Save data from editwindow.
 */
public class SaveInPage extends HttpServlet {

    /**
     * doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices services = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = services.getDocumentMapper();

        int documentId = Integer.parseInt( req.getParameter( "meta_id" ) );
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)documentMapper.getDocument( documentId );

        TemplateMapper templateMapper = services.getTemplateMapper();

        TemplateDomainObject requestedTemplate = getRequestedTemplate( req, templateMapper );

        TemplateGroupDomainObject requestedTemplateGroup = getRequestedTemplateGroup( req, templateMapper );

        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( textDocument );
        Set allowedTemplateGroupIds = textDocumentPermissionSet.getAllowedTemplateGroupIds();

        // Check if user has write rights
        if ( !textDocumentPermissionSet.getEditTemplates()
             || null != requestedTemplateGroup && !allowedTemplateGroupIds.contains(new Integer(requestedTemplateGroup.getId()))) {
            errorNoPermission(documentId, user, req, res);
            return;
        }

        if ( req.getParameter( "update" ) != null ) {
            Writer out = res.getWriter();

            req.getSession().setAttribute( "flags", new Integer( 0 ) );

            if ( requestedTemplate == null ) {
                errorNoTemplateSelected(documentId, services, user, out);
                return;
            } else if (!templateMapper.templateGroupContains(requestedTemplateGroup, requestedTemplate)) {
                errorNoPermission(documentId, user, req, res);
                return ;
            }

            // save textdoc
            textDocument.setTemplateId( requestedTemplate.getId() );
            if ( null != requestedTemplateGroup ) {
                textDocument.setTemplateGroupId( requestedTemplateGroup.getId() );
            }
            try {
                documentMapper.saveDocument( textDocument, user );
                services.updateMainLog( "Text docs  [" + textDocument.getId() + "] updated by user: [" + user.getFullName()
                                        + "]" );
            } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
                throw new UnhandledException(e);
            } catch ( NoPermissionToEditDocumentException e ) {
                throw new ShouldHaveCheckedPermissionsEarlierException(e);
            } catch ( NoPermissionToAddDocumentToMenuException e ) {
                throw new ConcurrentDocumentModificationException(e);
            }

            Utility.setDefaultHtmlContentType( res );
            AdminDoc.adminDoc( documentId, user, req, res, getServletContext() );

        } else if ( req.getParameter( "preview" ) != null ) {
            if ( requestedTemplate == null ) { // If the user didn't select a template
                Writer out = res.getWriter();
                Utility.setDefaultHtmlContentType( res );
                errorNoTemplateSelected(documentId, services, user, out);
                return;
            }
            Object[] temp = services.getTemplateMapper().getDemoTemplate( requestedTemplate.getId() );
            if ( temp != null ) {
                String demoTemplateName = requestedTemplate.getId() + "." + temp[0];
                // Set content-type depending on type of demo-template.
                res.setContentType( getServletContext().getMimeType( demoTemplateName ) );
                byte[] bytes = (byte[])temp[1];
                ServletOutputStream out = res.getOutputStream();
                res.setContentLength( bytes.length );
                out.write( bytes );
            } else {
                Utility.setDefaultHtmlContentType( res );
                String htmlStr = services.getAdminTemplate( "no_demotemplate.html", user, null );
                Writer out = res.getWriter();
                out.write( htmlStr );
            }
        } else if ( req.getParameter( "change_group" ) != null ) {
            Utility.setDefaultHtmlContentType( res );

            req.getSession().setAttribute( "flags", new Integer( ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE ) );

            if ( null != requestedTemplateGroup ) {
                user.setTemplateGroup( requestedTemplateGroup );
            }

            AdminDoc.adminDoc( documentId, user, req, res, getServletContext() );

        }
    }

    private void errorNoPermission(int documentId, UserDomainObject user, HttpServletRequest req,
                                   HttpServletResponse res
    ) throws IOException, ServletException {
        Utility.setDefaultHtmlContentType( res );

        AdminDoc.adminDoc( documentId, user, req, res, getServletContext() );
    }

    private void errorNoTemplateSelected(int documentId, ImcmsServices services, UserDomainObject user,
                                         Writer out) throws IOException {
        List vec = new ArrayList();
        vec.add( "#meta_id#" );
        vec.add( String.valueOf( documentId ) );
        String htmlStr = services.getAdminTemplate( "inPage_admin_no_template.html", user, vec );
        out.write( htmlStr );
    }

    private TemplateGroupDomainObject getRequestedTemplateGroup( HttpServletRequest req, TemplateMapper templateMapper ) {
        try {
            return templateMapper.getTemplateGroupById( Integer.parseInt( req.getParameter( "group" ) ) );
        } catch ( NumberFormatException nfe ) {
            return null;
        }
    }

    private TemplateDomainObject getRequestedTemplate( HttpServletRequest req, TemplateMapper templateMapper ) {
        try {
            return templateMapper.getTemplateById( Integer.parseInt( req.getParameter( "template" ) ) );
        } catch ( NumberFormatException nfe ) {
            return null;
        }
    }

}
