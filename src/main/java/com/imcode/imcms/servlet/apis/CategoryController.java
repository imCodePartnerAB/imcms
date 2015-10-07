package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.Imcms;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Realise API for working with  {@link DocumentDomainObject}
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @RequestMapping
    public Object getCategoriesList() {
        CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();

        return Stream.of(categoryMapper.getAllCategoryTypes())
                .distinct()
                .collect(
                        Collectors.toMap(
                                CategoryTypeDomainObject::getName,
                                val -> new Object() {
                                    public List<Object> items = Stream.of(categoryMapper.getAllCategoriesOfType(val))
                                            .collect(Collectors.toList());
                                    public boolean isMultiple = val.isMultiselect();
                                }
                        )
                );
    }
}
