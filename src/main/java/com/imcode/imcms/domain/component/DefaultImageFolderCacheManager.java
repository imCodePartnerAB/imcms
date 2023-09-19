package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.storage.StoragePath;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static imcode.server.ImcmsConstants.IMAGE_FOLDER_CACHE_NAME;

@Component
public class DefaultImageFolderCacheManager implements ImageFolderCacheManager{

    private final Ehcache cache;

    public DefaultImageFolderCacheManager(){
        this.cache = CacheManager.getCacheManager(null).getEhcache(IMAGE_FOLDER_CACHE_NAME);
    }

    public ImageFolderDTO getCache(StoragePath storagePath){
        return Optional.ofNullable(cache.get(generateKey(storagePath)))
                .map(e -> (ImageFolderDTO) e.getObjectValue())
                .orElse(null);
    }

    public boolean existInCache(StoragePath storagePath){
        return cache.isKeyInCache(generateKey(storagePath));
    }

    public void cache(StoragePath storagePath, ImageFolderDTO imageFolder){
        cache.put(new Element(generateKey(storagePath), imageFolder));
    }

    public void invalidate(StoragePath storagePath){
        cache.remove(generateKey(storagePath));
    }

    private String generateKey(StoragePath storagePath){
        return storagePath.toString();
    }

}
