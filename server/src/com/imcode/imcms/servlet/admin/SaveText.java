package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.server.document.TextDocumentDomainObject.Text;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TextDocumentDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Save text in a document.
 */
public class SaveText extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        // Check if user has permission to be here
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !imcref.checkDocAdminRights( meta_id, user, imcode.server.IMCConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        // get text_no
        int txt_no = Integer.parseInt( req.getParameter( "txt_no" ) );

        // get text
        String text_string = req.getParameter( "text" );

        int text_format = Integer.parseInt( req.getParameter( "format_type" ) );

        String text_type = req.getParameter( "txt_type" ); // ex. pollrequest-1
        if ( text_type == null ) {
            text_type = "";
        }

        TextDocumentDomainObject.Text text = new TextDocumentDomainObject.Text( text_string, text_format );

        user.put( "flags", new Integer( imcode.server.IMCConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS ) );

        if ( req.getParameter( "ok" ) != null ) {
            DocumentMapper documentMapper = imcref.getDocumentMapper();
            documentMapper.saveText( text, documentMapper.getDocument(meta_id), txt_no, user, text_type );
        }

        String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
        if ( output != null ) {
            out.write( output );
        }
    }
}
