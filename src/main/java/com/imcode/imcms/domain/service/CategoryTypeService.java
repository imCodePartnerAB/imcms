package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;

import java.util.List;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.12.17.
 */
public interface CategoryTypeService {
    List<CategoryTypeDTO> getAll();
}
