package com.imcode.imcms.mapping.mappers;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryToDtoMapper implements Mappable<Category, CategoryDTO> {

    @Override
    public CategoryDTO map(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

}
