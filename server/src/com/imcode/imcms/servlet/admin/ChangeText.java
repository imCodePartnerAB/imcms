package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Edit textdocument in a document.
 */

public class ChangeText extends HttpServlet {
    private static final String JSP__CHANGE_TEXT = "change_text.jsp";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices service = Imcms.getServices();
        Utility.setDefaultHtmlContentType( res );

        Writer out = res.getWriter();
        int meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        // Check if user has admin rights to edit textfield
        if ( !service.checkDocAdminRights( meta_id, user, 65536 ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        forward(req, res, user);

    }

    public void forward(HttpServletRequest request, HttpServletResponse response, UserDomainObject user) throws IOException, ServletException {
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP__CHANGE_TEXT;
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

}
