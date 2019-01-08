package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import org.junit.Assert;
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

    @Test
    public void invertCaseTest() {
        Assert.assertEquals("aBaBaB000", invertCase("AbAbAb000"));
    }

    @Test
    public void findByNameIgnoreCase_When_CategoriesExist_ExpectedNotNullTest() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final String testTypeName2 = "test_type_name2" + System.currentTimeMillis();
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );

        final CategoryTypeJPA categoryType2 = new CategoryTypeJPA(
                null, testTypeName2, 0, false, false
        );
        categoryTypeRepository.save(categoryType);
        categoryTypeRepository.save(categoryType2);

        final List<CategoryTypeJPA> types = categoryTypeRepository.findAll();

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
    public void createCategoryType_When_CategoryTypeNotExist_Expected_CorrectCategoryType() {
        final String testTypeName = "test_type_name";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        assertNotNull(savedType);
        assertEquals(categoryType, savedType);
    }

    @Test
    public void editCategoryType_When_CategoryTypeExist_Expected_CorrectEditedCategoryType() {
        final String testTypeName = "test_type_name";
        final String testEditTypeName = "edit_type_name";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );

        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        assertNotNull(savedType);

        CategoryTypeJPA getCategoryType = categoryTypeRepository.findOne(savedType.getId());

        getCategoryType.setName(testEditTypeName);

        CategoryTypeJPA editSavedType = categoryTypeRepository.save(getCategoryType);

        assertNotNull(editSavedType);
        assertEquals(savedType.getId(), editSavedType.getId());
        assertNotEquals(testTypeName, editSavedType.getName());
        assertEquals(savedType.getName(), editSavedType.getName());
    }

    @Test
    public void removeCategoryType_When_CategoriesTypeExist_Expected_RemovedCorrectCategoryType() {
        final String testTypeName = "test_type_name";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA saved = categoryTypeRepository.save(categoryType);

        final Integer savedId = saved.getId();
        CategoryType foundCategoryType = categoryTypeRepository.findOne(savedId);

        assertNotNull(foundCategoryType);

        categoryTypeRepository.delete(savedId);

        foundCategoryType = categoryTypeRepository.findOne(savedId);

        assertNull(foundCategoryType);
    }

}
