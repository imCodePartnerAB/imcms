package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.MissingLoginDataException;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImcmsSetupFilter implements Filter {

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    private static final Logger log = LogManager.getLogger(ImcmsSetupFilter.class);

    private final ExecutorService incrementSessionCounterExecutor = Executors.newSingleThreadExecutor();

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

    public void doFilter(ServletRequest r, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        r.setCharacterEncoding(Imcms.DEFAULT_ENCODING);

        HttpServletRequest request = (HttpServletRequest) r;

        HttpSession session = request.getSession();

        ImcmsServices service = Imcms.getServices();
        if (session.isNew()) {
            incrementSessionCounterExecutor.submit(service::incrementSessionCounter);
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
            final ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper =
                    service.getImcmsAuthenticatorAndUserAndRoleMapper();
            try {
                userAndRoleMapper.checkUserIpAllowed(user, request);

            } catch (UserIpIsNotAllowedException e) {
                userAndRoleMapper.forwardDeniedUserToMessagePage(user, request, response);
                return;
            }

            Utility.makeUserLoggedIn(request, (HttpServletResponse) response, user);
        } else {

			//ldap causes errors because there are no such provider...
	        if (user.isImcmsExternal() && StringUtils.isNotBlank(user.getExternalProviderId())) {
                service.getAuthenticationProviderService()
                        .getAuthenticationProvider(user.getExternalProviderId())
                        .updateAuthData(request);
            }

        }



        ResourceBundle resourceBundle = Utility.getResourceBundle(request);
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));

        Utility.initRequestWithApi(request, user);

	    try (CloseableThreadContext.Instance contextStack = CloseableThreadContext.pushAll(Collections.emptyList())) {
		    String contextPath = request.getContextPath();
		    if (!"".equals(contextPath)) {
			    contextStack.push(contextPath);
		    }
		    contextStack.push(StringUtils.substringAfterLast(request.getRequestURI(), "/"));
		    handleDocumentUri(chain, request, response, service, fallbackDecoder);
		    contextStack.pushAll(Collections.emptyList());
	    }
    }

    private void handleDocumentUri(FilterChain chain, HttpServletRequest request, ServletResponse response,
                                   ImcmsServices service, FallbackDecoder fallbackDecoder) throws ServletException, IOException {
        String path = Utility.fallbackUrlDecode(request.getRequestURI(), fallbackDecoder);
        path = StringUtils.substringAfter(path, request.getContextPath());
        String documentIdString = getDocumentIdString(service, path);
        ServletContext servletContext = request.getSession().getServletContext();
        Set resources = servletContext.getResourcePaths(path);
        if (null == resources || 0 == resources.size()) {
            DocumentDomainObject document = service.getDocumentMapper().getDocument(documentIdString);
            if (null != document) {
                try {
                    GetDoc.viewDoc(document, request, (HttpServletResponse) response);
                    return;
                } catch (NumberFormatException nfe) {
                }
            }
        }
        try {
            chain.doFilter(request, response);

        } catch (MissingLoginDataException e) {
            log.error("Null login data received. User redirected to missingLoginData.jsp.", e);
            throw new MissingLoginDataException(e);
        } catch (Exception e) {
            final Throwable cause = e.getCause();

            if (cause != null && ("Broken pipe".equals(cause.getMessage()))) {
                // seems that user aborted request by himself, so don't worry
                return;
            }

            log.error("Received error: ", e);

            // try to go to the same URL once more with some fixes
            try {
                ServletRequest newRequest = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getParameter(String name) {
                        String parameter = super.getParameter(name);
                        return checkParam(parameter);
                    }

                    @Override
                    public String[] getParameterValues(String paramName) {
                        String[] values = super.getParameterValues(paramName);

                        if (values != null) {
                            for (int index = 0; index < values.length; index++) {
                                values[index] = checkParam(values[index]);
                            }
                        }

                        return values;
                    }

                    private String checkParam(String parameter) {
                        if (parameter != null && parameter.contains(",")) {
                            parameter = parameter.split(",")[0];
                        }
                        return parameter;
                    }
                };
                chain.doFilter(newRequest, response);
            } catch (IllegalStateException ignore) {
            } catch (NullPointerException ignore) {
            } catch (Exception e1) {
                log.error(e1.getMessage(), e1);
                Utility.invokeInternalErrorServletWith(request, (HttpServletResponse) response, e);
            }
        }
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

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
        incrementSessionCounterExecutor.shutdownNow();
    }

}
