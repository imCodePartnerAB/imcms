package com.imcode.imcms.servlet;

import com.google.common.html.HtmlEscapers;
import imcode.server.Imcms;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;

public final class XssFilter implements Filter {

    public final boolean include = Boolean.parseBoolean(Imcms.getServerProperties().getProperty("xss-include"));

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if(!include) {
            chain.doFilter(request, response);
            return;
        }

        Enumeration<String> parameterNames =  request.getParameterNames();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if (parameterNames.hasMoreElements() && httpRequest.getMethod().equals("GET")) {
            String url = completeUrl(httpRequest, parameterNames, false);
            String newUrl = completeUrl(httpRequest, request.getParameterNames(), true);

            if (!url.equals(newUrl)) {
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

    private String completeUrl(HttpServletRequest request, Enumeration<String> parameterNames, boolean encode) throws UnsupportedEncodingException {

        final StringBuffer requestURL = request.getRequestURL();

        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();
            String value;
            if(encode){
                value = Arrays.stream(request.getParameterValues(paramName))
                        .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
		                .map(StringEscapeUtils::unescapeHtml)
		                //StringEscapeUtils::escapeHtml brakes UTF-8 characters..
		                .map(s -> HtmlEscapers.htmlEscaper().escape(s))
                        .findFirst().orElse("");
            }else{
                value = request.getParameter(paramName);
            }

            requestURL.append(handleParameterForRequest(requestURL, paramName, URLEncoder.encode(value,"UTF-8")));
        }

        return requestURL.toString();
    }

    private String handleParameterForRequest(StringBuffer requestURL, String paramName, String value) {
        return (requestURL.toString().contains("?"))
                ? String.format("&%s=%s", paramName, value)
                : String.format("?%s=%s", paramName, value);
    }
}
