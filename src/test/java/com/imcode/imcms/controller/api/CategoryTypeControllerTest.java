package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
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
    public void getAllExpectedOkAndJsonContentEqualsCategoryTypeDtoAsJsonTest() throws Exception {
        categoryDataInitializer.init(4);
        final String expectedCategories = asJson(categoryDataInitializer.getCategoryTypesAsDTO());
        getAllExpectedOkAndJsonContentEquals(expectedCategories);
    }

    @Test
    public void getAllExpectedOkAndJsonContentEqualsEmptyArrayOnNonExistingCategoryTypesTest() throws Exception {
        getAllExpectedOkAndJsonContentEquals("[]");
    }

}
