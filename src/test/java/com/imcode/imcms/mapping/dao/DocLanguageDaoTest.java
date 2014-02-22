package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocLanguage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@Transactional
public class DocLanguageDaoTest {

    @Inject
    DocLanguageDao dao;

    @PersistenceContext
    EntityManager entityManager;

    public List<DocLanguage> recreateLanguages() {
        entityManager.createQuery("DELETE FROM DocLanguage").executeUpdate();
        entityManager.clear();

        DocLanguage en = new DocLanguage("en", "English", "English", true);
        DocLanguage se = new DocLanguage("se", "Swedish", "Svenska", true);

        return Arrays.asList(entityManager.merge(en), entityManager.merge(se));
    }

    @Test
    public void testFindAll() throws Exception {
        recreateLanguages();
        assertEquals(2, dao.findAll().size());
    }

    @Test
    public void testGetById() throws Exception {
        List<DocLanguage> languages = recreateLanguages();

        assertEquals(languages.get(0), dao.findOne(languages.get(0).getId()));
        assertEquals(languages.get(1), dao.findOne(languages.get(1).getId()));
    }

    @Test
    public void testGetByCode() throws Exception {
        List<DocLanguage> languages = recreateLanguages();

        assertEquals(languages.get(0), dao.getByCode("en"));
        assertEquals(languages.get(1), dao.getByCode("se"));
    }

    @Test
    public void testSave() throws Exception {
        recreateLanguages();

        DocLanguage language = new DocLanguage();

        language.setId(1);
        language.setCode("en");
        language.setName("English");

        dao.save(language);
    }

    @Test
    public void testDeleteByCode() throws Exception {
        recreateLanguages();

        assertNotNull(dao.getByCode("en"));
        dao.deleteByCode("en");
        assertNull(dao.getByCode("en"));
    }
}