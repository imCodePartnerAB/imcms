package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @see CategoryService
 */
@Transactional
public class CategoryServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Test
    public void getAll_Expected_categoryServiceContainSavedCategory() {
        final String testTypeName = "test_type_name";
        final String testDescription = "Some description";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );

        final CategoryTypeJPA savedCategoryType = new CategoryTypeJPA(categoryTypeService.create(categoryType));

        final CategoryJPA category = new CategoryJPA(
                null, testTypeName, testDescription, testImageUrl, savedCategoryType
        );
        Category savedCategory = categoryService.save(category);

        assertNotNull(savedCategory);
        assertFalse(categoryService.getAll().isEmpty());
        assertTrue(categoryService.getAll().contains(savedCategory));
    }

    @Test
    public void getById_When_Exist_Expect_NotNull() {
        final String testTypeName = "test_type_name";
        final String testDescription = "Some description";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );

        final CategoryTypeJPA savedCategoryType = new CategoryTypeJPA(categoryTypeService.create(categoryType));

        final CategoryJPA category = new CategoryJPA(
                null, testTypeName, testDescription, testImageUrl, savedCategoryType
        );
        Category savedCategory = categoryService.save(category);

        assertNotNull(savedCategory);

        final Integer id = savedCategory.getId();
        assertTrue(categoryService.getById(id).isPresent());
    }

    @Test
    public void save_When_NotExistBefore_Expect_Saved() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = new CategoryTypeJPA(categoryTypeService.create(categoryType));

        final String testCategoryName = "test_category_name" + System.currentTimeMillis();
        final Category category = new CategoryJPA(testCategoryName, "dummy", "", savedType);
        final Category savedCategory = categoryService.save(category);

        final Optional<Category> oFound = categoryService.getById(savedCategory.getId());

        assertTrue(oFound.isPresent());

        final Category foundCategory = oFound.get();

        assertNotNull(foundCategory);
        assertEquals(savedCategory, foundCategory);
    }

    @Test
    public void update_When_CategoryExist_Expected_UpdatedCorrectEntity() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = new CategoryTypeJPA(categoryTypeService.create(categoryType));

        final String testCategoryName = "test_category_name" + System.currentTimeMillis();
        final Category category = new CategoryJPA(testCategoryName, "dummy", "", savedType);
        final Category savedCategory = categoryService.save(category);

        final Optional<Category> oFound = categoryService.getById(savedCategory.getId());

        assertTrue(oFound.isPresent());

        final String otherName = "Other_category_name";
        final Category getCategory = oFound.get();
        getCategory.setName(otherName);

        final Category savedUpdateCategory = categoryService.update(getCategory);

        assertEquals(getCategory.getId(), savedCategory.getId());
        assertNotNull(savedUpdateCategory);
        assertNotEquals(oFound, savedUpdateCategory);

    }

    @Test
    public void update_When_CategoryNameExist_Expected_CorrectException() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = new CategoryTypeJPA(categoryTypeService.create(categoryType));

        final String testCategoryName1 = "test_category_name" + System.currentTimeMillis();
        final String testCategoryName2 = "test_category_name2" + System.currentTimeMillis();
        final Category category1 = new CategoryJPA(testCategoryName1, "dummy", "", savedType);
        final Category category2 = new CategoryJPA(testCategoryName2, "ho-ho-ho", "../", savedType);
        final Category savedCategory1 = categoryService.save(category1);

        categoryService.save(category2);

        final Optional<Category> oFound = categoryService.getById(savedCategory1.getId());

        assertTrue(oFound.isPresent());


        final Category getCategory = oFound.get();
        getCategory.setName(testCategoryName2);

        assertThrows(IllegalArgumentException.class, () -> categoryService.update(getCategory));
    }

    @Test
    public void update_When_CategoryNameIsEmpty_Expected_CorrectException() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = new CategoryTypeJPA(categoryTypeService.create(categoryType));

        final String testCategoryName1 = "test_category_name" + System.currentTimeMillis();
        final Category category1 = new CategoryJPA(testCategoryName1, "dummy", "", savedType);
        final Category savedCategory1 = categoryService.save(category1);

        final Optional<Category> oFound = categoryService.getById(savedCategory1.getId());

        assertTrue(oFound.isPresent());

        final Category getCategory = oFound.get();
        getCategory.setName("");

        assertThrows(IllegalArgumentException.class, () -> categoryService.update(getCategory));
    }

    @Test
    public void delete_When_Exist_Expect_Deleted() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = new CategoryTypeJPA(categoryTypeService.create(categoryType));

        final String testCategoryName = "test_category_name" + System.currentTimeMillis();
        final Category category = new CategoryJPA(testCategoryName, "dummy", "", savedType);
        final Category saved = categoryService.save(category);
        final Integer savedCategoryId = saved.getId();

        assertTrue(categoryService.getById(savedCategoryId).isPresent());

        categoryService.delete(savedCategoryId);

        assertFalse(categoryService.getById(savedCategoryId).isPresent());
    }
}
