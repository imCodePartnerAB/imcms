package com.imcode.imcms.servlet;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

public final class XssFilter implements Filter {

    public static AtomicBoolean isVisitFilter = new AtomicBoolean(false);

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        Enumeration<String> parameterNames =  request.getParameterNames();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;


        if (parameterNames.hasMoreElements() && httpRequest.getMethod().equals("GET")) {
            String newUrl = encodeParametersRequest(httpRequest, parameterNames);

            if (!isVisitFilter.get()) {
                isVisitFilter.set(true);
                servletResponse.sendRedirect(newUrl);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
    }

    private String encodeParametersRequest(HttpServletRequest request, Enumeration<String> parameterNames) {

        final StringBuffer requestURL = request.getRequestURL();

        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();

            String encodeValue = Arrays.stream(request.getParameterValues(paramName))
                    .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                    .map(StringEscapeUtils::escapeHtml4)
                    .findFirst().orElse("");

            requestURL.append(handleParameterForRequest(requestURL, paramName, encodeValue));
        }

        return requestURL.toString();
    }

    private String handleParameterForRequest(StringBuffer requestURL, String paramName, String value) {
        return (requestURL.toString().contains("?"))
                ? String.format("&%s=%s", paramName, value)
                : String.format("?%s=%s", paramName, value);
    }
}
