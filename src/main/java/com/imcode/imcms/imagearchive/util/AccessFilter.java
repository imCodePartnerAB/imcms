package com.imcode.imcms.imagearchive.util;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AccessFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        setUTF8CharacterEncoding(req, resp);

        if (req instanceof HttpServletRequest) {
            setRequestUrl((HttpServletRequest) req);
        }

        chain.doFilter(req, resp);
    }

    private void setRequestUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null) {
            url += "?" + queryString;
        }

        request.setAttribute("requestUrl", url);
    }

    private void setUTF8CharacterEncoding(ServletRequest request, ServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
    }
}
