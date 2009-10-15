package com.imcode.imcms;

import com.imcode.imcms.servlet.CmsFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import imcode.server.Imcms;

/**
 * Imcms front filter.
 *
 * Must be first filter in the chain configured to intercept all requests.
 */
public class ImcmsFilter implements Filter {

    /** Sends service unavailable. */
    private Filter maintenanceModeFilter = new Filter() {

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {
            HttpServletResponse response = (HttpServletResponse)servletResponse;

            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        public void init(FilterConfig filterConfig) throws ServletException {}

        public void destroy() {}
    };


    /** Processes request normally. */
    private Filter cmsModeFilter = new Filter() {

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {
            filterChain.doFilter(request, response);
        }

        public void init(FilterConfig filterConfig) throws ServletException {}

        public void destroy() {}
    };


    /** Set to maintenanceModeFilter or cmsModeFilter. */
    private volatile Filter delegateFilter;



    /**
     * Routes invocation to the delegate filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        delegateFilter.doFilter(request, response, filterChain);
    }


    public void init(FilterConfig filterConfig) throws ServletException {
        Imcms.setImcmsFilter(this);
        updateDelegateFilter();
    }

    
    public void destroy() { }


    /**
     * Updates delegate filter.
     */
    public void updateDelegateFilter() {
        delegateFilter = Imcms.getMode() == ImcmsMode.CMS
                ? cmsModeFilter
                : maintenanceModeFilter;
    }
}