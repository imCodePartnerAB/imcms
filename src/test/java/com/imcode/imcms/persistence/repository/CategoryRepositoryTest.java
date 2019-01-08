package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class CategoryRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Autowired
    private MetaRepository metaRepository;

    @Test
    public void createCategory_When_CategoryNotExist_Expected_CorrectCategory() {
        final String testTypeName = "test_type_name";
        final String testDescription = "Some description";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );

        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final CategoryJPA category = new CategoryJPA(
                null, testTypeName, testDescription, testImageUrl, savedType
        );
        CategoryJPA savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory);
        assertEquals(category, savedCategory);
    }

    @Test
    public void editCategory_When_CategoryExist_Expected_CorrectEditedCategory() {
        final String testNameCategory = "test Name";
        final String testEditNameCategory = "edit Name";
        final String testTypeName = "test_type_name";
        final String testDescription = "Some description";
        final String testEditDescription = "Some description..";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );

        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final CategoryJPA category = new CategoryJPA(
                null, testNameCategory, testDescription, testImageUrl, savedType
        );
        CategoryJPA savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory);

        savedCategory.setName(testEditNameCategory);
        savedCategory.setDescription(testEditDescription);

        CategoryJPA editCategory = categoryRepository.save(savedCategory);

        assertNotNull(editCategory);

        assertNotEquals(testNameCategory, editCategory.getName());
        assertEquals(savedCategory, editCategory);
    }

    @Test
    public void findByType_Expected_CorrectNameTest() {
        final String testTypeName = "test_type_name";
        final String testNameCategory = "test Name";
        final String testDescription = "Some description";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final CategoryJPA category = new CategoryJPA(
                null, testNameCategory, testDescription, testImageUrl, savedType
        );

        CategoryJPA savedCategory = categoryRepository.save(category);

        final List<CategoryJPA> categories = categoryRepository.findByType(savedCategory.getType());

        assertEquals(1, categories.size());
        assertEquals(testNameCategory, categories.get(0).getName());
    }

    @Test
    public void findByNameAndType_Expected_ExistCategoryWithCorrectNameAndCategoryTypeNameTest() {
        final String testTypeName = "test_type_name";
        final String testNameCategory = "test Name";
        final String testDescription = "Some description";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final CategoryJPA category = new CategoryJPA(
                null, testNameCategory, testDescription, testImageUrl, savedType
        );

        final CategoryJPA savedCategory = categoryRepository.save(category);

        final CategoryJPA foundCategory = categoryRepository.findByNameAndType(
                savedCategory.getName(), savedCategory.getType()
        );

        assertNotNull(foundCategory);
        assertEquals(category.getName(), foundCategory.getName());
        assertEquals(category.getType().getName(), foundCategory.getType().getName());
    }

    @Test
    public void findByNameAndType_When_CategoryNameNotMatch_Expected_NullCategoryTest() {
        final String testTypeName = "test_type_name";
        final String testNameCategory = "test Name";
        final String testDescription = "Some description";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final CategoryJPA category = new CategoryJPA(
                null, testNameCategory, testDescription, testImageUrl, savedType
        );

        final CategoryJPA savedCategory = categoryRepository.save(category);

        final String fakeCategoryName = "fake";
        final CategoryJPA foundCategory = categoryRepository.findByNameAndType(fakeCategoryName, savedCategory.getType());

        assertNull(foundCategory);
    }

    @Test
    public void findCategoryDocIds_Expected_CorrectCategoryDocId() {
        final String testTypeName = "test_type_name";
        final String testNameCategory = "test Name";
        final String testDescription = "Some description";
        final String testImageUrl = "/test";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final CategoryJPA category = new CategoryJPA(
                null, testNameCategory, testDescription, testImageUrl, savedType
        );

        final CategoryJPA savedCategory = categoryRepository.save(category);
        final List<Meta> allMetas = metaRepository.findAll();

        assertFalse(allMetas.isEmpty()); // at least one doc with id=1001 should exist

        final Meta firstDoc = allMetas.get(0);
        final Integer docId = firstDoc.getId();
        final Integer categoryId = savedCategory.getId();

        firstDoc.setCategories(new HashSet<>(Collections.singleton(savedCategory)));
        metaRepository.save(firstDoc);

        final List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(categoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.isEmpty());
        assertTrue(categoryDocIds.contains(docId));
    }

    @Test
    public void deleteDocumentCategory() {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final String testCategoryName = "test_category_name" + System.currentTimeMillis();
        final CategoryJPA category = new CategoryJPA(testCategoryName, "dummy", "", savedType);
        final Category saved = categoryRepository.save(category);
        final List<Meta> allMetas = metaRepository.findAll();

        assertFalse(allMetas.isEmpty()); // at least one doc with id=1001 should exist

        final Meta firstDoc = allMetas.get(0);
        final Integer docId = firstDoc.getId();
        final Set<Category> docCategories = firstDoc.getCategories();
        final Integer docCategoryId = saved.getId();

        assertFalse(docCategories.contains(saved));

        docCategories.add(saved);
        firstDoc.setCategories(docCategories);

        metaRepository.save(firstDoc);

        List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(docCategoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.isEmpty());
        assertTrue(categoryDocIds.contains(docId));

        categoryRepository.deleteDocumentCategory(docCategoryId);

        categoryDocIds = categoryRepository.findCategoryDocIds(docCategoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.contains(docId));
    }

    @Test
    public void deleteByDocIdAndCategoryId() {
        final String testTypeName = "test_type_name";
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = categoryTypeRepository.save(categoryType);

        final String testCategoryName = "test_category_name" + System.currentTimeMillis();
        final CategoryJPA category = new CategoryJPA(testCategoryName, "dummy", "", savedType);
        final Category savedCategory = categoryRepository.save(category);
        final List<Meta> allMetas = metaRepository.findAll();
        final List<CategoryJPA> categories = categoryRepository.findAll();

        assertFalse(categories.isEmpty());
        assertFalse(allMetas.isEmpty());

        final Meta firstDoc = allMetas.get(0);
        final Integer docId = firstDoc.getId();
        final Integer docCategoryId = savedCategory.getId();

        metaRepository.save(firstDoc);

        categoryRepository.deleteByDocIdAndCategoryId(docId, docCategoryId);

        List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(docCategoryId);

        assertNotNull(categoryDocIds);
        assertTrue(categoryDocIds.isEmpty());
    }
}
