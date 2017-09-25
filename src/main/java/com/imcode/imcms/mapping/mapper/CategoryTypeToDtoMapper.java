package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.dto.CategoryTypeDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryTypeToDtoMapper implements Mappable<CategoryType, CategoryTypeDTO> {

    private final Mappable<Category, CategoryDTO> categoryMapper;

    @Autowired
    public CategoryTypeToDtoMapper(Mappable<Category, CategoryDTO> categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryTypeDTO map(CategoryType categoryTypeDO) {
        return new CategoryTypeDTO(
                categoryTypeDO.getId(),
                categoryTypeDO.getName(),
                categoryTypeDO.isMultiSelect(),
                categoryTypeDO
                        .getCategories()
                        .stream()
                        .map(categoryMapper::map)
                        .collect(Collectors.toList())
        );
    }

}
