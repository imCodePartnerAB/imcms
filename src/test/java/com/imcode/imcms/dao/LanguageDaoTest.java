package com.imcode.imcms.dao;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.SystemProperty;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;
import static com.imcode.imcms.dao.Utils.languageDao;
import static com.imcode.imcms.dao.Utils.systemDao;
import static org.testng.Assert.assertEquals;


/**
 *
 */
@Test(groups = "dao")
public class LanguageDaoTest extends DaoTest {

    @Test
    public void getAllLanguages() {
        List<I18nLanguage> languages = languageDao.getAllLanguages();
        assertTrue(languages.size() == 3, "DB contains 3 languages.");
    }


    @Test
    public void getLanguageByCode() {
        for (String code: new String[] {"en", "sv", "ee"}) {
            I18nLanguage language = languageDao.getByCode(code);
            assertNotNull(language, String.format("Language with code %s is exists.", code));
            assertEquals(code, language.getId(), "Language code is correct.");
        }

        assertNull(languageDao.getByCode("ru"), String.format("Language with code %s does not exists.", "ru"));
    }

    @Test
    public void getLanguageById() {
        for (Integer id: new Integer[] {1,2,3}) {
            I18nLanguage language = languageDao.getById(id);
            assertNotNull(language, String.format("Language with id %d is exists.", id));
            assertEquals(id, language.getId(), "Language id is correct.");
        }

        assertNull(languageDao.getById(4), String.format("Language with id %d does not exists.", 4));
    }


    @Test(dependsOnMethods = {"getLanguageByCode", "getLanguageById"})
    public void saveLanguage() {
        final Integer id = 4;
        final String code = "ru";

        assertNull(languageDao.getById(4), String.format("Language with id %d does not exists.", id));
        assertNull(languageDao.getByCode(code), String.format("Language with code %s does not exists.", code));

        I18nLanguage language = new I18nLanguage();
        language.setId(id);
        language.setCode(code);
        language.setName("Russian");
        language.setNativeName("Russkij");
        language.setEnabled(true);

        languageDao.saveLanguage(language);

        assertNotNull(languageDao.getById(4), String.format("Language with id %d exists.", id));
        assertNotNull(languageDao.getByCode(code), String.format("Language with code %s exists.", code));
    }


    @Test(dependsOnMethods = {"getLanguageByCode", "getLanguageById"})
    public void updateLanguage() {
        I18nLanguage language = languageDao.getById(1);

        assertTrue(language.isEnabled(), "Language is enabled.");
        
        language.setEnabled(false);

        languageDao.saveLanguage(language);

        language = languageDao.getById(1);
        
        assertTrue(!language.isEnabled(), "Language is disabled.");
    }    


    @Test(dependsOnMethods = {"getLanguageById"})
    public void getDefaultLanguage() {
        SystemProperty property = systemDao.getProperty("LanguageId");

        assertNotNull(property, "LanguageId system property exists.");
        assertEquals(property.getValue(), new Integer(1), String.format("LanguageId system property is set to %d.", 1));

        I18nLanguage language = languageDao.getById(property.getValueAsInteger());

        assertNotNull(language, "Default language exists.");
    }


    @Test(dependsOnMethods = "getDefaultLanguage")
    public void changeDefaultLanguage() {
        SystemProperty property = systemDao.getProperty("LanguageId");
        property.setValue("2");
        systemDao.saveProperty(property);

        I18nLanguage language = languageDao.getById(systemDao.getProperty("LanguageId").getValueAsInteger());

        assertEquals(language.getId(), new Integer(2), "Language id is correct.");
    }

    @Override
    protected String getDataSetFileName() {
        return "dbunit-language.xml";
    }
}
