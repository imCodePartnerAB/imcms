package com.imcode.imcms.dao;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.TextHistory;
import com.imcode.imcms.util.Factory;
import imcode.server.document.textdocument.TextDomainObject;

import imcode.server.user.UserDomainObject;

import org.dbunit.DataSourceDatabaseTester;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.List;

import static org.junit.Assert.*;

public class TextDaoTest {
	
	static TextDao textDao;

    static DataSourceDatabaseTester dbTester;

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
                new Class[] {I18nLanguage.class, TextDomainObject.class, TextHistory.class},
                "src/main/resources/I18nLanguage.hbm.xml",
                "src/main/resources/Text.hbm.xml");

        textDao = new TextDao();
        textDao.setSessionFactory(sf);

        Script.runDBScripts("text_dao.sql");
    }
    


    @Test
    public void saveText() {
        TextDomainObject text = Factory.createText(1001, 0, 0, ENGLISH);

        textDao.saveText(text);
    }


    @Test
    public void updateText() {
        TextDomainObject text = Factory.createText(1001, 0, 0, ENGLISH);

        textDao.saveText(text);

        TextDomainObject updatedText = Factory.createText(1001, 0, 0, ENGLISH);
        updatedText.setId(text.getId());

        updatedText.setText("updated text");
        textDao.save(updatedText);
    }


    @Test
    public void deleteTexts() {
        for (int no = 0; no < 3; no++) {
            TextDomainObject text_en = Factory.createText(1001, 0, no, ENGLISH);
            TextDomainObject text_sw = Factory.createText(1001, 0, no, SWEDISH);

            textDao.saveText(text_en);
            textDao.saveText(text_sw);
        }

        int deletedCount_en = textDao.deleteTexts(1001, 0, ENGLISH.getId());

        assertEquals(deletedCount_en, 3);

        int deletedCount_sw = textDao.deleteTexts(1001, 0, SWEDISH);

        assertEquals(deletedCount_sw, 3);
    }

    @Test
    public void saveTextHistory() {
        TextHistory textHistory = new TextHistory(Factory.createText(1001, 0, 0, ENGLISH), ADMIN);

        textDao.saveTextHistory(textHistory);
    }


    @Test
    public void getTextsByDocIdAndDocVersionNo() {
        for (int versionNo = 0; versionNo < 3; versionNo++) {
            for (int no = 0; no < 5; no++) {
                TextDomainObject text_en = Factory.createText(1001, versionNo, no, ENGLISH);
                TextDomainObject text_sw = Factory.createText(1001, versionNo, no, SWEDISH);

                textDao.saveText(text_en);
                textDao.saveText(text_sw);
            }
        }

        for (int versionNo = 0; versionNo < 3; versionNo++) {
            List<TextDomainObject> texts = textDao.getTexts(1001, versionNo);

            assertEquals(texts.size(), 5 * 2);
        }
    }


    @Test
    public void getTextsByDocIdAndDocVersionNoAndLanguage() {
        for (int versionNo = 0; versionNo < 3; versionNo++) {
            for (int no = 0; no < 5; no++) {
                TextDomainObject text_en = Factory.createText(1001, versionNo, no, ENGLISH);
                TextDomainObject text_sw = Factory.createText(1001, versionNo, no, SWEDISH);

                textDao.saveText(text_en);
                textDao.saveText(text_sw);
            }
        }

        for (int versionNo = 0; versionNo < 3; versionNo++) {
            List<TextDomainObject> texts_en = textDao.getTexts(1001, versionNo, ENGLISH);
            List<TextDomainObject> texts_sw = textDao.getTexts(1001, versionNo, SWEDISH);

            assertEquals(texts_en.size(), 5);
            assertEquals(texts_sw.size(), 5);
        }
    }
}