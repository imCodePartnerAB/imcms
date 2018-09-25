package com.imcode.imcms.mapping;

import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import com.imcode.imcms.persistence.repository.ImageCacheRepository;
import com.imcode.imcms.servlet.ImageCacheManager;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Deprecated
@Transactional
public class ImageCacheMapper {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ImageCacheMapper.class);

    private final ImageCacheRepository imageCacheRepository;

    public ImageCacheMapper(ImageCacheRepository imageCacheRepository) {
        this.imageCacheRepository = imageCacheRepository;
    }


    public void deleteDocumentImagesCache(Map<Integer, ImageDomainObject> images) {
        Set<String> cacheIds = new HashSet<>();

        for (int imageIndex : images.keySet()) {
            ImageDomainObject image = images.get(imageIndex);

            if (image.isEmpty()) {
                continue;
            }

            ImageCacheDomainObject imageCache = new ImageCacheDomainObject();
            imageCache.setWidth(image.getWidth());
            imageCache.setHeight(image.getHeight());
            imageCache.setFormat(image.getFormat());
            imageCache.setCropRegion(image.getCropRegion());
            imageCache.setRotateDirection(image.getRotateDirection());

            ImageSource source = image.getSource();
            if (source instanceof FileDocumentImageSource) {
                FileDocumentImageSource fileDocSource = (FileDocumentImageSource) source;
                imageCache.setResource(Integer.toString(fileDocSource.getFileDocument().getId()));
                imageCache.setType(ImageCacheDomainObject.TYPE_FILE_DOCUMENT);
            } else {
                imageCache.setResource(image.getUrlPathRelativeToContextPath());
                imageCache.setType(ImageCacheDomainObject.TYPE_PATH);
            }
            imageCache.generateId();

            cacheIds.add(imageCache.getId());
        }

        if (cacheIds.isEmpty()) {
            return;
        }

        imageCacheRepository.deleteAllById(cacheIds);

        ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getTextImageCacheFileSizeTotal() {
        return Optional.ofNullable(imageCacheRepository.fileSizeTotal()).orElse(0L);
    }

    public void deleteTextImageCacheLFUEntries() {
        long count = Optional.ofNullable(imageCacheRepository.countEntries()).orElse(0L);

        int deleteCount = (int) Math.ceil(count * 0.1);
        if (deleteCount < 1) {
            return;
        }

        List<String> cacheIds = imageCacheRepository.idsByFrequency();

        if (cacheIds.isEmpty()) {
            return;
        }

        cacheIds = cacheIds.subList(0, deleteCount);

        imageCacheRepository.deleteAllById(cacheIds);

        ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
    }

    public void addImageCache(ImageCacheDomainObject imageCache) {
        imageCacheRepository.deleteById(imageCache.getId());
        imageCacheRepository.save(imageCache);
        imageCacheRepository.flush();
    }

    public List<ImageCacheDomainObject> getAllImageResourcesByResourcePath(String resourcePath) {
        return imageCacheRepository.findAllByResource(resourcePath);
    }

    public void incrementFrequency(String cacheId) {
        imageCacheRepository.incFrequency(cacheId, Integer.MAX_VALUE);
    }

}
