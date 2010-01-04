package com.imcode.imcms.dao;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang.StringUtils;

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
                image.setDocId(documentId);
                image.setName("" + imageId);
				
				image.setLanguage(language);
			}
			
			if (image != null) {
				images.add(setImageSource(image));
			}			
		}
						
		return images;
	}


    @Transactional
	public synchronized ImageDomainObject getImage(int languageId,
			Integer docId, Integer docVersionNo, int no) {
		
		ImageDomainObject image = (ImageDomainObject)getSession().createQuery("select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no and i.language.id = :languageId")
			.setParameter("docId", docId)
			.setParameter("docVersionNo", docVersionNo)
			.setParameter("no", "" + no)
			.setParameter("languageId", languageId)
			.uniqueResult();
		
		return setImageSource(image);
	}
	
	@Transactional
	public ImageDomainObject saveImage(ImageDomainObject image) {
		saveOrUpdate(image);
		
		return image;
	}
	
	@Transactional
	public List<ImageDomainObject> getImages(Integer docId, Integer docVersionNo) {
		Collection<ImageDomainObject> images =  findByNamedQueryAndNamedParam("Image.getByDocIdAndDocVersionNo",
				new String[] {"docId", "docVersionNo"},
				new Object[] {docId, docVersionNo}	
		);

        return (List<ImageDomainObject>)setImagesSources(images);
    }

	@Transactional
	public Collection<ImageDomainObject> getImages(Integer docId, Integer docVersionNo, Integer languageId) {
        Collection<ImageDomainObject> images = findByNamedQueryAndNamedParam("Image.getByDocIdAndDocVersionNoAndLanguageId",
				new String[] {"docId", "docVersionNo", "languageId"},
				new Object[] {docId, docVersionNo, languageId}
		);

        return setImagesSources(images);
    }


	@Transactional
	public int deleteImages(Integer docId, Integer docVersionNo) {
		return getSession().getNamedQuery("Image.deleteImages")
			.setParameter("docId", docId)
			.setParameter("docVersionNo", docVersionNo)
			.executeUpdate();
	}    


    /**
     * Initializes images sources.
     */
	public static Collection<ImageDomainObject> setImagesSources(Collection<ImageDomainObject> images) {
		for (ImageDomainObject image: images) {
            setImageSource(image);
        }

        return images;
	}

    
    /**
     * Initializes image source. 
     */
	public static ImageDomainObject setImageSource(ImageDomainObject image) {
		if (image == null) {
			return null;
		}

		String url = image.getImageUrl();

		if (!StringUtils.isBlank(url)) {
			image.setSource(new ImagesPathRelativePathImageSource(url));
		}

		return image;
	}

	public LanguageDao getLanguageDao() {
		return languageDao;
	}

	public void setLanguageDao(LanguageDao languageDao) {
		this.languageDao = languageDao;
	}    
}
