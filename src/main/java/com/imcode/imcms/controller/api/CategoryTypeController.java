package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.CategoryType;
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

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/category-types")
public class CategoryTypeController {

    private final CategoryTypeService categoryTypeService;

    private final DocumentMapper documentMapper;

    @Autowired
    CategoryTypeController(CategoryTypeService categoryTypeService, DocumentMapper documentMapper) {
        this.categoryTypeService = categoryTypeService;
        this.documentMapper = documentMapper;
    }

    @GetMapping
    public List<CategoryType> getCategoryTypes() {
        return categoryTypeService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryType getById(@PathVariable Integer id) {
        return categoryTypeService.get(id).orElseThrow(() -> new EmptyResultDataAccessException(id));
    }

    @PostMapping
    public CategoryType create(@RequestBody CategoryTypeDTO categoryType) {
        return categoryTypeService.create(categoryType);
    }

    @PutMapping
    public CategoryType update(@RequestBody CategoryTypeDTO categoryType) {
        return categoryTypeService.update(categoryType);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        categoryTypeService.delete(id);
    }

    @DeleteMapping("/force/{id}")
    public void deleteForceById(@PathVariable Integer id) {
        final Collection<Integer> docIds = categoryTypeService.deleteForce(id);
        docIds.forEach(documentMapper::invalidateDocument); //categories can control what is displayed on the docs, so we must invalidate cache and reindex
    }
}
