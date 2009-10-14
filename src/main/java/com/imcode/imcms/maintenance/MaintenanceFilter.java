package com.imcode.imcms.maintenance;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Maintenance filter.
 */
public class MaintenanceFilter implements Filter {


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        request.getRequestDispatcher("/WEB-INF/maintenance/index.jsp").forward(servletRequest, servletResponse);
    }


    public void init(FilterConfig filterConfig) throws ServletException { }


    public void destroy() { }

}
