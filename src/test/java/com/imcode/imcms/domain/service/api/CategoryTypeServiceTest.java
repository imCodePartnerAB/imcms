package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public void get_When_CategoryTypeExist_Expect_Found() {
        CategoryTypeDTO categoryTypeDTO = categoryTypeInitializer();

        final Optional<CategoryType> oFound = categoryTypeService.get(categoryTypeDTO.getId());

        assertTrue(oFound.isPresent());

        final CategoryType found = oFound.get();

        assertEquals(found, categoryTypeDTO);
    }

    @Test
    public void getAll_Expected_CorrectEntities() {
        deleteInitData(categoryTypeRepository.findAll());
        assertTrue(categoryTypeRepository.findAll().isEmpty());
        List<CategoryType> categoryTypes = categoriesTypesInit(2);

        assertEquals(categoryTypes.size(), categoryTypeService.getAll().size());
    }

    @Test
    public void create_When_NotExitBefore_Expect_Saved() {
        CategoryTypeDTO categoryTypeDTO = categoryTypeInitializer();
        assertTrue(categoryTypeService.getAll().contains(categoryTypeDTO));
    }

    @Test
    public void create_When_CategoryTypeNameExist_Expect_CorrectException() {
        CategoryTypeDTO categoryTypeDTO = categoryTypeInitializer();
        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.create(categoryTypeDTO));
    }

    @Test
    public void create_When_CategoryTypeNameEmpty_Expect_CorrectException() {
        final CategoryType categoryType = new CategoryTypeJPA(
                null, "", 0, false, false
        );
        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.create(categoryType));
    }

    @Test
    public void update_When_CategoryTypeExist_Expect_UpdateEntity() {
        CategoryTypeDTO categoryTypeDTO = categoryTypeInitializer();
        categoryTypeDTO.setName("Other Test Name");

        final CategoryType updated = categoryTypeService.update(categoryTypeDTO);

        assertNotNull(updated);
        assertEquals(categoryTypeDTO.getId(), updated.getId());
    }

    @Test
    public void update_When_CategoryTypeNameSame_Expect_CorrectException() {
        final CategoryTypeDTO categoryTypeDTO1 = categoryTypeInitializer();
        final CategoryTypeDTO categoryTypeDTO2 = categoryTypeInitializer();

        assertNotEquals(categoryTypeDTO1, categoryTypeDTO2);

        categoryTypeDTO1.setName(categoryTypeDTO2.getName());

        assertEquals(categoryTypeDTO1.getName(), categoryTypeDTO2.getName());
        assertThrows(IllegalArgumentException.class, () -> categoryTypeService.update(categoryTypeDTO1));
    }

    @Test
    public void delete_When_CategoryTypeHasNotCategories_Expect_Deleted() {
        final CategoryTypeDTO categoryTypeDTO = categoryTypeInitializer();
        initCategory(categoryTypeDTO);
        final Integer savedId = categoryTypeDTO.getId();
        Optional<CategoryType> oFound = categoryTypeService.get(savedId);

        assertTrue(oFound.isPresent());
        assertFalse(categoryService.getAll().isEmpty());

        deleteCategories(categoryService.getAll());

        assertTrue(categoryService.getAll().isEmpty());

        categoryTypeService.delete(oFound.get().getId());

        oFound = categoryTypeService.get(savedId);

        assertFalse(oFound.isPresent());
    }

    @Test
    public void delete_When_CategoryTypeNotBelongCategories_Expect_Deleted() {
        final CategoryTypeDTO categoryTypeDTO = categoryTypeInitializer();
        final CategoryTypeDTO categoryTypeDTO2 = categoryTypeInitializer();
        initCategory(categoryTypeDTO2);
        final Integer savedId = categoryTypeDTO.getId();
        Optional<CategoryType> oFound = categoryTypeService.get(savedId);

        assertTrue(oFound.isPresent());
        assertFalse(categoryService.getAll().isEmpty());

        categoryTypeService.delete(oFound.get().getId());

        oFound = categoryTypeService.get(savedId);

        assertFalse(oFound.isPresent());
    }

    @Test
    public void delete_When_CategoriesExist_Expect_CorrectException() {
        final CategoryTypeDTO categoryTypeDTO = categoryTypeInitializer();
        initCategory(categoryTypeDTO);
        assertThrows(EmptyResultDataAccessException.class, () -> categoryTypeService.delete(categoryTypeDTO.getId()));
    }

    private CategoryTypeDTO categoryTypeInitializer() {
        String name = "test_type_name" + System.currentTimeMillis();
        int maxChoices = 0;
        boolean inherited = false;
        boolean imageArchive = false;
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, name, maxChoices, inherited, imageArchive
        );

        return new CategoryTypeDTO(categoryTypeService.create(categoryType));
    }

    private List<CategoryType> categoriesTypesInit(int countElements) {
        return IntStream.range(0, countElements)
                .mapToObj(i -> new CategoryTypeJPA("CategoryType" + i + "Name", 0, false, false))
                .map(categoryTypeRepository::saveAndFlush)
                .collect(Collectors.toList());
    }

    private void deleteInitData(List<CategoryTypeJPA> categoryTypes) {
        List<Integer> ids = categoryTypes.stream().map(CategoryType::getId).collect(Collectors.toList());
        for (Integer id : ids) {
            categoryTypeService.delete(id);
        }
    }

    private void deleteCategories(List<Category> categories) {
        List<Integer> ids = categories.stream().map(Category::getId).collect(Collectors.toList());
        for (Integer id : ids) {
            categoryService.delete(id);
        }
    }

    private Category initCategory(CategoryTypeDTO categoryTypeDTO) {
        final CategoryDTO categoryDTO = new CategoryDTO(
                null, "name", "description", "url", categoryTypeDTO
        );
        return categoryService.save(categoryDTO);
    }
}
