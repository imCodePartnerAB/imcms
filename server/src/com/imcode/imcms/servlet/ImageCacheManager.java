package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.document.textdocument.ImageCacheDomainObject;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imcode.imcms.mapping.ImageCacheMapper;
import imcode.util.ImcmsImageUtils;
import java.util.Collection;

@Deprecated
public class ImageCacheManager {
	private static final Log log = LogFactory.getLog(ImageCacheManager.class);
	
	private static final int CACHE_BUCKETS_COUNT = 40;
	
	private static final File IMAGE_CACHE_PATH = Imcms.getServices().getConfig().getImageCachePath();
	private static final long MAX_CACHE_SIZE = Imcms.getServices().getConfig().getImageCacheMaxSize();
	
	
	public static File getCacheFile(ImageCacheDomainObject imageCache) {
		// disk based hash buckets
		String cacheId = imageCache.getId();
		int bucket = getBucket(cacheId);
		
		String entryPath = String.format("%d/%s", bucket, cacheId);
		
		File imageFile = new File(IMAGE_CACHE_PATH, entryPath);
		if (!imageFile.exists()) {
            return null;
		}
		
        Imcms.getServices().getImageCacheMapper().incrementFrequency(imageCache.getId());
		
		return imageFile;
	}
	
	public static File storeImage(ImageCacheDomainObject imageCache, File imageFile, boolean deleteFile) {
		ImageCacheMapper imageCacheMapper = Imcms.getServices().getImageCacheMapper();
		
        imageCache.setFrequency(1);

        // clear 10% of text images cache, if too many entries
        if (imageCacheMapper.getTextImageCacheFileSizeTotal() >= MAX_CACHE_SIZE) {
            imageCacheMapper.deleteTextImageCacheLFUEntries();
        }
		
		File cacheFile = processImage(imageCache, imageFile, deleteFile);
		if (cacheFile == null) {
			return null;
		}
		
		imageCache.setFileSize((int) cacheFile.length());
		imageCacheMapper.addImageCache(imageCache);
		
		return cacheFile;
	}
	
	public static void deleteTextImageCacheEntries(Collection<String> cacheIds) {
		for (String cacheId : cacheIds) {
			int bucket = getBucket(cacheId);
			
			File entryFile = new File(IMAGE_CACHE_PATH, String.format("%d/%s", bucket, cacheId));
			if (entryFile.exists()) {
				entryFile.delete();
			}
		}
	}
	
	private static File processImage(ImageCacheDomainObject imageCache, File imageFile, boolean deleteFile) {
		File bucketsRootFile = IMAGE_CACHE_PATH;
		
		int bucket = getBucket(imageCache.getId());
		File bucketFile = new File(bucketsRootFile, Integer.toString(bucket));
		if (!bucketFile.exists()) {
			bucketFile.mkdirs();
		}
		
		File cacheFile = new File(bucketFile, imageCache.getId());
		
		try {
			boolean result = ImcmsImageUtils.generateImage(imageFile, cacheFile, imageCache.getFormat(),
                    imageCache.getWidth(), imageCache.getHeight(), imageCache.getCropRegion(), imageCache.getRotateDirection());
			
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
