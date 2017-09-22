package com.imcode.imcms.service;

import com.imcode.imcms.mapping.dto.CategoryTypeDTO;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import com.imcode.imcms.mapping.jpa.doc.CategoryTypeRepository;
import com.imcode.imcms.mapping.mapper.Mappable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;
    private final Mappable<CategoryType, CategoryTypeDTO> mapper;

    @Autowired
    public CategoryTypeService(CategoryTypeRepository categoryTypeRepository, Mappable<CategoryType, CategoryTypeDTO> mapper) {
        this.categoryTypeRepository = categoryTypeRepository;
        this.mapper = mapper;
    }

    public List<CategoryTypeDTO> getAll() {
        return categoryTypeRepository.findAllFetchCategoriesEagerly()
                .stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

}
