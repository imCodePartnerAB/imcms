package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import com.imcode.imcms.servlet.GetDoc;
import imcode.server.*;
import imcode.server.document.*;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.Html;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AdminDoc extends HttpServlet {

    private static final String PARAMETER__META_ID = "meta_id";
    public static final String PARAMETER__DISPATCH_FLAGS = "flags";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {
        doPost( req, res );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        int metaId = Integer.parseInt( req.getParameter( PARAMETER__META_ID ) );
        int flags = Integer.parseInt( (String)ObjectUtils.defaultIfNull( req.getParameter( PARAMETER__DISPATCH_FLAGS ), "0" ) );

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( metaId );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.canEdit( document )) {
            flags = 0;
        }

        PageFlow pageFlow = createFlow( document, flags, user );

        if ( null != pageFlow && user.canEdit( document )) {
            pageFlow.dispatch( req, res );
        } else {

            Utility.setDefaultHtmlContentType( res );
            int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );

            String tempstring = AdminDoc.adminDoc( meta_id, user, req, res );

            if ( tempstring != null ) {
                byte[] tempbytes = tempstring.getBytes( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
                res.setContentLength( tempbytes.length );
                res.getOutputStream().write( tempbytes );
            }
        }
    }

    private PageFlow createFlow( DocumentDomainObject document, int flags, UserDomainObject user ) {
        RedirectToDocumentCommand returnCommand = new RedirectToDocumentCommand( document );
        DocumentMapper.SaveEditedDocumentCommand saveDocumentCommand = new DocumentMapper.SaveEditedDocumentCommand();

        PageFlow pageFlow = null;
        if ( ImcmsConstants.DISPATCH_FLAG__DOCINFO_PAGE == flags && user.canEditDocumentInformationFor( document ) ) {
            pageFlow = new EditDocumentInformationPageFlow( document, returnCommand, saveDocumentCommand );
        } else if ( ImcmsConstants.DISPATCH_FLAG__DOCUMENT_PERMISSIONS_PAGE == flags && user.canEditPermissionsFor( document ) ) {
            pageFlow = new EditDocumentPermissionsPageFlow( document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof BrowserDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_BROWSER_DOCUMENT == flags ) {
            pageFlow = new EditBrowserDocumentPageFlow( (BrowserDocumentDomainObject)document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof HtmlDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_HTML_DOCUMENT == flags ) {
            pageFlow = new EditHtmlDocumentPageFlow( (HtmlDocumentDomainObject)document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof UrlDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT == flags ) {
            pageFlow = new EditUrlDocumentPageFlow( (UrlDocumentDomainObject)document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof FileDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_FILE_DOCUMENT == flags ) {
            pageFlow = new EditFileDocumentPageFlow( (FileDocumentDomainObject)document, getServletContext(), returnCommand, saveDocumentCommand, null );

        }
        return pageFlow;
    }

    public static String adminDoc( int meta_id, UserDomainObject user, HttpServletRequest req,
                                   HttpServletResponse res ) throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();

        Stack history = (Stack)user.get( "history" );
        if ( history == null ) {
            history = new Stack();
            user.put( "history", history );
        }
        Integer meta_int = new Integer( meta_id );
        if ( history.empty() || !history.peek().equals( meta_int ) ) {
            history.push( meta_int );
        }

        DocumentDomainObject document = imcref.getDocumentMapper().getDocument( meta_id );
        if ( null == document ) {
            return GetDoc.getDocumentDoesNotExistPage( res, user );
        }

        int doc_type = document.getDocumentTypeId();

        Integer userflags = (Integer)user.remove( PARAMETER__DISPATCH_FLAGS );		// Get the flags from the user-object
        int flags = userflags == null ? 0 : userflags.intValue();	// Are there flags? Set to 0 if not.


        try {
            flags = Integer.parseInt( req.getParameter( PARAMETER__DISPATCH_FLAGS ) );	// Check if we have a "flags" in the request too. In that case it takes precedence.
        } catch ( NumberFormatException ex ) {
            if ( flags == 0 ) {
                if ( doc_type != 1 && doc_type != 2 ) {
                    List vec = new ArrayList( 4 );
                    vec.add( "#adminMode#" );
                    vec.add( Html.getAdminButtons( user, document, req, res ) );
                    vec.add( "#doc_type_description#" );
                    vec.add( imcref.getAdminTemplate( "adminbuttons/adminbuttons" + doc_type + "_description.html", user, null ) );
                    return imcref.getAdminTemplate( "docinfo.html", user, vec );
                }
            }
        }

        if ( !imcref.checkDocAdminRights( meta_id, user, flags ) ) {
            return GetDoc.getDoc( meta_id, req, res );
        }

        // Lets detect which view the admin wants
        switch ( doc_type ) {

            case DocumentDomainObject.DOCTYPE_ID_CONFERENCE:
            case DocumentDomainObject.DOCTYPE_ID_BILLBOARD:
            case DocumentDomainObject.DOCTYPE_ID_CHAT:
                GetDoc.redirectToExternalDocumentTypeWithAction( document, req, res, "change" );
                return null;

            default:
                DocumentRequest documentRequest = new DocumentRequest( imcref, user, document, null, req, res );
                ParserParameters parserParameters = new ParserParameters( documentRequest );
                parserParameters.setFlags( flags );
                String editingMenuIndexStr = req.getParameter( "editmenu" );
                if ( null != editingMenuIndexStr ) {
                    parserParameters.setEditingMenuIndex( Integer.valueOf( editingMenuIndexStr ) );
                }
                return imcref.parsePage( parserParameters );

        }
    }

    private static class RedirectToDocumentCommand implements DispatchCommand {

        private final DocumentDomainObject document;

        RedirectToDocumentCommand( DocumentDomainObject document ) {
            this.document = document;
        }

        public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException {
            response.sendRedirect( "AdminDoc?meta_id=" + document.getId() );
        }
    }

}
