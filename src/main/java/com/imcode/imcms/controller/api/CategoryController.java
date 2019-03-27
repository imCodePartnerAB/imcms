package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/category-type/{id}")
    public List<CategoryJPA> getCategoriesByCategoryTypeId(@PathVariable Integer id) {
        return categoryService.getCategoriesByCategoryType(id);
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable Integer id) {
        return categoryService.getById(id).orElseThrow(() -> new EmptyResultDataAccessException(id));
    }

    @PostMapping
    public Category create(@RequestBody CategoryDTO category) {
        return categoryService.save(category);
    }

    @PutMapping
    public Category update(@RequestBody CategoryDTO category) {
        return categoryService.update(category);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        categoryService.delete(id);
    }
}
