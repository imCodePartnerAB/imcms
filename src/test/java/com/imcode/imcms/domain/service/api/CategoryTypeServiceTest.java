package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class CategoryTypeServiceTest {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryDataInitializer categoryDataInitilizer;

    @Before
    public void setUpCategoryDataInitilizer() {
        categoryDataInitilizer.createData(4);
    }

    @After
    public void clearData() {
        categoryDataInitilizer.cleanRepositories();
    }

    @Test
    public void getAllExpectedEqualsCategoryTypesAsDtoTest() {
        assertEquals(categoryDataInitilizer.getCategoryTypesAsDTO(), categoryTypeService.getAll());
    }

    @Test
    public void save_When_NotExitBefore_Expect_Saved() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(testTypeName, 0, false, false);

        categoryTypeService.save(categoryType);

        final long numberOfMatchedCategoriesByName = categoryTypeService.getAll()
                .stream()
                .filter(categoryType1 -> testTypeName.equals(categoryType1.getName()))
                .count();

        assertEquals(numberOfMatchedCategoriesByName, 1L);
    }

}
