package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryTypeControllerTest extends AbstractControllerTest {
    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Before
    public void prepareData() {
        categoryDataInitializer.cleanRepositories();
    }

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
        categoryDataInitializer.createData(4);
        final String expectedCategories = asJson(categoryDataInitializer.getCategoryTypesAsDTO());
        getAllExpectedOkAndJsonContentEquals(expectedCategories);
    }

    @Test
    public void getAllExpectedOkAndJsonContentEqualsEmptyArrayOnNonExistingCategoryTypesTest() throws Exception {
        getAllExpectedOkAndJsonContentEquals("[]");
    }

}
