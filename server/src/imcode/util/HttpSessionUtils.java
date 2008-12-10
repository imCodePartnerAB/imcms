package imcode.util;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class HttpSessionUtils {

    private final static Logger log = Logger.getLogger( HttpSessionUtils.class.getName() );

    private static final String SESSION_ATTRIBUTE_NAME__SESSION_MAP = HttpSessionUtils.class.getName() + ".sessionMap";

    private static final int MAX_COUNT__SESSION_OBJECTS = 7;

    private HttpSessionUtils() {
    }

    public static void setSessionAttributeAndSetNameInRequestAttribute( final Serializable objectToAddToSession,
                                                                        HttpServletRequest request,
                                                                        final String sessionAttributeNameRequestAttributeName ) {
        String sessionAttributeName ;
        if (objectToAddToSession instanceof HttpSessionAttribute) {
            sessionAttributeName = ((HttpSessionAttribute)objectToAddToSession).getSessionAttributeName() ;
        } else {
            sessionAttributeName = getSessionAttributeNameFromRequest( request, sessionAttributeNameRequestAttributeName );
        }
        if ( null == sessionAttributeName || !objectToAddToSession.equals( get( request, sessionAttributeName ) ) ) {
            sessionAttributeName = createUniqueNameForObject( objectToAddToSession );
            put( request, sessionAttributeName, objectToAddToSession );
        }
        request.setAttribute( sessionAttributeNameRequestAttributeName, sessionAttributeName );
    }

    private static Object get( HttpServletRequest request, String sessionAttributeName ) {
        return getSessionMap(request).get( sessionAttributeName );
    }

    public static void put( HttpServletRequest request, String sessionAttributeName,
                             final Serializable objectToAddToSession ) {
        Map sessionMap = getSessionMap( request );
        if ( MAX_COUNT__SESSION_OBJECTS == sessionMap.size() ) {
            log.debug( "SessionMap is full. Least recently used object will be evicted.") ;
        }
        if (objectToAddToSession instanceof HttpSessionAttribute) {
            ((HttpSessionAttribute)objectToAddToSession).setSessionAttributeName(sessionAttributeName) ;
        }
        sessionMap.put( sessionAttributeName, objectToAddToSession );
        log.debug( "Put in session: \"" + sessionAttributeName + "\": " + objectToAddToSession.getClass() + ". Size: "
                   + sessionMap.size() );
    }

    private static Map getSessionMap( HttpServletRequest request ) {
        HttpSession session = request.getSession();
        Map sessionMap = (Map)session.getAttribute( SESSION_ATTRIBUTE_NAME__SESSION_MAP );
        if ( null == sessionMap ) {
            sessionMap = Collections.synchronizedMap( new LRUMap(MAX_COUNT__SESSION_OBJECTS) );
            session.setAttribute( SESSION_ATTRIBUTE_NAME__SESSION_MAP, sessionMap );
        }
        return sessionMap;
    }

    public static String createUniqueNameForObject( Object object ) {
        return Integer.toString( object.getClass().hashCode(), Character.MAX_RADIX )
               + RandomStringUtils.randomAlphanumeric( 4 );
    }

    public static Object getSessionAttributeWithNameInRequest( HttpServletRequest request,
                                                               String requestAttributeOrParameterName ) {
        String sessionAttributeName = getSessionAttributeNameFromRequest( request, requestAttributeOrParameterName );
        Map sessionMap = getSessionMap( request );
        Object sessionAttribute = sessionMap.get( sessionAttributeName );
        if ( null == sessionAttribute ) {
            removeSessionAttribute( request, sessionAttributeName );
        }
        return sessionAttribute;
    }

    public static Object removeSessionAttribute( HttpServletRequest request, String sessionAttributeName ) {
        Map sessionMap = getSessionMap( request );
        Object object = sessionMap.remove( sessionAttributeName );
        if ( null != object ) {
            log.debug( "Removed from session: \"" + sessionAttributeName + "\": " + object.getClass()
                       + ". Size: " + sessionMap.size() );
        }
        return object;
    }

    public static String getSessionAttributeNameFromRequest( HttpServletRequest request,
                                                             String requestAttributeOrParameterName ) {
        String sessionAttributeName = (String)request.getAttribute( requestAttributeOrParameterName );
        if ( null == sessionAttributeName ) {
            sessionAttributeName = request.getParameter( requestAttributeOrParameterName );
        }
        return sessionAttributeName;
    }
}
