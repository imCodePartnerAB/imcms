package com.imcode.imcms.servlet;

import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

import static imcode.server.ImcmsConstants.API_PREFIX;
import static imcode.server.ImcmsConstants.LOGIN_URL;
import static imcode.util.Utility.getUserLanguageFromCookie;
import static imcode.util.Utility.writeUserLanguageCookie;

/**
 * Front filter.
 * <p>
 *
 * @see imcode.server.Imcms
 */
public class ImcmsSetupFilter implements Filter {

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    public static final String USER_LANGUAGE_IN_COOKIE_NAME = "userLanguage";

    private static final String USER_REMOTE_ADDRESS = "userRemoteAddress";
    private static final String USER_AGENT_BROWSER = "User-Agent";

    private final Logger logger = LogManager.getLogger(getClass());
    private FilterDelegate filterDelegate;

    public static void updateUserDocGetterCallback(HttpServletRequest request, UserDomainObject user) {
        DocGetterCallback docGetterCallback = user.getDocGetterCallback();
        docGetterCallback.setLanguage(Imcms.getLanguage());

        final String stringDocId = StringUtils.trimToEmpty(request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_ID));
        final Integer docId = stringDocId.isEmpty() ? null : Integer.valueOf(stringDocId);
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
                    Integer versionNo = versionStr.isEmpty() ? null : Integer.valueOf(versionStr);
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

            final String contextPath = request.getContextPath();

            if (request.getRequestURI().matches(contextPath.concat(LOGIN_URL) + "/?+")) {
                request.getRequestDispatcher(API_PREFIX.concat(LOGIN_URL)).forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            ImcmsServices service = Imcms.getServices();

            if (session.isNew()) {
                session.setAttribute(USER_REMOTE_ADDRESS, request.getRemoteAddr());
                session.setAttribute(USER_AGENT_BROWSER, request.getHeader(USER_AGENT_BROWSER));
                service.incrementSessionCounter();
                setDomainSessionCookie(response, session);

            } else if (isRemoteAddressInvalid(request, response)) {
                request.getRequestDispatcher(API_PREFIX.concat(LOGIN_URL)).forward(request, response);
                return;
            }

            request = new UriEncodingWorkaroundWrapper(request, Imcms.getDefaultFallbackDecoder());

            UserDomainObject user = Utility.getLoggedOnUser(request);
            final ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper();

            if (null == user) {
                user = userAndRoleMapper.getDefaultUser();
                user.setLanguageIso639_2(service.getLanguageMapper().getDefaultLanguage());
                assert user.isActive();

                Imcms.setUser(user);
                Imcms.setLanguage(Utility.getUserLanguageFromCookie(request.getCookies()));
                Utility.makeUserLoggedIn(request, response, user);
            } else {
//                final String login = req.getParameter(REQUEST_PARAMETER__USERNAME);
//                final String password = req.getParameter(REQUEST_PARAMETER__PASSWORD);
//                final UserDomainObject userToCheckAccess;
//                if (null != login && null != password) {
//                    userToCheckAccess = service.verifyUser(login, password);
//                } else {
//                    userToCheckAccess = user;
//                }
//                if (redirectToLoginIfRestricted(request, response, userAndRoleMapper, userToCheckAccess)) return;

                if (!user.isDefaultUser() && !user.isAuthenticatedByIp() && service.getConfig().isDenyMultipleUserLogin()) {
                    String sessionId = session.getId();
                    String lastUserSessionId = userAndRoleMapper.getUserSessionId(user);

                    if (lastUserSessionId != null && !lastUserSessionId.equals(sessionId)) {
                        VerifyUser.forwardToLoginPageTooManySessions(request, response);
                        return;
                    }
                }

                if (user.isImcmsExternal()) {
                    service.getAuthenticationProvidersService()
                            .getAuthenticationProvider(user.getExternalProviderId())
                            .updateAuthData(request);
                }

                ResourceBundle resourceBundle = Utility.getResourceBundle(request);
                Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));
                Imcms.setUser(user);
                Imcms.setLanguage(updateUserRelatedLanguageAndWriteLangCookie(request, response, service));
            }

            ImcmsSetupFilter.updateUserDocGetterCallback(request, user);

            Utility.initRequestWithApi(request, user);

            session.removeAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);

            filterChain.doFilter(request, response);
        } finally {
            Imcms.removeUser();
        }
    }

    private static Language updateUserRelatedLanguageAndWriteLangCookie(HttpServletRequest request, HttpServletResponse response, ImcmsServices services) {
        final String path = Utility.updatePathIfEmpty(Utility.decodePathFromRequest(request, Imcms.getDefaultFallbackDecoder()));
        final String documentIdString = Utility.extractDocumentIdentifier(path);

        final CommonContentService commonContentService = services.getCommonContentService();
		final LanguageService languageService = services.getLanguageService();
        final String requestedLangCode = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);

        Language language = null;
        if (StringUtils.isNotEmpty(requestedLangCode)) {
            language = languageService.findByCode(requestedLangCode);
            writeUserLanguageCookie(response, language.getCode());

        } else if (commonContentService.existsByAlias(documentIdString)) {
            final DocumentDomainObject document = services.getDocumentMapper().getDocument(documentIdString);
            if (document != null && !document.isDefaultLanguageAliasEnabled()) {    //don't change the user language if the document has one common alias
                language = commonContentService.getByAlias(documentIdString).get().getLanguage();
                writeUserLanguageCookie(response, language.getCode());
            }
		}

        return (language == null) ? getUserLanguageFromCookie(request.getCookies()) : language;
    }

    private boolean redirectToLoginIfRestricted(HttpServletRequest request,
                                                HttpServletResponse response,
                                                ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper,
                                                UserDomainObject userToCheckAccess) throws ServletException, IOException {

        //Ugly resource filter.... to show at least login page
        //auth-providers is allowed api call
        if (!request.getRequestURI().matches(".*(css|jpg|png|gif|js|ico|ttf|auth-providers)$")) {
            if (!userAndRoleMapper.isAllowedToAccess(request.getRemoteAddr(), userToCheckAccess)) {
                Utility.forwardToLogin(request, response);
                return true;
            }
        }
        return false;
    }

    private boolean isRemoteAddressInvalid(final HttpServletRequest request, final HttpServletResponse response) {

        boolean invalidRemoteAddress = false;

        final String cookieUserRemoteAddress = (String) request.getSession().getAttribute(USER_REMOTE_ADDRESS);
        final String cookieUserAgentBrowser = (String) request.getSession().getAttribute(USER_AGENT_BROWSER);

        if (null == cookieUserRemoteAddress) return false;

        if (null == cookieUserAgentBrowser) return false;

        if (!request.getRemoteAddr().equals(cookieUserRemoteAddress) || !request.getHeader(USER_AGENT_BROWSER).equals(cookieUserAgentBrowser)) {

            Arrays.stream(request.getCookies())
                    .filter(cookie -> {
                        final String cookieName = cookie.getName();

                        return cookieName.equals(JSESSIONID_COOKIE_NAME)
                                || cookieName.equals(USER_LANGUAGE_IN_COOKIE_NAME);
                    })
                    .forEach(cookie -> {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);

                        response.addCookie(cookie);
                    });

            invalidRemoteAddress = true;
        }

        return invalidRemoteAddress;
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
