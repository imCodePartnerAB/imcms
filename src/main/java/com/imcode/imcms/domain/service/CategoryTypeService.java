package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.CategoryType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryTypeService {

    Optional<CategoryType> get(int id);

	Optional<CategoryType> getByName(String name);

    List<CategoryType> getAll();

    CategoryType create(CategoryType saveMe);

    CategoryType update(CategoryType updateMe);

    void delete(int id);

    Collection<Integer> deleteForce(int id);

	boolean existsByName(String name);
}
