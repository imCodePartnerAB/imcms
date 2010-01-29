package com.imcode.imcms.servlet;

import com.imcode.imcms.api.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;

import java.io.IOException;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.imcode.imcms.api.I18nLanguage;

/**
 * Front filter - intercepts all requests expect backdoor.
 *
 * Also initializes and starts Imcms.
 *
 * @see imcode.server.Imcms
 */
public class ImcmsFilter implements Filter, ImcmsListener {


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

        public void doFilter(ServletRequest r, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {
            r.setCharacterEncoding(Imcms.DEFAULT_ENCODING);

            HttpServletRequest request = (HttpServletRequest) r;

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
            }

            ResourceBundle resourceBundle = Utility.getResourceBundle(request);
            Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

            updateDocRequest(request, session, user);

            Utility.initRequestWithApi(request, user);

            NDC.setMaxDepth( 0 );
            String contextPath = request.getContextPath();
            if ( !"".equals( contextPath ) ) {
                NDC.push( contextPath );
            }
            NDC.push( StringUtils.substringAfterLast( request.getRequestURI(), "/" ) );

            handleDocumentUri(filterChain, request, response, service, fallbackDecoder);
            NDC.setMaxDepth( 0 );
        }

        public void init(FilterConfig filterConfig) throws ServletException {}
        public void destroy() {}
    };


    /** Set to maintenanceModeFilter or cmsModeFilter. */
    private volatile Filter delegateFilter;


    /**
     * Routes invocation to the delegate filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        delegateFilter.doFilter(request, response, filterChain);
    }


    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        File path = new File(servletContext.getRealPath("/"));

        logEnvironment(servletContext);

        Imcms.setPath(path);
        Imcms.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
        Imcms.addListener(this);

        try {
            logger.info("Starting CMS.");
            Imcms.start();
            Imcms.setNormalMode();
        } catch (Exception e) {
            logger.error("Error starting CMS.", e);
            Imcms.setMaintenanceMode();
        }
    }


    public void destroy() {
        Imcms.stop();
    }


    /**
     * Updates delegate filter.
     */
    public void onImcmsModeChange(ImcmsMode newMode) {
        delegateFilter = newMode == ImcmsMode.NORMAL
                ? normalModeFilter
                : maintenanceModeFilter;
    }

    public void onImcmsStart() {}

    public void onImcmsStop() {
        onImcmsModeChange(ImcmsMode.MAINTENANCE);
    }
    
    public void onImcmsStartEx(Exception ex) {
       onImcmsModeChange(ImcmsMode.MAINTENANCE);
    }


    private void handleDocumentUri(FilterChain chain, HttpServletRequest request, ServletResponse response,
                                   ImcmsServices service, FallbackDecoder fallbackDecoder) throws ServletException, IOException {
        String path = Utility.fallbackUrlDecode(request.getRequestURI(), fallbackDecoder) ;
        path = StringUtils.substringAfter( path, request.getContextPath() ) ;
        String documentIdString = getDocumentIdString(service, path);
        ServletContext servletContext = request.getSession().getServletContext();
        Set resourcePaths = servletContext.getResourcePaths(path);
        
        if (resourcePaths == null || resourcePaths.size() == 0) {
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

    
    public void logEnvironment(ServletContext servletContext) {
        logger.info("Servlet Engine: " + servletContext.getServerInfo());

        String[] systemPropertyNames = new String[] {
                "java.version",
                "java.vendor",
                "java.class.path",
                "os.name",
                "os.arch",
                "os.version",
        };

        for ( int i = 0; i < systemPropertyNames.length; i++ ) {
            String systemPropertyName = systemPropertyNames[i];
            logger.info(systemPropertyName + ": " + System.getProperty(systemPropertyName));
        }
    }


    /**
     *
     * @param request
     * @param session
     * @param user
     *
     * todo: add security check, not all users can switch mode or request custom document version.
     */
    private void updateDocRequest(HttpServletRequest request, HttpSession session, UserDomainObject user) {
        DocumentRequest newDocRequest;
        DocumentRequest oldDocRequest = (DocumentRequest)session.getAttribute(ImcmsConstants.SESSION_ATTR__DOC_REQUEST);

        String languageCode = request.getParameter(ImcmsConstants.REQUEST_PARAM__LANGUAGE);
        String docIdStr = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID);
        String docVersionNoStr = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION_NO);
        String docVersionModeStr = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION_MODE);

        if (languageCode == null && docIdStr == null && docVersionNoStr == null && docVersionModeStr == null) {
            newDocRequest = oldDocRequest != null
                    ? oldDocRequest
                    : new DocumentRequest(user, Imcms.getI18nSupport().getDefaultLanguage());
        } else {
            I18nLanguage language = null;

            if (languageCode != null) {
                language = Imcms.getI18nSupport().getByCode(languageCode);
            } else if (oldDocRequest != null) {
                language = oldDocRequest.getLanguage();
            } else {
                if (language == null) {// && /*user.isDefaultUser() && */
                    Map<String, I18nLanguage> i18nHosts = Imcms.getI18nSupport().getHosts();

                    if (i18nHosts.size() > 0) {
                        String hostname = request.getServerName();
                        language = i18nHosts.get(hostname);

                        if (logger.isTraceEnabled()) {
                            logger.trace("Hostname [" + hostname + "] mapped to language [" + language + "].");
                        }
                    }
                }

                if (language == null) {
                    language = Imcms.getI18nSupport().getDefaultLanguage();
                }
            }

            if (docIdStr != null && docVersionNoStr != null) {
                try {
                    Integer docId = Integer.parseInt(docIdStr);
                    Integer docVersionNo = Integer.parseInt(docVersionNoStr);

                    newDocRequest = new DocumentRequest.CustomDocRequest(user, language, docId, docVersionNo);
                } catch (NumberFormatException e) {
                    throw new AssertionError(e);
                }
            } else if (docVersionModeStr != null) {
                if (docVersionModeStr.toUpperCase().charAt(0) == 'W') {
                    newDocRequest = new DocumentRequest.WorkingDocRequest(user, language);
                } else {
                    newDocRequest = new DocumentRequest(user, language);
                }
            } else {
                newDocRequest = oldDocRequest;
                newDocRequest.setLanguage(language);
            }
        }

        session.setAttribute(ImcmsConstants.SESSION_ATTR__DOC_REQUEST, newDocRequest);

        Imcms.setDocumentRequest(newDocRequest);
    }
}