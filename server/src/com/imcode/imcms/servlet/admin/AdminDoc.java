package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.GetDoc;
import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.BrowserDocumentDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Html;
import imcode.util.Utility;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

            adminDoc( meta_id, user, req, res, getServletContext() );
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

    public static void adminDoc(int meta_id, UserDomainObject user, HttpServletRequest req,
                                HttpServletResponse res, ServletContext servletContext) throws IOException, ServletException {
        final ImcmsServices imcref = Imcms.getServices();

        HttpSession session = req.getSession();
        Stack history = (Stack)session.getAttribute( "history" );
        if ( history == null ) {
            history = new Stack();
            session.setAttribute( "history", history );
        }
        Integer meta_int = new Integer( meta_id );
        if ( history.empty() || !history.peek().equals( meta_int ) ) {
            history.push( meta_int );
        }

        DocumentDomainObject document = imcref.getDocumentMapper().getDocument( meta_id );
        if ( null == document ) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return ;
        }

        int doc_type = document.getDocumentTypeId();

        Integer userflags = (Integer)session.getAttribute( PARAMETER__DISPATCH_FLAGS );		// Get the flags from the user-object
        session.removeAttribute(PARAMETER__DISPATCH_FLAGS);
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
                    Utility.setDefaultHtmlContentType(res);
                    res.getWriter().write(imcref.getAdminTemplate( "docinfo.html", user, vec ));
                    return ;
                }
            }
        }

        if ( !user.canEdit( document ) ) {
            GetDoc.viewDoc( ""+meta_id, req, res );
            return ;
        }

        DocumentRequest documentRequest = new DocumentRequest( imcref, user, document, null, req, res );
        final ParserParameters parserParameters = new ParserParameters( documentRequest );
        parserParameters.setFlags( flags );
        imcref.parsePage( parserParameters, res.getWriter());
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
