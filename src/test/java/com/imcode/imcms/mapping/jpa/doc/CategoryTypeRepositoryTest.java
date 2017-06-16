//package com.imcode.imcms.mapping.jpa.doc;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.inject.Inject;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {com.imcode.imcms.config.MainConfig.class})
//@Transactional
//public class CategoryTypeRepositoryTest {
//
//
//    @Inject
//    CategoryTypeRepository repository;
//
//    List<CategoryType> types;
//
//    @Before
//    public void setUp() {
//        types = recreateTypes();
//    }
//
//    public List<CategoryType> recreateTypes() {
//        return Arrays.asList(
//                repository.saveAndFlush(new CategoryType("DocCategoryTypeOne", 0, false, false)),
//                repository.saveAndFlush(new CategoryType("DocCategoryTypeTwo", 0, false, false))
//        );
//    }
//
//    @Test
//    public void testFindByName() throws Exception {
//        CategoryType type1 = repository.findByName("DocCategoryTypeOne");
//        CategoryType type2 = repository.findByName("DocCategoryTypeTWO");
//
//        assertNotNull(type1);
//        assertNull(type2);
//    }
//
//    @Test
//    public void testFindByNameIgnoreCase() throws Exception {
//        CategoryType type1 = repository.findByNameIgnoreCase("DocCategoryTypeOne");
//        CategoryType type2 = repository.findByNameIgnoreCase("DocCategoryTypeTWO");
//
//        assertNotNull(type1);
//        assertNotNull(type2);
//    }
//}
