package imcode.util;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.WeakHashMap;

public class HttpSessionUtils {

    private final static Logger log = Logger.getLogger( HttpSessionUtils.class.getName() );

    private final static Map globalMap = new WeakHashMap();

    private static final String SESSION_ATTRIBUTE_NAME__SESSION_MAP = HttpSessionUtils.class.getName() + ".sessionMap";

    private static final int MAX_COUNT__SESSION_OBJECTS = 5;

    private HttpSessionUtils() {
    }

    public static void setSessionAttributeAndSetNameInRequestAttribute( final Object objectToAddToSession,
                                                                        HttpServletRequest request,
                                                                        final String sessionAttributeNameRequestAttributeName ) {
        String sessionAttributeName = getSessionAttributeNameFromRequest( request, sessionAttributeNameRequestAttributeName );
        HttpSession session = request.getSession();
        if ( null == sessionAttributeName || null == session.getAttribute( sessionAttributeName ) ) {
            sessionAttributeName = createUniqueNameForObject( objectToAddToSession );
            put( request, sessionAttributeName, objectToAddToSession );
            request.setAttribute( sessionAttributeNameRequestAttributeName, sessionAttributeName );
        }
    }

    private static void put( HttpServletRequest request, String sessionAttributeName,
                             final Object objectToAddToSession ) {
        globalMap.put( objectToAddToSession, null );
        LRUMap sessionMap = getSessionMap( request );
        if (sessionMap.isFull()) {
            log.warn( "SessionMap is full. Least recently used object will be evicted.") ;
        }
        sessionMap.put( sessionAttributeName, objectToAddToSession );
        log.debug( "Put in session: \"" + sessionAttributeName + "\": " + objectToAddToSession.getClass() + ". Sizes: "
                   + sessionMap.size()
                   + "/"
                   + globalMap.size() );
    }

    private static LRUMap getSessionMap( HttpServletRequest request ) {
        HttpSession session = request.getSession();
        LRUMap sessionMap = (LRUMap)session.getAttribute( SESSION_ATTRIBUTE_NAME__SESSION_MAP );
        if ( null == sessionMap ) {
            sessionMap = new LRUMap(MAX_COUNT__SESSION_OBJECTS);
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
            remove( request, sessionAttributeName );
        }
        return sessionAttribute;
    }

    public static Object removeSessionAttributeWithNameInRequest( HttpServletRequest request,
                                                                  String requestAttributeOrParameterName ) {
        String sessionAttributeName = getSessionAttributeNameFromRequest( request, requestAttributeOrParameterName );
        return remove( request, sessionAttributeName );
    }

    private static Object remove( HttpServletRequest request, String sessionAttributeName ) {
        Map sessionMap = getSessionMap( request );
        Object object = sessionMap.remove( sessionAttributeName );
        if ( null != object ) {
            globalMap.remove( object );
            log.debug( "Removed from session: \"" + sessionAttributeName + "\": " + object.getClass()
                       + ". Sizes: " + sessionMap.size() + "/" + globalMap.size() );
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
