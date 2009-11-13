package com.imcode.imcms.dao;

import static com.imcode.imcms.mapping.TextDocumentInitializer.setImageSource;
import imcode.server.document.textdocument.ImageDomainObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nLanguage;

public class ImageDao extends HibernateTemplate {
	
	private LanguageDao languageDao;
	
	@Transactional
	public synchronized List<ImageDomainObject> getImagesByIndex(
			Integer documentId, Integer documentVersion, int imageId, boolean createImageIfNotExists) {

		List<I18nLanguage> languages = languageDao.getAllLanguages();		
		List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();
		
		for (I18nLanguage language: languages) {
			ImageDomainObject image = getImage(language.getId(), documentId, documentVersion, imageId);
			
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
			Integer metaId, Integer docVersionNo, int no) {
		
		ImageDomainObject image = (ImageDomainObject)getSession().createQuery("select i from Image i where i.metaId = :metaId AND i.docVersionNo = :docVersionNo and i.no = :no and i.language.id = :languageId")
			.setParameter("metaId", metaId)
			.setParameter("docVersionNo", docVersionNo)
			.setParameter("no", "" + no)
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
	public Collection<ImageDomainObject> getImages(Integer metaId, Integer docVersionNo) {
		return findByNamedQueryAndNamedParam("Image.getByMetaIdAndDocVersionNo",
				new String[] {"metaId", "docVersionNo"},
				new Object[] {metaId, docVersionNo}	
		);
    }

	@Transactional
	public Collection<ImageDomainObject> getImages(Integer docId, Integer docVersionNo, Integer languageId) {
		return findByNamedQueryAndNamedParam("Image.getByDocIdAndDocVersionNoAndLanguageId",
				new String[] {"docId", "docVersionNo", "languageId"},
				new Object[] {docId, docVersionNo, languageId}
		);
    }
	
	public LanguageDao getLanguageDao() {
		return languageDao;
	}

	public void setLanguageDao(LanguageDao languageDao) {
		this.languageDao = languageDao;
	}	
}
