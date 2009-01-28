package com.imcode.imcms.dao;

import static com.imcode.imcms.mapping.TextDocumentInitializer.setImageSource;
import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;

public class ImageDao extends HibernateTemplate {
	
	private LanguageDao languageDao;
	
	@Transactional
	public synchronized List<ImageDomainObject> getImagesByIndex(
			Integer documentId, int imageId, boolean createImageIfNotExists) {

		List<I18nLanguage> languages = languageDao.getAllLanguages();		
		List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();
		
		for (I18nLanguage language: languages) {
			ImageDomainObject image = getImage(language.getId(), documentId, imageId);
			
			if (image == null && createImageIfNotExists) {
					image = new ImageDomainObject();
					image.setMetaId(documentId);
					image.setName("" + imageId);
				
				image.setLanguage(language);
			}
			
			if (image != null) {
				images.add(setImageSource(image));
			}			
		}
						
		return images;
	}


	/*
	@Transactional
	public synchronized List<ImageDomainObject> getImagesByLanguage(int metaId, int languageId) {
		List<ImageDomainObject> images = findByNamedQueryAndNamedParam(
				"Image.getAllDocumentImagesByLanguage", 
					new String[] {"metaId", "languageId"}, 
					new Object[] {metaId, languageId});
		
		for (ImageDomainObject image: images) {
			setImageSource(image);
		}
		
		return images;
	}
	*/
	
	public synchronized ImageDomainObject getImage(int languageId, 
			Integer documentId, int index) {
		
		ImageDomainObject image = (ImageDomainObject)getSession().createQuery("select i from I18nImage i where i.metaId = :metaId and i.name = :name and i.language.id = :languageId")
			.setParameter("metaId", documentId)
			.setParameter("name", "" + index)
			.setParameter("languageId", languageId)
			.uniqueResult();
		
		return image;
	}
	
	@Transactional
	public ImageDomainObject saveImage(ImageDomainObject image) {
		image.setImageUrl(image.getSource().toStorageString());
		image.setType(image.getSource().getTypeId());

		saveOrUpdate(image);
		
		return image;
	}
	
	@Transactional
	public Collection<ImageDomainObject> getImages(Integer documentId) {
		return find("select i from I18nImage i where i.metaId = ?", documentId);
	}
	
	public LanguageDao getLanguageDao() {
		return languageDao;
	}

	public void setLanguageDao(LanguageDao languageDao) {
		this.languageDao = languageDao;
	}	
}
