package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAll();

    Optional<Category> getById(int id);

    Optional<Category> getByName(String name, int categoryTypeId);

    Optional<Category> getByName(String name, String categoryTypeName);

    Category save(Category saveMe);

    Category update(Category updateMe);

    void delete(int id);

    Collection<Integer> deleteForce(int id);

    List<Category> getCategoriesByCategoryType(Integer id);

	boolean existsByName(String name);
}
