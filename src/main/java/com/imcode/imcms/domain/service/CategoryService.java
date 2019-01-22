package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.CategoryJPA;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAll();

    Optional<Category> getById(int id);

    Category save(Category saveMe);

    Category update(Category updateMe);

    void delete(int id);

    List<CategoryJPA> getCategoriesByCategoryType(Integer id);

}
