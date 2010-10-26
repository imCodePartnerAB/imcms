package com.imcode.imcms.servlet;

import com.imcode.imcms.api.GetDocumentCallback;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;

import java.io.IOException;
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
 * Front filter - initializes Imcms and intercepts all requests.
 *
 * Request handling depends on a current imcms mode:
 * When in maintenance mode service unavailable error is sent.
 * Otherwise request is processed normally.
 *
 * @see imcode.server.Imcms
 * @see imcode.server.Imcms#mode
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
                
            // todo: add check AND NOT logged in by IP; optimize;
            // In case system denies multiple login for the same user
            // invalidate current session if it does not match to
            // last user's session and redirect a user to the login page.
            } else if (!user.isDefaultUser() && service.getConfig().isDenyMultipleUserLogin()) {
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
            createAndSetDocGetterCallback(request, user);

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
        Imcms.addListener(new ImcmsListener() {

            public void onImcmsStart() { /*ignore*/ }

            public void onImcmsStop() {
                onImcmsModeChange(ImcmsMode.MAINTENANCE);
            }

            public void onImcmsStartEx(Exception ex) {
                onImcmsModeChange(ImcmsMode.MAINTENANCE);
            }

            // Change delegate filter.
            public void onImcmsModeChange(ImcmsMode newMode) {
                delegateFilter = newMode == ImcmsMode.NORMAL
                        ? normalModeFilter
                        : maintenanceModeFilter;
            }
        });

        
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


    /**
     * Create and sets doc getter callback to an user. 
     *
     * All users allowed to change document language but only privileged users allowed to switch
     * document's version.
     *
     * If document version parameter is present but it is a blank then it is treated as default version no.
     * 
     * @param request
     * @param user
     */
    private void createAndSetDocGetterCallback(HttpServletRequest request, UserDomainObject user) {
        GetDocumentCallback docGetterCallback = user.getDocGetterCallback();

        String languageCode = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);
        I18nLanguage defaultLanguage = Imcms.getI18nSupport().getDefaultLanguage();
        I18nLanguage language = languageCode != null
                ? Imcms.getI18nSupport().getByCode(languageCode)
                : docGetterCallback != null
                    ? docGetterCallback.getParams().language
                    : null;

        if (language == null) language = Imcms.getI18nSupport().getForHost(request.getServerName());
        if (language == null) language = defaultLanguage;
        
        GetDocumentCallback.Params params = new GetDocumentCallback.Params(user, language, defaultLanguage);

        // Switch version
        if (!user.isDefaultUser()) {
            String docIdentity = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID);
            String docVersionNoStr = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION);
            
            if (docIdentity != null && docVersionNoStr != null) {
                try {
                    Integer docId = Imcms.getServices().getDocumentMapper().toDocumentId(docIdentity);

                    if (docId == null) {
                        throw new RuntimeException(
                                String.format("Document with identity %s does not exists.", docIdentity));
                    }

                    if (StringUtils.isEmpty(docVersionNoStr)) {
                        docGetterCallback = new GetDocumentCallback.GetDocumentCallbackDefault(params);
                    } else {
                        Integer docVersionNo = Integer.parseInt(docVersionNoStr);

                        if (docVersionNo.equals(DocumentVersion.WORKING_VERSION_NO)) {
                            docGetterCallback = new GetDocumentCallback.GetDocumentCallbackWorking(params, docId);
                        } else {
                            docGetterCallback = new GetDocumentCallback.GetDocumentCallbackCustom(params, docId, docVersionNo);
                        }
                    }
               } catch (NumberFormatException e) {
                    throw new AssertionError(e);
               }
            }
        }

        docGetterCallback = docGetterCallback == null
                ? new GetDocumentCallback.GetDocumentCallbackDefault(params)
                : docGetterCallback.copy(params);

        user.setDocGetterCallback(docGetterCallback);
    }
}