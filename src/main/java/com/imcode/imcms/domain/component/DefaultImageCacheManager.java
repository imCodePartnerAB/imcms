package com.imcode.imcms.domain.component;

import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

import static imcode.server.ImcmsConstants.OTHER_CACHE_NAME;
import static imcode.server.ImcmsConstants.PUBLIC_CACHE_NAME;

@Component
public class DefaultImageCacheManager implements ImageCacheManager {

    private final EhCacheCacheManager cacheManager;

    public DefaultImageCacheManager(@Qualifier("ehCacheCacheManager") EhCacheCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void removePublicImagesFromCacheByKey(String key) {
        final Ehcache publicCache = Objects.requireNonNull(cacheManager.getCacheManager()).getEhcache(PUBLIC_CACHE_NAME);

        removeImagesFromCache(publicCache, key);
    }

    @Override
    public void removeAllImagesFromCacheByKey(String key) {
        final Ehcache otherCache = Objects.requireNonNull(cacheManager.getCacheManager()).getEhcache(OTHER_CACHE_NAME);

        removePublicImagesFromCacheByKey(key);

        removeImagesFromCache(otherCache, key);
    }

    @Override
    public void removeOtherImagesFromCacheByKey(String key) {
        final Ehcache otherCache = Objects.requireNonNull(cacheManager.getCacheManager()).getEhcache(OTHER_CACHE_NAME);

        removeImagesFromCache(otherCache, key);
    }


    private void removeImagesFromCache(Ehcache cache, String key) {
        Arrays.stream(cache.getKeys().toArray())
                .map(Object::toString)
                .filter(cacheKey -> String.valueOf(cacheKey).contains(key))
                .forEach(cache::remove);
    }
}
