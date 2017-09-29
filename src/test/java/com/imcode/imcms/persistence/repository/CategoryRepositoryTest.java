package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.persistence.entity.Category;
import com.imcode.imcms.persistence.entity.CategoryType;
import com.imcode.imcms.util.datainitializer.CategoryDataInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Before
    public void setUp() {
        categoryDataInitializer.init(2);
    }

    @Test
    public void findByTypeExpectedCorrectNameTest() throws Exception {
        final List<CategoryType> types = categoryDataInitializer.getTypes();
        final List<Category> categories = categoryRepository.findByType(types.get(0));

        assertEquals(1, categories.size());
        assertEquals("Category0Name", categories.get(0).getName());
    }

    @Test
    public void findByNameAndTypeExpectedExistCategoryWithCorrectNameAndCategoryTypeNameTest() throws Exception {
        final List<CategoryType> types = categoryDataInitializer.getTypes();

        final Category category = categoryRepository.findByNameAndType("Category0Name", types.get(0));

        assertNotNull(category);
        assertEquals("Category0Name", category.getName());
        assertEquals("CategoryType0Name", category.getType().getName());
    }

    @Test
    public void findByNameAndTypeExpectedNullCategoryTest() throws Exception {
        final List<CategoryType> types = categoryDataInitializer.getTypes();

        final Category category = categoryRepository.findByNameAndType("Category0Name", types.get(1));

        assertNull(category);
    }


}
