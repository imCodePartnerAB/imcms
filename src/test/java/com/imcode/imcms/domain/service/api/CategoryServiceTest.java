package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @see CategoryService
 */
@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryDataInitializer categoryDataInitilizer;

    @Before
    public void setUp() {
        categoryDataInitilizer.createData(4);
    }

    @Test
    public void getAllExpectedEqualsCategoriesAsDtoTest() {
        assertEquals(categoryDataInitilizer.getCategoriesAsDTO(), categoryService.getAll());
    }

    @Test
    public void getById_When_Exist_Expect_NotNull() {
        final Integer id = categoryDataInitilizer.getCategoriesAsDTO().get(0).getId();
        assertNotNull(categoryService.getById(id));
    }

    @Test
    public void save_When_NotExistBefore_Expect_Saved() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false, new ArrayList<>()
        );
        final CategoryTypeJPA savedType = new CategoryTypeJPA(categoryTypeService.save(categoryType));

        final String testCategoryName = "test_category_name" + System.currentTimeMillis();
        final Category category = new CategoryJPA(testCategoryName, "dummy", "", savedType);
        final Category saved = categoryService.save(category);

        final Category found = categoryService.getById(saved.getId());

        assertEquals(saved, found);
    }
}
