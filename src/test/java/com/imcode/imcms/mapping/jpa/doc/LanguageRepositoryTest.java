//package com.imcode.imcms.mapping.jpa.doc;
//
//import com.imcode.imcms.mapping.jpa.JpaConfiguration;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {JpaConfiguration.class})
//@Transactional
//public class LanguageRepositoryTest {
//
//    @Inject
//    LanguageRepository repository;
//
//    @PersistenceContext
//    EntityManager entityManager;
//
//    public List<Language> recreateLanguages() {
//        repository.deleteAll();
//
//        return Arrays.asList(
//                repository.saveAndFlush(new Language("en", "English", "English")),
//                repository.saveAndFlush(new Language("se", "Swedish", "Svenska"))
//        );
//    }
//
////    @Test
////    public void testFindAll() throws Exception {
////        recreateLanguages();
////        assertEquals(2, repository.findAll().size());
////    }
//
////    @Test
////    public void testFindById() throws Exception {
////        List<Language> languages = recreateLanguages();
////
////        assertEquals(languages.get(0), repository.findOne(languages.get(0).getId()));
////        assertEquals(languages.get(1), repository.findOne(languages.get(1).getId()));
////    }
//
////    @Test
////    public void testFindByCode() throws Exception {
////        List<Language> languages = recreateLanguages();
////
////        assertEquals(languages.get(0), repository.findByCode("en"));
////        assertEquals(languages.get(1), repository.findByCode("se"));
////    }
//
////    @Test
////    public void testSave() throws Exception {
////        recreateLanguages();
////
////        Language language = new Language();
////
////        language.setId(1);
////        language.setCode("en");
////        language.setName("English");
////
////        repository.save(language);
////    }
//
////    @Test
////    public void testDeleteByCode() throws Exception {
////        recreateLanguages();
////
////        assertNotNull(repository.findByCode("en"));
////        repository.deleteByCode("en");
////        assertNull(repository.findByCode("en"));
////    }
//}