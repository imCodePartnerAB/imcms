package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.test.TestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Couldn't use <code>Transactional</code> annotation
 * because we work with bidirectional Category-CategoryType relation.
 * We need manually clean up results.
 *
 * @see org.springframework.transaction.annotation.Transactional
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CategoryTypeRepositoryTest {

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private List<CategoryType> types;
    private List<Category> categories;

    @Before
    public void createCategoriesAndTypes() {
        deleteCategoriesAndTypes();
        types = recreateTypes();
        categories = recreateCategories();
    }

    /**
     * Clean up
     */
    @After
    public void deleteCategoriesAndTypes() {
        categoryRepository.deleteAll();
        categoryRepository.flush();
        categoryTypeRepository.deleteAll();
        categoryTypeRepository.flush();
    }

    @Test
    public void testFindByNameIgnoreCase() throws Exception {
        CategoryType type1 = categoryTypeRepository.findByNameIgnoreCase("dOCcATEGORYtYPEoNE");
        CategoryType type2 = categoryTypeRepository.findByNameIgnoreCase("dOCcATEGORYtYPEtWO");

        assertNotNull(type1);
        assertNotNull(type2);
    }

    @Test
    public void findAllFetchCategoriesEagerly() {
        final List<CategoryType> categoryTypesWithCategories = categoryTypeRepository.findAllFetchCategoriesEagerly();

        assertEquals(4, categoryTypesWithCategories.size());

        final List<Category> categoriesGroup1 = categoryTypesWithCategories.get(0).getCategories();
        final List<Category> categoriesGroup2 = categoryTypesWithCategories.get(1).getCategories();

        assertEquals(2, categoriesGroup1.size());
        assertEquals(2, categoriesGroup2.size());

        assertTrue(categories.contains(categoriesGroup1.get(0)));
        assertTrue(categories.contains(categoriesGroup1.get(1)));
        assertTrue(categories.contains(categoriesGroup2.get(0)));
        assertTrue(categories.contains(categoriesGroup2.get(1)));

    }

    private List<CategoryType> recreateTypes() {
        return Arrays.asList(
                categoryTypeRepository.saveAndFlush(new CategoryType("DocCategoryTypeOne", 0, false, false)),
                categoryTypeRepository.saveAndFlush(new CategoryType("DocCategoryTypeTwo", 0, false, false))
        );
    }

    private List<Category> recreateCategories() {
        return Arrays.asList(
                categoryRepository.saveAndFlush(
                        new Category(
                                "Group1", "Group1Description", "Group1ImageUrl", types.get(0)
                        )
                ),
                categoryRepository.saveAndFlush(
                        new Category(
                                "Group11", "Group2Description", "Group2ImageUrl", types.get(0)
                        )
                ),
                categoryRepository.saveAndFlush(
                        new Category(
                                "Group2", "Group2Description", "Group2ImageUrl", types.get(1)
                        )
                ),
                categoryRepository.saveAndFlush(
                        new Category(
                                "Group22", "Group2Description", "Group2ImageUrl", types.get(1)
                        )
                )
        );
    }
}
