package imcode.util;

import javax.servlet.http.HttpServletRequest;

public class HttpSessionUtils {

    private HttpSessionUtils() {
    }

    public static void addObjectToSessionAndSetSessionAttributeNameInRequest( final Object objectToAddToSession,
                                                                              HttpServletRequest request,
                                                                              final String sessionAttributeNameRequestAttributeName ) {
        final String sessionAttributeName = objectToAddToSession.getClass().getName() + "." + System.currentTimeMillis();
        request.getSession().setAttribute( sessionAttributeName, objectToAddToSession );
        request.setAttribute( sessionAttributeNameRequestAttributeName, sessionAttributeName );
    }

    public static Object getObjectFromSessionWithKeyInRequest( HttpServletRequest request,
                                                               String requestAttributeOrParameterName ) {
        String sessionAttributeName = getSessionAttributeNameFromRequest( request, requestAttributeOrParameterName );
        return request.getSession().getAttribute( sessionAttributeName );
    }

    public static void removeObjectFromSessionWithKeyInRequest( HttpServletRequest request,
                                                                 String requestAttributeOrParameterName ) {
        String sessionAttributeName = getSessionAttributeNameFromRequest( request, requestAttributeOrParameterName );
        request.getSession().removeAttribute( sessionAttributeName );
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
