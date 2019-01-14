package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CategoryTypeRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @BeforeEach
    public void cleanUp() {
        categoryDataInitializer.cleanRepositories();
    }

    @Test
    public void invertCaseTest() {
        Assert.assertEquals("aBaBaB000", invertCase("AbAbAb000"));
    }

    @Test
    public void findByNameIgnoreCase_When_CategoriesExists_ExpectedNotNullTest() {
        categoryDataInitializer.createData(4);
        final List<CategoryTypeJPA> types = categoryDataInitializer.getTypes();

        types.stream()
                .map(CategoryTypeJPA::getName)
                .map(this::invertCase)
                .map(categoryTypeRepository::findByNameIgnoreCase)
                .forEach(Assert::assertNotNull);
    }


    private String invertCase(String str) {
        return str
                .chars()
                .map(charCode -> Character.isLowerCase(charCode)
                        ? Character.toUpperCase(charCode) : Character.toLowerCase(charCode))
                .mapToObj(charCode -> String.valueOf((char) charCode))
                .collect(Collectors.joining());
    }

    @Test
    public void createCategoryType_When_CategoryTypeNotExists_Expected_CorrectCategoryType() {
        categoryDataInitializer.createData(4);
        final String testTypeName = "test_type_name";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        assertNotNull(savedType);
        assertEquals(categoryType, savedType);
    }

    @Test
    public void updateCategoryType_When_CategoryTypeExists_Expected_CorrectEditedCategoryType() {
        categoryDataInitializer.createData(4);
        final List<CategoryTypeJPA> categoriesTypes = categoryDataInitializer.getTypes();
        assertFalse(categoriesTypes.isEmpty());
        final CategoryType firstCategoryType = categoriesTypes.get(0);
        final CategoryTypeJPA categoryType = categoryTypeRepository.findOne(firstCategoryType.getId());

        categoryType.setName("time");

        final CategoryTypeJPA updateCategoryType = categoryTypeRepository.save(categoryType);

        assertNotNull(updateCategoryType);
        assertEquals(categoryType.getId(), updateCategoryType.getId());
    }

    @Test
    public void removeCategoryType_When_CategoriesTypeExists_Expected_RemovedCorrectCategoryType() {
        categoryDataInitializer.createData(4);
        final List<CategoryTypeJPA> categoriesTypes = categoryDataInitializer.getTypes();
        assertFalse(categoriesTypes.isEmpty());

        final CategoryType firstCategoryType = categoriesTypes.get(0);
        final Integer id = firstCategoryType.getId();
        CategoryType foundCategoryType = categoryTypeRepository.findOne(id);

        assertNotNull(foundCategoryType);

        categoryTypeRepository.delete(id);

        foundCategoryType = categoryTypeRepository.findOne(firstCategoryType.getId());

        assertNull(foundCategoryType);
    }
}
