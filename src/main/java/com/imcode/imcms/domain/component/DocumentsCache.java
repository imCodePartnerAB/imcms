package com.imcode.imcms.domain.component;

import net.sf.ehcache.Ehcache;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 06.09.18.
 */
public interface DocumentsCache {

    String calculateKey(HttpServletRequest request);

    String calculateKey(final String documentIdString, final String langCode);

    void setCache(Ehcache cache);

    void invalidateDoc(Integer id, String alias);

    boolean isDocumentAlreadyCached(String cacheKey);
}
