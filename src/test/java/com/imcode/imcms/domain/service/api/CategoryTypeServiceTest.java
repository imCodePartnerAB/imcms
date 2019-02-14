package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.CategoryAlreadyExistsException;
import com.imcode.imcms.components.datainitializer.CategoryTypeDataInitializer;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
public class CategoryTypeServiceTest extends WebAppSpringTestConfig {

    private static int COUNT_DATA = 3;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryTypeDataInitializer categoryTypeDataInitializer;

    @BeforeEach
    public void cleanUp() {
        categoryTypeDataInitializer.cleanRepositories();
    }

    @Test
    public void get_When_CategoryTypeExists_Expect_Found() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final List<CategoryTypeDTO> categoryTypesAsDTO = categoryTypeDataInitializer.getCategoryTypesAsDTO();
        final CategoryType firstCategoryType = categoryTypesAsDTO.get(0);
        final Optional<CategoryType> foundType = categoryTypeService.get(firstCategoryType.getId());

        assertTrue(foundType.isPresent());

        assertEquals(foundType.get(), firstCategoryType);
    }

    @Test
    public void getAll_WhenCategoryTypeExists_Expected_CorrectEntities() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        assertNotNull(categoryTypeService.getAll());
        assertEquals(typesData.size(), categoryTypeService.getAll().size());
    }

    @Test
    public void create_When_CategoryTypeNotExists_Expected_Saved() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final CategoryTypeDTO categoryType = new CategoryTypeDTO(new CategoryTypeJPA(
                null, "name", 0, false, false
        ));
        assertNotNull(categoryTypeService.create(categoryType));
        assertEquals(COUNT_DATA + 1, categoryTypeService.getAll().size());
    }

    @Test
    public void create_When_CategoryTypeNameAlreadyExists_Expected_CorrectException() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());

        final CategoryType categoryType = new CategoryTypeJPA(
                typesData.get(0).getName(), 0, false, false
        );
        assertThrows(DataIntegrityViolationException.class, () -> categoryTypeService.create(categoryType));
    }

    @Test
    public void create_When_CategoryTypeNameEmpty_Expected_CorrectException() {
        final CategoryType categoryType = new CategoryTypeJPA(
                null, "", 0, false, false
        );
        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.create(categoryType));
    }

    @Test
    public void update_When_CategoryTypeExists_Expected_UpdateEntity() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final CategoryType firstCategoryType = typesData.get(0);
        firstCategoryType.setName("Other Test Name");

        final CategoryType updated = categoryTypeService.update(firstCategoryType);

        assertNotNull(updated);
        assertEquals(firstCategoryType.getId(), updated.getId());
    }

    @Test
    public void update_When_CategoryTypeNameDuplicated_Expected_CorrectException() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final CategoryType firstCategoryType = typesData.get(0);

        firstCategoryType.setName(typesData.get(1).getName());

        assertEquals(firstCategoryType.getName(), typesData.get(1).getName());
        assertThrows(DataIntegrityViolationException.class, () -> categoryTypeService.update(firstCategoryType));
    }

    @Test
    public void delete_When_CategoryTypeHasNotCategories_Expected_Deleted() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final int firstCategoryTypeId = typesData.get(0).getId();
        Optional<CategoryType> foundType = categoryTypeService.get(firstCategoryTypeId);

        assertTrue(foundType.isPresent());

        categoryTypeService.delete(foundType.get().getId());

        foundType = categoryTypeService.get(firstCategoryTypeId);

        assertFalse(foundType.isPresent());
    }

    @Test
    public void delete_When_CategoriesExists_Expected_CorrectException() {
        final List<CategoryTypeJPA> typesData = categoryTypeDataInitializer.createTypeData(COUNT_DATA);
        assertEquals(3, typesData.size());
        final List<CategoryTypeDTO> categoryTypesAsDTO = categoryTypeDataInitializer.getCategoryTypesAsDTO();
        assertEquals(typesData.size(), categoryTypesAsDTO.size());

        final CategoryTypeDTO firstCategoryType = categoryTypesAsDTO.get(0);
        createCategory(firstCategoryType);
        assertThrows(CategoryAlreadyExistsException.class, () -> categoryTypeService.delete(firstCategoryType.getId()));
    }

    private Category createCategory(CategoryTypeDTO categoryTypeDTO) {
        final CategoryDTO categoryDTO = new CategoryDTO(
                null, "name", "description", "url", categoryTypeDTO
        );
        return categoryService.save(categoryDTO);
    }
}
