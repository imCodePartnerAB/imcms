package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.mappers.Mappable;
import imcode.server.Imcms;
import imcode.server.document.CategoryDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private CategoryMapper categoryMapper;

    private final Mappable<CategoryDomainObject, CategoryDTO> mapper;

    @Autowired
    public CategoryController(Mappable<CategoryDomainObject, CategoryDTO> mapper) {
        categoryMapper = Imcms.getServices().getCategoryMapper();
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CategoryDTO> getCategories() {
        return categoryMapper.getAllCategories().stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

}
