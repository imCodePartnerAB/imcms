package com.imcode.imcms.controller;

import com.imcode.imcms.mapping.dto.CategoryTypeDTO;
import com.imcode.imcms.service.CategoryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category-types")
public class CategoryTypeController {

    private final CategoryTypeService categoryTypeService;

    @Autowired
    public CategoryTypeController(CategoryTypeService categoryTypeService) {
        this.categoryTypeService = categoryTypeService;
    }

    @GetMapping
    public List<CategoryTypeDTO> getCategoryTypes() {
        return categoryTypeService.getAll();
    }

}
