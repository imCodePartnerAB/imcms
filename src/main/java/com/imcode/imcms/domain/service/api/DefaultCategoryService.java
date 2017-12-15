package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
class DefaultCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    DefaultCategoryService(CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Category getById(int id) {
        return new CategoryDTO(categoryRepository.findOne(id));
    }

    @Override
    public Category save(Category saveMe) {
        return new CategoryDTO(categoryRepository.save(new CategoryJPA(saveMe)));
    }
}
