package com.imcode.imcms.servlet.superadmin;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class UrlDocTest extends HttpServlet {

    private static Logger log = Logger.getLogger( UrlDocTest.class.getName() );

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        Map hash = imcref.sqlQueryHash( "select meta_id, url_ref from url_docs order by meta_id", new String[0] );
        String[] meta_id = (String[])hash.get( "meta_id" );
        String[] url_ref = (String[])hash.get( "url_ref" );
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !imcref.checkAdminRights( user ) ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        out.print( imcref.getAdminTemplate( "UrlDocTestHead.html", user, null ) );
        out.flush();
        for ( int i = 0; meta_id != null && i < meta_id.length; i++ ) {
            String found = "green", reached = "green", ok = "red";
            String tmp = url_ref[i];
            URL url;
            String reslt = null;
            try {
                if ( tmp.indexOf( "://" ) == -1 ) {
                    tmp = "http://" + url_ref[i];
                    if ( tmp.length() == 7 ) {
                        url_ref[i] = "&nbsp;";
                        throw new UnknownHostException();
                    }
                }
                url = new URL( tmp );
                reslt = testUrl( url );
            } catch ( UnknownHostException ex ) {
                found = "red";
                reached = "red";
            } catch ( MalformedURLException ex ) {
                found = "red";
                reached = "red";
            } catch ( IOException ex ) {
                reached = "red";
            }
            if ( reached.equals( "green" ) ) {
                //int result = conn.getResponseCode() ;
                int result = status( reslt );
                if ( result == 200 ) {
                    ok = "green";
                } else if ( result >= 300 && result < 400 ) {
                    int lindex = reslt.indexOf( "\r\nLocation: " );
                    if ( lindex != -1 ) {
                        url_ref[i] = reslt.substring( lindex + 12, reslt.indexOf( "\r\n", lindex + 12 ) ).trim();
                        //log("Redirect to: "+url_ref[i]) ;
                        i--;
                        continue;
                    }
                } else if ( result >= 400 && result < 500 ) {
                    ok = "red";
                } else {
                    ok = "red";
                    //		log ("GET "+url.getHost()+":"+reslt.trim()) ;
                }
            }

            Vector vec = new Vector();
            vec.add( "#meta_id#" );
            vec.add( "<a href=\"AdminDoc?meta_id=" + meta_id[i] + "\" target=\"_new\">" + meta_id[i] + "</a>" );
            vec.add( "#url#" );
            String foo = url_ref[i];
            if ( foo.indexOf( ":/" ) == -1 ) {
                foo = "http://" + foo;
            }
            if ( url_ref[i].length() > 60 ) {
                url_ref[i] = url_ref[i].substring( 0, 27 ) + " ... " + url_ref[i].substring( url_ref[i].length() - 27 );
            }
            String href = "<a href=\"" + foo + "\" target=\"_new\">" + url_ref[i] + "</a>";
            vec.add( href );
            vec.add( "#found#" );
            vec.add( found );
            vec.add( "#reached#" );
            vec.add( reached );
            vec.add( "#ok#" );
            vec.add( ok );
            out.print( imcref.getAdminTemplate( "UrlDocTestRow.html", user, vec ) );
            out.flush();
        }
        out.print( imcref.getAdminTemplate( "UrlDocTestTail.html", user, null ) );
    }

    private String testUrl( URL url ) throws IOException {
        int port = url.getPort() == -1 ? 80 : url.getPort();
        StringBuffer result = new StringBuffer();
        try {
            Socket sock = new Socket( url.getHost(), port );
            sock.setSoTimeout( 5000 );
            PrintStream out = new PrintStream( sock.getOutputStream() );
            BufferedReader in = new BufferedReader( new InputStreamReader( sock.getInputStream(), "8859_1" ) );
            String path = "".equals( url.getPath() ) ? "/" : url.getPath();
            String cmd = "GET " + path + " HTTP/1.0\r\nHost: " + url.getHost() + "\r\n\r\n";
            out.print( cmd );
            String line;
            while ( ( line = in.readLine() ) != null && line.length() > 0 ) {
                result.append( line + "\r\n" );
            }
            sock.close();
        } catch ( SocketException ex ) {
            log( "SocketException in UrlDocTest, connecting to " + url.toString() + " : " + ex.getMessage() );
        }
        return result.toString();
    }

    private int status( String str ) {
        try {
            if ( str.indexOf( "HTTP/" ) == -1 ) {
                return 0;
            }
            StringTokenizer st = new StringTokenizer( str );
            st.nextToken();
            return Integer.parseInt( st.nextToken() );
        } catch ( NumberFormatException ex ) {
            log.debug( "Exception occured" + ex );
            return 0;
        } catch ( NullPointerException ex ) {
            log.debug( "Exception occured" + ex );
            return 0;
        }
    }
}
