//package com.imcode.imcms.mapping.jpa.doc;
//
//import com.imcode.imcms.mapping.jpa.JpaConfiguration;
//import com.imcode.imcms.mapping.jpa.doc.CategoryRepository;
//import com.imcode.imcms.mapping.jpa.doc.CategoryTypeRepository;
//import com.imcode.imcms.mapping.jpa.doc.Category;
//import com.imcode.imcms.mapping.jpa.doc.CategoryType;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.inject.Inject;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.*;
//import static org.hamcrest.CoreMatchers.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {JpaConfiguration.class})
//@Transactional
//public class CategoryRepositoryTest {
//
//    @Inject
//    CategoryTypeRepository categoryTypeRepository;
//
//    @Inject
//    CategoryRepository categoryRepository;
//
//    List<CategoryType> types;
//    List<Category> categories;
//
//    @Before
//    public void setUp() {
//        types = recreateTypes();
//        categories = recreateCategories();
//    }
//
//    public List<CategoryType> recreateTypes() {
//        return Arrays.asList(
//                categoryTypeRepository.saveAndFlush(new CategoryType("DocCategoryTypeOne", 0, false, false)),
//                categoryTypeRepository.saveAndFlush(new CategoryType("DocCategoryTypeTwo", 0, false, false))
//        );
//    }
//
//    public List<Category> recreateCategories() {
//        return Arrays.asList(
//                categoryRepository.saveAndFlush(
//                        new Category(
//                                "Group1", "Group1Description", "Group1ImageUrl", types.get(0)
//                        )
//                ),
//                categoryRepository.saveAndFlush(
//                        new Category(
//                                "Group2", "Group2Description", "Group2ImageUrl", types.get(1)
//                        )
//                )
//        );
//    }
//
//
////    @Test
////    public void testFindByType() throws Exception {
////        List<Category> categoryList = categoryRepository.findByType(types.get(1));
////
////        assertThat(categoryList.size(), is(1));
////        assertThat(categoryList.get(0).getName(), is("Group2"));
////    }
//
////    @Test
////    public void testFindByNameAndType() throws Exception {
////        Category category1 = categoryRepository.findByNameAndType("Group1", types.get(1));
////        Category category2 = categoryRepository.findByNameAndType("Group2", types.get(1));
////
////        assertNull(category1);
////        assertNotNull(category2);
////
////        assertThat(category2.getName(), is("Group2"));
////    }
//
////    @Test
////    public void testFindCategoryDocIds() throws Exception {
////        fail();
////    }
////
////    @Test
////    public void testDeleteByDocIdAndCategoryId() throws Exception {
////        fail();
////    }
//}
