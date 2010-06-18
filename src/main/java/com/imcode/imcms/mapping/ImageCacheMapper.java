package com.imcode.imcms.mapping;

import com.imcode.imcms.dao.ImageDao;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.imcode.imcms.servlet.ImageCacheManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class ImageCacheMapper {
    private static final Logger log = Logger.getLogger(ImageCacheMapper.class);
    
    @Autowired
    private SessionFactory factory;

    
	public void deleteDocumentImagesCache(int metaId, Map<Integer, ImageDomainObject> images) {
		Set<String> cacheIds = new HashSet<String>();
		
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

        factory.getCurrentSession()
                .getNamedQuery("ImageCache.deleteAllById")
                .setParameterList("ids", cacheIds)
                .executeUpdate();

        ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
	}

    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
	public long getTextImageCacheFileSizeTotal() {
        Number total = (Number) factory.getCurrentSession()
                .getNamedQuery("ImageCache.fileSizeTotal")
                .uniqueResult();

        return (total != null ? total.longValue() : 0L);
	}
	
	public void deleteTextImageCacheLFUEntries() {
        Session session = factory.getCurrentSession();

        Number count = (Number) session
                .getNamedQuery("ImageCache.countEntries")
                .uniqueResult();
		
		int deleteCount = (int) Math.ceil(count.longValue() * 0.1);
		if (deleteCount < 1) {
			return;
		}

        List<String> cacheIds = session
                .getNamedQuery("ImageCache.idsByFrequency")
                .setMaxResults(deleteCount)
                .list();
        
        if (cacheIds.isEmpty()) {
            return;
        }
        
        session.getNamedQuery("ImageCache.deleteAllById")
                .setParameterList("ids", cacheIds)
                .executeUpdate();

        ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
	}
	
	public void addImageCache(ImageCacheDomainObject imageCache) {
        Session session = factory.getCurrentSession();

        session.getNamedQuery("ImageCache.deleteById")
                .setString("id", imageCache.getId())
                .executeUpdate();

        session.persist(imageCache);
        session.flush();
	}
	
	public void incrementFrequency(String cacheId) {
        factory.getCurrentSession()
                .getNamedQuery("ImageCache.incFrequency")
                .setString("id", cacheId)
                .setInteger("maxFreq", Integer.MAX_VALUE)
                .executeUpdate();
	}

    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
	public List<ImageDomainObject> getAllDocumentImages() {

        List<ImageDomainObject> images = factory.getCurrentSession()
                .getNamedQuery("Image.allImages")
                .list();

        ImageDao.setImagesSources(images);

	    return images;
	}
}
