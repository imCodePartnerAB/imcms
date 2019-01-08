package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
public class CategoryTypeServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Autowired
    private CategoryService categoryService;

    @Test
    public void get_When_Exist_Expect_Found() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = new CategoryTypeDTO(categoryTypeRepository.save(categoryType));

        final Optional<CategoryType> oFound = categoryTypeService.get(saved.getId());

        assertTrue(oFound.isPresent());

        final CategoryType found = oFound.get();

        assertEquals(found, saved);
    }

    @Test
    public void getAll_Expected_ContainSavedCategoryType() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.create(categoryType);
        assertTrue(categoryTypeService.getAll().contains(saved));
    }

    @Test
    public void create_When_NotExitBefore_Expect_Saved() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.create(categoryType);

        final Optional<CategoryType> oFound = categoryTypeService.get(saved.getId());

        assertTrue(oFound.isPresent());

        final CategoryType found = oFound.get();

        assertEquals(found, saved);
    }

    @Test
    public void create_When_CategoryNameExist_Expect_CorrectException() {
        final String testTypeName = "test_type_name";
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType categoryType2 = new CategoryTypeJPA(
                null, testTypeName, 0, true, false
        );

        categoryTypeService.create(categoryType);

        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.create(categoryType2));
    }

    @Test
    public void create_When_CategoryNameEmpty_Expect_CorrectException() {
        final CategoryType categoryType = new CategoryTypeJPA(
                null, "", 0, false, false
        );
        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.create(categoryType));
    }

    @Test
    public void update_When_Exist_Expect_CorrectUpdateEntity() {
        final String testTypeName = "test_type_name";
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.create(categoryType);

        final Optional<CategoryType> oFound = categoryTypeService.get(saved.getId());

        assertTrue(oFound.isPresent());

        CategoryType getCategoryType = oFound.get();

        getCategoryType.setName("Other Test Name");

        final CategoryType updated = categoryTypeService.update(getCategoryType);

        assertEquals(saved.getId(), updated.getId());
        assertNotNull(updated);
        assertNotEquals(oFound, updated);
    }

    @Test
    public void update_When_CategoryTypeNameExist_Expect_CorrectException() {
        final String testTypeName = "test_type_name";
        final String testTypeName2 = "test_type_second";
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType categoryType2 = new CategoryTypeJPA(
                null, testTypeName2, 0, true, false
        );
        final CategoryType saved1 = categoryTypeService.create(categoryType);
        final CategoryType saved2 = categoryTypeService.create(categoryType2);

        final Optional<CategoryType> firstFound = categoryTypeService.get(saved1.getId());
        final Optional<CategoryType> secondFound = categoryTypeService.get(saved2.getId());

        assertTrue(firstFound.isPresent());
        assertTrue(secondFound.isPresent());

        CategoryType getCategoryType1 = firstFound.get();
        CategoryType getCategoryType2 = secondFound.get();

        getCategoryType2.setName(getCategoryType1.getName());

        assertNotEquals(firstFound, secondFound);

        assertEquals(getCategoryType1.getName(), getCategoryType2.getName());

        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.update(getCategoryType2));
    }

    @Test
    public void delete_When_CategoriesAllDeleted_Expect_Deleted() {
        final String testTypeName = "test_type_name";
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.create(categoryType);

        final Integer savedId = saved.getId();
        Optional<CategoryType> oFound = categoryTypeService.get(savedId);

        assertTrue(oFound.isPresent());

        List<Category> categories = categoryService.getAll();

        CategoryType getCategoryType = oFound.get();

        categories.removeIf(
                categoryJPA -> categoryJPA.getType().equals(getCategoryType)
        );

        categoryTypeService.delete(oFound.get().getId());

        oFound = categoryTypeService.get(savedId);

        assertFalse(oFound.isPresent());
    }

    @Test
    public void delete_When_CategoriesExist_Expect_CorrectException() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.create(categoryType);

        CategoryTypeJPA categoryTypeJPA = new CategoryTypeJPA();
        categoryTypeJPA.setId(saved.getId());
        categoryTypeJPA.setName(saved.getName());
        categoryTypeJPA.setMultiSelect(saved.isMultiSelect());
        categoryTypeJPA.setInherited(saved.isInherited());
        categoryTypeJPA.setImageArchive(saved.isImageArchive());

        Category category = new CategoryJPA(
                "some name", "some desc", "", categoryTypeJPA
        );

        assertNotNull(category);

        categoryService.save(category);

        final Integer savedCategoryTypeId = categoryTypeJPA.getId();
        CategoryType oFound = categoryTypeRepository.findOne(savedCategoryTypeId);

        assertNotNull(oFound);

        assertEquals(category.getType(), oFound);

        assertThrows(EmptyResultDataAccessException.class, () -> categoryTypeService.delete(oFound.getId()));
    }
}
