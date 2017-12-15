package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.CategoryType;

import java.util.List;

public interface CategoryTypeService {

    List<CategoryType> getAll();

    void save(CategoryType saveMe);

}
