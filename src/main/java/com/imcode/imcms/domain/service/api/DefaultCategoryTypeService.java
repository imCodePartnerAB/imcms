package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.CategoryAlreadyExistsException;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultCategoryTypeService implements CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Autowired
    DefaultCategoryTypeService(CategoryTypeRepository categoryTypeRepository,
                               CategoryRepository categoryRepository,
                               ModelMapper modelMapper) {
        this.categoryTypeRepository = categoryTypeRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<CategoryType> get(int id) {
        return Optional.ofNullable(categoryTypeRepository.findOne(id)).map(CategoryTypeDTO::new);
    }

    @Override
    public List<CategoryType> getAll() {
        return categoryTypeRepository.findAll()
                .stream()
                .map(CategoryTypeDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryType create(CategoryType saveMe) {
        if (saveMe.getName().isEmpty()) {
            throw new IllegalArgumentException();
        }
        final CategoryTypeJPA savedCategoryType = categoryTypeRepository.save(
                modelMapper.map(saveMe, CategoryTypeJPA.class)
        );
        return new CategoryTypeDTO(savedCategoryType);
    }

    @Override
    public CategoryType update(CategoryType updateMe) {
        final CategoryType receivedCategoryType = categoryTypeRepository.findOne(updateMe.getId());
        receivedCategoryType.setId(updateMe.getId());
        receivedCategoryType.setName(updateMe.getName());
        receivedCategoryType.setImageArchive(updateMe.isImageArchive());
        receivedCategoryType.setInherited(updateMe.isInherited());
        receivedCategoryType.setMultiSelect(updateMe.isMultiSelect());
        final CategoryTypeJPA updatedCategoryType = categoryTypeRepository.saveAndFlush(modelMapper.map(
                receivedCategoryType, CategoryTypeJPA.class
        ));

        return new CategoryTypeDTO(updatedCategoryType);
    }

    @Override
    public void delete(int id) {
        List<CategoryJPA> categories = categoryRepository.findAll();
        for (CategoryJPA category : categories) {
            if (category.getType().equals(categoryTypeRepository.findOne(id))) {
                throw new CategoryAlreadyExistsException("CategoryType has categories!");
            }
        }
        categoryTypeRepository.delete(id);
    }

}
