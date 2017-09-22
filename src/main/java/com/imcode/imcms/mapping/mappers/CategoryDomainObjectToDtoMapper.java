package com.imcode.imcms.mapping.mappers;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import imcode.server.document.CategoryDomainObject;
import org.springframework.stereotype.Service;

@Service
public class CategoryDomainObjectToDtoMapper implements Mappable<CategoryDomainObject, CategoryDTO> {

    @Override
    public CategoryDTO map(CategoryDomainObject categoryDO) {
        return new CategoryDTO(categoryDO.getId(), categoryDO.getName());
    }

}
