package com.imcode.imcms.filters;

import imcode.server.ImcmsConstants;
import imcode.util.Utility;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.web.filter.SimpleCachingHeadersPageCachingFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cache Supervisor Filter, desires should chain go through ehcache filter or not
 *
 * @author Serhii from Ubrainians for Imcode
 * 04.11.16
 */
public class ImcmsCacheSupervisor extends SimpleCachingHeadersPageCachingFilter {

    private List<String> cacheURLs = new ArrayList<>();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
                         FilterChain chain) throws ServletException, IOException {
        //todo: implement logic that not changed documents (check header "If-Modified-Since") will be cached too
        if (Utility.containsAny(request.getRequestURI(), cacheURLs)) {
            try {
                super.doFilter(request, response, chain);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doInit(FilterConfig filterConfig) throws CacheException {
        final String cacheMarkersConfig = filterConfig.getInitParameter("cacheMarkers");

        if (StringUtils.isNotBlank(cacheMarkersConfig)) {
            final String[] cacheMarkers = cacheMarkersConfig.split("\\n");
            cacheURLs.addAll(Stream
                    .of(cacheMarkers)
                    .map(String::trim)
                    .collect(Collectors.toList())
            );
        }

        final ServletContext servletContext = filterConfig.getServletContext();
        final ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        final Properties properties = ctx.getBean("imcmsProperties", Properties.class);
        final String imageUrl = properties.getProperty("ImageUrl");
        final String generatedImagesPath = servletContext.getContextPath() + imageUrl + ImcmsConstants.IMAGE_GENERATED_FOLDER;
        cacheURLs.add(generatedImagesPath);

        super.doInit(filterConfig);
    }
}
