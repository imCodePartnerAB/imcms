package imcode.util;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.WebAppGlobalConstants;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

public class Utility {

    private Utility() {

    }

    /**
     * Transforms a long containing an ip into a String.
     */
    public static String ipLongToString( long ip ) {
        return ( ( ip >>> 24 ) & 255 ) + "." + ( ( ip >>> 16 ) & 255 ) + "." + ( ( ip >>> 8 ) & 255 ) + "."
               + ( ip & 255 );
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
     * Make a HttpServletResponse non-cacheable
     */
    public static void setNoCache( HttpServletResponse res ) {
        res.setHeader( "Cache-Control", "no-cache; must-revalidate;" );
        res.setHeader( "Pragma", "no-cache;" );
    }

    public static UserDomainObject getLoggedOnUser( HttpServletRequest req ) {
        HttpSession session = req.getSession( true );
        UserDomainObject user = (UserDomainObject)session.getAttribute( WebAppGlobalConstants.LOGGED_IN_USER );
        return user;
    }

    public static int compareDatesWithNullFirst( Date date1, Date date2 ) {
        if ( null == date1 && null == date2 ) {
            return 0;
        } else if ( null == date1 ) {
            return -1;
        } else if ( null == date2 ) {
            return +1;
        } else {
            return date1.compareTo( date2 );
        }
    }

    public static void setDefaultHtmlContentType( HttpServletResponse res ) {
        res.setContentType( "text/html; charset=" + WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
    }

    public static void redirectToStartDocument( HttpServletRequest req, HttpServletResponse res ) throws IOException {
        res.sendRedirect( req.getContextPath() + "/servlet/StartDoc" );
    }

    public static boolean isValidEmail( String email ) {
        return Pattern.compile( "\\w+@\\w+" ).matcher( email ).find();
    }

    public static void removeNullValuesFromMap( Map map ) {
        Collection values = map.values();
        for ( Iterator iterator = values.iterator(); iterator.hasNext(); ) {
            if ( null == iterator.next() ) {
                iterator.remove();
            }
        }
    }

    public static String getQueryStringExcludingParameter(HttpServletRequest request, String parameterNameToExclude) {
        Map requestParameters = new HashMap(request.getParameterMap()) ;
        requestParameters.remove( parameterNameToExclude ) ;
        return createQueryStringFromParameterMap( requestParameters );
    }

    public static String createQueryStringFromParameterMap( Map requestParameters ) {
        Set requestParameterStrings = SetUtils.orderedSet(new HashSet()) ;
        for ( Iterator iterator = requestParameters.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String parameterName = (String)entry.getKey();
            String[] parameterValues = (String[])entry.getValue();
            for ( int i = 0; i < parameterValues.length; i++ ) {
                String parameterValue = parameterValues[i];
                requestParameterStrings.add( URLEncoder.encode( parameterName ) + "=" + URLEncoder.encode( parameterValue )) ;
            }
        }
        return StringUtils.join( requestParameterStrings.iterator(), "&" ) ;
    }

    public static Collection collectImageDirectories() {
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        final File imagePath = service.getConfig().getImagePath();
        return FileUtility.collectRelativeSubdirectoriesStartingWith( imagePath );
    }

}
