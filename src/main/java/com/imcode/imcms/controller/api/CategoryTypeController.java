package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.security.CheckAccess;
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
@RequestMapping("/category-types")
public class CategoryTypeController {

    private final CategoryTypeService categoryTypeService;

    @Autowired
    CategoryTypeController(CategoryTypeService categoryTypeService) {
        this.categoryTypeService = categoryTypeService;
    }

    @GetMapping
    public List<CategoryType> getCategoryTypes() {
        return categoryTypeService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryType getById(@PathVariable Integer id) {
        return categoryTypeService.get(id).orElseThrow(() -> new EmptyResultDataAccessException(id));
    }

    @CheckAccess
    @PostMapping
    public CategoryType create(@RequestBody CategoryTypeDTO categoryType) {
        return categoryTypeService.create(categoryType);
    }

    @CheckAccess
    @PutMapping
    public CategoryType update(@RequestBody CategoryTypeDTO categoryType) {
        return categoryTypeService.update(categoryType);
    }

    @CheckAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        categoryTypeService.delete(id);
    }
}
