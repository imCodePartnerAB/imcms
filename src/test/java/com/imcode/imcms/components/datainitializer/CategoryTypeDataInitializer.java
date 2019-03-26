package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CategoryTypeDataInitializer extends TestDataCleaner { //todo create tests for this init

    private final CategoryTypeRepository categoryTypeRepository;

    private List<CategoryTypeJPA> types;
    private int elementsCount;

    public CategoryTypeDataInitializer(CategoryTypeRepository categoryTypeRepository) {
        super(categoryTypeRepository);
        this.categoryTypeRepository = categoryTypeRepository;
    }


    public List<CategoryTypeJPA> createTypeData(Integer elementsCount) {
        cleanRepositories();
        this.elementsCount = elementsCount;
        types = categoriesTypesInit();
        return types;
    }

    public List<CategoryTypeDTO> getCategoryTypesAsDTO() {
        return types.stream()
                .map(CategoryTypeDTO::new)
                .collect(Collectors.toList());
    }

    private List<CategoryTypeJPA> categoriesTypesInit() {
        return IntStream.range(0, elementsCount)
                .mapToObj(i -> new CategoryTypeJPA(null, "CategoryTypeName" + i + "DESC", false, false))
                .map(categoryTypeRepository::saveAndFlush)
                .collect(Collectors.toList());
    }
}
