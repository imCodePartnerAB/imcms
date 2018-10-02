package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CategoryControllerTest extends AbstractControllerTest {
    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @AfterEach
    public void cleanRepos() {
        categoryDataInitializer.cleanRepositories();
    }

    @Override
    protected String controllerPath() {
        return "/categories";
    }

    @Test
    public void getAllExpectedOkAndJsonContentEqualsCategoyDtosAsJsonTest() throws Exception {
        categoryDataInitializer.createData(4);
        final String expectedCategories = asJson(categoryDataInitializer.getCategoriesAsDTO());
        getAllExpectedOkAndJsonContentEquals(expectedCategories);
    }

    @Test
    public void getAllExpectedOkAndJsonContentEqualsEmptyArrayOnNonExistingCategoryTest() throws Exception {
        getAllExpectedOkAndJsonContentEquals("[]");
    }

}
