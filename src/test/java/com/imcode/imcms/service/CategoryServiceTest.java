package com.imcode.imcms.service;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryRepository;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import com.imcode.imcms.mapping.jpa.doc.CategoryTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @see CategoryService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private Function<Category, CategoryDTO> mapper;

    private List<CategoryType> types;
    private List<Category> categories;

    @Before
    public void setUp() {
        types = recreateTypes();
        categories = recreateCategories();
    }

    @Test
    public void getAllTest() {
        final List<CategoryDTO> expected = categories.stream().map(mapper).collect(Collectors.toList());
        final List<CategoryDTO> actual = categoryService.getAll();
        assertEquals(expected, actual);
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
                                "Group2", "Group2Description", "Group2ImageUrl", types.get(1)
                        )
                )
        );
    }

}
