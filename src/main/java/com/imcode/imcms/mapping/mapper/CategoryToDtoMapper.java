package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CategoryToDtoMapper implements Function<Category, CategoryDTO> {

    @Override
    public CategoryDTO apply(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

}
