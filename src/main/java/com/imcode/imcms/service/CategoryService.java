package com.imcode.imcms.service;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.persistence.entity.Category;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Function<Category, CategoryDTO> mapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, Function<Category, CategoryDTO> mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

}
