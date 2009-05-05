package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.util.image.Filter;
import imcode.util.image.ImageOp;
import imcode.util.image.Resize;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imcode.imcms.mapping.ImageCacheMapper;

public class ImageCacheManager {
	private static final Log log = LogFactory.getLog(ImageCacheManager.class);
	
	private static final int CACHE_BUCKETS_COUNT = 20;
	
	private static final File IMAGE_CACHE_PATH = Imcms.getServices().getConfig().getImageCachePath();
	private static final long MAX_CACHE_SIZE = Imcms.getServices().getConfig().getImageCacheMaxSize();
	
	// images that are assigned to documents using meta_id and image_index go here
	private static final File DOCUMENT_IMAGE_CACHE_BUCKETS_FILE = new File(IMAGE_CACHE_PATH, "doc_image_cache");
	
	// images that are assigned to document texts (in xinha) go here
	private static final File TEXT_IMAGE_CACHE_BUCKETS_FILE = new File(IMAGE_CACHE_PATH, "text_image_cache");
	
	
	public static File getCacheFile(ImageCacheDomainObject imageCache) {
		// disk based hash buckets
		String cacheId = imageCache.getId();
		int bucket = getBucket(cacheId);
		
		String entryPath = String.format("%d/%s", bucket, cacheId);
		
		File imageFile = new File(DOCUMENT_IMAGE_CACHE_BUCKETS_FILE, entryPath);
		if (!imageFile.exists()) {
			imageFile = new File(TEXT_IMAGE_CACHE_BUCKETS_FILE, entryPath);
			
			if (!imageFile.exists()) {
				return null;
			}
		}
		
		if (imageCache.getMetaId() <= 0) {
			Imcms.getServices().getImageCacheMapper().incrementFrequency(imageCache.getId());
		}
		
		return imageFile;
	}
	
	public static File storeImage(ImageCacheDomainObject imageCache, File imageFile, boolean deleteFile) {
		ImageCacheMapper imageCacheMapper = Imcms.getServices().getImageCacheMapper();
		
		if (imageCache.getMetaId() > 0) {
			// delete any previous document image cache for meta_id and image_index
			String cacheId = imageCacheMapper.deleteDocumentImageCache(imageCache.getMetaId(), imageCache.getImageIndex());
			if (cacheId != null) {
				deleteDocumentImageCacheEntry(cacheId);
			}
		} else {
			imageCache.setFrequency(1);
			
			// clear 10% of text images cache, if too many entries
			if (imageCacheMapper.getTextImageCacheFileSizeTotal() >= MAX_CACHE_SIZE) {
				imageCacheMapper.deleteTextImageCacheLFUEntries();
			}
		}
		
		File cacheFile = processImage(imageCache, imageFile, deleteFile);
		if (cacheFile == null) {
			return null;
		}
		
		imageCache.setFileSize((int) cacheFile.length());
		imageCacheMapper.addImageCache(imageCache);
		
		return cacheFile;
	}
	
	public static void deleteTextImageCacheEntries(List<String> cacheIds) {
		for (String cacheId : cacheIds) {
			int bucket = getBucket(cacheId);
			
			File entryFile = new File(TEXT_IMAGE_CACHE_BUCKETS_FILE, String.format("%d/%s", bucket, cacheId));
			if (entryFile.exists()) {
				entryFile.delete();
			}
		}
	}
	
	public static void deleteDocumentImageCacheEntry(String cacheId) {
		int bucket = getBucket(cacheId);
		
		File entryFile = new File(DOCUMENT_IMAGE_CACHE_BUCKETS_FILE, String.format("%d/%s", bucket, cacheId));
		if (entryFile.exists()) {
			entryFile.delete();
		}
	}
	
	private static File processImage(ImageCacheDomainObject imageCache, File imageFile, boolean deleteFile) {
		File bucketsRootFile = TEXT_IMAGE_CACHE_BUCKETS_FILE;
		if (imageCache.getMetaId() > 0) {
			bucketsRootFile = DOCUMENT_IMAGE_CACHE_BUCKETS_FILE;
		}
		
		if (!bucketsRootFile.exists()) {
			bucketsRootFile.mkdir();
		}
		
		int bucket = getBucket(imageCache.getId());
		File bucketFile = new File(bucketsRootFile, Integer.toString(bucket));
		if (!bucketFile.exists()) {
			bucketFile.mkdir();
		}
		
		File cacheFile = new File(bucketFile, imageCache.getId());
		
		try {
			ImageOp operation = new ImageOp().input(imageFile);
			
			CropRegion cropRegion = imageCache.getCropRegion();
			int width = imageCache.getWidth();
			int height = imageCache.getHeight();
			
			if (cropRegion.isValid()) {
				int cropWidth = cropRegion.getWidth();
				int cropHeight = cropRegion.getHeight();
				
				operation.crop(cropRegion.getCropX1(), cropRegion.getCropY1(), cropWidth, cropHeight);
			}
			
			if (width > 0 || height > 0) {
                Integer w = (width > 0 ? width : null);
                Integer h = (height > 0 ? height : null);
                Resize resize = (width > 0 && height > 0 ? Resize.FORCE : Resize.DEFAULT);
                
                operation.filter(Filter.LANCZOS);
                operation.resize(w, h, resize);
	        }
			
			if (imageCache.getFormat() != null) {
				operation.outputFormat(imageCache.getFormat());
			}
			
			if (!operation.processToFile(cacheFile)) {
				if (cacheFile.exists()) {
					cacheFile.delete();
				}
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
