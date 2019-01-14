package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.CategoryAlreadyExistsException;
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
import org.springframework.dao.DataIntegrityViolationException;
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
    public void get_When_CategoryTypeExists_Expect_Found() {
        CategoryTypeDTO categoryTypeDTO = createCategoryType();

        final Optional<CategoryType> foundType = categoryTypeService.get(categoryTypeDTO.getId());

        assertTrue(foundType.isPresent());

        assertEquals(foundType.get(), categoryTypeDTO);
    }

    @Test
    public void getAll_WhenCategoryTypeExists_Expected_CorrectEntities() {
        categoryTypeRepository.deleteAll();
        assertTrue(categoryTypeRepository.findAll().isEmpty());
        final List<CategoryType> categoryTypes = categoriesTypesInit(2);

        assertEquals(categoryTypes.size(), categoryTypeService.getAll().size());
        assertNotNull(categoryTypeService.getAll());
    }

    @Test
    public void create_When_CategoryTypeNotExists_Expected_Saved() {
        CategoryTypeDTO categoryTypeDTO = createCategoryType();
        assertNotNull(categoryTypeDTO);
        assertTrue(categoryTypeService.getAll().contains(categoryTypeDTO));
    }

    @Test
    public void create_When_CategoryTypeNameAlreadyExists_Expected_CorrectException() {
        CategoryTypeDTO categoryTypeDTO = createCategoryType();
        assertNotNull(categoryTypeDTO);
        final CategoryType categoryType = new CategoryTypeJPA(
                categoryTypeDTO.getName(), 0, false, false
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
        CategoryTypeDTO categoryTypeDTO = createCategoryType();
        categoryTypeDTO.setName("Other Test Name");

        final CategoryType updated = categoryTypeService.update(categoryTypeDTO);

        assertNotNull(updated);
        assertEquals(categoryTypeDTO.getId(), updated.getId());
    }

    @Test
    public void update_When_CategoryTypeNameDuplicated_Expected_CorrectException() {
        final CategoryTypeDTO categoryTypeDTO1 = createCategoryType();
        final CategoryTypeDTO categoryTypeDTO2 = createCategoryType();

        assertNotEquals(categoryTypeDTO1, categoryTypeDTO2);

        categoryTypeDTO1.setName(categoryTypeDTO2.getName());

        assertEquals(categoryTypeDTO1.getName(), categoryTypeDTO2.getName());
        assertThrows(DataIntegrityViolationException.class, () -> categoryTypeService.update(categoryTypeDTO1));
    }

    @Test
    public void delete_When_CategoryTypeHasNotCategories_Expected_Deleted() {
        final CategoryTypeDTO categoryTypeDTO = createCategoryType();
        createCategory(categoryTypeDTO);
        final Integer savedId = categoryTypeDTO.getId();
        Optional<CategoryType> foundType = categoryTypeService.get(savedId);

        assertTrue(foundType.isPresent());
        assertFalse(categoryService.getAll().isEmpty());

        deleteCategories(categoryService.getAll());

        assertTrue(categoryService.getAll().isEmpty());

        categoryTypeService.delete(foundType.get().getId());

        foundType = categoryTypeService.get(savedId);

        assertFalse(foundType.isPresent());
    }

    @Test
    public void delete_When_CategoriesExists_Expected_CorrectException() {
        final CategoryTypeDTO categoryTypeDTO = createCategoryType();
        createCategory(categoryTypeDTO);
        assertThrows(CategoryAlreadyExistsException.class, () -> categoryTypeService.delete(categoryTypeDTO.getId()));
    }

    private CategoryTypeDTO createCategoryType() {
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

    private void deleteCategories(List<Category> categories) {
        List<Integer> ids = categories.stream().map(Category::getId).collect(Collectors.toList());
        for (Integer id : ids) {
            categoryService.delete(id);
        }
    }

    private Category createCategory(CategoryTypeDTO categoryTypeDTO) {
        final CategoryDTO categoryDTO = new CategoryDTO(
                null, "name", "description", "url", categoryTypeDTO
        );
        return categoryService.save(categoryDTO);
    }
}
