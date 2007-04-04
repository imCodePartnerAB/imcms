package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SuperAdminFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(servletRequest);
        if (!cms.getCurrentUser().isSuperAdmin()) {
            (( HttpServletResponse)servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
            servletResponse.getWriter().write("You need to be superadmin to access this resource.");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}
