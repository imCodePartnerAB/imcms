package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.CategoryType;

import java.util.List;

public interface CategoryTypeService {

    CategoryType get(int id);

    List<CategoryType> getAll();

    CategoryType save(CategoryType saveMe);

}
