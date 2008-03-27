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
	public List<ImageDomainObject> getImages(
			List<I18nLanguage> languages, 
			int metaId, int imageId, boolean createImageIfNotExists) {
		
		ImageDomainObject defaultImage = getDefaultImage(metaId, imageId);
				
		if (logger.isTraceEnabled()) {
			logger.trace("Default image is " + defaultImage);
		}				
		
		List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();
		
		for (I18nLanguage language: languages) {
			ImageDomainObject image = getImage(language.getId(), metaId, imageId);
			
			if (image == null && createImageIfNotExists) {
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
	
	public ImageDomainObject getImage(int languageId, 
			int metaId, int index) {
		
		return (ImageDomainObject)getSession().createQuery("select i from I18nImage i where i.metaId = :metaId and i.name = :name and i.language.id = :languageId")
			.setParameter("metaId", metaId)
			.setParameter("name", "" + index)
			.setParameter("languageId", languageId)
			.uniqueResult();
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
