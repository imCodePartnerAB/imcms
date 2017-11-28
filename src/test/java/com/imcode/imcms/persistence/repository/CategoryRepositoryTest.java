package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Before
    public void setUp() {
        categoryDataInitializer.createData(2);
    }

    @Test
    public void findByTypeExpectedCorrectNameTest() throws Exception {
        final List<CategoryTypeJPA> types = categoryDataInitializer.getTypes();
        final List<CategoryJPA> categories = categoryRepository.findByType(types.get(0));

        assertEquals(1, categories.size());
        assertEquals("Category0Name", categories.get(0).getName());
    }

    @Test
    public void findByNameAndTypeExpectedExistCategoryWithCorrectNameAndCategoryTypeNameTest() throws Exception {
        final List<CategoryTypeJPA> types = categoryDataInitializer.getTypes();

        final CategoryJPA category = categoryRepository.findByNameAndType("Category0Name", types.get(0));

        assertNotNull(category);
        assertEquals("Category0Name", category.getName());
        assertEquals("CategoryType0Name", category.getType().getName());
    }

    @Test
    public void findByNameAndTypeExpectedNullCategoryTest() throws Exception {
        final List<CategoryTypeJPA> types = categoryDataInitializer.getTypes();

        final CategoryJPA category = categoryRepository.findByNameAndType("Category0Name", types.get(1));

        assertNull(category);
    }


}
