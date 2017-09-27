package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.test.TestConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @see CategoryToDtoMapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CategoryToDtoMapperTest extends AbstractMapperTest<Category, CategoryDTO> {

    @Override
    Category getOrigin() {
        final Category category = new Category();
        category.setId(0);
        category.setName("Test");
        return category;
    }

    @Override
    CategoryDTO getExpectedResult() {
        final Category origin = getOrigin();
        return new CategoryDTO(origin.getId(), origin.getName());
    }

}
