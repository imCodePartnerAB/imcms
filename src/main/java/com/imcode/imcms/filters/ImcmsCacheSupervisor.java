package com.imcode.imcms.filters;

import imcode.server.ImcmsConstants;
import imcode.util.Utility;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.Header;
import net.sf.ehcache.constructs.web.PageInfo;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cache Supervisor Filter, desires should chain go through ehcache filter or not
 *
 *  @deprecated this filter is implemented in a strange way, sets weird header values, and caches the entire response.
 *
 * @author Serhii from Ubrainians for Imcode
 * @author Dmytro from Ubrainians for Imcode
 * 04.11.16
 */
@Deprecated
public class ImcmsCacheSupervisor extends SimpleCachingHeadersPageCachingFilter {

    private static final int MILLISECONDS_PER_SECOND = 1000;

    private List<String> cacheURLs = new ArrayList<>();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
                         FilterChain chain) throws ServletException, IOException {
        if (Utility.containsAny(request.getRequestURI(), cacheURLs)) {
            final String cacheKey = calculateKey(request);

            final boolean isDocumentAlreadyCached = blockingCache.isKeyInCache(cacheKey);
            if (isDocumentAlreadyCached) {
                final Element tempPageInfo = blockingCache.get(cacheKey);
                if (null != tempPageInfo) {

                    final String lastModifiedHeader =
                            Optional.ofNullable(request.getHeader("Last-Modified"))
                                    .orElse(request.getHeader("If-Modified-Since"));

                    removeFileFromCacheIfRemovedOrModified(request, cacheKey, lastModifiedHeader);
                }

                try {
                    super.doFilter(request, response, chain);
                    return;
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        }

        chain.doFilter(request, response);
    }

    private void removeFileFromCacheIfRemovedOrModified(HttpServletRequest request, String cacheKey, String lastModifiedHeader) {
        if (null != lastModifiedHeader) {
            final File file = new File(request.getSession().getServletContext().getRealPath(request.getRequestURI()));
            if (file.exists()) {

                final long fileModificationDate = file.lastModified();

                final long responseModifiedDate = LocalDateTime.parse(lastModifiedHeader).atZone(ZoneId.systemDefault()).toEpochSecond();
                if (fileModificationDate == responseModifiedDate) {
                    blockingCache.remove(cacheKey);
                }
            } else {
                blockingCache.remove(cacheKey);
            }
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


    @Override
    protected PageInfo buildPage(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain chain) throws Exception {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final GenericResponseWrapper wrapper = new GenericResponseWrapper(response, outputStream);

        chain.doFilter(request, wrapper);
        wrapper.flush();

        long timeToLiveSeconds = blockingCache.getCacheConfiguration().getTimeToLiveSeconds();

        PageInfo pageInfo = new PageInfo(
                wrapper.getStatus(), wrapper.getContentType(),
                wrapper.getCookies(), outputStream.toByteArray(), true,
                timeToLiveSeconds, wrapper.getAllHeaders()
        );

        final List<Header<? extends Serializable>> headers = pageInfo.getHeaders();

        long ttlMilliseconds = calculateTimeToLiveMilliseconds();

        //Remove any conflicting headers
        for (final Iterator<Header<? extends Serializable>> headerItr = headers.iterator(); headerItr.hasNext(); ) {
            final Header<? extends Serializable> header = headerItr.next();

            final String name = header.getName();

            if ("Last-Modified".equalsIgnoreCase(name)
                    || "Expires".equalsIgnoreCase(name)
                    || "Cache-Control".equalsIgnoreCase(name)
                    || "ETag".equalsIgnoreCase(name)) {
                headerItr.remove();
            }
        }

        //add expires and last-modified headers

        //trim the milliseconds off the value since the header is only accurate down to the second
        long lastModified = pageInfo.getCreated().getTime();
        lastModified = TimeUnit.MILLISECONDS.toSeconds(lastModified);
        lastModified = TimeUnit.SECONDS.toMillis(lastModified);

        headers.add(new Header<>("Last-Modified", lastModified));
        headers.add(new Header<>("Expires", System.currentTimeMillis() + ttlMilliseconds));
        headers.add(new Header<>("Cache-Control", "no-cache,max-age=" + ttlMilliseconds / MILLISECONDS_PER_SECOND));
        headers.add(new Header<>("ETag", generateEtag(ttlMilliseconds)));

        return pageInfo;
    }

    /**
     * ETags are required to have double quotes around the value, unlike any other header.
     *
     * The ehcache eTag is effectively the Expires time, but accurate to milliseconds, i.e.
     * no conversion to the nearest second is done as is done for the Expires tag. It therefore
     * is the most precise indicator of whether the client cached version is the same as the server
     * version.
     *
     * MD5 is not used to calculate ETag, as it is in some implementations, because it does not
     * add any extra value in this situation, and it has a higher cost.
     *
     * @see "http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.3"
     */
    private String generateEtag(long ttlMilliseconds) {
        long eTagRaw = System.currentTimeMillis() + ttlMilliseconds;
        return "\"" + eTagRaw + "\"";
    }

}
