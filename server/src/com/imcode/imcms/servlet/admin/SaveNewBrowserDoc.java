package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Save a new browserdocument.
 */
public class SaveNewBrowserDoc extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String htmlStr = "";

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        // get meta_id
        int parent_meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );

        // get new_meta_id
        int meta_id = Integer.parseInt( req.getParameter( "new_meta_id" ) );

        // Check if user has write rights
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !imcref.checkDocAdminRights( meta_id, user ) ) {
            String start_url = imcref.getStartUrl();
            log( "User " + user.getUserId() + " was denied access to meta_id " + meta_id + " and was sent to " + start_url );
            String scheme = req.getScheme();
            String serverName = req.getServerName();
            int p = req.getServerPort();
            String port = ( p == 80 ) ? "" : ":" + p;
            res.sendRedirect( scheme + "://" + serverName + port + start_url );
            return;
        }

        if ( req.getParameter( "cancel" ) != null ) {
            String sqlStr = "delete from browser_docs where to_meta_id = 0 and meta_id = ?";
            imcref.sqlUpdateQuery( sqlStr, new String[]{"" + meta_id} );
            String output = AdminDoc.adminDoc( parent_meta_id, parent_meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;

        } else if ( req.getParameter( "ok" ) != null ) {
            Enumeration enum = req.getParameterNames();
            while ( enum.hasMoreElements() ) {
                String param = (String)enum.nextElement();
                if ( param.indexOf( "bid" ) == 0 ) {
                    String bid = param.substring( 3 );
                    String to = req.getParameter( param );
                    try {
                        int t = Integer.parseInt( to );
                        imcref.sqlUpdateQuery( "update browser_docs set to_meta_id = ? where meta_id = ? and browser_id = ?", new String[]{"" + t, "" + meta_id, bid} );
                    } catch ( NumberFormatException ex ) {
                        if ( !bid.equals( "0" ) ) {
                            imcref.sqlUpdateQuery( "delete from browser_docs where meta_id = ? and browser_id = ?", new String[]{"" + meta_id, bid} );
                        }
                    }
                }
            }
            imcref.activateChild( meta_id, user );

            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;

        } else if ( req.getParameter( "add_browsers" ) != null ) {
            String[] browsers = req.getParameterValues( "new_browsers" );
            if ( browsers != null ) {
                for ( int i = 0; i < browsers.length; i++ ) {
                    imcref.sqlUpdateQuery( "insert into browser_docs (meta_id,to_meta_id,browser_id) values (?,?,?)", new String[]{"" + meta_id, "0", browsers[i]} );
                }
            }
            Vector vec = new Vector();
            Hashtable hash = imcref.sqlQueryHash( "select name,browsers.browser_id,to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = ? order by value desc,name asc", new String[]{"" + meta_id} );
            String[] b_id = (String[])hash.get( "browser_id" );
            String[] nm = (String[])hash.get( "name" );
            String[] to = (String[])hash.get( "to_meta_id" );
            String bs = "";
            if ( b_id != null ) {
                // FIXME: Move to template
                bs += "<table width=\"50%\" border=\"0\">";
                for ( int i = 0; i < b_id.length; i++ ) {
                    String[] temparr = {" ", "&nbsp;"};
                    // FIXME: Move to template
                    bs += "<tr><td>" + Parser.parseDoc( nm[i], temparr ) + ":</td><td><input type=\"text\" name=\"bid" + b_id[i] + "\" value=\"" + ( to[i].equals( "0" ) ? "" : to[i] ) + "\"></td></tr>";
                }
                // FIXME: Move to template
                bs += "</table>";
            }
            vec.add( "#browsers#" );
            vec.add( bs );
            String sqlStr = "select browser_id,name from browsers where browser_id not in (select browsers.browser_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = ? ) order by value desc,name asc";
            hash = imcref.sqlQueryHash( sqlStr, new String[]{"" + meta_id} );
            b_id = (String[])hash.get( "browser_id" );
            nm = (String[])hash.get( "name" );
            String nb = "";
            if ( b_id != null ) {
                for ( int i = 0; i < b_id.length; i++ ) {
                    // FIXME: Move to template
                    nb += "<option value=\"" + b_id[i] + "\">" + nm[i] + "</option>";
                }
            }
            vec.add( "#new_browsers#" );
            vec.add( nb );
            vec.add( "#new_meta_id#" );
            vec.add( String.valueOf( meta_id ) );
            vec.add( "#getDocType#" );
            // FIXME: Move to template
            vec.add( "<INPUT TYPE=\"hidden\" NAME=\"doc_type\" VALUE=\"6\">" );
            vec.add( "#DocMenuNo#" );
            vec.add( "" );
            vec.add( "#getMetaId#" );
            vec.add( String.valueOf( parent_meta_id ) );
            htmlStr = imcref.getAdminTemplate( "new_browser_doc.html", user, vec );
        }

        out.write( htmlStr );
    }
}
