package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.poll.PollHandlingSystem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Save textdocument in a document.
 */
public final class SaveText extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        req.setCharacterEncoding( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();

        // Check if user has permission to be here
        ImcmsServices imcref = Imcms.getServices();
        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !imcref.checkDocAdminRights( meta_id, user, imcode.server.ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        // get text_no
        int txt_no = Integer.parseInt( req.getParameter( "txt_no" ) );

        // get textdocument
        String text_string = req.getParameter( "text" );

        int text_format = Integer.parseInt( req.getParameter( "format_type" ) );

        String text_type = req.getParameter( "txt_type" ); // ex. pollrequest-1
        if ( text_type == null ) {
            text_type = "";
        }

        TextDomainObject text = new TextDomainObject( text_string, text_format );

        user.put( "flags", new Integer( imcode.server.ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS ) );

        if ( req.getParameter( "ok" ) != null ) {
            DocumentMapper documentMapper = imcref.getDocumentMapper();
            TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument(meta_id);

            saveText( documentMapper, text, document, txt_no, text_type, imcref, meta_id, user );
        }

        String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
        if ( output != null ) {
            out.write( output );
        }
    }

    private void saveText( DocumentMapper documentMapper, TextDomainObject text, TextDocumentDomainObject document,
                           int txt_no, String text_type, ImcmsServices imcref, int meta_id,
                           UserDomainObject user ) {
        document.setText( txt_no, text );

        documentMapper.saveDocument( document, user );

        if ( !"".equals( text_type ) ) {

            if ( text_type.startsWith( "poll" ) ) {
                PollHandlingSystem poll = imcref.getPollHandlingSystem();
                poll.savePollparameter( text_type, document.getId(), txt_no, text.getText() );
            }
        }
        imcref.updateMainLog( "Text " + txt_no + " in [" + meta_id + "] modified by user: [" + user.getFullName() + "]");
    }
}
