
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;

/**
 * Save an urldocument.
 * Shows a change_meta.html which calls SaveMeta
 */
public class SaveUrlDoc extends HttpServlet {

    /**
     * doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        imcode.server.user.UserDomainObject user;
        String url_ref = "";
        String values[];
        int meta_id;

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        // get meta_id
        meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );

        // get urlref
        values = req.getParameterValues( "url_ref" );
        if ( values != null )
            url_ref = values[0];

        String target = req.getParameter( "target" );
        if ( "_other".equals( target ) ) {
            target = req.getParameter( "frame_name" );
        }

        // Get the session
        HttpSession session = req.getSession( true );

        // Does the session indicate this user already logged in?
        Object done = session.getAttribute( "logon.isDone" );  // marker object
        user = (imcode.server.user.UserDomainObject)done;

        if ( done == null ) {
            // No logon.isDone means he hasn't logged in.
            // Save the request URL as the true target and redirect to the login page.
            String scheme = req.getScheme();
            String serverName = req.getServerName();
            int p = req.getServerPort();
            String port = ( p == 80 ) ? "" : ":" + p;
            res.sendRedirect( scheme + "://" + serverName + port + start_url );
            return;
        }
        // Check if user has write rights
        if ( !imcref.checkDocAdminRights( meta_id, user, 65536 ) ) {
            log( "User " + user.getUserId() + " was denied access to meta_id " + meta_id + " and was sent to " + start_url );
            String scheme = req.getScheme();
            String serverName = req.getServerName();
            int p = req.getServerPort();
            String port = ( p == 80 ) ? "" : ":" + p;
            res.sendRedirect( scheme + "://" + serverName + port + start_url );
            return;
        }

        // FIXME: Move to SProc
        imcref.sqlUpdateQuery( "update url_docs set url_ref = ? where meta_id = ?", new String[]{url_ref, "" + meta_id} );
        imcref.sqlUpdateQuery( "update meta set target = ? where meta_id = ?", new String[]{target, "" + meta_id} );

        imcref.touchDocument( meta_id );

        String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
        if ( output != null ) {
            out.write( output );
        }
        return;
    }
}
