package imcode.util;

import imcode.server.ApplicationServer;

import javax.servlet.http.*;
import javax.servlet.http.HttpUtils;
import java.io.*;
import java.util.*;

public class Utility {

    private static final String PREFERENCES_FILENAME = "host.properties";

    private Utility() {

    }

    /**
     Takes a path-string and returns a file. The path is prepended with the webapp dir if the path is relative.
     **/
    public static File getAbsolutePathFromString( String pathString ) {
        File path = new File( pathString );
        if ( !path.isAbsolute() ) {
            path = new File( imcode.server.WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath(), pathString );
        }
        return path;
    }

    /**
     Fetches a preference from the config file for a domain,
     as a File representing an absolute path, with the webapp dir prepended if the path is relative.
     @param pref The name of the preference to fetch.
     */
    public static File getDomainPrefPath( String pref ) throws IOException {
        return getAbsolutePathFromString( getDomainPref( pref ) );
    }

    /**
     Fetches a preference from the config file for a domain.
     @param pref The name of the preference to fetch.
     */
    public static String getDomainPref( String pref ) throws IOException {
        return Prefs.get( pref, PREFERENCES_FILENAME );
    }

    /**
     Redirects to a local URI. Considered relative unless prefixed with "/".
     @param req The request to get the redirect data from.
     @param res The response to use for redirecting.
     @param uri The URI to redirect to. Relative unless prefixed with "/".
     */
    public static void redirect( HttpServletRequest req, HttpServletResponse res, String uri ) throws IOException {
        StringBuffer requrl = HttpUtils.getRequestURL( req );
        if ( uri.startsWith( "/" ) ) {
            int slash = requrl.toString().indexOf( "://" ) + 3;
            slash = requrl.toString().indexOf( "/", slash );
            requrl.replace( slash, requrl.length(), uri );
        } else {
            int slash = requrl.toString().lastIndexOf( "/" ) + 1;
            requrl.replace( slash, requrl.length(), uri );
        }
        res.sendRedirect( requrl.toString() );
    }


    /**
     * Transforms a long containing an ip into a String.
     */
    public static String ipLongToString( long ip ) {
        return ( ( ip >>> 24 ) & 255 ) + "." + ( ( ip >>> 16 ) & 255 ) + "." + ( ( ip >>> 8 ) & 255 ) + "." + ( ip & 255 );
    }

    /**
     * Transforms a String containing an ip into a long.
     */
    public static long ipStringToLong( String ip ) {
        long ipInt = 0;
        StringTokenizer ipTok = new StringTokenizer( ip, "." );
        for ( int exp = 3; ipTok.hasMoreTokens(); --exp ) {
            int ipNum = Integer.parseInt( ipTok.nextToken() );
            ipInt += ( ipNum * Math.pow( 256, exp ) );
        }
        return ipInt;
    }


    /**
     Make a HttpServletResponse non-cacheable
     **/
    public static void setNoCache( HttpServletResponse res ) {
        res.setHeader( "Cache-Control", "no-cache; must-revalidate;" );
        res.setHeader( "Pragma", "no-cache;" );
    }


    public static imcode.server.user.UserDomainObject getLoggedOnUserOrRedirect (HttpServletRequest req, HttpServletResponse res) throws IOException {
        return getLoggedOnUserOrRedirect( req, res, ApplicationServer.getIMCServiceInterface().getStartUrl()) ;
    }

    public static imcode.server.user.UserDomainObject getLoggedOnUserOrRedirect (HttpServletRequest req, HttpServletResponse res, String start_url) throws IOException {

		HttpSession session = req.getSession(true) ;
		imcode.server.user.UserDomainObject user = (imcode.server.user.UserDomainObject) session.getAttribute("logon.isDone") ;

		if (user == null) {
			res.sendRedirect( start_url ) ;
			return null;
		}
		return user;
	}

    public static boolean toBoolean( String property ) {
        if (null == property) {
            return false ;
        }
        property = property.toLowerCase() ;
        if ("1".equals(property) || "y".equals(property) || "yes".equals(property) || "true".equals( property )) {
            return true ;
        }
        return false ;
    }

    public static int compareDatesWithNullFirst( Date date1, Date date2 ) {
        if (null == date1 && null == date2) {
            return 0 ;
        } else if (null == date1) {
            return -1 ;
        } else if (null == date2) {
            return +1 ;
        } else {
            return date1.compareTo( date2 ) ;
        }
    }

}
