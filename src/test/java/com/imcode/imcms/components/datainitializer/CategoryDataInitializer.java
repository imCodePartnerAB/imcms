package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CategoryDataInitializer extends TestDataCleaner {

    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryRepository categoryRepository;

    private List<CategoryTypeJPA> types;
    private List<CategoryJPA> categories;

    private int elementsCount;

    public CategoryDataInitializer(CategoryTypeRepository categoryTypeRepository,
                                   CategoryRepository categoryRepository) {

        super(categoryRepository, categoryTypeRepository);
        this.categoryTypeRepository = categoryTypeRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryJPA> createData(Integer elementsCount) {
        cleanRepositories();
        this.elementsCount = elementsCount;
        types = recreateTypes();
        categories = recreateCategories();
        return categories;
    }

    public List<CategoryTypeJPA> createTypeData(Integer elementsCount) {
        cleanRepositories();
        this.elementsCount = elementsCount;
        types = recreateTypes();
        return types;
    }

    public List<CategoryTypeJPA> getTypes() {
        return types;
    }

    public List<CategoryDTO> getCategoriesAsDTO() {
        return categories.stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    public List<CategoryTypeDTO> getCategoryTypesAsDTO() {
        return types.stream()
                .map(CategoryTypeDTO::new)
                .collect(Collectors.toList());
    }

    private List<CategoryTypeJPA> recreateTypes() {
        return IntStream.range(0, elementsCount)
                .mapToObj(i -> new CategoryTypeJPA("CategoryType" + i + "Name", 0, false, false))
                .map(categoryTypeRepository::saveAndFlush)
                .collect(Collectors.toList());
    }

    private List<CategoryJPA> recreateCategories() {
        return IntStream.range(0, elementsCount)
                .mapToObj(i -> new CategoryJPA("Category" + i + "Name",
                        "Category" + i + "Description",
                        "Category" + i + "ImageUrl",
                        types.get(i)))
                .map(categoryRepository::saveAndFlush)
                .collect(Collectors.toList());
    }

}
