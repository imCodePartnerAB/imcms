package com.imcode.imcms.controller;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.util.datainitializer.CategoryDataInitializer;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
@WebAppConfiguration
public class CategoryTypeControllerTest extends AbstractControllerTest {
    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @After
    public void cleanRepos() {
        categoryDataInitializer.cleanRepositories();
    }

    @Override
    protected String controllerPath() {
        return "/category-types";
    }

    @Test
    public void getAllExpectedOkAndJsonContentEqualsCategoyTypeDtosAsJsonTest() throws Exception {
        categoryDataInitializer.init(4);
        final String expectedCategories = asJson(categoryDataInitializer.getCategoryTypesAsDTO());
        getAllExpectedOkAndJsonContentEquals(expectedCategories);
    }

    @Test
    public void getAllExpectedOkAndJsonContentEqualsEmptyArrayOnNonExistingCategoryTypesTest() throws Exception {
        getAllExpectedOkAndJsonContentEquals("[]");
    }

}
