package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CategoryTypeControllerTest extends AbstractControllerTest {

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @BeforeEach
    public void prepareData() {
        categoryDataInitializer.cleanRepositories();
    }

    @AfterEach
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
