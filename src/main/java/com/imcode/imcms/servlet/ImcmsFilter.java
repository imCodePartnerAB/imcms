package com.imcode.imcms.servlet;

import com.imcode.imcms.api.DocGetterCallbackUtil;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.Set;

import javax.mail.Session;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

/**
 * Front filter - initializes Imcms and intercepts all requests.
 *
 * Request handling depends on a current imcms mode:
 * When in maintenance mode then service unavailable error is sent.
 * Otherwise request is processed normally.
 *
 * @see imcode.server.Imcms
 */
public class ImcmsFilter implements Filter {

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";

    private final Logger logger = Logger.getLogger(getClass());

    /** Sends service unavailable. */
    private Filter maintenanceModeFilter = new Filter() {

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {
            HttpServletResponse response = (HttpServletResponse)servletResponse;

            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        public void init(FilterConfig filterConfig) throws ServletException {}
        public void destroy() {}
    };

    /** Processes request normally. */
    private Filter normalModeFilter = new Filter() {

        public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
                throws IOException, ServletException {
            try {
                req.setCharacterEncoding(Imcms.DEFAULT_ENCODING);

                HttpServletRequest request = (HttpServletRequest)req;
                HttpServletResponse response = (HttpServletResponse)res;

                HttpSession session = request.getSession();

                ImcmsServices service = Imcms.getServices();

                if ( session.isNew() ) {
                    service.incrementSessionCounter();
                    setDomainSessionCookie( response, session );
                }

                String workaroundUriEncoding = service.getConfig().getWorkaroundUriEncoding();
                FallbackDecoder fallbackDecoder = new FallbackDecoder(Charset.forName(Imcms.DEFAULT_ENCODING),
                                                                      null != workaroundUriEncoding ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset());
                if ( null != workaroundUriEncoding ) {
                    request = new UriEncodingWorkaroundWrapper(request, fallbackDecoder);
                }

                UserDomainObject user = Utility.getLoggedOnUser(request) ;
                if ( null == user ) {
                    user = service.verifyUserByIpOrDefault(request.getRemoteAddr()) ;
                    assert user.isActive() ;
                    Utility.makeUserLoggedIn(request, user);

                // todo: optimize;
                // In case system denies multiple sessions for the same logged-in user and the user was not authenticated by an IP:
                // -invalidates current session if it does not match to last user's session
                // -redirects to the login page.
                } else if (!user.isDefaultUser() && !user.isAuthenticatedByIp() && service.getConfig().isDenyMultipleUserLogin()) {
                    String sessionId = session.getId();
                    String lastUserSessionId = service
                            .getImcmsAuthenticatorAndUserAndRoleMapper()
                            .getUserSessionId(user);

                    if (lastUserSessionId != null && !lastUserSessionId.equals(sessionId)) {
                        VerifyUser.forwardToLoginPageTooManySessions(request, response);

                        return;
                    }
                }

                ResourceBundle resourceBundle = Utility.getResourceBundle(request);
                Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

                Imcms.setUser(user);
                DocGetterCallbackUtil.createAndSetDocGetterCallback(request, Imcms.getServices(), user);

                Utility.initRequestWithApi(request, user);

                NDC.setMaxDepth( 0 );
                String contextPath = request.getContextPath();
                if ( !"".equals( contextPath ) ) {
                    NDC.push( contextPath );
                }
                NDC.push( StringUtils.substringAfterLast( request.getRequestURI(), "/" ) );

                handleDocumentUri(filterChain, request, response, service, fallbackDecoder);
                NDC.setMaxDepth( 0 );
            } finally {
                Imcms.removeUser();
            }
        }

        public void init(FilterConfig filterConfig) throws ServletException {}
        public void destroy() {}
    };


    /** Set to maintenanceModeFilter or normalModeFilter. */
    private volatile Filter delegateFilter;


    /**
     * Routes invocations to the delegate filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        delegateFilter.doFilter(request, response, filterChain);
    }


    /**
     * Initializes Imcms and attempts to starts Imcms.
     *
     * Also creates ImcmsModeListener which changes delegateFilter according to Imcms mode. 
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            logger.info("Starting CMS.");
            Imcms.start();
            delegateFilter = normalModeFilter;
        } catch (Exception e) {
            logger.error("Error starting CMS.", e);
            delegateFilter = maintenanceModeFilter;
        }
    }


    public void destroy() {
        Imcms.stop();
    }
    

    /**
     * When request path matches a physical or mapped resource then processes request normally.
     * Otherwise threats a request as a document request.
     * @see GetDoc#viewDoc(String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 
     *
     * @param chain
     * @param request
     * @param response
     * @param service
     * @param fallbackDecoder
     * @throws ServletException
     * @throws IOException
     */
    private void handleDocumentUri(FilterChain chain, HttpServletRequest request, ServletResponse response,
                                   ImcmsServices service, FallbackDecoder fallbackDecoder) throws ServletException, IOException {
        String path = Utility.fallbackUrlDecode(request.getRequestURI(), fallbackDecoder) ;
        path = StringUtils.substringAfter( path, request.getContextPath() ) ;
        ServletContext servletContext = request.getSession().getServletContext();
        Set resourcePaths = servletContext.getResourcePaths(path);
        
        if (resourcePaths == null || resourcePaths.size() == 0) {
            String documentIdString = getDocumentIdString(service, path);
            
            DocumentDomainObject document = service.getDocumentMapper().getDocument(documentIdString);
            
            if (null != document) {
                try {
                    GetDoc.viewDoc( document, request, (HttpServletResponse)response );
                    return ;
                } catch( NumberFormatException nfe ) {}
            }
        }
        
        chain.doFilter( request, response );
    }

    public static String getDocumentIdString(ImcmsServices service, String path) {
        String documentPathPrefix = service.getConfig().getDocumentPathPrefix() ;
        String documentIdString = null ;
        if ( StringUtils.isNotBlank( documentPathPrefix ) && path.startsWith( documentPathPrefix )) {
            documentIdString = path.substring( documentPathPrefix.length());
            if (documentIdString.endsWith( "/" ) ) {
                documentIdString = documentIdString.substring(0,documentIdString.length()-1);
            }
        }
        return documentIdString;
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
}