package com.imcode.imcms.mapping;

import com.imcode.imcms.servlet.ImageCacheManager;
import imcode.server.document.TextDocumentUtils;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageCacheDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class ImageCacheMapper {

    private static final Logger log = Logger.getLogger(ImageCacheMapper.class);

    @Inject
    private EntityManager entityManager;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }


    public void deleteDocumentImagesCache(int docId, Map<Integer, ImageDomainObject> images) {
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

        getCurrentSession()
                .getNamedQuery("ImageCache.deleteAllById")
                .setParameterList("ids", cacheIds)
                .executeUpdate();

        ImageCacheManager.deleteTextImageCacheEntries(cacheIds);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getTextImageCacheFileSizeTotal() {
        Number total = (Number) getCurrentSession()
                .getNamedQuery("ImageCache.fileSizeTotal")
                .uniqueResult();

        return (total != null ? total.longValue() : 0L);
    }

    public void deleteTextImageCacheLFUEntries() {
        Session session = getCurrentSession();

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
        Session session = getCurrentSession();

        session.getNamedQuery("ImageCache.deleteById")
                .setString("id", imageCache.getId())
                .executeUpdate();

        session.persist(imageCache);
        session.flush();
    }

    public void incrementFrequency(String cacheId) {
        getCurrentSession()
                .getNamedQuery("ImageCache.incFrequency")
                .setString("id", cacheId)
                .setInteger("maxFreq", Integer.MAX_VALUE)
                .executeUpdate();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<ImageDomainObject> getAllDocumentImages() {
        // fixme: init image from db - replace with text doc content mapper
        List<ImageDomainObject> images = getCurrentSession()
                .getNamedQuery("Image.allImages")
                .list();

        TextDocumentUtils.initImagesSources(images);

        return images;
    }
}
