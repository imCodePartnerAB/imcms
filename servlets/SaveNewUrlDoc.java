
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;

/**
 * Save a new urldocument.
 */
public class SaveNewUrlDoc extends HttpServlet {

    /**
     * doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        imcode.server.user.UserDomainObject user;

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        String meta_id = req.getParameter( "meta_id" );
        String new_meta_id = req.getParameter( "new_meta_id" );
        String url_ref = req.getParameter( "url_ref" );

        HttpSession session = req.getSession( true );
        user = (UserDomainObject)session.getAttribute( "logon.isDone" );
        if ( user == null ) {
            // Save the request URL as the true target and redirect to the login page.
            String scheme = req.getScheme();
            String serverName = req.getServerName();
            int p = req.getServerPort();
            String port = ( p == 80 ) ? "" : ":" + p;
            res.sendRedirect( scheme + "://" + serverName + port + start_url );
            return;
        }

        String target = req.getParameter( "target" );
        if ( "_other".equals( target ) ) {
            target = req.getParameter( "frame_name" );
        }

        if ( req.getParameter( "cancel" ) != null ) {
            String output = AdminDoc.adminDoc( Integer.parseInt( meta_id ), Integer.parseInt( meta_id ), user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }

        DocumentMapper.insertIntoUrlDocs(imcref, Integer.parseInt(new_meta_id), url_ref, target);

        String output = AdminDoc.adminDoc( Integer.parseInt( new_meta_id ), Integer.parseInt( new_meta_id ), user, req, res );
        if ( output != null ) {
            out.write( output );
        }
    }

}
