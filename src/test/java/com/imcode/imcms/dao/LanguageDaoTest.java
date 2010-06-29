package com.imcode.imcms.dao;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.SystemProperty;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;


public class LanguageDaoTest {

    LanguageDao languageDao;

    SystemDao systemDao;


    @BeforeClass
    public static void recreateDB() {
        Script.recreateDB();
    }


    @Before
    public void resetDBData() {
        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {SystemProperty.class, I18nLanguage.class},
                "src/main/resources/com/imcode/imcms/hbm/I18nLanguage.hbm.xml");

        Script.runDBScripts("language_dao.sql");

        systemDao = new SystemDao();
        languageDao = new LanguageDao();
        
        systemDao.setSessionFactory(sf);
        languageDao.setSessionFactory(sf);
    }
    

    @Test
    public void getAllLanguages() {
        List<I18nLanguage> languages = languageDao.getAllLanguages();
        assertTrue( "DB contains 2 languages.", languages.size() == 2);
    }


    @Test
    public void getLanguageByCode() {
        for (String code: new String[] {"en", "sv"}) {
            I18nLanguage language = languageDao.getByCode(code);
            assertNotNull(String.format("Language with code %s is exists.", code), language);
            assertEquals("Language code is correct.", code, language.getCode());
        }

        assertNull(String.format("Language with code %s does not exists.", "ee"), languageDao.getByCode("ee"));
    }

    @Test
    public void getLanguageById() {
        for (Integer id: new Integer[] {1,2}) {
            I18nLanguage language = languageDao.getById(id);
            assertNotNull(String.format("Language with id %d is exists.", id), language);
            assertEquals("Language id is correct.", id, language.getId());
        }

        assertNull(String.format("Language with id %d does not exists.", 3), languageDao.getById(3));
    }


    @Test
    public void saveLanguage() {
        final Integer id = 3;
        final String code = "ee";

        assertNull(String.format("Language with id %d does not exists.", id), languageDao.getById(3));
        assertNull(String.format("Language with code %s does not exists.", code), languageDao.getByCode(code));

        I18nLanguage language = new I18nLanguage();
        language.setId(id);
        language.setCode(code);
        language.setName("Estonain");
        language.setNativeName("Eesti");
        language.setEnabled(true);

        languageDao.saveLanguage(language);

        assertNotNull(String.format("Language with id %d exists.", id), languageDao.getById(3));
        assertNotNull(String.format("Language with code %s exists.", code), languageDao.getByCode(code));
    }


    @Test
    public void updateLanguage() {
        I18nLanguage language = languageDao.getById(1);

        assertTrue("Language is enabled.", language.isEnabled());
        
        language.setEnabled(false);

        languageDao.saveLanguage(language);

        language = languageDao.getById(1);
        
        assertFalse("Language is disabled.", language.isEnabled());
    }    


    @Test
    public void getDefaultLanguage() {
        SystemProperty property = systemDao.getProperty("DefaultLanguageId");

        assertNotNull("DefaultLanguageId system property exists.", property);
        assertEquals(String.format("DefaultLanguageId system property is set to %d.", 1), new Integer(1), property.getValueAsInteger());

        I18nLanguage language = languageDao.getById(property.getValueAsInteger());

        assertNotNull("Default language exists.", language);
    }


    @Test
    public void changeDefaultLanguage() {
        SystemProperty property = systemDao.getProperty("languageId");
        property.setValue("2");
        systemDao.saveProperty(property);

        I18nLanguage language = languageDao.getById(systemDao.getProperty("DefaultLanguageId").getValueAsInteger());

        assertEquals("Language id is correct.", language.getId(), new Integer(2));
    }
}