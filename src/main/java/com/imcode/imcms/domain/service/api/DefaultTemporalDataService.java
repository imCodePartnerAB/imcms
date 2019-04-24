package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.PublicDocumentsCache;
import com.imcode.imcms.domain.service.TemporalDataService;
import imcode.server.document.index.ResolvingQueryIndex;
import org.springframework.stereotype.Service;

import static imcode.server.ImcmsConstants.*;
import static net.sf.ehcache.CacheManager.getCacheManager;

//TODO cover by tests if possible
@Service
public class DefaultTemporalDataService implements TemporalDataService {

    private final PublicDocumentsCache publicDocumentsCache;

    private final ResolvingQueryIndex resolvingQueryIndex;

    public DefaultTemporalDataService(PublicDocumentsCache publicDocumentsCache, ResolvingQueryIndex resolvingQueryIndex) {
        this.publicDocumentsCache = publicDocumentsCache;
        this.resolvingQueryIndex = resolvingQueryIndex;
    }

    @Override
    public void invalidatePublicDocumentCache() {
        publicDocumentsCache.invalidateCache();
    }

    @Override
    public void invalidateStaticContentCache() {
        getCacheManager(null).getEhcache(STATIC_CACHE_NAME).removeAll();
    }

    @Override
    public void invalidateOtherContentCache() {
        getCacheManager(null).getEhcache(OTHER_CACHE_NAME).removeAll();
    }

    @Override
    public void rebuildDocumentIndex() {
        resolvingQueryIndex.rebuild();
    }
}
