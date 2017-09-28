package com.imcode.imcms.util.datainitializer;

import com.imcode.imcms.mapping.dto.CategoryDTO;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryRepository;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import com.imcode.imcms.mapping.jpa.doc.CategoryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CategoryDataInitializer {

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Function<Category, CategoryDTO> mapper;

    private List<CategoryType> types;
    private List<Category> categories;

    private int elementsCount;

    public void init(int elementsCount) {
        clearRepos();
        this.elementsCount = elementsCount;
        types = recreateTypes();
        categories = recreateCategories();
    }

    private void clearRepos() {
        categoryRepository.deleteAll();
        categoryRepository.flush();
        categoryTypeRepository.deleteAll();
        categoryTypeRepository.flush();
    }

    public List<CategoryType> getTypes() {
        return types;
    }

    public List<CategoryDTO> getCategoriesAsDTO() {
        return categories.stream().map(mapper).collect(Collectors.toList());
    }

    private List<CategoryType> recreateTypes() {
        return IntStream.range(0, elementsCount)
                .mapToObj(i -> new CategoryType("CategoryType" + i + "Name", 0, false, false))
                .map(categoryTypeRepository::saveAndFlush)
                .collect(Collectors.toList());
    }

    private List<Category> recreateCategories() {
        return IntStream.range(0, elementsCount)
                .mapToObj(i -> new Category("Category" + i + "Name",
                        "Category" + i + "Description",
                        "Category" + i + "ImageUrl",
                        types.get(i)))
                .map(categoryRepository::saveAndFlush)
                .collect(Collectors.toList());
    }

}
