package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import com.imcode.imcms.servlet.GetDoc;
import imcode.server.*;
import imcode.server.document.*;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
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
        if ( !documentMapper.userHasMoreThanReadPermissionOnDocument( user, document ) ) {
            flags = 0;
        }

        HttpPageFlow httpPageFlow = createFlow( document, flags );

        if ( null != httpPageFlow ) {
            httpPageFlow.dispatch( req, res );
        } else {
            ImcmsServices imcref = Imcms.getServices();

            // Find the start-page
            int start_doc = imcref.getSystemData().getStartDocument();

            Utility.setDefaultHtmlContentType( res );
            int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
            int parent_meta_id;
            String parent_meta_str = req.getParameter( "parent_meta_id" );
            if ( parent_meta_str != null ) {
                parent_meta_id = Integer.parseInt( parent_meta_str );
            } else {
                parent_meta_id = start_doc;
            }

            String tempstring = AdminDoc.adminDoc( meta_id, parent_meta_id, user, req, res );

            if ( tempstring != null ) {
                byte[] tempbytes = tempstring.getBytes( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
                res.setContentLength( tempbytes.length );
                res.getOutputStream().write( tempbytes );
            }

        }
    }

    private HttpPageFlow createFlow( DocumentDomainObject document, int flags ) {
        RedirectToDocumentCommand returnCommand = new RedirectToDocumentCommand( document );
        DocumentMapper.SaveEditedDocumentCommand saveDocumentCommand = new DocumentMapper.SaveEditedDocumentCommand();

        HttpPageFlow httpPageFlow = null;
        if ( ImcmsConstants.DISPATCH_FLAG__DOCINFO_PAGE == flags ) {
            httpPageFlow = new EditDocumentInformationPageFlow( document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof BrowserDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_BROWSER_DOCUMENT == flags ) {
            httpPageFlow = new EditBrowserDocumentPageFlow( (BrowserDocumentDomainObject)document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof HtmlDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_HTML_DOCUMENT == flags ) {
            httpPageFlow = new EditHtmlDocumentPageFlow( (HtmlDocumentDomainObject)document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof UrlDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT == flags ) {
            httpPageFlow = new EditUrlDocumentPageFlow( (UrlDocumentDomainObject)document, returnCommand, saveDocumentCommand );
        } else if ( document instanceof FileDocumentDomainObject
                    && ImcmsConstants.DISPATCH_FLAG__EDIT_FILE_DOCUMENT == flags ) {
            httpPageFlow = new EditFileDocumentPageFlow( (FileDocumentDomainObject)document, getServletContext(), returnCommand, saveDocumentCommand, null );

        }
        return httpPageFlow;
    }

    public static String adminDoc( int meta_id, int parent_meta_id, UserDomainObject user, HttpServletRequest req,
                                   HttpServletResponse res ) throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();

        String htmlStr;
        String lang_prefix = user.getLanguageIso639_2();

        Stack history = (Stack)user.get( "history" );
        if ( history == null ) {
            history = new Stack();
            user.put( "history", history );
        }
        Integer meta_int = new Integer( meta_id );
        if ( history.empty() || !history.peek().equals( meta_int ) ) {
            history.push( meta_int );
        }

        int doc_type = imcref.getDocType( meta_id );

        Integer userflags = (Integer)user.remove( PARAMETER__DISPATCH_FLAGS );		// Get the flags from the user-object
        int flags = userflags == null ? 0 : userflags.intValue();	// Are there flags? Set to 0 if not.

        DocumentDomainObject document = imcref.getDocumentMapper().getDocument( meta_id );

        try {
            flags = Integer.parseInt( req.getParameter( PARAMETER__DISPATCH_FLAGS ) );	// Check if we have a "flags" in the request too. In that case it takes precedence.
        } catch ( NumberFormatException ex ) {
            if ( flags == 0 ) {
                if ( doc_type != 1 && doc_type != 2 ) {
                    List vec = new ArrayList( 4 );
                    vec.add( "#adminMode#" );
                    vec.add( imcref.getAdminButtons( user, document ) );
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
        if ( ( flags & 4 ) != 0 ) { // User rights
            htmlStr = imcode.util.MetaDataParser.parseMetaPermission( String.valueOf( meta_id ), String.valueOf( meta_id ), user, "docinfo/change_meta_rights.html" );
            return htmlStr;
        } else {

            switch ( doc_type ) {

                default:
                    DocumentRequest documentRequest = new DocumentRequest( imcref, user, document, null, req, res );
                    ParserParameters parserParameters = new ParserParameters();
                    parserParameters.setDocumentRequest( documentRequest );
                    parserParameters.setFlags( flags );
                    String editingMenuIndexStr = req.getParameter( "editmenu" );
                    if ( null != editingMenuIndexStr ) {
                        parserParameters.setEditingMenuIndex( Integer.valueOf( editingMenuIndexStr ) );
                    }
                    return imcref.parsePage( parserParameters );

                case DocumentDomainObject.DOCTYPE_CONFERENCE:
                case DocumentDomainObject.DOCTYPE_BILLBOARD:
                case DocumentDomainObject.DOCTYPE_CHAT:
                    GetDoc.redirectToExternalDocumentTypeWithAction( document, res, "change" );
                    return null;

            }
        }
    }

    private static class RedirectToDocumentCommand implements DispatchCommand {

        private final DocumentDomainObject document;

        public RedirectToDocumentCommand( DocumentDomainObject document ) {
            this.document = document;
        }

        public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException {
            response.sendRedirect( "AdminDoc?meta_id=" + document.getId() );
        }
    }

}
