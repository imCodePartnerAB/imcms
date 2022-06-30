package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SuperAdminFilter implements Filter {

    public void init(FilterConfig filterConfig) {}

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

	    final UserDomainObject user = Imcms.getUser();
        if (!user.isSuperAdmin()) {
	        writeResponseMessage(servletRequest,servletResponse, "You need to be superadmin to access this resource.");
			return;
        }

	    final String endpoint = ((HttpServletRequest) servletRequest).getRequestURI();
	    if (endpoint.startsWith("/api/files") && !Imcms.getServices().getAccessService().hasUserFileAdminAccess(user.getId())) {
		    writeResponseMessage(servletRequest, servletResponse, "You do not have access to this resource.");
		    return;
		}

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {}

	private void writeResponseMessage(ServletRequest servletRequest, ServletResponse servletResponse, String message) throws IOException {
		((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
		servletResponse.getWriter().write(message);
	}
}
