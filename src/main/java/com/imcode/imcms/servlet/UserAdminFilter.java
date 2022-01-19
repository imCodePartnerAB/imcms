package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserAdminFilter implements Filter {

    public void init(FilterConfig filterConfig) {}

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        final UserDomainObject user = Imcms.getUser();

        boolean accessToAdminPages = Imcms.getServices()
                .getAccessService()
                .getTotalRolePermissionsByUser(user).isAccessToAdminPages();

        if (!user.isSuperAdmin() && !accessToAdminPages) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
            servletResponse.getWriter().write("You do not have the necessary permission to access this resource.");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {}
}
