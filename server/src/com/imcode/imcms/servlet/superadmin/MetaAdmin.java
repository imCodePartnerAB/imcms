package com.imcode.imcms.servlet.superadmin;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaAdmin extends HttpServlet {

    public static final String PARAMETER_BUTTON__SHOW_INTERVAL = "showinterval";
    private static final String PARAMETER_BUTTON__SHOW_SPAN = "showspan";
    private static final String PARAMETER__INTERVAL = "interval";
    private static final String PARAMETER__START = "start";
    private static final int ABBREVIATE_LENGTH = 80;
    private final static int DEFAULT_START_DOCUMENT_ID = 1001;
    private static final String PARAMETER__ENDMETA = "endmeta";
    private static final String PARAMETER__STARTMETA = "startmeta";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );

        if (!user.isSuperAdmin()) {
            return ;
        }

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        Utility.setDefaultHtmlContentType( res );

        ServletOutputStream out = res.getOutputStream();
        int user_id = user.getId();

        boolean list = false; // show list if true.
        if ( req.getParameter( PARAMETER_BUTTON__SHOW_INTERVAL ) != null
             || req.getParameter( PARAMETER_BUTTON__SHOW_SPAN ) != null ) {
            list = true;
        }

        int min = getMinDocumentId( imcref );
        int max = getMaxDocumentId( imcref );
        int end = max;
        int interval;
        try {
            interval = Integer.parseInt( req.getParameter( PARAMETER__INTERVAL ) );
        } catch ( NumberFormatException ex ) {
            interval = getMaxInterval( max-min ) ;
        }
        int start;
        try {
            start = Integer.parseInt( req.getParameter( PARAMETER__START ) );
            list = true;
        } catch ( NumberFormatException ex ) {
            start = DEFAULT_START_DOCUMENT_ID;
        }

        if ( req.getParameter( PARAMETER_BUTTON__SHOW_SPAN ) != null ) {
            end = Integer.parseInt( req.getParameter( PARAMETER__ENDMETA ) );
            start = Integer.parseInt( req.getParameter( PARAMETER__STARTMETA ) );
        }

        Map hash = new HashMap();
        String[] meta_id = {""};

        if ( list ) {
            hash = imcref.sqlProcedureHash( "GetDocs", new String[]{"" + user_id, "" + start, "" + end} );
            meta_id = (String[])hash.get( "meta_id" );
            try {
                end = Integer.parseInt( meta_id[meta_id.length - 1] );
            } catch ( NullPointerException ex ) {
                end = start;
                list = false;
            }

        }

        List vec = new ArrayList();
        vec.add( "#start#" );
        vec.add( String.valueOf( start ) );
        vec.add( "#end#" );
        vec.add( String.valueOf( end ) );

        vec.add( "#intervals#" );
        String tmp = createIntervalsOptionList( max-min, interval );

        if ( !list ) {
            start = DEFAULT_START_DOCUMENT_ID;
        }
        vec.add( tmp );
        vec.add( "#starts#" );
        tmp = createStartsOptionList( min, max, start, interval );
        vec.add( tmp );
        out.println( imcref.getAdminTemplate( "MetaAdminControl.html", user, vec ) );
        if ( !list ) {
            return;
        }

        String[] pc = (String[])hash.get( "parentcount" );
        String[] hl = (String[])hash.get( "meta_headline" );
        String[] types = (String[])hash.get( "doc_type" );

        out.println( "<hr>" );

        for ( int i = 0; i < meta_id.length; i++ ) {
            out.println( "<ul>" );
            if ( Integer.parseInt( meta_id[i] ) < start ) {
                continue;
            }
            if ( Integer.parseInt( meta_id[i] ) > end ) {
                break;
            }
            out.println( "<li>" );
            hl[i] = StringUtils.abbreviate( hl[i], ABBREVIATE_LENGTH );

            out.println( "<A name=\"" + meta_id[i] + "\" href=\"AdminDoc?meta_id=" + meta_id[i]
                         + "\"><FONT COLOR=\"#FF0000\">"
                         + meta_id[i]
                         + "</FONT></A>&nbsp;<A name=\""
                         + meta_id[i]
                         + "\" href=\"GetDoc?meta_id="
                         + meta_id[i]
                         + "\">&nbsp;"
                         + pc[i]
                         + "&nbsp;parents&nbsp;:&nbsp;"
                         + StringEscapeUtils.escapeHtml( hl[i] )
                         + "</A>" );
            if ( types[i].equals( "2" ) ) {
                Map h2 = imcref.sqlProcedureHash( "GetMenuDocChilds", new String[]{meta_id[i], "" + user_id} );
                String[] childs = (String[])h2.get( "to_meta_id" );
                String[] hl2 = (String[])h2.get( "meta_headline" );
                if ( childs != null && childs.length != 0 ) {
                    out.println( "<ul>" );
                    for ( int j = 0; j < childs.length; j++ ) {
                        String address = "MetaAdmin?start=" + childs[j] + "&interval=" + interval + "#" + childs[j];
                        hl2[j] = StringUtils.abbreviate( hl2[j], ABBREVIATE_LENGTH );
                        out.println( "<li><A href=\"" + address + "\">" + childs[j] + ":&nbsp;"
                                     + StringEscapeUtils.escapeHtml( hl2[j] )
                                     + "</A></li>" );
                    }
                    out.println( "</ul>" );
                }
            } else if ( types[i].equals( "6" ) ) {
                Map h2 = imcref.sqlProcedureHash( "GetBrowserDocChilds", new String[]{meta_id[i], "" + user_id} );
                String[] childs = (String[])h2.get( "to_meta_id" );
                String[] hl2 = (String[])h2.get( "meta_headline" );
                if ( childs != null && childs.length != 0 ) {
                    out.println( "<ul>" );
                    for ( int j = 0; j < childs.length; j++ ) {
                        String address = "MetaAdmin?start=" + childs[j] + "&interval=" + interval + "#" + childs[j];
                        hl2[j] = StringUtils.abbreviate( hl2[j], ABBREVIATE_LENGTH );
                        out.println( "<li><A href=\"" + address + childs[j] + "\">" + childs[j] + ":&nbsp;"
                                     + StringEscapeUtils.escapeHtml( hl2[j] )
                                     + "</A></li>" );
                    }
                    out.println( "</ul>" );
                }
            }
            out.println( "</li></ul><br>" );
            out.flush();
        }
        out.println( "</body></html>" );
    }

    private int getMaxInterval( int count ) {
        if (0 == count) {
            return 10 ;
        }
        int countLog10 = (int)log10(count);
        return (int)Math.pow(10, 1+countLog10);
    }

    private double log10( int v ) {
        return Math.log( v ) / Math.log(10) ;
    }

    private int getMaxDocumentId( IMCServiceInterface imcref ) {
        return Integer.parseInt( imcref.sqlQueryStr( "select max(meta_id) from meta", new String[0] ) );
    }

    private int getMinDocumentId( IMCServiceInterface imcref ) {
        return Integer.parseInt( imcref.sqlQueryStr( "select min(meta_id) from meta", new String[0] ) );
    }

    private String createStartsOptionList( int min, int max, int start, int interval ) {
        String tmp;
        tmp = "";
        for ( int i = min; i <= max; i += interval ) {
            tmp += "<option value=\"" + i + "\" " + ( i == start ? "selected" : "" ) + ">" + i + "</option>";
        }
        return tmp;
    }

    private String createIntervalsOptionList( int count, int interval ) {
        String tmp = "";
        for ( int i = 10; i <= getMaxInterval( count ); i *= 10 ) {
            tmp += "<option value=\"" + i + "\" " + ( i == interval ? "selected" : "" ) + ">" + i + "</option>";
        }
        return tmp;
    }

}

