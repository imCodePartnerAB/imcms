package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAll();

    CategoryDTO getById(int id);

}
