package com.imcode.imcms.filters.xss;

import com.imcode.imcms.filters.xss.wrapper.XSSRequestWrapper;
import imcode.server.Imcms;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static imcode.server.ImcmsConstants.API_PREFIX;

/**
 * Wraps request parameters, headers and body.
 * Use Utility.unescapeValue() to return to the original.
 *
 * The filter can be disabled in the properties (xss-include).
 * Specify the URLs that do not need to be filtered in the properties (xss-exclusions).
 */
public final class XSSProtectionFilter implements Filter {

    private boolean include;
    private final HashSet<String> whiteList = new HashSet<>();
    private final String SUBDIRECTORIES = "/**";    //zero or more 'subdirectories' in url

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        whiteList.add(API_PREFIX + "/texts");
        whiteList.add(API_PREFIX + "/texts/validate");
        whiteList.add(API_PREFIX + "/documents/search");
        whiteList.add(API_PREFIX + "/images/files/search");
        whiteList.add(API_PREFIX + "/files" + SUBDIRECTORIES);

        include = Boolean.parseBoolean(Imcms.getServerProperties().getProperty("xss-include"));

        String xssExclusionsProperty = Imcms.getServerProperties().getProperty("xss-exclusions");
        if(xssExclusionsProperty != null){
            final String[] xssExclusions = xssExclusionsProperty.split(",\\s*");
            whiteList.addAll(Arrays.asList(xssExclusions));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(!include) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if(!request.getMethod().equals(HttpMethod.GET.name()) && urlInWhiteList(request.getRequestURI())){
            filterChain.doFilter(servletRequest, servletResponse);
        }else{
            filterChain.doFilter(new XSSRequestWrapper(request), servletResponse);
        }
    }

    @Override
    public void destroy() {}

    private boolean urlInWhiteList(String url){
        return whiteList.stream()
                .anyMatch(whiteUrl ->
                        whiteUrl.endsWith(SUBDIRECTORIES) ?
                                url.startsWith(whiteUrl.replace(SUBDIRECTORIES, "")) :
                                url.equals(whiteUrl));
    }
}
