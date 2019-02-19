package com.imcode.imcms.servlet;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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

import static com.imcode.imcms.servlet.VerifyUser.*;
import static imcode.server.ImcmsConstants.*;

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

    private static final String USER_REMOTE_ADDRESS = "userRemoteAddress";

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
                service.incrementSessionCounter();
                setDomainSessionCookie(response, session);

            } else if (isRemoteAddressInvalid(request, response)) {
                request.getRequestDispatcher(API_PREFIX.concat(LOGIN_URL)).forward(request, response);
                return;
            }

            final String workaroundUriEncoding = service.getConfig().getWorkaroundUriEncoding();
            final FallbackDecoder fallbackDecoder = new FallbackDecoder(
                    Charset.forName(Imcms.DEFAULT_ENCODING),
                    (null != workaroundUriEncoding) ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset()
            );

            if (null != workaroundUriEncoding) {
                request = new UriEncodingWorkaroundWrapper(request, fallbackDecoder);
            }

            UserDomainObject user = Utility.getLoggedOnUser(request);

            final ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper();
            final LanguageMapper languageMapper = service.getLanguageMapper();

            if (null == user) {
                user = userAndRoleMapper.getDefaultUser();
                assert user.isActive();
                Utility.makeUserLoggedIn(request, user);

                Imcms.setUser(user);
                Imcms.setLanguage(languageMapper.getLanguageByCode(user.getLanguage()));


                // todo: optimize;
                // In case system denies multiple sessions for the same logged-in user and the user was not authenticated by an IP:
                // -invalidates current session if it does not match to last user's session
                // -redirects to the login page.
            } else {
                final String login = req.getParameter(REQUEST_PARAMETER__USERNAME);
                final String password = req.getParameter(REQUEST_PARAMETER__PASSWORD);


                final UserDomainObject userToCheckAccess;
                if (null != login && null != password) {
                    userToCheckAccess = service.verifyUser(login, password);
                } else {
                    userToCheckAccess = user;
                }


                if (redirectToLoginIfRestricted(request, response, userAndRoleMapper, userToCheckAccess)) return;

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


                //Adding cookie to find out is user logged in
                if (!user.isDefaultUser()) {
                    Cookie cookie = new Cookie(USER_LOGGED_IN_COOKIE_NAME, Boolean.toString(true));
                    cookie.setMaxAge(session.getMaxInactiveInterval());
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }


                ResourceBundle resourceBundle = Utility.getResourceBundle(request);
                Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

                Imcms.setUser(user);


                final String requestedLangCode = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);
                session.setAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE, requestedLangCode);
                final Object langSession = session.getAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);
                final Cookie[] cookies = request.getCookies();
                final Optional<Cookie> userLanguageCookie;
                if (langSession != null) {
                    Imcms.setLanguage(languageMapper.getLanguageByCode(langSession.toString()));
                    final Cookie newUserLanguageCookie = new Cookie(USER_LANGUAGE_IN_COOKIE_NAME, requestedLangCode);
                    newUserLanguageCookie.setMaxAge(session.getMaxInactiveInterval());
                    newUserLanguageCookie.setPath("/");

                    response.addCookie(newUserLanguageCookie);
                    userLanguageCookie = Optional.of(newUserLanguageCookie);
                } else {
                    userLanguageCookie = Arrays.stream(cookies)
                            .filter(cookie -> cookie.getName().equals(USER_LANGUAGE_IN_COOKIE_NAME))
                            .findFirst();
                }

                if (cookies != null) {
                    final String langCode;

                    if (userLanguageCookie.isPresent()) {
                        langCode = (langSession != null) ? langSession.toString() : userLanguageCookie.get().getValue();

                    } else {
                        final String defaultLanguage = service.getConfig().getDefaultLanguage();
                        langCode = LanguageMapper.convert639_2to639_1(defaultLanguage);

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

            session.removeAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);
            ImcmsSetupFilter.updateUserDocGetterCallback(request, service, user);

            Utility.initRequestWithApi(request, user);

            filterChain.doFilter(request, response);
        } finally {
            Imcms.removeUser();
        }
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

        if (null == cookieUserRemoteAddress) return false;

        if (!request.getRemoteAddr().equals(cookieUserRemoteAddress)) {

            Arrays.stream(request.getCookies())
                    .filter(cookie -> {
                        final String cookieName = cookie.getName();

                        return cookieName.equals(JSESSIONID_COOKIE_NAME)
                                || cookieName.equals(USER_LOGGED_IN_COOKIE_NAME)
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