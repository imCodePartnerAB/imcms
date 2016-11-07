package com.imcode.imcms.filters;

import imcode.util.Utility;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.web.filter.SimpleCachingHeadersPageCachingFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cache Supervisor Filter, desires should chain go through ehcache filter or not
 *
 * @author Serhii from Ubrainians for Imcode
 *         04.11.16
 */
public class ImcmsCacheSupervisor extends SimpleCachingHeadersPageCachingFilter {

    private List<String> nonCacheURLs = new ArrayList<>();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
                         FilterChain chain) throws ServletException, IOException {

        if (Utility.containsAny(request.getRequestURI(), nonCacheURLs)) {
            chain.doFilter(request, response);

        } else {
            try {
                super.doFilter(request, response, chain);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }

    @Override
    public void doInit(FilterConfig filterConfig) throws CacheException {
        final String noCacheMarkersConfig = filterConfig.getInitParameter("noCacheMarkers");

        if (StringUtils.isNotBlank(noCacheMarkersConfig)) {
            final String[] noCacheMarkers = noCacheMarkersConfig.split("\\n");
            nonCacheURLs.addAll(Stream
                    .of(noCacheMarkers)
                    .map(String::trim)
                    .collect(Collectors.toList())
            );
        }

        super.doInit(filterConfig);
    }
}
