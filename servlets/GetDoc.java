
import java.util.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletOutputStream;

import imcode.util.*;
import imcode.util.log.*;
import imcode.server.*;
import imcode.server.parser.Document;
import imcode.server.parser.ParserParameters;
import imcode.util.IMCServiceRMI;

import org.apache.log4j.*;
import org.apache.oro.text.perl.Perl5Util;

/**
 Get a document = Parse data from database.
 */
public class GetDoc extends HttpServlet {

    private final static String CVS_REV = "$Revision$";
    private final static String CVS_DATE = "$Date$";

    private static Category trackLog = Logger.getInstance( IMCConstants.ACCESS_LOG );
    private static Category log = Logger.getInstance( GetDoc.class.getName() );

    /**
     doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );

        HttpSession session = req.getSession( true );

        String htmlStr = "";
        int meta_id;
        int parent_meta_id;

        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();

        try {
            meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        } catch ( NumberFormatException ex ) {
            // Find the start-page
            meta_id = imcref.getSystemData().getStartDocument();
            log.debug( "Exception occured" + ex );
        }
        String tempstring = getDoc( meta_id, meta_id, req, res );
        if ( tempstring != null ) {
            byte[] tempbytes = tempstring.getBytes( "8859_1" );
            res.setContentLength( tempbytes.length );
            out.write( tempbytes );
        }
        out.flush();
        out.close();

    }

    public static String getDoc( int meta_id, int parent_meta_id, HttpServletRequest req, HttpServletResponse res ) throws IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface( req );
        String start_url = imcref.getStartUrl();
        String host = req.getHeader( "host" );
        String no_permission_url = Utility.getDomainPref( "no_permission_url", host );
        String servlet_url = Utility.getDomainPref( "servlet_url", host );
        File file_path = Utility.getDomainPrefPath( "file_path", host );

        Vector vec = new Vector();
        imcode.server.SystemData sysData = imcref.getSystemData();
        String eMailServerMaster = sysData.getServerMasterAddress();
        vec.add( "#EMAIL_SERVER_MASTER#" );
        vec.add( eMailServerMaster );

        HttpSession session = req.getSession( true );
        Object done = session.getAttribute( "logon.isDone" );  // marker object
        imcode.server.User user = (imcode.server.User)done;
        if ( done == null ) {
            // Check the name and password for validity
            String ip = req.getRemoteAddr();
            user = StartDoc.ipAssignUser( ip, host );

            // Valid login.  Make a note in the session object.
            //			session = req.getSession( true );
            if ( user == null ) {
                session.setAttribute( "login.target", HttpUtils.getRequestURL( req ).toString() + "?" + req.getQueryString() );
                res.sendRedirect( start_url );
                return null;
            }
            session.setAttribute( "logon.isDone", user );  // just a marker object

            // get type of browser
            String value = req.getHeader( "User-Agent" );

            if ( value == null ) {
                value = "";
            }
            session.setAttribute( "browser_id", value );

            StartDoc.incrementSessionCounter( imcref, user, req );
        }

        String[] emp_ary = req.getParameterValues( "emp" );
        if ( emp_ary != null ) {
            user.put( "emphasize", emp_ary );
        }

        int doc_type;
        DocumentRequest documentRequest;
        Revisits revisits;
        String referrer = req.getHeader( "Referer" ); // Note, intended misspelling of "Referrer", according to the HTTP spec.
        Document referringDocument = null;
        Perl5Util perlrx = new Perl5Util();
        if ( null != referrer && perlrx.match( "/meta_id=(\\d+)/", referrer ) ) {
            int referring_meta_id = Integer.parseInt( perlrx.group( 1 ) );
            try {
                referringDocument = imcref.getDocument( referring_meta_id );
            } catch ( IndexOutOfBoundsException ex ) {
                referringDocument = null;
            }
        }
        try {
            documentRequest = new DocumentRequest( imcref, user, meta_id, referringDocument, req );

            // Get all cookies from request
            Cookie[] cookies = req.getCookies();

            // Find cookies and put in hash.
            Hashtable cookieHash = new Hashtable();

            for ( int i = 0; cookies != null && i < cookies.length; ++i ) {
                Cookie currentCookie = cookies[i];
                cookieHash.put( currentCookie.getName(), currentCookie.getValue() );
            }

            revisits = new Revisits();

            if ( cookieHash.get( "imVisits" ) == null ) {
                Date now = new Date();
                long lNow = now.getTime();
                String sNow = "" + lNow;
                Cookie resCookie = new Cookie( "imVisits", session.getId() + sNow );
                resCookie.setMaxAge( 31500000 );
                resCookie.setPath( "/" );
                res.addCookie( resCookie );
                revisits.setRevisitsId( session.getId() );
                revisits.setRevisitsDate( sNow );
            } else {
                revisits.setRevisitsId( cookieHash.get( "imVisits" ).toString() );
            }
            documentRequest.setRevisits( revisits );

            doc_type = documentRequest.getDocument().getDocumentType();
        } catch ( IndexOutOfBoundsException ex ) {
            return imcref.parseDoc( vec, "no_page.html", user.getLangPrefix() );
        }

        // FIXME: One of the places that need fixing. Number one, we should put the no-permission-page
        // among the templates for the default-language. Number two, we should use just one function for
        // checking permissions. Number three, since the user obviously has logged in, give him the page in his own language!

        if ( !imcref.checkDocRights( meta_id, user ) ) {
            session.setAttribute( "login.target", HttpUtils.getRequestURL( req ).toString() + "?" + req.getQueryString() );
            String redirect = no_permission_url;
            res.sendRedirect( redirect );
            return null;
        }

        Stack history = (Stack)user.get( "history" );
        if ( history == null ) {
            history = new Stack();
            user.put( "history", history );
        }

        Integer meta_int = new Integer( meta_id );
        if ( history.empty() || !history.peek().equals( meta_int ) ) {
            history.push( meta_int );
        }

        // check if external doc
        imcode.server.ExternalDocType ex_doc = imcref.isExternalDoc( meta_id, user );
        String htmlStr = "";
        if ( ex_doc != null ) {
            String paramStr = "?meta_id=" + meta_id + "&";
            paramStr += "parent_meta_id=" + parent_meta_id + "&";
            paramStr += "cookie_id=" + "1A" + "&";
            paramStr += "action=view";
            Utility.redirect( req, res, ex_doc.getCallServlet() + paramStr );
            // Log to accesslog
            trackLog.info( documentRequest );
            return null;
        }

        switch ( doc_type ) {

            case 5:	//URL-doc
                imcode.server.Table url_doc = imcref.isUrlDoc( meta_id, user );
                String temp = url_doc.getString( "url_ref" );
                if ( temp.indexOf( "://" ) == -1 ) {
                    temp = "http://" + temp;
                }
                res.sendRedirect( temp );
                // Log to accesslog
                trackLog.info( documentRequest );
                return null;

            case 6:	//browser-doc
                String br_id = (String)session.getAttribute( "browser_id" );
                String sqlStr = "select top 1 to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = " + meta_id + " and '" + br_id + "' like user_agent order by value desc";
                String tmp = imcref.sqlQueryStr( sqlStr );
                if ( tmp != null && ( !"".equals( tmp ) ) ) {
                    meta_id = Integer.parseInt( tmp );
                }

                Utility.redirect( req, res, "GetDoc?meta_id=" + meta_id + "&parent_meta_id=" + parent_meta_id );
                // Log to accesslog
                trackLog.info( documentRequest );
                return null;

            case 7:	//frameset-doc
                String html_str_temp = imcref.isFramesetDoc( meta_id, user );
                if ( html_str_temp == null ) {
                    throw new RuntimeException( "Null-frameset encountered." );
                }
                htmlStr = html_str_temp;
                // Log to accesslog
                trackLog.info( documentRequest );
                return htmlStr;

            case 8:	//fileupload-doc
                sqlStr = "select mime from fileupload_docs where meta_id = " + meta_id;
                String mimetype = imcref.sqlQueryStr( sqlStr );
                sqlStr = "select filename from fileupload_docs where meta_id = " + meta_id;
                String filename = imcref.sqlQueryStr( sqlStr );
                BufferedInputStream fr;
                try {
                    fr = new BufferedInputStream( new FileInputStream( new File( file_path, String.valueOf( meta_id ) + "_se" ) ) );
                } catch ( IOException ex ) {
                    htmlStr = imcref.parseDoc( vec, "no_page.html", user.getLangPrefix() );
                    return htmlStr;
                }
                int len = fr.available();
                ServletOutputStream out = res.getOutputStream();
                res.setContentLength( len );
                res.setContentType( mimetype );
                String content_disposition = ( null != req.getParameter( "download" ) ? "attachment" : "inline" ) + "; filename=\"" + filename + "\"";
                res.setHeader( "Content-Disposition", content_disposition );
                try {
                    int bytes_read = 0;
                    byte buffer[] = new byte[32768];
                    while ( -1 != ( bytes_read = fr.read( buffer ) ) ) {
                        out.write( buffer, 0, bytes_read );
                    }
                } catch ( java.net.SocketException ex ) {
                    log.debug( "Exception occured" + ex );
                }
                fr.close();
                out.flush();
                out.close();
                // Log to accesslog
                trackLog.info( documentRequest );
                return null;

            default:

                String externalparam = null;
                if ( req.getParameter( "externalClass" ) != null || req.getAttribute( "externalClass" ) != null ) {
                    String className;
                    if ( req.getParameter( "externalClass" ) != null ) {
                        className = req.getParameter( "externalClass" );
                    } else {
                        className = (String)req.getAttribute( "externalClass" );
                    }
                    try {
                        Class cl = Class.forName( className );
                        imcode.external.GetDocControllerInterface obj = (imcode.external.GetDocControllerInterface)cl.newInstance();
                        externalparam = obj.createString( req );
                    } catch ( Exception e ) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace( new PrintWriter( sw ) );
                        externalparam = "<!-- Exception: " + sw.toString() + " -->";
                    }
                }

                user.setTemplateGroup( -1 );
                ParserParameters paramsToParser = new ParserParameters();

                paramsToParser.setTemplate( req.getParameter( "template" ) );
                paramsToParser.setParameter( req.getParameter( "param" ) );
                paramsToParser.setExternalParameter( externalparam );

                paramsToParser.setReadrunnerParameters( Readrunner.getReadrunnerParameters( req ) );

                String result = imcref.parsePage( documentRequest, 0, paramsToParser );
                // Log to accesslog
                trackLog.info( documentRequest );
                return result;
        }
    }

}
