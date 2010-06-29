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

import static org.junit.Assert.*;

public class ImageDaoTest {

    static ImageDao imageDao;

    static LanguageDao languageDao;
	
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
                "src/main/resources/com/imcode/imcms/hbm/I18nLanguage.hbm.xml",
                "src/main/resources/com/imcode/imcms/hbm/Image.hbm.xml");

        imageDao = new ImageDao();
        imageDao.setSessionFactory(sf);

        languageDao = new LanguageDao();
        languageDao.setSessionFactory(sf);

        imageDao.setLanguageDao(languageDao);

        Script.runDBScripts("image_dao.sql");
    }

    @Test
	public void testGetImagesByIndex() {
        List<ImageDomainObject> images = imageDao.getImagesByIndex(1001, 0, 0, false);
        assertEquals(2, images.size());
    }


    @Test
	public void getImagesByDocIdAndDocVersionNo() {
		List<ImageDomainObject> images = imageDao.getImages(1001, 0);

        assertEquals(6, images.size());
    }

    @Test
	public void getImagesByDocIdAndDocVersionNoAndLanguage() {
        Collection<ImageDomainObject> images = imageDao.getImages(1001, 0, ENGLISH.getId());
        
        assertEquals(3, images.size());
    }


    @Test
	public void getImageByDocIdAndDocVersionNoAndNoAndLanguage() {
		ImageDomainObject image = imageDao.getImage (ENGLISH.getId(), 1001, 0, 0);

        assertNotNull(image);
	}


	@Test
	public void deleteImages() {
        int deletedCount = imageDao.deleteImages(1001, 0, ENGLISH.getId());

        assertEquals(3, deletedCount);
	}


	@Test
	public void saveImage() {
        ImageDomainObject image = Factory.createImage(1001, 0, ENGLISH, 1000);

        imageDao.saveImage(image);
	}

	@Test
	public void saveImageHistory() {
        ImageDomainObject image = Factory.createImage(1001, 0, ENGLISH, 1000);
        
        ImageHistory imageHistory = new ImageHistory(image, ADMIN);

        imageDao.saveImageHistory(imageHistory);
	}
}