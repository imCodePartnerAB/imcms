package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Save data from editwindow.
 */
public class SaveInPage extends HttpServlet {

    /**
     * doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = imcref.getDocumentMapper();

        int documentId = Integer.parseInt( req.getParameter( "meta_id" ) );
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)documentMapper.getDocument( documentId );

        TemplateMapper templateMapper = imcref.getTemplateMapper();

        TemplateDomainObject requestedTemplate = getRequestedTemplate( req, templateMapper );

        TemplateGroupDomainObject requestedTemplateGroup = getRequestedTemplateGroup( req, templateMapper );

        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentMapper.getDocumentPermissionSetForUser( textDocument, user );
        TemplateGroupDomainObject[] allowedTemplateGroups = textDocumentPermissionSet.getAllowedTemplateGroups();

        boolean requestedTemplateGroupIsAllowed = null == requestedTemplateGroup;
        boolean requestedTemplateIsAllowed = null == requestedTemplate;
        for ( int i = 0; i < allowedTemplateGroups.length; i++ ) {
            TemplateGroupDomainObject allowedTemplateGroup = allowedTemplateGroups[i];
            if ( allowedTemplateGroup.equals( requestedTemplateGroup ) ) {
                requestedTemplateGroupIsAllowed = true;
            }
            if ( templateMapper.templateGroupContainsTemplate( allowedTemplateGroup, requestedTemplate ) ) {
                requestedTemplateIsAllowed = true;
            }
        }

        // Check if user has write rights
        if ( !imcref.checkDocAdminRights( documentId, user, imcode.server.IMCConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE )
             || !requestedTemplateIsAllowed
             || !requestedTemplateGroupIsAllowed ) {	// Checking to see if user may edit this
            Utility.setDefaultHtmlContentType( res );

            String output = AdminDoc.adminDoc( documentId, documentId, user, req, res );
            if ( output != null ) {
                Writer out = res.getWriter();
                out.write( output );
            }
            return;
        }

        if ( req.getParameter( "update" ) != null ) {
            Writer out = res.getWriter();

            Utility.setDefaultHtmlContentType( res );
            user.put( "flags", new Integer( 0 ) );

            if ( requestedTemplate == null ) {
                List vec = new ArrayList();
                vec.add( "#meta_id#" );
                vec.add( String.valueOf( documentId ) );
                String htmlStr = imcref.getAdminTemplate( "inPage_admin_no_template.html", user, vec );
                out.write( htmlStr );
                return;
            }

            // save textdoc
            textDocument.setTemplate( requestedTemplate );
            if ( null != requestedTemplateGroup ) {
                textDocument.setTemplateGroupId( requestedTemplateGroup.getId() );
            }
            try {
                documentMapper.saveDocument( textDocument, user );
                imcref.updateMainLog( "Text docs  [" + textDocument.getId() + "] updated by user: [" + user.getFullName()
                                   + "]" );
            } catch ( MaxCategoryDomainObjectsOfTypeExceededException e ) {
                throw new RuntimeException( e );
            }

            // return page
            String output = AdminDoc.adminDoc( documentId, documentId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;

        } else if ( req.getParameter( "preview" ) != null ) {
            if ( requestedTemplate == null ) { // If the user didn't select a template
                List vec = new ArrayList();
                vec.add( "#meta_id#" );
                vec.add( String.valueOf( documentId ) );
                Utility.setDefaultHtmlContentType( res );
                String htmlStr = imcref.getAdminTemplate( "inPage_admin_no_template.html", user, vec );
                Writer out = res.getWriter();
                out.write( htmlStr );
                return;
            }
            Object[] temp = imcref.getDemoTemplate( requestedTemplate.getId() );
            if ( temp != null ) {
                String demoTemplateName = requestedTemplate.getId() + "." + temp[0];
                // Set content-type depending on type of demo-template.
                res.setContentType( getServletContext().getMimeType( demoTemplateName ) );
                byte[] bytes = (byte[])temp[1];
                ServletOutputStream out = res.getOutputStream();
                res.setContentLength( bytes.length );
                out.write( bytes );
                return;
            } else {
                Utility.setDefaultHtmlContentType( res );
                String htmlStr = imcref.getAdminTemplate( "no_demotemplate.html", user, null );
                Writer out = res.getWriter();
                out.write( htmlStr );
                return;
            }
        } else if ( req.getParameter( "change_group" ) != null ) {
            Utility.setDefaultHtmlContentType( res );
            Writer out = res.getWriter();

            user.put( "flags", new Integer( imcode.server.IMCConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE ) );

            if ( null != requestedTemplateGroup ) {
                user.setTemplateGroup( requestedTemplateGroup );
            }

            String output = AdminDoc.adminDoc( documentId, documentId, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;

        }
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
