package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.QueryStringDecodingHttpServletRequestWrapper;
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

    public void doFilter( ServletRequest r, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
        r.setCharacterEncoding(Imcms.DEFAULT_ENCODING);

        HttpServletRequest request = (HttpServletRequest) r;
        String method = request.getMethod();
        if ("GET".equals(method) || "HEAD".equals(method)) {
            request = new QueryStringDecodingHttpServletRequestWrapper(request);
        }

        HttpSession session = request.getSession();

        ImcmsServices service = Imcms.getServices();
        if ( session.isNew() ) {
            service.incrementSessionCounter();
            setDomainSessionCookie( response, session );
        }

        UserDomainObject user = Utility.getLoggedOnUser(request) ;
        if ( null == user ) {
            user = service.verifyUserByIpOrDefault(request.getRemoteAddr()) ;
            assert user.isActive() ;
            Utility.makeUserLoggedIn(request, user);
        }

        Utility.initRequestWithApi(request, user);

        NDC.setMaxDepth( 0 );
        String contextPath = request.getContextPath();
        if ( !"".equals( contextPath ) ) {
            NDC.push( contextPath );
        }
        NDC.push( StringUtils.substringAfterLast( request.getRequestURI(), "/" ) );

        handleDocumentUri(chain, request, response, service);
        NDC.setMaxDepth( 0 );
    }

    private void handleDocumentUri(FilterChain chain, HttpServletRequest httpServletRequest, ServletResponse response,
                                   ImcmsServices service
    ) throws ServletException, IOException {
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
                chain.doFilter( httpServletRequest, response );
            }
        } else {
            chain.doFilter( httpServletRequest, response );
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
