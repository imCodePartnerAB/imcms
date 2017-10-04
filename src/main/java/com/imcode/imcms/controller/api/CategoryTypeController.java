package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.api.CategoryTypeService;
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
