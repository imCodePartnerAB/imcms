package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class SaveInclude extends HttpServlet {

    private final static DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS " );

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, java.io.IOException {
        ImcmsServices imcref = Imcms.getServices();

        Utility.setDefaultHtmlContentType( res );

        Writer out = res.getWriter();

        String meta_id_str = req.getParameter( "meta_id" );
        int meta_id = Integer.parseInt( meta_id_str );

        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Check if user has permission to edit includes for this document
        if ( !imcref.checkDocAdminRights( meta_id, user, imcode.server.ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_INCLUDES ) ) {	// Checking to see if user may edit this
            sendPermissionDenied( imcref, out, meta_id, user );
            return;
        }

        String included_meta_id = req.getParameter( "include_meta_id" );
        String include_id = req.getParameter( "include_id" );

        if ( included_meta_id != null && include_id != null ) {
            included_meta_id = included_meta_id.trim();
            include_id = include_id.trim();
            if ( "".equals( included_meta_id ) ) {
                DocumentMapper.sprocDeleteInclude( imcref, meta_id, Integer.parseInt( include_id ) );
                imcref.updateMainLog( dateFormat.format( new java.util.Date() ) + "Include nr [" + include_id + "] on ["
                              + meta_id_str
                              + "] removed by user: ["
                              + user.getFullName()
                              + "]" );
            } else {
                try {
                    int included_meta_id_int = Integer.parseInt( included_meta_id );

                    String[] docTypeStrArr = imcref.sqlProcedure( "GetDocType", new String[]{included_meta_id} );
                    int docType = Integer.parseInt( docTypeStrArr[0] );
                    if ( null == docTypeStrArr || 0 == docTypeStrArr.length
                         || DocumentDomainObject.DOCTYPE_TEXT != docType ) {
                        sendBadId( imcref, out, meta_id, user );
                        return;
                    }

                    DocumentMapper documentMapper = imcref.getDocumentMapper();
                    // Make sure the user has permission to share the included document
                    DocumentDomainObject includedDocument = documentMapper.getDocument( included_meta_id_int );
                    if ( documentMapper.userHasPermissionToAddDocumentToAnyMenu( user, includedDocument ) ) {
                        DocumentMapper.sprocSetInclude( imcref, meta_id, Integer.parseInt( include_id ), included_meta_id_int );
                        imcref.updateMainLog( dateFormat.format( new java.util.Date() ) + "Include nr [" + include_id
                                      + "] on ["
                                      + meta_id_str
                                      + "] changed to ["
                                      + included_meta_id
                                      + "]  by user: ["
                                      + user.getFullName()
                                      + "]" );
                    } else {
                        sendPermissionDenied( imcref, out, meta_id, user );
                        return;
                    }
                } catch ( NumberFormatException ignored ) {
                    sendBadId( imcref, out, meta_id, user );
                    return;
                }
            }
        }

        String tempstring = AdminDoc.adminDoc( meta_id, user, req, res );
        if ( tempstring != null ) {
            out.write( tempstring );
        }
        return;
    }

    private void sendPermissionDenied( ImcmsServices imcref, Writer out, int meta_id, UserDomainObject user ) throws IOException {
        Vector vec = new Vector( 2 );
        vec.add( "#meta_id#" );
        vec.add( String.valueOf( meta_id ) );
        String htmlStr = imcref.getAdminTemplate( "include_permission_denied.html", user, vec );
        out.write( htmlStr );
    }

    private void sendBadId( ImcmsServices imcref, Writer out, int meta_id, UserDomainObject user ) throws IOException {
        Vector vec = new Vector( 2 );
        vec.add( "#meta_id#" );
        vec.add( String.valueOf( meta_id ) );
        String htmlStr = imcref.getAdminTemplate( "include_bad_id.html", user, vec );
        out.write( htmlStr );
    }

}
