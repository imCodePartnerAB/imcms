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
    public void setUp() {
        categoryDataInitializer.createData(2);
    }

    @Test
    public void findByTypeExpectedCorrectNameTest() {
        final List<CategoryTypeJPA> types = categoryDataInitializer.getTypes();
        final List<CategoryJPA> categories = categoryRepository.findByType(types.get(0));

        assertEquals(1, categories.size());
        assertEquals("Category0Name", categories.get(0).getName());
    }

    @Test
    public void findByNameAndTypeExpectedExistCategoryWithCorrectNameAndCategoryTypeNameTest() {
        final List<CategoryTypeJPA> types = categoryDataInitializer.getTypes();

        final CategoryJPA category = categoryRepository.findByNameAndType("Category0Name", types.get(0));

        assertNotNull(category);
        assertEquals("Category0Name", category.getName());
        assertEquals("CategoryType0Name", category.getType().getName());
    }

    @Test
    public void findByNameAndTypeExpectedNullCategoryTest() {
        final List<CategoryTypeJPA> types = categoryDataInitializer.getTypes();

        final CategoryJPA category = categoryRepository.findByNameAndType("Category0Name", types.get(1));

        assertNull(category);
    }

    @Test
    public void findCategoryDocIds() {
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
        final Integer categoryId = saved.getId();

        firstDoc.setCategories(new HashSet<>(Collections.singleton(saved)));
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

}
