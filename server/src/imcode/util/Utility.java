package imcode.util;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.text.*;

public class Utility {

    private Utility() {

    }

    /**
     * Transforms a long containing an ip into a String.
     */
    public static String ipLongToString( long ip ) {
        return ( ip >>> 24 & 255 ) + "." + ( ip >>> 16 & 255 ) + "." + ( ip >>> 8 & 255 ) + "."
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
            ipInt += ipNum * Math.pow( 256, exp );
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

    public static String getQueryStringExcludingParameter( HttpServletRequest request, String parameterNameToExclude ) {

        MultiMap requestParameters = new MultiHashMap();
        Map parameterMap = request.getParameterMap();
        convertArrayValuedMapToMultiMap( parameterMap, requestParameters );

        requestParameters.remove( parameterNameToExclude );
        return createQueryStringFromParameterMultiMap( requestParameters );
    }

    private static void convertArrayValuedMapToMultiMap( Map map, MultiMap multiMap ) {
        for ( Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Object name = entry.getKey();
            Object[] values = (Object[])entry.getValue();
            for ( int i = 0; i < values.length; i++ ) {
                Object value = values[i];
                multiMap.put( name, value ) ;
            }
        }
    }

    public static String createQueryStringFromParameterMultiMap( MultiMap requestParameters ) {
        Set requestParameterStrings = SetUtils.orderedSet( new HashSet() );
        for ( Iterator iterator = requestParameters.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String parameterName = (String)entry.getKey();
            Collection parameterValues = (Collection)entry.getValue();
            for ( Iterator valuesIterator = parameterValues.iterator(); valuesIterator.hasNext(); ) {
                String parameterValue = (String)valuesIterator.next();
                requestParameterStrings.add( URLEncoder.encode( parameterName ) + "="
                                             + URLEncoder.encode( parameterValue ) );
            }
        }
        return StringUtils.join( requestParameterStrings.iterator(), "&" );
    }

    public static Collection collectImageDirectories() {
        ImcmsServices service = Imcms.getServices();
        final File imagePath = service.getConfig().getImagePath();
        return FileUtility.collectRelativeSubdirectoriesStartingWith( imagePath );
    }

    public static Object firstElementOfSetByOrderOf( Set set, Comparator comparator ) {
        SortedSet sortedSet = new TreeSet( comparator );
        sortedSet.addAll( set );
        return sortedSet.iterator().next();
    }

    public static ImageSize getImageSize( InputStream inputStream ) throws IOException {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream( inputStream );
        Iterator imageReadersIterator = ImageIO.getImageReaders( imageInputStream );
        if ( !imageReadersIterator.hasNext() ) {
            throw new IOException( "Can't read image format." ) ;
        }
        ImageReader imageReader = (ImageReader)imageReadersIterator.next();
        imageReader.setInput( imageInputStream, true, true );
        int width = imageReader.getWidth( 0 );
        int height = imageReader.getHeight( 0 );
        imageReader.dispose();
        return new ImageSize( width, height );
    }

    public static String getHumanReadableSize(long size, String separator) {
        double displaySize = size ;
        DecimalFormat df = new DecimalFormat( "#.#" );
        DecimalFormatSymbols decimalFormatSymbols = df.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator( '.' );
        df.setDecimalFormatSymbols( decimalFormatSymbols );
        String sizeSuffix = "B";
        if ( displaySize >= ( 1024 * 1024 ) ) {
            displaySize /= ( 1024 * 1024 );
            sizeSuffix = "MB";
        } else if ( displaySize >= 1024 ) {
            displaySize /= 1024;
            sizeSuffix = "kB";
        }
        return df.format(displaySize)+separator+sizeSuffix ;
    }

    public static String getHumanReadableTimeLength(long milliseconds) {
        long ms = milliseconds ;
        TimeLengthSuffixPair[] pairs = new TimeLengthSuffixPair[] {
            new TimeLengthSuffixPair( DateUtils.MILLIS_IN_HOUR, "h" ),
            new TimeLengthSuffixPair( DateUtils.MILLIS_IN_MINUTE, "m" ),
            new TimeLengthSuffixPair( DateUtils.MILLIS_IN_SECOND, "s" ),
            new TimeLengthSuffixPair( 1, "ms" ),
        };
        List resultList = new ArrayList() ;
        for ( int i = 0; i < pairs.length; i++ ) {
            TimeLengthSuffixPair pair = pairs[i];
            long timeLength = pair.timeLength;
            if ( ms >= timeLength ) {
                long unitTime = ms / timeLength;
                ms %= timeLength;
                resultList.add( unitTime + pair.suffix );
            }
        }
        return StringUtils.join( resultList.iterator(), ", " ) ;
    }

    public static boolean parameterIsSet( HttpServletRequest request, String parameter ) {
        return null != request.getParameter( parameter );
    }

    public static int[] getParameterInts( HttpServletRequest request, String parameterName ) {
        String[] parameterValues = request.getParameterValues( parameterName );
        if (null == parameterValues) {
            return new int[0] ;
        }
        int[] parameterInts = new int[parameterValues.length] ;
        for ( int i = 0; i < parameterValues.length; i++ ) {
            parameterInts[i] = Integer.parseInt(parameterValues[i]);
        }
        return parameterInts;
    }

    public static String getContents( String path, HttpServletRequest request,
                                HttpServletResponse response ) throws ServletException, IOException {
        HttpServletResponseWrapper collectingHttpServletResponse = new CollectingHttpServletResponse( response );
        request.getRequestDispatcher( path ).include( request, collectingHttpServletResponse );
        return collectingHttpServletResponse.toString();
    }

    private static class TimeLengthSuffixPair {
        long timeLength;
        String suffix;

        TimeLengthSuffixPair( long timeLength, String suffix ) {
            this.timeLength = timeLength;
            this.suffix = suffix;
        }
    }

    public static String formatUser( UserDomainObject user ) {
        return StringEscapeUtils.escapeHtml( user.getLastName() + ", " + user.getFirstName() + " (" + user.getLoginName() + ")" );
    }

    public static String getAbsolutePathToDocument(HttpServletRequest request, DocumentDomainObject document) {
        String documentPathPrefix = Imcms.getServices().getConfig().getDocumentPathPrefix() ;
        if (StringUtils.isBlank( documentPathPrefix )) {
            documentPathPrefix = "/servlet/GetDoc?meta_id=" ;
        }
        return request.getContextPath() + documentPathPrefix + document.getId() ;
    }

    public static String formatHtmlDatetime( Date datetime ) {
        if ( null == datetime ) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING + "'&nbsp;'"
                                                      + DateConstants.TIME_NO_SECONDS_FORMAT_STRING );
        return dateFormat.format( datetime );
    }

}
