package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.security.AccessRoleType;
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

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final DocumentMapper documentMapper;

    @Autowired
    CategoryController(CategoryService categoryService, DocumentMapper documentMapper) {
        this.categoryService = categoryService;
        this.documentMapper = documentMapper;
    }

    @GetMapping
    public List<Category> getCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/category-type/{id}")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public List<Category> getCategoriesByCategoryTypeId(@PathVariable Integer id) {
        return categoryService.getCategoriesByCategoryType(id);
    }

    @GetMapping("/{id}")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public Category getById(@PathVariable Integer id) {
        return categoryService.getById(id).orElseThrow(() -> new EmptyResultDataAccessException(id));
    }

    @PostMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public Category create(@RequestBody CategoryDTO category) {
        return categoryService.save(category);
    }

    @PutMapping
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public Category update(@RequestBody CategoryDTO category) {
        return categoryService.update(category);
    }

    @DeleteMapping("/{id}")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public void deleteById(@PathVariable Integer id) {
        categoryService.delete(id);
    }

    @DeleteMapping("/force/{id}")
    @CheckAccess(role = AccessRoleType.ADMIN_PAGES)
    public void deleteForceById(@PathVariable Integer id) {
        final Collection<Integer> docIds = categoryService.deleteForce(id);
        docIds.forEach(documentMapper::invalidateDocument); //categories can control what is displayed on the docs, so we must invalidate cache and reindex
    }
}
