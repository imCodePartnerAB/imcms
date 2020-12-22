package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.server.Imcms;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SuperAdminFilter implements Filter {

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(servletRequest);
        final User currentUser = cms.getCurrentUser();

        boolean isNotAccessToDocumentEditor = !Imcms.getServices()
                .getAccessService()
                .hasUserAccessToDocumentEditor(currentUser.getInternal());

        if (!currentUser.isSuperAdmin() && isNotAccessToDocumentEditor) { // add new feature: if role has access to documents - accessible
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
            servletResponse.getWriter().write("You need to be superadmin to access this resource.");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}
