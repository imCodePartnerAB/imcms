package com.imcode.imcms.service;

import com.imcode.imcms.mapping.dto.CategoryTypeDTO;
import com.imcode.imcms.persistence.entity.CategoryType;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;
    private final Function<CategoryType, CategoryTypeDTO> mapper;

    @Autowired
    public CategoryTypeService(CategoryTypeRepository categoryTypeRepository, Function<CategoryType, CategoryTypeDTO> mapper) {
        this.categoryTypeRepository = categoryTypeRepository;
        this.mapper = mapper;
    }

    public List<CategoryTypeDTO> getAll() {
        return categoryTypeRepository.findAll()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

}
