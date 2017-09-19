package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.dto.CategoryDTO;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.Imcms;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private CategoryMapper categoryMapper;

    public CategoriesController() {
        categoryMapper = Imcms.getServices().getCategoryMapper();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CategoryDTO> getCategories() {
        return categoryMapper.getAllCategories().stream()
                .map(CategoryDTO::of)
                .collect(Collectors.toList());
    }

}
