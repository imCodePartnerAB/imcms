package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.dto.CategoryTypeDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import com.imcode.imcms.test.TestConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @see CategoryTypeToDtoMapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CategoryTypeToDtoMapperTest extends AbstractMapperTest<CategoryType, CategoryTypeDTO> {

    @Autowired
    private Function<Category, CategoryDTO> mapper;

    @Override
    public CategoryType getOrigin() {
        final CategoryType categoryType = new CategoryType(0, "Test", 0, false, false);
        categoryType.setCategories(Arrays.asList(
                new Category(0, "Test 0"),
                new Category(1, "Test 1"),
                new Category(2, "Test 2"),
                new Category(3, "Test 3"),
                new Category(4, "Test 4")
        ));
        return categoryType;
    }

    @Override
    public CategoryTypeDTO getExpectedResult() {
        final CategoryType origin = getOrigin();
        final List<CategoryDTO> categoriesOfCategoryType = origin.getCategories()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new CategoryTypeDTO(origin.getId(), origin.getName(), origin.isMultiSelect(), categoriesOfCategoryType);
    }

}
