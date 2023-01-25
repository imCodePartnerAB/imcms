package com.imcode.imcms.servlet;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.mapping.DocumentMeta;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static imcode.server.ImcmsConstants.*;
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
	    String docLangCode = Optional.ofNullable(request.getSession())
			    .map(httpSession -> StringUtils.trimToEmpty((String) httpSession.getAttribute(REQUEST_PARAM__DOC_LANGUAGE))).orElse("");
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
                session.setAttribute(USER_AGENT_BROWSER, request.getHeader(USER_AGENT_BROWSER));
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

	        final Cookie[] cookies = request.getCookies();

            if (null == user) {
                user = userAndRoleMapper.getDefaultUser();
                user.setLanguageIso639_2(languageMapper.getDefaultLanguage());
                assert user.isActive();
                Utility.makeUserLoggedIn(request, response, user);

	            Imcms.setUser(user);
	            final String userLanguage;
	            if (cookies != null) {
		            final Optional<Cookie> languageCookie = Arrays.stream(cookies)
				            .filter(cookie -> cookie.getName().equals(USER_LANGUAGE_IN_COOKIE_NAME))
				            .findFirst();
		            userLanguage = languageCookie.isPresent() ? languageCookie.get().getValue() : user.getLanguage();
	            } else {
		            userLanguage = user.getLanguage();
	            }
	            Imcms.setLanguage(languageMapper.getLanguageByCode(userLanguage));
            } else {
//                final String login = req.getParameter(REQUEST_PARAMETER__USERNAME);
//                final String password = req.getParameter(REQUEST_PARAMETER__PASSWORD);


//                final UserDomainObject userToCheckAccess;
//                if (null != login && null != password) {
//                    userToCheckAccess = service.verifyUser(login, password);
//                } else {
//                    userToCheckAccess = user;
//                }
//
//
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

	            final String path = Utility.updatePathIfEmpty(Utility.decodePathFromRequest(request, fallbackDecoder));
	            final String documentId = getDocumentIdString(Imcms.getServices(), path);

	            Language aliasLanguage = null;
	            DocumentDomainObject documentDomainObject = null;
	            final Integer id = service.getDocumentMapper().toDocumentId(documentId);
	            if (id != null) {
		            final Version latestDocVersion = service.getVersionService().getLatestVersion(id);
		            final List<CommonContent> commonContentList = service.getCommonContentService().getOrCreateCommonContents(id, latestDocVersion.getNo());
		            documentDomainObject = service.getDocumentMapper().getDefaultDocument(id);
		            aliasLanguage = commonContentList.stream()
				            .filter(commonContent -> documentId.equalsIgnoreCase(commonContent.getAlias()))
				            .map(CommonContent::getLanguage)
				            .findFirst()
				            .orElse(null);
	            }

	            final boolean showInDefaultLanguageMode = Optional.ofNullable(documentDomainObject)
			            .map(document -> document.getDisabledLanguageShowMode().equals(DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE))
			            .orElse(false);

	            final String requestedLangCode = request.getParameter(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);
	            final String defaultLanguageCode = service.getLanguageService().getDefaultLanguage().getCode();

	            if (StringUtils.isNotEmpty(requestedLangCode)) {
		            writeUserLanguageCookie(response, requestedLangCode);
		            Imcms.setLanguage(languageMapper.getLanguageByCode(requestedLangCode));
		            session.setAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE, requestedLangCode);

	            } else if (aliasLanguage != null) {
		            writeUserLanguageCookie(response, aliasLanguage.getCode());
		            Imcms.setLanguage(languageMapper.getLanguageByCode(aliasLanguage.getCode()));
		            session.setAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE, aliasLanguage.getCode());

				} else if (showInDefaultLanguageMode) {
		            writeUserLanguageCookie(response, user.getLanguage());
		            Imcms.setLanguage(languageMapper.getLanguageByCode(user.getLanguage()));
		            session.setAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE, user.getLanguage());

				} else if (cookies != null) {
		            Arrays.stream(cookies)
				            .filter(cookie -> cookie.getName().equals(USER_LANGUAGE_IN_COOKIE_NAME))
				            .findFirst()
				            .ifPresentOrElse(
						            cookie -> Imcms.setLanguage(languageMapper.getLanguageByCode(cookie.getValue())),
						            () -> Imcms.setLanguage(languageMapper.getLanguageByCode(defaultLanguageCode)));
	            } else {
		            Imcms.setLanguage(languageMapper.getLanguageByCode(defaultLanguageCode));
		            writeUserLanguageCookie(response, defaultLanguageCode);
	            }
            }

	        ImcmsSetupFilter.updateUserDocGetterCallback(request, service, user);

	        Utility.initRequestWithApi(request, user);

	        session.removeAttribute(ImcmsConstants.REQUEST_PARAM__DOC_LANGUAGE);

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