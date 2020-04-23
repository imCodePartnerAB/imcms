package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.web.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * @author Victor Pavlenko from Ubrainians for imCode
 * 06.09.18.
 */
@Component
@Slf4j
public class PublicDocumentsCache implements DocumentsCache {

    private static final String PUBLIC_DOC_CACHE = "PublicDocumentsCache";

    private final List<String> languages;

    private AtomicLong amountDocsInCaches = new AtomicLong(-1);

    private Ehcache cache;
    //set on String because may has default value empty line - false
    @Value("${disabledCache}")
    private String disabledCache;

    @Autowired
    public PublicDocumentsCache(LanguageService languageService) {
        languages = languageService.getAvailableLanguages().stream().map(Language::getCode).collect(Collectors.toList());
    }

    @Override
    public String calculateKey(HttpServletRequest request) {
        String path = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());
        String docIdentifier = extractDocIdentifier(path);
        String qsIdentifier = buildGetPostQueryStrings(request);
        String documentIdString = docIdentifier + qsIdentifier;
        String langCode = Imcms.getLanguage().getCode();
        return calculateKey(documentIdString, langCode);
    }

    private String extractDocIdentifier(String path) {
        String documentPathPrefix = Imcms.getServices().getConfig().getDocumentPathPrefix();
        String documentId = null;

        if (StringUtils.isNotBlank(documentPathPrefix) && path.startsWith(documentPathPrefix)) {
            documentId = path.substring(documentPathPrefix.length());

            if (documentId.endsWith("/")) documentId = documentId.substring(0, documentId.length() - 1);
            if (documentId.contains("/")) documentId = StringUtils.substringAfterLast(documentId, "/");
            if ("".equals(documentId)) documentId = String.valueOf(ImcmsConstants.DEFAULT_START_DOC_ID);
        }

        return documentId;
    }

    @Override
    public String calculateKey(String documentIdString, String langCode) {
        return documentIdString + "-" + langCode;
    }

    @Override
    public void invalidateDoc(Integer id, String alias) {
        if (cache == null) return;

        for (String language : languages) {
            cache.remove(calculateKey(String.valueOf(id), language));

            if (StringUtils.isNotBlank(alias)) {
                cache.remove(calculateKey(alias, language));
            }
        }
    }

    @Override
    public void invalidateItem(String key) {
        if (cache == null) return;
        if (StringUtils.isNotBlank(key)) {
            cache.remove(key);
        }
    }

    @Override
    public void invalidateCache() {
        cache.removeAll();
    }

    @Override
    public void setCache(Ehcache cache) {
        this.cache = cache;
    }

    @Override
    public PageInfo getPageInfoFromCache(String key) {
        if (!cache.isKeyInCache(key)) {
            return null;
        } else {
            final Element element = cache.get(key);
            return (PageInfo) element.getObjectValue();
        }
    }

    @Override
    public boolean isDocumentAlreadyCached(String cacheKey) {
        if (cache == null) return false;

        return cache.isKeyInCache(cacheKey);
    }

    @Override
    public void setDisableCachesByProperty() {
        if (Boolean.parseBoolean(disabledCache)) {
            Ehcache publicDocCache = this.cache.getCacheManager().getEhcache(PUBLIC_DOC_CACHE);
            if (null != publicDocCache) {
                publicDocCache.removeAll();
            }
        }
    }

    @Override
    public long getAmountOfCachedDocuments() {
        return amountDocsInCaches.get();
    }

    @Override
    public void setAmountOfCachedDocuments(Integer number) {
        amountDocsInCaches.set(number);
    }

    @Override
    public String getDisabledCacheValue() {
        return disabledCache;
    }

    private String buildGetPostQueryStrings(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        String queryString = request.getQueryString();
        String Q_DIVIDER = StringUtils.isBlank(queryString) ? "?" : "&";
        final Set<String> queryStringParameterNames = getQueryStringParameterNames(request);
        sb.append(StringUtils.isBlank(queryString) ? "" : "?QS_GET&" + queryString);
        final Enumeration<String> en = request.getParameterNames();
        try {
            int p = 0;
            while (en.hasMoreElements()) {
                String name = en.nextElement();
                if (queryStringParameterNames.contains(name)) continue;// Ignore if in GET.
                String[] values = request.getParameterValues(name);
                if (null != values) {
                    for (String value : values) {
                        if (0 == p) sb.append(Q_DIVIDER).append("QS_POST&");
                        else sb.append("&");
                        sb.append(name).append("=").append(URLEncoder.encode(value, "UTF-8"));
                        p++;
                    }
                }
            }
        } catch (Exception ignore) {
            log.error("In buildGetPostQueryStrings don't build query...");
        }
        return sb.toString();
    }

    private Set<String> getQueryStringParameterNames(HttpServletRequest request) {
        final Set<String> qsNames = new HashSet<>();
        final String queryString = request.getQueryString();

        if (!StringUtils.isBlank(queryString)) {
            Arrays.stream(queryString.split("&"))
                    .forEach(nameValuePair ->
                            qsNames.add(nameValuePair.split("=")[0])
                    );
        }

        return qsNames;
    }
}
