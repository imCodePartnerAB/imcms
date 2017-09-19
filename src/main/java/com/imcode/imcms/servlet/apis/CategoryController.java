package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.dto.CategoryDTO;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.Imcms;
import imcode.server.document.CategoryTypeDomainObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: 19.09.17 Make todos according to order
@RestController
// TODO: 19.09.17 Replace value to "/categories" => (order = 2)
@RequestMapping("/")
public class CategoryController {

    private CategoryMapper categoryMapper;

    public CategoryController() {
        categoryMapper = Imcms.getServices().getCategoryMapper();
    }

    // TODO: 19.09.17 Remove me => (order = 1)
    @RequestMapping("/category")
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

    // TODO: 19.09.17 Remove value attribute => (order = 3)
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public List<CategoryDTO> getCategories() {
        return categoryMapper.getAllCategories().stream()
                .map(CategoryDTO::of)
                .collect(Collectors.toList());
    }


}
