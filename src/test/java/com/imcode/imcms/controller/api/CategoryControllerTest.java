package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.DataUseCategoryException;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.CategoryTypeDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CategoryControllerTest extends AbstractControllerTest {
    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private CategoryController categoryController;

    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private CategoryTypeDataInitializer categoryTypeDataInitializer;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @BeforeEach
    public void cleanRepos() {
        categoryDataInitializer.cleanRepositories();
        categoryTypeDataInitializer.cleanRepositories();
    }

    @Override
    protected String controllerPath() {
        return "/categories";
    }

    @Test
    public void getAll_Expected_OkAndCorrectEntities() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        categoryDataInitializer.createData(4);
        final String expectedCategories = asJson(categoryController.getCategories());
        getAllExpectedOkAndJsonContentEquals(expectedCategories);
    }

    @Test
    public void getAll_WhenCategoryNotCreated_Expected_OkAndEmptyResult() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }


    @Test
    public void getById_WhenCategoryExists_Expected_OkAndCorrectEntity() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        categoryDataInitializer.createData(2);
        final List<Category> categories = categoryController.getCategories();
        assertFalse(categoryController.getCategories().isEmpty());

        final Category firstCategory = categories.get(0);
        final int id = firstCategory.getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                controllerPath() + "/" + id
        );

        assertNotNull(requestBuilder);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(categories.get(0)));
    }

    @Test
    public void getCategoriesByCategoryTypeId_When_CategoriesUsingCategoryType_Expected_OkCorrectEntity() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        final List<CategoryJPA> categories = categoryDataInitializer.createData(1);

        final int id = categories.get(0).getType().getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                controllerPath() + "/categoryType/" + id
        );

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(categories));

    }

    @Test
    public void getCategoriesByCategoryTypeId_When_CategoriesNotUsingCategoryType_Expected_OkEmptyResult() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        categoryDataInitializer.createData(2);
        final CategoryTypeDTO categoryType = new CategoryTypeDTO(new CategoryTypeJPA(
                null, "other", 0, false, true
        ));
        final CategoryType createdCategoryType = categoryTypeService.create(categoryType);
        final int categoryTypeId = createdCategoryType.getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                controllerPath() + "/categoryType/" + categoryTypeId
        );

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");

    }

    @Test
    public void create_WhenCategoryNotExists_Expected_Ok() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(1);
        final CategoryDTO category = new CategoryDTO(new CategoryJPA(
                null, "name", "description", "url", typesData.get(0)
        ));
        performPostWithContentExpectOk(category);

        assertEquals(1, categoryController.getCategories().size());
    }

    @Test
    public void create_WhenCategoryNameExists_Expected_CorrectException() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(1);
        final List<CategoryJPA> categories = categoryDataInitializer.createData(2);

        assertFalse(categoryController.getCategories().isEmpty());

        final String existsName = categories.get(0).getName();
        final CategoryDTO category = new CategoryDTO(new CategoryJPA(
                null, existsName, "description", "url", typesData.get(0)
        ));
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(category);

        performRequestBuilderExpectException(DataIntegrityViolationException.class, requestBuilder);
    }

    @Test
    public void update_WhenCategoryNameExistsInCategoryType_Expected_CorrectException() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        categoryDataInitializer.createData(2);
        final List<Category> categories = categoryController.getCategories();
        assertFalse(categoryController.getCategories().isEmpty());

        final Category firstCategory = categories.get(0);
        firstCategory.setName(categories.get(1).getName());
        firstCategory.setType(categories.get(1).getType());

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(firstCategory);

        performRequestBuilderExpectException(DataIntegrityViolationException.class, requestBuilder);

    }

    @Test
    public void update_WhenCategoryNameExistsButNotInCurrentCategoryType_Expected_Updated() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        categoryDataInitializer.createData(2);
        final List<Category> categories = categoryController.getCategories();
        assertFalse(categoryController.getCategories().isEmpty());

        final Category firstCategory = categories.get(0);
        firstCategory.setName(categories.get(1).getName());

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(firstCategory);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(firstCategory));

    }

    @Test
    public void update_WhenCategoryNameNotExists_Expected_OkAndUpdatedCorrectEntity() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        categoryDataInitializer.createData(2);
        final List<Category> categories = categoryController.getCategories();
        assertFalse(categoryController.getCategories().isEmpty());

        final Category firstCategory = categories.get(0);
        firstCategory.setName("Other name1");

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(firstCategory);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(firstCategory));
    }

    @Test
    public void delete_WhenDocumentsUsingCategory_Expected_CorrectException() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        categoryDataInitializer.createData(2);
        final List<Category> categories = categoryController.getCategories();
        assertFalse(categoryController.getCategories().isEmpty());

        final DocumentDTO doc = documentDataInitializer.createData();
        assertNotNull(doc);

        final Category firstCategory = categories.get(0);
        assertNotNull(firstCategory);

        doc.setCategories(new HashSet<>(Collections.singleton(firstCategory)));
        final DocumentDTO savedDoc = documentService.save(doc);
        assertNotNull(savedDoc);

        final int id = firstCategory.getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                controllerPath() + "/" + id
        );

        performRequestBuilderExpectException(DataUseCategoryException.class, requestBuilder);
    }

    @Test
    public void delete_WhenDocumentsNotUseCategory_Expected_OkAndDeleted() throws Exception {
        assertTrue(categoryController.getCategories().isEmpty());
        categoryDataInitializer.createData(2);
        final List<Category> categories = categoryController.getCategories();
        assertFalse(categoryController.getCategories().isEmpty());

        final int firstCategoryId = categories.get(0).getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                controllerPath() + "/" + firstCategoryId
        );
        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(categories.size() - 1, categoryController.getCategories().size());
    }
}
