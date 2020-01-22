package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.web.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * @author Victor Pavlenko from Ubrainians for imCode
 * 06.09.18.
 */
@Component
public class PublicDocumentsCache implements DocumentsCache {

    private static final Logger logger = Logger.getLogger(PublicDocumentsCache.class);
    private static final String PUBLIC_DOC_CACHE = "PublicDocumentsCache";

    private final List<String> languages;

    private AtomicLong amountDocsInCaches = new AtomicLong(-1);

    private Ehcache cache;
    //set on String because may has default value empty line - false
    @Value("${cacheDisable}")
    private String isDisableCache;

    @Autowired
    public PublicDocumentsCache(LanguageService languageService) {
        languages = languageService.getAvailableLanguages().stream().map(Language::getCode).collect(Collectors.toList());
    }

    @Override
    public String calculateKey(HttpServletRequest request) {
        final String path = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());
        final String docIdentifier = extractDocIdentifier(path);
        //Needed to fetch correct page with specified request params
        final String queryString = request.getQueryString();
        final String documentIdString = StringUtils.isBlank(queryString)
                ? docIdentifier
                : docIdentifier + "?" + queryString;
        final String langCode = Imcms.getLanguage().getCode();

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
        if (Boolean.parseBoolean(isDisableCache)) {
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
}
