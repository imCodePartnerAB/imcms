package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.CategoryTypeHasCategoryException;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultCategoryTypeService implements CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;

    private final CategoryService categoryService;

    private final ModelMapper modelMapper;

    @Autowired
    DefaultCategoryTypeService(CategoryTypeRepository categoryTypeRepository,
                               CategoryService categoryService,
                               ModelMapper modelMapper) {
        this.categoryTypeRepository = categoryTypeRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<CategoryType> get(int id) {
        return categoryTypeRepository.findById(id).map(CategoryTypeDTO::new);
    }

	@Override
	public Optional<CategoryType> getByName(String name) {
		return categoryTypeRepository.findByName(name).map(CategoryTypeDTO::new);
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
        saveMe.setName(saveMe.getName());
        final CategoryTypeJPA savedCategoryType = categoryTypeRepository.save(
                modelMapper.map(saveMe, CategoryTypeJPA.class)
        );
        return new CategoryTypeDTO(savedCategoryType);
    }

    @Override
    public CategoryType update(CategoryType updateMe) {
	    final CategoryType receivedCategoryType = categoryTypeRepository.getOne(updateMe.getId());
        receivedCategoryType.setId(updateMe.getId());
        receivedCategoryType.setName(updateMe.getName());
        receivedCategoryType.setInherited(updateMe.isInherited());
        receivedCategoryType.setMultiSelect(updateMe.isMultiSelect());
        receivedCategoryType.setVisible(updateMe.isVisible());
        final CategoryTypeJPA updatedCategoryType = categoryTypeRepository.saveAndFlush(modelMapper.map(
                receivedCategoryType, CategoryTypeJPA.class
        ));

        return new CategoryTypeDTO(updatedCategoryType);
    }

    @Override
    public void delete(int id) {
        if (!categoryService.getCategoriesByCategoryType(id).isEmpty()) {
            throw new CategoryTypeHasCategoryException("CategoryType has categories!");
        }
	    categoryTypeRepository.deleteById(id);
    }

    @Override
    public Collection<Integer> deleteForce(int id){
        final Set<Integer> docIds = new HashSet<>();

	    categoryService.getCategoriesByCategoryType(id).forEach(category -> {
		    final Collection<Integer> docs = categoryService.deleteForce(category.getId());
		    docIds.addAll(docs);
	    });
	    categoryTypeRepository.deleteById(id);

        return docIds;
    }

	@Override
	public boolean existsByName(String name) {
		return categoryTypeRepository.existsByName(name);
	}

}
