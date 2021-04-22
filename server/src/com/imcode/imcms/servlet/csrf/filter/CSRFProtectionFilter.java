package com.imcode.imcms.servlet.csrf.filter;

import com.imcode.imcms.servlet.csrf.CsrfTokenManager;
import com.imcode.imcms.servlet.csrf.component.CSRFTokenManagerImpl;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CSRFProtectionFilter implements Filter {

    private static final Log log = LogFactory.getLog(CSRFProtectionFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final UserDomainObject user = Utility.getLoggedOnUser(request);
        final CsrfTokenManager csrfTokenManager = new CSRFTokenManagerImpl();

        if (user != null) {
            if (!user.isDefaultUser()) {
                if (!csrfTokenManager.isCorrectTokenForCurrentUser(user, request)) {
                    Utility.makeUserLoggedOut(request);
                    log.error("Potential CSRF detected on user login name: " + user.getLoginName());
                    throw new ServletException("Potential CSRF detected!! Inform a scary sysadmin ASAP!");
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

}
