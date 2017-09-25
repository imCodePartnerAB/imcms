package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryToDtoMapper implements Mappable<Category, CategoryDTO> {

    @Override
    public CategoryDTO map(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

}
