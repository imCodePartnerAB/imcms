package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CategoryDTO;
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
    private final Function<Category, CategoryDTO> categoryToCategoryDTO;

    @Autowired
    CategoryService(CategoryRepository categoryRepository,
                    Function<Category, CategoryDTO> categoryToCategoryDTO) {

        this.categoryRepository = categoryRepository;
        this.categoryToCategoryDTO = categoryToCategoryDTO;
    }

    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryToCategoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getById(int id) {
        return categoryToCategoryDTO.apply(categoryRepository.findOne(id));
    }

}
