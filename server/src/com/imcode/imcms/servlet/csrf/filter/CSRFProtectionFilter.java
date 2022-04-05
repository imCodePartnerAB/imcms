package com.imcode.imcms.servlet.csrf.filter;

import com.imcode.imcms.servlet.csrf.CsrfTokenManager;
import com.imcode.imcms.servlet.csrf.component.CSRFTokenManagerImpl;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CSRFProtectionFilter implements Filter {

    private static final Logger log = LogManager.getLogger(CSRFProtectionFilter.class);
    private final CsrfTokenManager csrfTokenManager = new CSRFTokenManagerImpl();
    private final boolean include = Boolean.parseBoolean(Imcms.getServerProperties().getProperty("csrf-include"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

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
            if (!csrfTokenManager.isCorrectTokenForCurrentUser(user, request)) {
                Utility.makeUserLoggedOut(request);
                log.error("Potential CSRF detected on user login name: " + user.getLoginName());
                //add default user for system
                Utility.makeUserLoggedIn(request, response, Imcms.getServices().verifyUserByIpOrDefault(request.getRemoteAddr()));
                throw new ServletException("Potential CSRF detected!! Inform a scary sysadmin ASAP!");
            } else {
                if (!request.getMethod().equals(HttpGet.METHOD_NAME) && csrfTokenManager.isTimeExpired(request)) {
                    Utility.setUserToken(request, response, user);
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

}
