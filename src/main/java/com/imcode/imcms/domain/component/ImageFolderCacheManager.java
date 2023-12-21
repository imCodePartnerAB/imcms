package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.storage.StoragePath;

public interface ImageFolderCacheManager {

    ImageFolderDTO getCache(StoragePath storagePath);

    boolean existInCache(StoragePath storagePath);

    void cache(StoragePath storagePath, ImageFolderDTO imageFolder);

    void invalidate(StoragePath... storagePaths);

    void invalidate();

}
