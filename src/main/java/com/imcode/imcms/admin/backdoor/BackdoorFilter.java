package com.imcode.imcms.admin.backdoor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Backdoor filter.
 */
public class BackdoorFilter implements Filter {

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String uri = request.getRequestURI();
        // TODO: refactor hardcoded
        //String dispatchPath = servletPath.replace("admin", "WEB-INF");
        System.out.println("URI: "
        + uri);

        //request.getRequestDispatcher(dispatchPath).forward(request, response);
        request.getRequestDispatcher(uri).forward(request, response);
    }


    public void init(FilterConfig filterConfig) throws ServletException {}


    public void destroy() { }

}
