package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @BeforeEach
    public void cleanUp() {
        categoryDataInitializer.cleanRepositories();
    }

    @Test
    public void createCategory_When_CategoryNotExist_Expected_Created() {
        categoryDataInitializer.createData(4);
        final List<CategoryTypeJPA> categories = categoryDataInitializer.getTypes();
        final CategoryTypeJPA firstCategoryType = new CategoryTypeJPA(categories.get(0));
        final CategoryJPA category = new CategoryJPA(null, "name", "dummy", firstCategoryType);
        CategoryJPA savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory);
        assertEquals(category, savedCategory);
    }

    @Test
    public void updateCategory_When_CategoryExist_Expected_UpdatedEntity() {
        categoryDataInitializer.createData(4);
        final List<CategoryJPA> categories = categoryRepository.findAll();
        final CategoryJPA firstCategoryDTO = new CategoryJPA(categories.get(0));

        assertNotNull(firstCategoryDTO);

        final String otherName = "Other_category_name";
        firstCategoryDTO.setName(otherName);

        CategoryJPA updateCategory = categoryRepository.save(firstCategoryDTO);

        assertNotNull(updateCategory);
	    assertEquals(updateCategory, categoryRepository.getOne(updateCategory.getId()));
    }

    @Test
    public void findByType_When_CategoryExist_Expected_FoundCorrectEntity() {
        categoryDataInitializer.createData(4);
        final List<CategoryJPA> categories = categoryRepository.findAll();
        final CategoryJPA firstCategoryDTO = new CategoryJPA(categories.get(0));

        assertNotNull(firstCategoryDTO);

        final List<CategoryJPA> foundCategories = categoryRepository.findByTypeId(firstCategoryDTO.getType().getId());

        assertEquals(1, foundCategories.size());
        assertEquals(firstCategoryDTO.getName(), foundCategories.get(0).getName());
    }

    @Test
    public void findByNameAndType_When_CategoryExist_Expected_CorrectEntity() {
        categoryDataInitializer.createData(4);
        final List<CategoryJPA> categories = categoryRepository.findAll();
        final CategoryJPA firstCategory = new CategoryJPA(categories.get(0));

        assertNotNull(firstCategory);

        final CategoryJPA foundCategory = categoryRepository.findByNameAndType(
                firstCategory.getName(), firstCategory.getType()
        );

        assertNotNull(foundCategory);
        assertEquals(firstCategory.getName(), foundCategory.getName());
        assertEquals(firstCategory.getType().getName(), foundCategory.getType().getName());
    }

    @Test
    public void findByCategoryTypeId_When_CategoriesUsingCategoryType_Expected_CorrectEntity() {
        final List<CategoryJPA> categories = categoryDataInitializer.createData(1);
        assertEquals(categories, categoryRepository.findByTypeId(categories.get(0).getType().getId()));
    }

    @Test
    public void findByCategoryTypeId_When_CategoriesNotUsingCategoryType_Expected_EmptyResult() {
        final List<CategoryJPA> categories = categoryDataInitializer.createData(2);
        final CategoryTypeJPA categoryType = new CategoryTypeJPA(
                null, "name1", false, true, true
        );
        final CategoryTypeJPA saved = categoryTypeRepository.save(categoryType);
        assertNotEquals(categories, categoryRepository.findByTypeId(saved.getId()));
        assertTrue(categoryRepository.findByTypeId(saved.getId()).isEmpty());

    }

    @Test
    public void findByNameAndType_When_CategoryNameNotExist_Expected_NullCategory() {
        categoryDataInitializer.createData(4);
        final List<CategoryJPA> categories = categoryRepository.findAll();
        final CategoryJPA firstCategory = new CategoryJPA(categories.get(0));

        assertNotNull(firstCategory);

        final String fakeCategoryName = "fake";
        final CategoryJPA foundCategory = categoryRepository.findByNameAndType(fakeCategoryName, firstCategory.getType());

        assertNull(foundCategory);
    }

    @Test
    public void findCategoryDocIds_When_CategoryHasDocs_Expected_CorrectCategoryDocId() { //todo need add init document?
        categoryDataInitializer.createData(4);
        final List<CategoryJPA> categories = categoryRepository.findAll();
        final CategoryJPA firstCategory = new CategoryJPA(categories.get(0));

        assertNotNull(firstCategory);

        final List<Meta> allMetas = metaRepository.findAll();

        assertFalse(allMetas.isEmpty()); // at least one doc with id=1001 should exist

        final Meta firstDoc = allMetas.get(0);
        final Integer docId = firstDoc.getId();
        final Integer categoryId = firstCategory.getId();

        firstDoc.setCategories(new HashSet<>(Collections.singleton(firstCategory)));
        metaRepository.save(firstDoc);

        final List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(categoryId);

        assertNotNull(categoryDocIds);
        assertFalse(categoryDocIds.isEmpty());
        assertTrue(categoryDocIds.contains(docId));
    }

    @Test
    public void deleteDocumentCategory_When_DocumenthasCategory_Expected_Deleted() {
        categoryDataInitializer.createData(4);
        final List<CategoryJPA> categories = categoryRepository.findAll();
        final CategoryJPA firstCategory = new CategoryJPA(categories.get(0));

        assertNotNull(firstCategory);

        final List<Meta> allMetas = metaRepository.findAll();

        assertFalse(allMetas.isEmpty()); // at least one doc with id=1001 should exist

        final Meta firstDoc = allMetas.get(0);
        final Integer docId = firstDoc.getId();
        final Set<Category> docCategories = firstDoc.getCategories();
        final Integer docCategoryId = firstCategory.getId();

        assertFalse(docCategories.contains(firstCategory));

        docCategories.add(firstCategory);
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
    public void deleteByDocIdAndCategoryId_When_DocumenthasCategory_Expected_DeletedCorrectEntity() {
        categoryDataInitializer.createData(4);
        final List<CategoryJPA> categories = categoryRepository.findAll();
        final CategoryJPA firstCategory = new CategoryJPA(categories.get(0));

        assertNotNull(firstCategory);

        final List<Meta> allMetas = metaRepository.findAll();

        assertFalse(allMetas.isEmpty());

        final Meta firstDoc = allMetas.get(0);
        final Integer docId = firstDoc.getId();
        final Integer docCategoryId = firstCategory.getId();

        metaRepository.save(firstDoc);

        categoryRepository.deleteByDocIdAndCategoryId(docId, docCategoryId);

        List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(docCategoryId);

        assertNotNull(categoryDocIds);
        assertTrue(categoryDocIds.isEmpty());
    }
}
