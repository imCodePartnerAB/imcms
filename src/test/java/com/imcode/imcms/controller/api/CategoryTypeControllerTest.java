package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.exception.CategoryTypeHasCategoryException;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.CategoryTypeDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class CategoryTypeControllerTest extends AbstractControllerTest {

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private CategoryTypeController categoryTypeController;

    @Autowired
    private CategoryTypeDataInitializer categoryTypeDataInitializer;

    @BeforeEach
    public void prepareData() {
        categoryDataInitializer.cleanRepositories();
        categoryTypeDataInitializer.cleanRepositories();
    }

    @Override
    protected String controllerPath() {
        return "/category-types";
    }

    @Test
    public void getAll_Expected_OkAndCorrectEntities() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        categoryTypeDataInitializer.createTypeData(4);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(categoryTypeController.getCategoryTypes()));
    }

    @Test
    public void getAll_WhenCategoryTypeNotCreated_Expected_OkAndEmptyResult() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void getById_WhenCategoryTypeExists_Expected_OkAndCorrectEntity() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        categoryTypeDataInitializer.createTypeData(4);

        final List<CategoryType> categoryTypes = categoryTypeController.getCategoryTypes();
        assertFalse(categoryTypeController.getCategoryTypes().isEmpty());

        final CategoryType firstCategoryType = categoryTypes.get(0);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                controllerPath() + "/" + firstCategoryType.getId()
        );

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(firstCategoryType));
    }

    @Test
    public void create_WhenCategoryTypeNotExists_Expected_OkAndCorrectEntity() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());

        final CategoryTypeDTO categoryType = new CategoryTypeDTO(new CategoryTypeJPA(
                null, "name", false, false
        ));

        performPostWithContentExpectOk(categoryType);
        assertEquals(1, categoryTypeController.getCategoryTypes().size());
    }

    @Test
    public void create_WhenCategoryTypeNameExists_Expected_CorrectException() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        categoryTypeDataInitializer.createTypeData(4);

        final List<CategoryType> categoryTypes = categoryTypeController.getCategoryTypes();
        assertFalse(categoryTypes.isEmpty());

        final CategoryTypeDTO categoryType = new CategoryTypeDTO(new CategoryTypeJPA(
                null, categoryTypes.get(0).getName(), false, false
        ));

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(categoryType);

        performRequestBuilderExpectException(DataIntegrityViolationException.class, requestBuilder);
    }

    @Test
    public void update_WhenCategoryTypeNameExists_Expected_CorrectException() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        categoryTypeDataInitializer.createTypeData(4);

        final List<CategoryType> categoryTypes = categoryTypeController.getCategoryTypes();
        assertFalse(categoryTypeController.getCategoryTypes().isEmpty());

        final CategoryType firstCategoryType = categoryTypes.get(0);
        firstCategoryType.setName(categoryTypes.get(1).getName());

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(firstCategoryType);

        performRequestBuilderExpectException(DataIntegrityViolationException.class, requestBuilder);
    }

    @Test
    public void update_WhenCategoryTypeNameNotExists_Expected_OkAndUpdatedCorrectEntity() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        categoryTypeDataInitializer.createTypeData(4);

        final List<CategoryType> categoryTypes = categoryTypeController.getCategoryTypes();
        assertFalse(categoryTypeController.getCategoryTypes().isEmpty());

        CategoryType firstCategoryType = categoryTypes.get(0);
        firstCategoryType.setName("Other name");

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(firstCategoryType);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(firstCategoryType));

    }

    @Test
    public void delete_WhenCategoryTypeHasCategories_Expected_CorrectException() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        categoryDataInitializer.createData(2);

        final List<CategoryType> categoryTypes = categoryTypeController.getCategoryTypes();
        assertFalse(categoryTypeController.getCategoryTypes().isEmpty());

        final CategoryType firstCategoryType = categoryTypes.get(0);
        final int id = firstCategoryType.getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + id);

        performRequestBuilderExpectException(CategoryTypeHasCategoryException.class, requestBuilder);
    }

    @Test
    public void delete_WhenCategoryTypeHasNotCategories_Expected_OkAndDeleted() throws Exception {
        assertTrue(categoryTypeController.getCategoryTypes().isEmpty());
        categoryTypeDataInitializer.createTypeData(4);

        final List<CategoryType> categoryTypes = categoryTypeController.getCategoryTypes();
        assertFalse(categoryTypeController.getCategoryTypes().isEmpty());

        final CategoryType firstCategoryType = categoryTypes.get(0);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                controllerPath() + "/" + firstCategoryType.getId()
        );

        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(categoryTypes.size() - 1, categoryTypeController.getCategoryTypes().size());
    }
}
