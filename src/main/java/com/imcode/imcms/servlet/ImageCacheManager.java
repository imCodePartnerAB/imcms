package com.imcode.imcms.servlet;

import com.imcode.imcms.mapping.ImageCacheMapper;
import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.util.ImcmsImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Collection;

@Deprecated
public class ImageCacheManager {
    private static final Logger log = LogManager.getLogger(ImageCacheManager.class);

    private static final int CACHE_BUCKETS_COUNT = 40;

    private static File imageCachePath = null;
    private static long maxCacheSize = 0;

    private static ImageCacheMapper cacheMapper = Imcms.getServices().getManagedBean(ImageCacheMapper.class);

    // Needed to init correctly settings for image handling in static context
    // No much sense into better refactoring since it deprecated.
    static {
        ImageCacheManager.cacheMapper = Imcms.getServices().getManagedBean(ImageCacheMapper.class);
        final Config config = Imcms.getServices().getConfig();
        imageCachePath = config.getImageCachePath();
        maxCacheSize = config.getImageCacheMaxSize();
    }

    public ImageCacheManager(ImageCacheMapper cacheMapper, Config config) {
        ImageCacheManager.cacheMapper = cacheMapper;
        imageCachePath = config.getImageCachePath();
        maxCacheSize = config.getImageCacheMaxSize();
    }

    public static File getCacheFile(ImageCacheDomainObject imageCache) {
        // disk based hash buckets
        String cacheId = imageCache.getId();
        int bucket = getBucket(cacheId);

        String entryPath = String.format("%d/%s", bucket, cacheId);

        File imageFile = new File(imageCachePath, entryPath);
        if (!imageFile.exists()) {
            return null;
        }

        cacheMapper.incrementFrequency(imageCache.getId());

        return imageFile;
    }

    public static File storeImage(ImageCacheDomainObject imageCache, File imageFile, boolean deleteFile,
                                  boolean withoutCropOperation) {

        imageCache.setFrequency(1);

        // clear 10% of text images cache, if too many entries
        if (cacheMapper.getTextImageCacheFileSizeTotal() >= maxCacheSize) {
            cacheMapper.deleteTextImageCacheLFUEntries();
        }

        File cacheFile = processImage(imageCache, imageFile, deleteFile, withoutCropOperation);
        if (cacheFile == null) {
            return null;
        }

        imageCache.setFileSize((int) cacheFile.length());
        cacheMapper.addImageCache(imageCache);

        return cacheFile;
    }

    public static void deleteTextImageCacheEntries(Collection<String> cacheIds) {
        for (String cacheId : cacheIds) {
            int bucket = getBucket(cacheId);

            File entryFile = new File(imageCachePath, String.format("%d/%s", bucket, cacheId));
            if (entryFile.exists()) {
                entryFile.delete();
            }
        }
    }

    private static File processImage(ImageCacheDomainObject imageCache, File imageFile, boolean deleteFile,
                                     boolean withoutCropOperation) {

        int bucket = getBucket(imageCache.getId());
        File bucketFile = new File(imageCachePath, Integer.toString(bucket));
        if (!bucketFile.exists()) {
            bucketFile.mkdirs();
        }

        File cacheFile = new File(bucketFile, imageCache.getId());

        try {
            boolean result = ImcmsImageUtils.generateImage(imageFile, cacheFile, imageCache, withoutCropOperation);


            if (!result && cacheFile.exists()) {
                cacheFile.delete();
            }

            return cacheFile;

        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);

            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        } finally {
            if (deleteFile) {
                imageFile.delete();
            }
        }

        return null;
    }

    private static int getBucket(String cacheId) {
        return Math.abs(hashCode(cacheId)) % CACHE_BUCKETS_COUNT;
    }

    private static int hashCode(String value) {
        int hash = 0;

        for (int i = 0, len = value.length(); i < len; i++) {
            hash = hash * 31 + value.charAt(i);
        }

        return hash;
    }
}
