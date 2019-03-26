package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CategoryDataInitializer extends TestDataCleaner {

    private final CategoryTypeDataInitializer categoryTypeDataInitializer;
    private final CategoryRepository categoryRepository;

    private List<CategoryTypeJPA> types;
    private List<CategoryJPA> categories;

    private int elementsCount;

    public CategoryDataInitializer(CategoryTypeDataInitializer categoryTypeDataInitializer,
                                   CategoryRepository categoryRepository) {

        super(categoryRepository);
        this.categoryTypeDataInitializer = categoryTypeDataInitializer;
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryJPA> createData(Integer elementsCount) {
        cleanRepositories();
        this.elementsCount = elementsCount;
        types = categoryTypeDataInitializer.createTypeData(elementsCount);
        categories = recreateCategories();
        return categories;
    }

    public List<CategoryTypeJPA> getTypes() {
        return types;
    }

    public List<CategoryDTO> getCategoriesAsDTO() {
        return categories.stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    private List<CategoryJPA> recreateCategories() {
        return IntStream.range(0, elementsCount)
                .mapToObj(i -> new CategoryJPA(null, "Category" + i + "Name",
                        "Category" + i + "Description",
                        types.get(i)))
                .map(categoryRepository::saveAndFlush)
                .collect(Collectors.toList());
    }

}
