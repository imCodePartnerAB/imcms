package com.imcode.imcms.maintenance;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Maintenance filter.
 */
public class MaintenanceFilter implements Filter {

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String servletPath = request.getServletPath();
        // TODO: refactor hardcoded
        String dispatchPath = servletPath.replace("admin", "WEB-INF");

        request.getRequestDispatcher(dispatchPath).forward(request, response);
    }


    public void init(FilterConfig filterConfig) throws ServletException {}


    public void destroy() { }

}
