package com.imcode.imcms.service;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.util.datainitializer.CategoryDataInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CategoryTypeServiceTest {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryDataInitializer categoryDataInitilizer;

    @Before
    public void setUpCategoryDataInitilizer() {
        categoryDataInitilizer.init(4);
    }

    @Test
    public void getAllExpectedEqualsCategoryTypesAsDtoTest() {
        assertEquals(categoryDataInitilizer.getCategoryTypesAsDTO(), categoryTypeService.getAll());
    }

    @After
    public void clearData() {
        categoryDataInitilizer.cleanRepositories();
    }

}
