package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.storage.StoragePath;

import java.util.function.Supplier;

public interface ImageFolderCacheManager {

    ImageFolderDTO getOrPut(StoragePath storagePath, Supplier<ImageFolderDTO> imageFolderSupplier);

    ImageFolderDTO getCache(StoragePath storagePath);

    boolean existInCache(StoragePath storagePath);

    void cache(StoragePath storagePath, ImageFolderDTO imageFolder);

    void invalidate(StoragePath... storagePaths);

    void invalidate();

}
