package imcode.util;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DefaultContentManagementSystem;
import com.imcode.imcms.servlet.VerifyUser;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

public class Utility {

   private final static Logger log = Logger.getLogger( Utility.class.getName() );

    private final static String NO_PERMISSION_URL = "no_permission.jsp";
    private final static String CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE = "com.imcode.imcms.ImcmsSystem";

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
        res.setDateHeader( "Expires", 0 );
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

    public static boolean parameterIsSet( HttpServletRequest request, String parameter ) {
        return null != request.getParameter( parameter );
    }

    public static int[] getParameterInts( HttpServletRequest request, String parameterName ) {
        String[] parameterValues = request.getParameterValues( parameterName );
        if (null == parameterValues) {
            return new int[0] ;
        }
        return convertStringArrayToIntArray( parameterValues );
    }

    public static int[] convertStringArrayToIntArray( String[] strings ) {
        int[] parameterInts = new int[strings.length] ;
        for ( int i = 0; i < strings.length; i++ ) {
            parameterInts[i] = Integer.parseInt(strings[i]);
        }
        return parameterInts;
    }

    public static String getContents( String path, HttpServletRequest request,
                                HttpServletResponse response ) throws ServletException, IOException {
        HttpServletResponseWrapper collectingHttpServletResponse = new CollectingHttpServletResponse( response );
        request.getRequestDispatcher( path ).include( request, collectingHttpServletResponse );
        return collectingHttpServletResponse.toString();
    }

    public static String formatDate( Date oneWeekAgo ) {
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
        String formattedDate = dateFormat.format( oneWeekAgo );
        return formattedDate;
    }

    public static String formatUser( UserDomainObject user ) {
        return StringEscapeUtils.escapeHtml( user.getLastName() + ", " + user.getFirstName() + " (" + user.getLoginName() + ")" );
    }

    public static String getAbsolutePathToDocument(HttpServletRequest request, DocumentDomainObject document) {
        if (null == document) {
            return null ;
        }
        return request.getContextPath() + getContextRelativePathToDocument( document ) ;
    }

    public static String getContextRelativePathToDocument( DocumentDomainObject document ) {
        if (null == document) {
            return null ;
        }
        return getContextRelativePathToDocumentWithId(document.getId());
    }

    public static String getContextRelativePathToDocumentWithId(int documentId) {
        String documentPathPrefix = Imcms.getServices().getConfig().getDocumentPathPrefix() ;
        if (StringUtils.isBlank( documentPathPrefix )) {
            documentPathPrefix = "/servlet/GetDoc?meta_id=" ;
        }
        return documentPathPrefix + documentId;
    }

    public static String formatHtmlDatetime( Date datetime ) {
        if ( null == datetime ) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING + "'&nbsp;'"
                                                      + DateConstants.TIME_NO_SECONDS_FORMAT_STRING );
        return dateFormat.format( datetime );
    }

    public static void forwardToLogin( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        UserDomainObject user = getLoggedOnUser( request );
        StringBuffer loginTarget = request.getRequestURL() ;
        String queryString = request.getQueryString();
        if (null != queryString) {
            loginTarget.append( "?" ).append( queryString );
        }
        String noPermissionPage = "/imcms/" + user.getLanguageIso639_2() + "/login/" + NO_PERMISSION_URL+"?"+VerifyUser.REQUEST_PARAMETER__NEXT_URL+"="+URLEncoder.encode( loginTarget.toString() );
        response.setStatus( HttpServletResponse.SC_FORBIDDEN );
        request.getRequestDispatcher( noPermissionPage ).forward( request,response );
    }

    public static String[] getParameterValues( HttpServletRequest request, String parameterName ) {
        String[] parameterValues = request.getParameterValues( parameterName );
        if (null == parameterValues) {
            parameterValues = new String[0];
        }
        return parameterValues ;
    }

    public static Date truncateDateToMinutePrecision( Date fieldValue ) {
        if (null == fieldValue) {
            return null ;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( fieldValue );
        calendar.set( Calendar.MILLISECOND, 0 );
        calendar.set( Calendar.SECOND, 0 );
        Date truncatedDate = calendar.getTime();
        return truncatedDate;
    }

    public static String getRequestURLWithoutPath( HttpServletRequest request ) {
        String requestUrl = request.getRequestURL().toString();
        int requestUrlStartOfHost = requestUrl.indexOf( "://" ) + 3;
        int requestUrlStartOfPath = requestUrl.indexOf( '/', requestUrlStartOfHost );
        String requestUrlWithoutPath = StringUtils.left( requestUrl, requestUrlStartOfPath );
        return requestUrlWithoutPath;
    }

    public static boolean throwableContainsMessageContaining( Throwable t, String s ) {
        Throwable throwable = t ;
        while (null != throwable) {
            String message = throwable.getMessage();
            log.debug( throwable+": "+message ) ;
            if (null != message && -1 != message.indexOf( s )) {
                return true ;
            }
            throwable = throwable.getCause() ;
        }
        return false ;
    }

    public static boolean classIsSignedByCertificatesInKeyStore( Class clazz, KeyStore keyStore ) {
        Object[] signers = clazz.getSigners();
        if ( null == signers ) {
            return false;
        }
        for ( int i = 0; i < signers.length; i++ ) {
            Object signer = signers[i];
            if ( !( signer instanceof Certificate ) ) {
                return false;
            }
            Certificate certificate = (Certificate)signer;
            try {
                if ( null == keyStore.getCertificateAlias( certificate ) ) {
                    return false;
                }
            } catch ( KeyStoreException e ) {
                throw new UnhandledException( e );
            }
        }
        return true ;
    }

    public static Map getMapViewOfObjectPairArray(final Object[][] array) {
        return new ArrayMap(array, new ObjectPairToMapEntryTransformer() );
    }

    public static String makeSqlStringFromDate(Date date) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING).format(date);
    }

    public static Date parseDateFormat(DateFormat dateFormat, String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (NullPointerException npe) {
            return null;
        } catch ( ParseException pe) {
            return null;
        }
    }

    private static class ObjectPairToMapEntryTransformer implements Transformer {
        public Object transform(Object input) {
            final Object[] pair = (Object[])input ;
            return new Map.Entry() {
                public Object getKey() {
                    return pair[0] ;
                }

                public Object getValue() {
                    return pair[1] ;
                }

                public Object setValue(Object value) {
                    throw new UnsupportedOperationException() ;
                }
            } ;
        }
    }

    private static class ArrayMap extends AbstractMap {

        private final Object[] array;
        private Transformer transformer;

        ArrayMap(Object[] array, Transformer transformer) {
            this.array = array;
            this.transformer = transformer;
        }

        public Set entrySet() {
            return new AbstractSet() {
                public int size() {
                    return array.length ;
                }
                public Iterator iterator() {
                    return new TransformIterator( new ObjectArrayIterator( array ), transformer );
                }
            };
        }
    }

    public static void outputXmlDocument( HttpServletResponse response, Document xmlDocument ) throws IOException {
        response.setContentType( "text/xml; charset=UTF-8" );
        writeXmlDocumentToStream( xmlDocument, response.getOutputStream() );
    }

    private static void writeXmlDocumentToStream( Document xmlDocument, OutputStream outputStream ) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
            StreamResult outputTarget = new StreamResult( outputStream );
            DOMSource xmlSource = new DOMSource( xmlDocument );
            transformer.transform( xmlSource, outputTarget );
        } catch ( TransformerConfigurationException e ) {
            throw new UnhandledException( e );
        } catch ( TransformerException e ) {
            throw new UnhandledException( e );
        }
    }

    public static void makeUserLoggedIn(HttpServletRequest req, UserDomainObject user) {
        req.getSession().setAttribute(WebAppGlobalConstants.LOGGED_IN_USER, user);
        // FIXME: Ugly hack to get the contextpath into DefaultImcmsServices.getVelocityContext()
        user.setCurrentContextPath( req.getContextPath() );
    }

    public static UserDomainObject getDefaultUser() {
        return Imcms.getServices().verifyUser( "User", "user" );
    }

    public static void makeUserLoggedInAsDefaultUser(HttpServletRequest req) {
        makeUserLoggedIn(req, getDefaultUser());
    }

    public static ContentManagementSystem initRequestWithApi(ServletRequest request, UserDomainObject currentUser) {
        NDC.push( "initRequestWithApi" );
        ImcmsServices service = Imcms.getServices();
        ContentManagementSystem imcmsSystem = DefaultContentManagementSystem.create( service, currentUser );
        request.setAttribute( CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE, imcmsSystem );
        NDC.pop();
        return imcmsSystem ;
    }

    public static ContentManagementSystem getContentManagementSystemFromRequest(ServletRequest request) {
        return (ContentManagementSystem)request.getAttribute( CONTENT_MANAGEMENT_SYSTEM_REQUEST_ATTRIBUTE );
    }
}
