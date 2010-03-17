package com.imcode.imcms.maintenance;

import javax.servlet.*;
import java.io.IOException;

/**
 * 
 */
public class MaintenanceFilter implements Filter {

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.getRequestDispatcher("/WEB-INF/maintenance/index.jsp").forward(servletRequest, servletResponse);
    }

    public void destroy() {
        
    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }    
}
