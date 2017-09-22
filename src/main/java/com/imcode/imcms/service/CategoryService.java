package com.imcode.imcms.service;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryRepository;
import com.imcode.imcms.mapping.mappers.Mappable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Mappable<Category, CategoryDTO> mapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, Mappable<Category, CategoryDTO> mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }


    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

}
