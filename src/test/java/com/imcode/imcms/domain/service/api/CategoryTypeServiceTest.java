package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
public class CategoryTypeServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    public void setUpCategoryDataInitializer() {
        categoryDataInitializer.createData(4);
    }

    @AfterEach
    public void clearData() {
        categoryDataInitializer.cleanRepositories();
    }

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
    public void getAllExpectedEqualsCategoryTypesAsDtoTest() {
        assertEquals(categoryDataInitializer.getCategoryTypesAsDTO(), categoryTypeService.getAll());
    }

    @Test
    public void save_When_NotExitBefore_Expect_Saved() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.save(categoryType);

        final Optional<CategoryType> oFound = categoryTypeService.get(saved.getId());

        assertTrue(oFound.isPresent());

        final CategoryType found = oFound.get();

        assertEquals(found, saved);
    }

    @Test
    public void update_When_Exist_Expect_CorrectUpdateEntity() {
        final String testTypeName = "test_type_name";
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.save(categoryType);

        final Optional<CategoryType> oFound = categoryTypeService.get(saved.getId());

        assertTrue(oFound.isPresent());

        CategoryType categoryType1 = oFound.get();

        categoryType1.setName("Other Test Name");

        final CategoryType updated = categoryTypeService.save(categoryType1);

        assertEquals(saved.getId(), updated.getId());
        assertNotNull(updated);
        assertNotEquals(oFound, categoryType1);
    }


    @Test
    public void delete_When_Exist_Expect_Deleted() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryType saved = categoryTypeService.save(categoryType);

        final Integer savedId = saved.getId();
        Optional<CategoryType> oFound = categoryTypeService.get(savedId);

        assertTrue(oFound.isPresent());

        List<Category> categoryDTOS = categoryService.getAll();

        categoryDTOS.clear();

        assertTrue(categoryDTOS.isEmpty());

        categoryTypeService.delete(savedId);

        oFound = categoryTypeService.get(savedId);

        assertFalse(oFound.isPresent());
    }
}
