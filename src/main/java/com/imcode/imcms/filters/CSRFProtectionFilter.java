package com.imcode.imcms.filters;

import com.imcode.imcms.components.CSRFTokenManager;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.http.client.methods.HttpGet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sets same tokens to the cookie and session. Validates these tokens on every request.
 * Changes the token if request is NOT GET and
 * ImcmsCSRFTokenManager.TOKEN_EXPIRATION_TIME_MINUTES have passed since the installation of the previous token.
 *
 * The filter can be disabled in the properties (csrf-include = false).
 */
public final class CSRFProtectionFilter implements Filter {

    private static final Logger log = LogManager.getLogger(CSRFProtectionFilter.class);
    private final CSRFTokenManager csrfTokenManager = Imcms.getServices().getManagedBean(CSRFTokenManager.class);
    private final boolean include = Boolean.parseBoolean(Imcms.getServerProperties().getProperty("csrf-include"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(!include) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final UserDomainObject user = Utility.getLoggedOnUser(request);

        if (user != null && !user.isDefaultUser()) {
            if (csrfTokenManager.isCorrectTokenForCurrentUser(user, request)) {
                if (!request.getMethod().equals(HttpGet.METHOD_NAME) && csrfTokenManager.isTimeExpired(request)) {
                    csrfTokenManager.setUserToken(request, response);
                }
            } else {
                Utility.makeUserLoggedOut(request, response);
                log.error("Potential CSRF detected on user login name: " + user.getLoginName());
                throw new ServletException("Potential CSRF detected! Inform a scary sysadmin ASAP!");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}
}
