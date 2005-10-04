package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.NDC;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ImcmsSetupFilter implements Filter {

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpSession session = httpServletRequest.getSession();

        ImcmsServices service = Imcms.getServices();
        if ( session.isNew() ) {
            service.incrementSessionCounter();
            setDomainSessionCookie( response, session );
        }

        UserDomainObject user = Utility.getLoggedOnUser(httpServletRequest) ;
        if ( null == user ) {
            user = service.verifyUserByIpOrDefault(request.getRemoteAddr()) ;
            assert user.isActive() ;
            Utility.makeUserLoggedIn(httpServletRequest, user);
        }

        Utility.initRequestWithApi(request, user);

        NDC.setMaxDepth( 0 );
        String contextPath = ( (HttpServletRequest)request ).getContextPath();
        if ( !"".equals( contextPath ) ) {
            NDC.push( contextPath );
        }
        NDC.push( StringUtils.substringAfterLast( ( (HttpServletRequest)request ).getRequestURI(), "/" ) );

        handleDocumentUri( httpServletRequest, service, request, response, chain );
        NDC.setMaxDepth( 0 );
    }

    private void handleDocumentUri( HttpServletRequest httpServletRequest, ImcmsServices service,
                                    ServletRequest request, ServletResponse response, FilterChain chain ) throws ServletException, IOException {
        String path = httpServletRequest.getRequestURI() ;
        path = StringUtils.substringAfter( path, httpServletRequest.getContextPath() ) ;
        String documentPathPrefix = service.getConfig().getDocumentPathPrefix() ;
        String documentIdString = null ;
        if (StringUtils.isNotBlank( documentPathPrefix ) && path.startsWith( documentPathPrefix )) {
            documentIdString = path.substring( documentPathPrefix.length() );
        }
        if (null != documentIdString && NumberUtils.isDigits( documentIdString )) {
            try {
                int documentId = Integer.parseInt( documentIdString ) ;
                GetDoc.output( documentId, httpServletRequest, (HttpServletResponse)response );
            } catch( NumberFormatException nfe ) {
                chain.doFilter( request, response );
            }
        } else {
            chain.doFilter( request, response );
        }
    }

    private void setDomainSessionCookie( ServletResponse response, HttpSession session ) {

        String domain = Imcms.getServices().getConfig().getSessionCookieDomain();
        if (StringUtils.isNotBlank(domain)) {
            Cookie cookie = new Cookie( JSESSIONID_COOKIE_NAME, session.getId());
            cookie.setDomain( domain );
            cookie.setPath( "/" );
            ((HttpServletResponse)response).addCookie( cookie );
        }
    }

    public void init( FilterConfig config ) throws ServletException {
    }

    public void destroy() {
    }

}
