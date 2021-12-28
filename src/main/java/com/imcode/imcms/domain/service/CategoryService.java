package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAll();

    Optional<Category> getById(int id);

    Category save(Category saveMe);

    Category update(Category updateMe);

    void delete(int id);

    Collection<Integer> deleteForce(int id);

    List<Category> getCategoriesByCategoryType(Integer id);
}
