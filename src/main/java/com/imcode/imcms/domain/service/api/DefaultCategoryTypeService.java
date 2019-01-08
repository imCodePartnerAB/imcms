package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
//(propagation = Propagation.REQUIRES_NEW)
class DefaultCategoryTypeService implements CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;

    private final CategoryRepository categoryRepository;

    @Autowired
    DefaultCategoryTypeService(CategoryTypeRepository categoryTypeRepository, CategoryRepository categoryRepository) {
        this.categoryTypeRepository = categoryTypeRepository;
        this.categoryRepository = categoryRepository;
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
        String categoryTypeName = saveMe.getName();
        if (categoryTypeName.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<CategoryType> categoriesTypeDTO = categoryTypeRepository.findAll()
                .stream()
                .map(CategoryTypeDTO::new)
                .collect(Collectors.toList());

        for (CategoryType categoryTypes : categoriesTypeDTO) {
            if (categoryTypes.getName().equals(categoryTypeName)) {
                throw new IllegalArgumentException();
            }
        }
        final CategoryTypeJPA savedCategoryType = categoryTypeRepository.save(new CategoryTypeJPA(saveMe));
        return new CategoryTypeDTO(savedCategoryType);
    }

    @Override
    public CategoryType update(CategoryType updateMe) {
        Integer categoryTypeId = updateMe.getId();
        String categoryTypeName = updateMe.getName();
        boolean isCategoryTypeMultiSelect = updateMe.isMultiSelect();
        boolean isCategoryTypeInherited = updateMe.isInherited();
        boolean isCategoryTypeImageArchive = updateMe.isImageArchive();

        CategoryType receivedCategoryType = categoryTypeRepository.findOne(categoryTypeId);

        for (CategoryType categoryTypes : getAll()) {
            if (categoryTypes.getName().equals(categoryTypeName)) {
                throw new IllegalArgumentException();
            } else {
                receivedCategoryType.setName(categoryTypeName);
                receivedCategoryType.setMultiSelect(isCategoryTypeMultiSelect);
                receivedCategoryType.setInherited(isCategoryTypeInherited);
                receivedCategoryType.setImageArchive(isCategoryTypeImageArchive);
            }
        }
        final CategoryTypeJPA updatedCategoryType = categoryTypeRepository.save(new CategoryTypeJPA(receivedCategoryType));
        return new CategoryTypeDTO(updatedCategoryType);
    }

    @Override
    public void delete(int id) {
        List<CategoryJPA> categories = categoryRepository.findAll();
        for (CategoryJPA category : categories) {
            if (category.getType().equals(categoryTypeRepository.findOne(id))) {
                throw new EmptyResultDataAccessException(id);
            }
        }
        categoryTypeRepository.delete(id);
    }

}
