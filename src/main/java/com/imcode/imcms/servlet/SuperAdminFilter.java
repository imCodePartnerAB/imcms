package com.imcode.imcms.servlet;

import imcode.server.Imcms;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SuperAdminFilter implements Filter {

    public void init(FilterConfig filterConfig) {}

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        if (!Imcms.getUser().isSuperAdmin()) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
            servletResponse.getWriter().write("You need to be superadmin to access this resource.");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {}
}
