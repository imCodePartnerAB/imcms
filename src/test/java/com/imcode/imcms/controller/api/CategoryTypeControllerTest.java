package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.CategoryAlreadyExistsException;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
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

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CategoryTypeControllerTest extends AbstractControllerTest {

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @BeforeEach
    public void prepareData() {
        categoryDataInitializer.cleanRepositories();
    }

    @Override
    protected String controllerPath() {
        return "/category-types";
    }

    @Test
    public void getAll_Expected_OkAndCorrectEntities() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        categoryDataInitializer.createTypeData(4);
        final String expectedCategories = asJson(categoryTypeService.getAll());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedCategories);
    }

    @Test
    public void getAll_WhenCategoryTypeNotCreated_Expected_OkAndEmptyResult() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void create_WhenCategoryTypeNotExists_Expected_OkAndCorrectEntity() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());

        final CategoryTypeDTO categoryType = new CategoryTypeDTO(new CategoryTypeJPA(
                null, "name", 0, false, false
        ));

        performPostWithContentExpectOk(categoryType);

        final List<CategoryType> categoriesTypes = categoryTypeService.getAll();

        assertFalse(categoriesTypes.isEmpty());
        assertEquals(1, categoriesTypes.size());
    }

    @Test
    public void create_WhenCategoryTypeNameExists_Expected_CorrectException() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        categoryDataInitializer.createTypeData(2);
        List<CategoryType> categoryTypes = categoryTypeService.getAll();
        assertFalse(categoryTypeService.getAll().isEmpty());

        final CategoryTypeDTO categoryType = new CategoryTypeDTO(new CategoryTypeJPA(
                null, categoryTypes.get(0).getName(), 0, false, false
        ));

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(categoryType);

        performRequestBuilderExpectException(DataIntegrityViolationException.class, requestBuilder);

        assertEquals(2, categoryTypes.size());
    }

    @Test
    public void update_WhenCategoryTypeNameExists_Expected_CorrectException() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        categoryDataInitializer.createTypeData(2);
        List<CategoryType> categoryTypes = categoryTypeService.getAll();
        assertFalse(categoryTypeService.getAll().isEmpty());

        CategoryType firstCategoryType = categoryTypes.get(0);
        firstCategoryType.setName(categoryTypes.get(1).getName());

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(firstCategoryType);

        performRequestBuilderExpectException(DataIntegrityViolationException.class, requestBuilder);
    }

    @Test
    public void update_WhenCategoryTypeNameNotExists_Expected_OkAndUpdatedCorrectEntity() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        categoryDataInitializer.createTypeData(2);
        List<CategoryType> categoryTypes = categoryTypeService.getAll();
        assertFalse(categoryTypeService.getAll().isEmpty());

        CategoryType firstCategoryType = categoryTypes.get(0);
        firstCategoryType.setName("Other name");

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(firstCategoryType);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(firstCategoryType));

    }

    @Test
    public void delete_WhenCategoryTypeHasCategories_Expected_CorrectException() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        categoryDataInitializer.createData(2);
        final List<CategoryType> categoryTypes = categoryTypeService.getAll();
        assertFalse(categoryTypeService.getAll().isEmpty());

        final CategoryType firstCategoryType = categoryTypes.get(0);
        final int id = firstCategoryType.getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + id);

        performRequestBuilderExpectException(CategoryAlreadyExistsException.class, requestBuilder);
    }

    @Test
    public void delete_WhenCategoryTypeHasNotCategories_Expected_OkAndDeleted() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        categoryDataInitializer.createTypeData(2);
        final List<CategoryType> categoryTypes = categoryTypeService.getAll();
        assertFalse(categoryTypeService.getAll().isEmpty());

        final CategoryType firstCategoryType = categoryTypes.get(0);
        final int id = firstCategoryType.getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + id);

        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(categoryTypes.size() - 1, categoryTypeService.getAll().size());
    }

    @Test
    public void getById_WhenCategoryTypeExists_Expected_OkAndCorrectEntity() throws Exception {
        assertTrue(categoryTypeService.getAll().isEmpty());
        categoryDataInitializer.createTypeData(2);
        final List<CategoryType> categoryTypes = categoryTypeService.getAll();
        assertFalse(categoryTypeService.getAll().isEmpty());

        final CategoryType firstCategoryType = categoryTypes.get(0);
        final int id = firstCategoryType.getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + id);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(firstCategoryType));


    }
}
