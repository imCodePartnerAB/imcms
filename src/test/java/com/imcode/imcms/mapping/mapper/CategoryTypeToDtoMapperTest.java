package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.dto.CategoryTypeDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.util.Value;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @see CategoryTypeToDtoMapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CategoryTypeToDtoMapperTest extends AbstractMapperTest<CategoryType, CategoryTypeDTO> {

    @Override
    CategoryType getOrigin() {
        final CategoryType categoryType = new CategoryType(0, "Test", 0, false, false);
        categoryType.setCategories(Arrays.asList(
                Value.with(new Category(), category -> {
                    category.setId(0);
                    category.setName("Test 0");
                }),
                Value.with(new Category(), category -> {
                    category.setId(1);
                    category.setName("Test 1");
                }),
                Value.with(new Category(), category -> {
                    category.setId(2);
                    category.setName("Test 2");
                }),
                Value.with(new Category(), category -> {
                    category.setId(3);
                    category.setName("Test 3");
                }),
                Value.with(new Category(), category -> {
                    category.setId(4);
                    category.setName("Test 4");
                })
        ));
        return categoryType;
    }

    @Override
    CategoryTypeDTO getExpectedResult() {
        final CategoryType origin = getOrigin();
        final List<CategoryDTO> categoriesOfCategoryType = origin.getCategories()
                .stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
        return new CategoryTypeDTO(origin.getId(), origin.getName(), origin.isMultiSelect(), categoriesOfCategoryType);
    }

}
