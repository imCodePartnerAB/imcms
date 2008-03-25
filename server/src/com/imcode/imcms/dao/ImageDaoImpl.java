package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;

public class ImageDaoImpl extends HibernateTemplate implements ImageDao {
	
	@Transactional
	public List<ImageDomainObject> getImages(int metaId, int imageId, 
			boolean createIfNotExists) {
		
		ImageDomainObject defaultImage = getDefaultImage(metaId, imageId);
				
		if (logger.isTraceEnabled()) {
			logger.trace("Default image is " + defaultImage);
		}				
		
		List<Object[]> languagesToImages = getSession().getNamedQuery("Image.getLanguagesToImagesByMetaId")
			.setParameter("metaId", metaId)
			.setParameter("name", "" + imageId).list();
		
		List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();			
		
		for (Object[] languageToImage: languagesToImages) {
			I18nLanguage language = (I18nLanguage)languageToImage[0];
			ImageDomainObject image = (ImageDomainObject)languageToImage[1];
			
			if (image == null && createIfNotExists) {
				evict(language);
				
				if (defaultImage != null) {				
					image = (ImageDomainObject)defaultImage.clone();
					image.setId(null);
				} else {
					image = new ImageDomainObject();
					image.setMetaId(metaId);
					image.setName("" + imageId);
				}
				
				image.setLanguage(language);
			}
			
			if (image != null) {
				images.add(setImageSource(image));
			}			
		}
						
		return images;
	}

	@Transactional
	public ImageDomainObject getDefaultImage(int metaId, int imageId) {
		ImageDomainObject image = (ImageDomainObject)getSession()
			.getNamedQuery("Image.getDefaultImage")
			.setParameter("metaId", metaId)
			.setParameter("name", "" + imageId).uniqueResult();
		
		return setImageSource(image);
	}

	@Transactional
	public void saveImagesMap(int metaId,
			Map<I18nLanguage, Map<Integer, ImageDomainObject>> imagesMap) {
		
		
		for (Map<Integer, ImageDomainObject> map: imagesMap.values()) {
			for (ImageDomainObject image: map.values()) {
				saveOrUpdate(image);
			}
		}		
	}

	@Transactional
	public List<ImageDomainObject> getImages(int metaId, int languageId) {
		List<ImageDomainObject> images = findByNamedQueryAndNamedParam(
				"Image.getAllDocumentImagesByLanguage", 
					new String[] {"metaId", "languageId"}, 
					new Object[] {metaId, languageId});
		
		for (ImageDomainObject image: images) {
			setImageSource(image);
		}
		
		return images;
	}
	
	private ImageDomainObject setImageSource(ImageDomainObject image) {
		if (image == null) {
			return null;
		}
		
		String url = image.getImageUrl();
		
		if (!StringUtils.isBlank(url)) {
			ImageSource imageSource = new ImagesPathRelativePathImageSource(url);
			image.setSource(imageSource);	
			image.setImageUrl(url);
			image.setType(imageSource.getTypeId());
		}
				
		return image;
	}
}
