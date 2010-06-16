package com.imcode.imcms.supervisor;

import javax.servlet.*;
import java.io.IOException;

/**
 * Forwards all request to WEB-INF/supervisor. 
 */
public class SupervisorFilter implements Filter {

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        servletRequest.getRequestDispatcher("/WEB-INF/supervisor/index.jsp").forward(servletRequest, servletResponse);
    }

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}
}
