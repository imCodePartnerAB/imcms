package com.imcode.imcms.domain.component;

public interface ImageCacheManager {

    void removePublicImagesFromCacheByKey(String key);

    void removeAllImagesFromCacheByKey(String key);

    void removeOtherImagesFromCacheByKey(String key);

}
