package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.config.TestConfig;
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
        categoryDataInitilizer.createData(4);
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
