package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAll();

    Category getById(int id);

    Category save(Category saveMe);

}
