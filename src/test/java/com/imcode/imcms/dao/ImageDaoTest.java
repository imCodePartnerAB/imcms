package com.imcode.imcms.dao;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.ImageHistory;
import com.imcode.imcms.api.TextHistory;
import com.imcode.imcms.util.Factory;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ImageDaoTest {

    static ImageDao imageDao;
	
    static I18nLanguage ENGLISH = Factory.createLanguage(1, "en", "English");

    static I18nLanguage SWEDISH = Factory.createLanguage(2, "sv", "Swedish");

    static UserDomainObject ADMIN = new UserDomainObject(0);

    
	@BeforeClass
    public static void init() {
        Script.recreateDB();
	}


    @Before
    public void resetDBData() {

        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {I18nLanguage.class, ImageDomainObject.class, TextHistory.class},
                "src/main/resources/I18nLanguage.hbm.xml",
                "src/main/resources/Image.hbm.xml");

        imageDao = new ImageDao();
        imageDao.setSessionFactory(sf);

        Script.runDBScripts("image_dao.sql");
    }

    @Test
    public void test() {
        
    }
//
//
//
//
//    @Test void getDocumentImagesByLanguage() {
//    	def language = languageDao.defaultLanguage
//        def images = imageDao.getDocumentImagesByLanguage(1001, language.id)
//
//        assertTrue(images.size() == 3)
//
//        images.each {
//    		assertTrue it.language.equals(language)
//    	}
//    }
//
//    @Test void getDocumentImagesByIndexWithCreate() {
//    	def metaId = 1001
//        def index = 10
//        def images = imageDao.getDocumentImagesByIndex(metaId, index, true)
//
//        assertTrue(images.size() == 2)
//    }
//
//
//    @Test void insertDocumentImages() {
//        def metaId = 1001
//        def index = 10
//        def images = imageDao.getDocumentImagesByIndex(metaId, index, true)
//
//        assertTrue(images.size() == 2)
//
//        String url = "file:///fake.gif"
//        def imageSource = new ImagesPathRelativePathImageSource(url)
//
//        images.each {
//        	assertTrue(it.type == null)
//
//            it.imageUrl = url
//        	it.source = imageSource
//        	it.type = imageSource.typeId
//        }
//
//        images = imageDao.saveDocumentImages(metaId, images)
//
//        images = imageDao.getDocumentImagesByIndex(metaId, index, false)
//
//        assertTrue(images.size() == 2)
//
//        images.each {
//            assertTrue it.getDocId == metaId
//        }
//    }
//
//    /*
//    @Test void getDocumentImagesByIndexNoCreate() {
//        def index = 1
//        def images = imageDao.getDocumentImagesByIndex(1001, index, false)
//
//        assertTrue(images.size() == 2)
//
//
//        //images.each {
//        //    assertTrue it.language.equals(language)
//        //}
//
//    }
//    */
//
//    /*
//	@Test void insertText() {
//		def language = languageDao.defaultLanguage
//		def metaId = 1001
//		def textIndex = 1000
//
//		def text = textDao.getText(metaId, textIndex, language.id)
//
//		assertNull(text)
//
//		text = new TextDomainObject()
//		text.setDocId(metaId)
//		text.setIndex(textIndex)
//		text.setLanguage(language)
//
//		textDao.insertText(text)
//
//		text = textDao.getText(metaId, textIndex, language.id)
//
//		assertNotNull(text)
//	}
//	*/
//
//    @Test void getImages() {
//        def metaId = 1001
//        def images = imageDao.getImages(metaId)
//
//        assertTrue(images.size() > 0)
//    }
//
//
//
//
//
//
//
//	private LanguageDao languageDao;
//
//	@Transactional
//	public synchronized List<ImageDomainObject> getImagesByIndex(
//			Integer documentId, Integer documentVersion, int imageId, boolean createImageIfNotExists) {
//
//		List<I18nLanguage> languages = languageDao.getAllLanguages();
//		List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();
//
//		for (I18nLanguage language: languages) {
//			ImageDomainObject image = getImage(language.getId(), documentId, documentVersion, imageId);
//
//			if (image == null && createImageIfNotExists) {
//                image = new ImageDomainObject();
//                image.setDocId(documentId);
//                image.setName("" + imageId);
//
//				image.setLanguage(language);
//			}
//
//			if (image != null) {
//				images.add(setImageSource(image));
//			}
//		}
//
//		return images;
//	}
//
//
//    @Transactional
//	public synchronized ImageDomainObject getImage(int languageId,
//			Integer docId, Integer docVersionNo, int no) {
//
//		ImageDomainObject image = (ImageDomainObject)getSession().createQuery("select i from Image i where i.docId = :docId AND i.docVersionNo = :docVersionNo and i.no = :no and i.language.id = :languageId")
//			.setParameter("docId", docId)
//			.setParameter("docVersionNo", docVersionNo)
//			.setParameter("no", "" + no)
//			.setParameter("languageId", languageId)
//			.uniqueResult();
//
//		return setImageSource(image);
//	}
//
//	@Transactional
//	public ImageDomainObject saveImage(ImageDomainObject image) {
//		saveOrUpdate(image);
//
//		return image;
//	}
//
//	@Transactional
//	public void saveImageHistory(ImageHistory imageHistory) {
//        save(imageHistory);
//	}
//
//	@Transactional
//	public List<ImageDomainObject> getImages(Integer docId, Integer docVersionNo) {
//		Collection<ImageDomainObject> images =  findByNamedQueryAndNamedParam("Image.getByDocIdAndDocVersionNo",
//				new String[] {"docId", "docVersionNo"},
//				new Object[] {docId, docVersionNo}
//		);
//
//        return (List<ImageDomainObject>)setImagesSources(images);
//    }
//
//	@Transactional
//	public Collection<ImageDomainObject> getImages(Integer docId, Integer docVersionNo, Integer languageId) {
//        Collection<ImageDomainObject> images = findByNamedQueryAndNamedParam("Image.getByDocIdAndDocVersionNoAndLanguageId",
//				new String[] {"docId", "docVersionNo", "languageId"},
//				new Object[] {docId, docVersionNo, languageId}
//		);
//
//        return setImagesSources(images);
//    }
//
//
//	@Transactional
//	public int deleteImages(Integer docId, Integer docVersionNo, Integer languageId) {
//		return getSession().getNamedQuery("Image.deleteImages")
//			.setParameter("docId", docId)
//			.setParameter("docVersionNo", docVersionNo)
//            .setParameter("languageId", languageId)
//			.executeUpdate();
//	}
//
//
//    /**
//     * Initializes images sources.
//     */
//	public static Collection<ImageDomainObject> setImagesSources(Collection<ImageDomainObject> images) {
//		for (ImageDomainObject image: images) {
//            setImageSource(image);
//        }
//
//        return images;
//	}
//
//
//    /**
//     * Initializes image source.
//     */
//	public static ImageDomainObject setImageSource(ImageDomainObject image) {
//		if (image == null) {
//			return null;
//		}
//
//		String url = image.getImageUrl();
//
//		if (!StringUtils.isBlank(url)) {
//			ImageSource imageSource = null;
//		    if (image.getType() == ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE) {
//		        imageSource = new ImageArchiveImageSource(url);
//		    } else {
//		        imageSource = new ImagesPathRelativePathImageSource(url);
//		    }
//
//			image.setSource(imageSource);
//		}
//
//		return image;
//	}
//
//	public LanguageDao getLanguageDao() {
//		return languageDao;
//	}
//
//	public void setLanguageDao(LanguageDao languageDao) {
//		this.languageDao = languageDao;
//	}
}