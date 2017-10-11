package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.persistence.entity.Category;
import com.imcode.imcms.persistence.entity.CategoryType;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;

@Component
public class CategoryDataInitializer extends AbstractTestDataInitializer<Integer, List<Category>> {

    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryRepository categoryRepository;
    private final Function<Category, CategoryDTO> categoryMapper;
    private final Function<CategoryType, CategoryTypeDTO> categoryTypeMapper;

    private List<CategoryType> types;
    private List<Category> categories;

    private int elementsCount;

    public CategoryDataInitializer(CategoryTypeRepository categoryTypeRepository,
                                   CategoryRepository categoryRepository,
                                   Function<Category, CategoryDTO> categoryMapper,
                                   Function<CategoryType, CategoryTypeDTO> categoryTypeMapper) {
        super(categoryRepository, categoryTypeRepository);
        this.categoryTypeRepository = categoryTypeRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.categoryTypeMapper = categoryTypeMapper;
    }

    @Override
    public List<Category> createData(Integer elementsCount) {
        cleanRepositories();
        this.elementsCount = elementsCount;
        types = recreateTypes();
        categories = recreateCategories();
        return categories;
    }

    public List<CategoryType> getTypes() {
        return types;
    }

    public List<CategoryDTO> getCategoriesAsDTO() {
        return categories.stream()
                .map(categoryMapper)
                .collect(Collectors.toList());
    }

    public List<CategoryTypeDTO> getCategoryTypesAsDTO() {
        IntStream.range(0, elementsCount)
                .forEach(i -> types.get(i).setCategories(singletonList(categories.get(i))));

        return types.stream()
                .map(categoryTypeMapper)
                .collect(Collectors.toList());
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
