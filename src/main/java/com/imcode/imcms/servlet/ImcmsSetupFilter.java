package com.imcode.imcms.servlet;

import com.google.common.primitives.Ints;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Front filter.
 * <p>
 *
 * @see imcode.server.Imcms
 */
public class ImcmsSetupFilter implements Filter {

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    public static final String USER_LOGGED_IN_COOKIE_NAME = "userLoggedIn";
    public static final String USER_LANGUAGE_IN_COOKIE_NAME = "userLanguage";

    private final Logger logger = Logger.getLogger(getClass());
    private FilterDelegate filterDelegate;

    public static String getDocumentIdString(ImcmsServices service, String path) {
        String documentPathPrefix = service.getConfig().getDocumentPathPrefix();
        String documentIdString = null;
        if (StringUtils.isNotBlank(documentPathPrefix) && path.startsWith(documentPathPrefix)) {
            documentIdString = path.substring(documentPathPrefix.length());
            if (documentIdString.endsWith("/")) {
                documentIdString = documentIdString.substring(0, documentIdString.length() - 1);
            }
        }
        return documentIdString;
    }

    public static void updateUserDocGetterCallback(HttpServletRequest request, ImcmsServices services, UserDomainObject user) {
        DocGetterCallback docGetterCallback = user.getDocGetterCallback();

        DocumentLanguages dls = services.getDocumentLanguages();
        DocumentLanguage defaultLanguage = dls.getDefault();
        String docLangCode = StringUtils.trimToEmpty(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE));
        DocumentLanguage preferredLanguage = Optional.ofNullable(dls.getByCode(docLangCode))
                .orElse(Optional.ofNullable(docGetterCallback.getLanguage())
                        .orElse(Optional.ofNullable(dls.getForHost(request.getServerName()))
                                .orElse(defaultLanguage)));

        docGetterCallback.setLanguage(preferredLanguage);

        Integer docId = Ints.tryParse(StringUtils.trimToEmpty(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID)));
        String versionStr = StringUtils.trimToNull(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_VERSION));

        if (null != docId && null != versionStr) {
            switch (versionStr.toLowerCase()) {
                case ImcmsConstants.REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_DEFAULT:
                    docGetterCallback.setDefault(docId);
                    break;

                case ImcmsConstants.REQUEST_PARAM_VALUE__DOC_VERSION__ALIAS_WORKING:
                    docGetterCallback.setWorking(docId);
                    break;

                default:
                    Integer versionNo = Ints.tryParse(versionStr);
                    if (null != versionNo) {
                        docGetterCallback.setCustom(docId, versionNo);
                    }
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {

        try {
            logger.info("Starting CMS.");
            Imcms.invokeStart();
            filterDelegate = this::doFilterNormally;

        } catch (Exception e) {
            logger.error("Error starting CMS.", e);
            filterDelegate = this::doFilterSendError;
        }
    }

    @Override
    public void destroy() {
        logger.info("Stopping CMS.");
    }

    /**
     * Routes invocations to the delegate filter.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        filterDelegate.doFilter(request, response, filterChain);
    }

    private void doFilterSendError(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }

    private void doFilterNormally(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            HttpSession session = request.getSession();
            ImcmsServices service = Imcms.getServices();

            if (session.isNew()) {
                service.incrementSessionCounter();
                setDomainSessionCookie(response, session);
            }

            String workaroundUriEncoding = service.getConfig().getWorkaroundUriEncoding();
            FallbackDecoder fallbackDecoder = new FallbackDecoder(Charset.forName(Imcms.DEFAULT_ENCODING),
                    null != workaroundUriEncoding ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset());
            if (null != workaroundUriEncoding) {
                request = new UriEncodingWorkaroundWrapper(request, fallbackDecoder);
            }

            UserDomainObject user = Utility.getLoggedOnUser(request);
            if (null == user) {
                user = service.verifyUserByIpOrDefault(request.getRemoteAddr());
                assert user.isActive();
                Utility.makeUserLoggedIn(request, user);

                // todo: optimize;
                // In case system denies multiple sessions for the same logged-in user and the user was not authenticated by an IP:
                // -invalidates current session if it does not match to last user's session
                // -redirects to the login page.
            } else {
                if (!user.isDefaultUser() && !user.isAuthenticatedByIp() && service.getConfig().isDenyMultipleUserLogin()) {
                    String sessionId = session.getId();
                    String lastUserSessionId = service
                            .getImcmsAuthenticatorAndUserAndRoleMapper()
                            .getUserSessionId(user);

                    if (lastUserSessionId != null && !lastUserSessionId.equals(sessionId)) {
                        VerifyUser.forwardToLoginPageTooManySessions(request, response);
                        return;
                    }
                }
                //Adding cookie to find out is user logged in
                if (!user.isDefaultUser()) {
                    Cookie cookie = new Cookie(USER_LOGGED_IN_COOKIE_NAME, Boolean.toString(true));
                    cookie.setMaxAge(session.getMaxInactiveInterval());
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }

            ResourceBundle resourceBundle = Utility.getResourceBundle(request);
            Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

            Imcms.setUser(user);

            final LanguageMapper languageMapper = service.getLanguageMapper();
            final String requestedLangCode = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);

            if (requestedLangCode != null) {
                Imcms.setLanguage(languageMapper.getLanguageByCode(requestedLangCode));

            } else {
                final Cookie[] cookies = request.getCookies();

                if (cookies != null) {
                    final Optional<Cookie> userLanguageCookie = Arrays.stream(cookies)
                            .filter(cookie -> cookie.getName().equals(USER_LANGUAGE_IN_COOKIE_NAME))
                            .findFirst();

                    final String langCode;

                    if (userLanguageCookie.isPresent()) {
                        langCode = userLanguageCookie.get().getValue();

                    } else {
                        langCode = LanguageMapper.convert639_2to639_1(user.getLanguageIso639_2());

                        final Cookie newUserLanguageCookie = new Cookie(USER_LANGUAGE_IN_COOKIE_NAME, langCode);
                        newUserLanguageCookie.setMaxAge(session.getMaxInactiveInterval());
                        newUserLanguageCookie.setPath("/");

                        response.addCookie(newUserLanguageCookie);
                    }

                    final Language language = languageMapper.getLanguageByCode(langCode);
                    Imcms.setLanguage(language);
                } else {
                    Imcms.setLanguage(languageMapper.getLanguageByCode(user.getLanguage()));
                }
            }

            ImcmsSetupFilter.updateUserDocGetterCallback(request, service, user);

            Utility.initRequestWithApi(request, user);

            NDC.setMaxDepth(0);
            String contextPath = request.getContextPath();
            if (!"".equals(contextPath)) {
                NDC.push(contextPath);
            }
            NDC.push(StringUtils.substringAfterLast(request.getRequestURI(), "/"));

            handleDocumentUri(filterChain, request, response, service, fallbackDecoder);
            NDC.setMaxDepth(0);
        } finally {
            Imcms.removeUser();
        }
    }

    /**
     * When request path matches a physical or mapped resource then processes request normally.
     * Otherwise threats a request as a document request.
     */
    private void handleDocumentUri(FilterChain chain,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   ImcmsServices service,
                                   FallbackDecoder fallbackDecoder)
            throws ServletException, IOException {

        String path = Utility.fallbackUrlDecode(request.getRequestURI(), fallbackDecoder);
        path = StringUtils.substringAfter(path, request.getContextPath());
        final Set resourcePaths = request.getSession().getServletContext().getResourcePaths(path);

        if (resourcePaths == null || resourcePaths.size() == 0) {
            final String documentIdString = getDocumentIdString(service, path);
            final String langCode = Imcms.getUser().getDocGetterCallback().getLanguage().getCode();
            final DocumentDomainObject document = service.getDocumentMapper()
                    .getVersionedDocument(documentIdString, langCode, request);

            request.setAttribute("contextPath", request.getContextPath());
            request.setAttribute("language", LanguageMapper.convert639_1to639_2(langCode));

            if (null != document) {
                if (Utility.isTextDocument(document)) {
                    final String newPath = "/api/viewDoc" + request.getServletPath();
                    request.getRequestDispatcher(newPath).forward(request, response);

                } else {
                    GetDoc.viewDoc(document, request, response);
                }

                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void setDomainSessionCookie(ServletResponse response, HttpSession session) {

        String domain = Imcms.getServices().getConfig().getSessionCookieDomain();
        if (StringUtils.isNotBlank(domain)) {
            Cookie cookie = new Cookie(JSESSIONID_COOKIE_NAME, session.getId());
            cookie.setDomain(domain);
            cookie.setPath("/");
            ((HttpServletResponse) response).addCookie(cookie);
        }
    }

    @FunctionalInterface
    private interface FilterDelegate {
        void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
                throws IOException, ServletException;
    }
}