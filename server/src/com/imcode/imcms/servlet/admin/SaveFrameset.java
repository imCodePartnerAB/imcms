package com.imcode.imcms.servlet.admin;

import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Save a framesetdocument.
 * Shows a change_meta.html which calls SaveMeta
 */
public class SaveFrameset extends HttpServlet {

	/**
	doPost()
	*/
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
		int meta_id ;

		res.setContentType( "text/html" );
		Writer out = res.getWriter( );

		// get meta_id
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

        String frame_set = req.getParameter("frame_set");

        UserDomainObject user = Utility.getLoggedOnUser( req );

		// Check if user has write rights
		if ( !imcref.checkDocAdminRights(meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
			if ( output != null ) {
				out.write(output) ;
			}
			return ;
		}

        if (req.getParameter("ok") != null) {	//User pressed ok on form in change_frameset_doc.html
            imcref.saveFrameset(meta_id, user, frame_set);
            DocumentMapper documentMapper = imcref.getDocumentMapper() ;
            documentMapper.touchDocument( documentMapper.getDocument( meta_id ) );

            String output = AdminDoc.adminDoc(meta_id, meta_id, user, req, res);
            if (output != null) {
                out.write(output);
            }
            return;
        }
	}
}

