package com.imcode.imcms.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.ContentManagementSystem;

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
