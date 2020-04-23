package com.imcode.imcms.filters;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.Header;
import net.sf.ehcache.constructs.web.PageInfo;
import net.sf.ehcache.constructs.web.filter.SimpleCachingHeadersPageCachingFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * Public version of document is cached. Default user will receive it.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 06.09.18.
 */
public class PublicDocumentCacheFilter extends SimpleCachingHeadersPageCachingFilter {

    private final static Logger logger = Logger.getLogger(PublicDocumentCacheFilter.class);

    private DocumentsCache documentsCache;

    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain chain) throws ServletException, IOException {

        final boolean enabledCache = !Boolean.parseBoolean(documentsCache.getDisabledCacheValue());

        if (Imcms.getUser().isDefaultUser() && enabledCache) {

            final ImcmsServices services = Imcms.getServices();

            final String workaroundUriEncoding = services.getConfig().getWorkaroundUriEncoding();
            final FallbackDecoder fallbackDecoder = new FallbackDecoder(
                    Charset.forName(Imcms.DEFAULT_ENCODING),
                    (null != workaroundUriEncoding) ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset()
            );

            String path = Utility.fallbackUrlDecode(request.getRequestURI(), fallbackDecoder);
            path = StringUtils.substringAfter(path, request.getContextPath());

            if ("/".equals(path)) path = "/" + String.valueOf(services.getSystemData().getStartDocument());

            final Set resourcePaths = request.getSession().getServletContext().getResourcePaths(path);

            if (resourcePaths == null || resourcePaths.size() == 0) {
                final String documentIdString = ImcmsSetupFilter.getDocumentIdString(services, path);
                final String langCode = Imcms.getUser().getDocGetterCallback().getLanguage().getCode();

                final String cacheKey = documentsCache.calculateKey(documentIdString, langCode);
                final boolean isDocumentAlreadyCached = documentsCache.isDocumentAlreadyCached(cacheKey);

                final BooleanSupplier textDocExist = () -> {
                    final DocumentDomainObject document = services.getDocumentMapper()
                            .getVersionedDocument(documentIdString, langCode, request);

                    return ((null != document) && Utility.isTextDocument(document));
                };

                final PageInfo tempPageInfo = documentsCache.getPageInfoFromCache(cacheKey);
                if (null != tempPageInfo) {
                    final String eTagRequest = request.getHeader("if-none-match");
                    if (null != eTagRequest) {
                        Optional<Header<? extends Serializable>> eTag = tempPageInfo.getHeaders().stream()
                                .filter(item -> item.getName().equals("ETag")).findFirst();
                        boolean isSameETag = eTag.isPresent() && eTagRequest.equals(eTag.get().getValue());

                        if (!isSameETag && isDocumentAlreadyCached) {
                            documentsCache.invalidateItem(cacheKey);
                            logger.info("Invalidate cache the page from docId and cacheKey: " + documentIdString + " " + cacheKey);
                        }
                    }
                }

                if (isDocumentAlreadyCached || textDocExist.getAsBoolean()) {
                    try {
                        super.doFilter(request, response, chain);
                        return;
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void doInit(FilterConfig filterConfig) throws CacheException {

        final ServletContext servletContext = filterConfig.getServletContext();
        final WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        documentsCache = ctx.getBean(DocumentsCache.class);

        super.doInit(filterConfig);

        documentsCache.setCache(blockingCache);
    }

    @Override
    protected String calculateKey(HttpServletRequest request) {
        return documentsCache.calculateKey(request);
    }

    /**
     * Builds the PageInfo object by passing the request along the filter chain
     *
     * @return a Serializable value object for the page or page fragment
     * @throws AlreadyGzippedException if an attempt is made to double gzip the body
     */
    @Override
    protected PageInfo buildPage(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain chain) throws Exception {
// whole method stolen from parents for small change...
        // Invoke the next entity in the chain
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final GenericResponseWrapper wrapper = new GenericResponseWrapper(response, outputStream);

        chain.doFilter(request, wrapper);
        wrapper.setContentType("text/html;charset=UTF-8"); // <- reason of overriding
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
        //no-cache needed just to  validated content over ETag before load. Should be needed by manual language switch
        headers.add(new Header<>("Cache-Control", "no-cache,max-age=" + ttlMilliseconds / 1000));
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
