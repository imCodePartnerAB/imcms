package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.imcode.imcms.api.I18nLanguage;

public class ImageDaoImpl extends HibernateTemplate implements ImageDao {
	
	public void saveAllImages(Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap) {
		
	}
	
	public Map<I18nLanguage, ImageDomainObject> getI18nImageMap(int metaId, int imageId) {
		Map<I18nLanguage, ImageDomainObject> map =
			new HashMap<I18nLanguage, ImageDomainObject>();

		List<ImageDomainObject> images = getAllImages(metaId, imageId);
		
		for (ImageDomainObject image: images) {
			map.put(image.getLanguage(), image);
		}
		
		return map;
	}
	
	public List<ImageDomainObject> getAllImages(int metaId, int imageId) {
		ImageDomainObject defaultImage = getDefaultImage(metaId, imageId);
		
		String defaultImageUrl = null;
		
		if (defaultImage != null) {
			defaultImageUrl = defaultImage.getImageUrl();
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("Default image is " + defaultImage);
		}
		
		// if defaultImage == null ???
				
		
		List<Object[]> languagesToImages = getSession().getNamedQuery("Image.getLanguagesToImagesByMetaId")
			.setParameter("metaId", metaId)
			.setParameter("imageId", "" + imageId).list();
		
		List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();			
		
		for (Object[] languageToImage: languagesToImages) {
			I18nLanguage language = (I18nLanguage)languageToImage[0];
			ImageDomainObject image = (ImageDomainObject)languageToImage[1];
			
			if (image == null) {
				if (defaultImage != null) {				
					image = (ImageDomainObject)defaultImage.clone();
				} else {
					image = new ImageDomainObject();
					image.setMetaId(metaId);
					image.setName("" + imageId);
				}
				
				image.setLanguage(language);
				image.setImageId(null);
				
				String imageUrl = image.getImageUrl();
				
				if (StringUtils.isBlank(imageUrl)) {
					imageUrl = defaultImageUrl;
				}
				
				if (StringUtils.isNotBlank(imageUrl)) {
					ImageSource imageSource = new ImagesPathRelativePathImageSource(imageUrl);
					// set or clone source.					
					image.setSource(imageSource);
				}
			}	
			
			images.add(image);
		}
						
		return images;
	}

	public ImageDomainObject getDefaultImage(int metaId, int imageId) {
		return (ImageDomainObject)getSession()
			.getNamedQuery("Image.getDefaultImage")
			.setParameter("metaId", metaId)
			.setParameter("imageId", "" + imageId).uniqueResult();
	}
}
