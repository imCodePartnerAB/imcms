package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocLanguage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class, DocLanguageDaoTestConf.class})
@Transactional
public class DocLanguageDaoTest {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.archive.autodetection", "");
        EntityManagerFactory end = Persistence.createEntityManagerFactory(null, properties);
        System.out.println("end = " + end);
    }

    @Inject
    DocLanguageDao docLanguageDao;

    @PersistenceContext
    EntityManager entityManager;

    public List<DocLanguage> recreateLanguages() {
        entityManager.createQuery("DELETE FROM DocLanguage").executeUpdate();
        entityManager.clear();

        DocLanguage en = new DocLanguage();
        DocLanguage se = new DocLanguage();

        en.setCode("en");
        se.setCode("se");

        en.setName("English");
        se.setName("Swedish");

        return Arrays.asList(entityManager.merge(en), entityManager.merge(se));
    }

    @Test
    public void testFindAll() throws Exception {
        recreateLanguages();
        assertEquals(2, docLanguageDao.findAll().size());
    }

    @Test
    public void testGetById() throws Exception {
        List<DocLanguage> languages = recreateLanguages();

        assertEquals(languages.get(0), docLanguageDao.findOne(languages.get(0).getId()));
        assertEquals(languages.get(1), docLanguageDao.findOne(languages.get(1).getId()));
    }

    @Test
    public void testGetByCode() throws Exception {
        List<DocLanguage> languages = recreateLanguages();

        assertEquals(languages.get(0), docLanguageDao.getByCode("en"));
        assertEquals(languages.get(1), docLanguageDao.getByCode("se"));
    }

    @Test
    public void testSave() throws Exception {
        recreateLanguages();

        DocLanguage language = new DocLanguage();

        language.setId(1);
        language.setCode("en");
        language.setName("English");

        docLanguageDao.save(language);
    }

    @Test
    public void testDeleteByCode() throws Exception {
        recreateLanguages();

        assertNotNull(docLanguageDao.getByCode("en"));
        docLanguageDao.deleteByCode("en");
        assertNull(docLanguageDao.getByCode("en"));
    }
}

@Configuration
@ComponentScan(basePackages = {"com.imcode.imcms.mapping.dao"})
class DocLanguageDaoTestConf {

}