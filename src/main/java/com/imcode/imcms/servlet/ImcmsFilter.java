package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.DocumentShowSettings;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;

import java.io.IOException;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ResourceBundle;

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

import com.imcode.imcms.api.DocumentVersionSelector;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;

/**
 * Front filter - intercepts all requests expect backdoor.
 *
 * Also responsible for Imcms initializing.
 *
 * TODO: Move logger configuration routines to Imcms with ability to switch between debug/nondebug? 
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
    private Filter cmsModeFilter = new Filter() {

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

            Imcms.setUser(user);

            updateUserI18nSetting(request, user);
            updateUserShowSettings(request, user);

            ResourceBundle resourceBundle = Utility.getResourceBundle(request);
            Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

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
        Imcms.setServletContext(servletContext);
        Imcms.setImcmsFilter(this);
        Imcms.setWebApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));

        try {
            logger.info("Starting CMS.");
            Imcms.startCms();
            Imcms.setCmsMode();
        } catch (Exception e) {
            logger.error("Error starting CMS.", e);
            Imcms.setMaintenanceMode();
        }
    }


    public void destroy() {
        Imcms.stopCms();
    }


    /**
     * Updates delegate filter.
     */
    public void updateDelegateFilter() {
        delegateFilter = Imcms.getMode() == ImcmsMode.CMS
                ? cmsModeFilter
                : maintenanceModeFilter;
    }


    private void handleDocumentUri(FilterChain chain, HttpServletRequest request, ServletResponse response,
                                   ImcmsServices service, FallbackDecoder fallbackDecoder) throws ServletException, IOException {
        String path = Utility.fallbackUrlDecode(request.getRequestURI(), fallbackDecoder) ;
        path = StringUtils.substringAfter( path, request.getContextPath() ) ;
        String documentIdString = getDocumentIdString(service, path);
        ServletContext servletContext = request.getSession().getServletContext();
        if ( null == servletContext.getResourcePaths(path) ) {
            //DocumentDomainObject document = service.getDocumentMapper().getDocument(documentIdString);
        	UserDomainObject user = Utility.getLoggedOnUser( request );
        	DocumentDomainObject document = service.getDocumentMapper().getDocumentForShowing(documentIdString, user);
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

    /**
     * Updates I18n setting for current user.
     * Changes user's language if requested.
     * Bounds user's language to session.
     *
     * @param request servlet request
     * @param user authenticated user
     *
     * @throws ServletException in case of an error.
     * @see com.imcode.imcms.api.I18nSupport
     */
    private void updateUserI18nSetting(HttpServletRequest request, UserDomainObject user)
    throws ServletException {
    	HttpSession session = request.getSession();
    	I18nLanguage language = (I18nLanguage)session.getAttribute("lang");

        Map<String, I18nLanguage> i18nHosts = Imcms.getI18nHosts();

    	if (language == null && /*user.isDefaultUser() && */i18nHosts.size() > 0) {
    		String hostname = request.getServerName();
    		language = i18nHosts.get(hostname);

    		if (logger.isTraceEnabled()) {
    			logger.trace("Hostname [" + hostname + "] mapped to language [" + language + "].");
    		}
    	}

    	String languageCode = request.getParameter("lang");

    	if (languageCode != null) {
    		language = I18nSupport.getByCode(languageCode);
    	}

    	// TODO: if session does not contain language
    	// do not allow admin operation and forward to front page ??

    	if (language == null) {
    		language = I18nSupport.getDefaultLanguage();
    	}


    	// TODO i18n: remove lang session parameter
    	// request and thread local parameters

		session.setAttribute("lang", language);
		request.setAttribute("currentLanguage", language);

    	I18nSupport.setCurrentLanguage(language);
    }


    /**
     * Updates logged in user's show settings.
     *
     * @param request servlet request
     * @param user authenticated user
     */
    // TODO: Add security check
    // View settings - comment about WORKING/|PUB mode
    private void updateUserShowSettings(HttpServletRequest request, UserDomainObject user) {
        String modeValue = request.getParameter("mode");
        if (modeValue != null) {
        	user.getDocumentShowSettings().setIgnoreI18nShowMode(Boolean.parseBoolean(modeValue.toLowerCase()));
        }

        String version = request.getParameter("version");
        if (version != null) {
        	DocumentShowSettings settings = user.getDocumentShowSettings();

            settings.setVersionSelector(DocumentVersionSelector.getSelector(version));
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
}