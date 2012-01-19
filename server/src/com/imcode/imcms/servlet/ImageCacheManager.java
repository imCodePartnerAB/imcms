package com.imcode.imcms.servlet;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.textdocument.ImageCacheDomainObject;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imcode.imcms.mapping.ImageCacheMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.*;
import imcode.util.ImcmsImageUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;


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
	
	public static File storeImage(ImageCacheDomainObject imageCache, File imageFile) {
		ImageCacheMapper imageCacheMapper = Imcms.getServices().getImageCacheMapper();
		
        imageCache.setFrequency(1);

        // clear 10% of text images cache, if too many entries
        if (imageCacheMapper.getCacheFileSizeTotal() >= MAX_CACHE_SIZE) {
            List<String> cacheIds = imageCacheMapper.deleteCacheLFUEntries();
            deleteCacheFiles(cacheIds);
        }
		
		File cacheFile = processImage(imageCache, imageFile);
		if (cacheFile == null) {
			return null;
		}
		
		imageCache.setFileSize((int) cacheFile.length());
		imageCacheMapper.addImageCache(imageCache);
		
		return cacheFile;
	}
	
    public static void clearAllCacheEntries() {
        clearCacheEntries(null, null, null);
    }
    
    public static void clearCacheEntries(int metaId) {
        clearCacheEntries(metaId, null, null);
    }
    
    public static void clearCacheEntries(int metaId, int no) {
        clearCacheEntries(metaId, no, null);
    }
    
    public static void clearCacheEntries(int metaId, String fileNo) {
        if (fileNo == null) {
            throw new IllegalArgumentException("fileNo can't be null");
        }
        
        clearCacheEntries(metaId, null, fileNo);
    }
    
    private static void clearCacheEntries(Integer metaId, Integer no, String fileNo) {
        ImageCacheMapper mapper = Imcms.getServices().getImageCacheMapper();
        
        if (metaId != null || no != null || fileNo != null) {
            List<String> ids = mapper.getImagesCacheIds(metaId, no, fileNo);

            deleteCacheFiles(ids);
            mapper.deleteImagesCache(ids);
        } else {
            deleteAllCacheFiles();
            mapper.deleteAllImagesCache();
        }
        
        deleteGeneratedFiles(metaId, no, fileNo);
    }
    
    private static void deleteGeneratedFiles(Integer metaId, Integer no, String fileNo) {
        DocumentMapper docMapper = Imcms.getServices().getDocumentMapper();
        
        int[] docIdsArr = docMapper.getAllDocumentIds();
        List<Integer> docIds = new ArrayList<Integer>(docIdsArr.length);
        
        
        for (int docId : docIdsArr) {
            docIds.add(docId);
        }
        
        List<DocumentDomainObject> docs = docMapper.getDocuments(docIds);
        
        for (DocumentDomainObject doc : docs) {
            if (!(doc instanceof TextDocumentDomainObject)) {
                continue;
            }
            
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) doc;
            
            if (metaId != null && !metaId.equals(textDoc.getId())) {
                continue;
            }
            
            for (Entry<Integer, ImageDomainObject> entry : textDoc.getImages().entrySet()) {
                int imageIndex = entry.getKey();
                ImageDomainObject image = entry.getValue();
                
                if (StringUtils.isEmpty(image.getGeneratedFilename())) {
                    continue;
                }
                
                if (no != null && !no.equals(imageIndex)) {
                    continue;
                }
                
                if (fileNo != null) {
                    ImageSource source = image.getSource();
                    
                    if (!(source instanceof FileDocumentImageSource)) {
                        continue;
                    }
                    
                    String fileId = ((FileDocumentImageSource) source).getFileDocument().getDefaultFileId();
                    
                    if (!fileNo.equals(fileId)) {
                        continue;
                    }
                }
                
                File generatedFile = image.getGeneratedFile();
                generatedFile.delete();
            }
        }
    }
    
	public static void deleteCacheFiles(Collection<String> cacheIds) {
		for (String cacheId : cacheIds) {
			int bucket = getBucket(cacheId);
			
			File entryFile = new File(IMAGE_CACHE_PATH, String.format("%d/%s", bucket, cacheId));
            entryFile.delete();
		}
	}
    
    private static void deleteAllCacheFiles() {
        File[] dirs = IMAGE_CACHE_PATH.listFiles();
        if (dirs == null) {
            return;
        }
        
        for (File dir : dirs) {
            if (!dir.isDirectory()) {
                continue;
            }
            
            File[] entries = dir.listFiles();
            if (entries == null) {
                continue;
            }
            
            for (File entry : entries) {
                if (entry.isFile()) {
                    entry.delete();
                }
            }
        }
    }
	
	private static File processImage(ImageCacheDomainObject imageCache, File imageFile) {
		File bucketsRootFile = IMAGE_CACHE_PATH;
		
		int bucket = getBucket(imageCache.getId());
		File bucketFile = new File(bucketsRootFile, Integer.toString(bucket));
		if (!bucketFile.exists()) {
			bucketFile.mkdirs();
		}
		
		File cacheFile = new File(bucketFile, imageCache.getId());
		
		try {
			boolean result = ImcmsImageUtils.generateImage(imageFile, cacheFile, imageCache.getFormat(),
                    imageCache.getWidth(), imageCache.getHeight(), imageCache.getResize(), 
                    imageCache.getCropRegion(), imageCache.getRotateDirection());
			
			if (!result && cacheFile.exists()) {
                cacheFile.delete();
            }
			
			return cacheFile;
		} catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			
			if (cacheFile.exists()) {
				cacheFile.delete();
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
