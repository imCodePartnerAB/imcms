package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.storage.StoragePath;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

import static imcode.server.ImcmsConstants.IMAGE_FOLDER_CACHE_NAME;

@Component
public class DefaultImageFolderCacheManager implements ImageFolderCacheManager{

    private final Ehcache cache;

    public DefaultImageFolderCacheManager(){
        this.cache = CacheManager.getCacheManager(null).getEhcache(IMAGE_FOLDER_CACHE_NAME);
    }

    @Override
    public ImageFolderDTO getOrPut(StoragePath storagePath, Supplier<ImageFolderDTO> imageFolderSupplier) {
        if(existInCache(storagePath))
            return getCache(storagePath);

        final ImageFolderDTO imageFolder = imageFolderSupplier.get();

        cache(storagePath, imageFolder);

        return imageFolder;
    }

    @Override
    public ImageFolderDTO getCache(StoragePath storagePath){
        return Optional.ofNullable(cache.get(generateKey(storagePath)))
                .map(e -> (ImageFolderDTO) e.getObjectValue())
                .orElse(null);
    }

    @Override
    public boolean existInCache(StoragePath storagePath){
        return cache.isKeyInCache(generateKey(storagePath));
    }

    @Override
    public void cache(StoragePath storagePath, ImageFolderDTO imageFolder){
        cache.put(new Element(generateKey(storagePath), imageFolder));
    }

    @Override
    public void invalidate(StoragePath... storagePaths){
        for(StoragePath storagePath: storagePaths){
            cache.remove(generateKey(storagePath));
        }
    }

    @Override
    public void invalidate(){
        cache.removeAll();
    }

    private String generateKey(StoragePath storagePath){
        return storagePath.toString();
    }

}
