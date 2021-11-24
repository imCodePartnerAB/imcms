package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.CategoryTypeHasCategoryException;
import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.apache.commons.lang.CharEncoding;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultCategoryTypeService implements CategoryTypeService {

    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryRepository categoryRepository;

    private final DocumentsCache documentsCache;

    private final ModelMapper modelMapper;

    @Autowired
    DefaultCategoryTypeService(CategoryTypeRepository categoryTypeRepository,
                               CategoryRepository categoryRepository,
                               DocumentsCache documentsCache,
                               ModelMapper modelMapper) {
        this.categoryTypeRepository = categoryTypeRepository;
        this.categoryRepository = categoryRepository;
        this.documentsCache = documentsCache;
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
        saveMe.setName(HtmlUtils.htmlEscape(saveMe.getName(), CharEncoding.UTF_8));
        final CategoryTypeJPA savedCategoryType = categoryTypeRepository.save(
                modelMapper.map(saveMe, CategoryTypeJPA.class)
        );
        return new CategoryTypeDTO(savedCategoryType);
    }

    @Override
    public CategoryType update(CategoryType updateMe) {
        final CategoryType receivedCategoryType = categoryTypeRepository.findOne(updateMe.getId());
        receivedCategoryType.setId(updateMe.getId());
        receivedCategoryType.setName(HtmlUtils.htmlEscape(updateMe.getName(), CharEncoding.UTF_8));
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
        if (!categoryRepository.findByType(categoryTypeRepository.findOne(id)).isEmpty()) {
            throw new CategoryTypeHasCategoryException("CategoryType has categories!");
        }
        categoryTypeRepository.delete(id);
    }

    @Override
    public void deleteForce(int id){
        for(CategoryJPA categoryJPA: categoryRepository.findByType(categoryTypeRepository.findOne(id))){
            categoryRepository.deleteDocumentCategory(categoryJPA.getId());
            categoryRepository.delete(categoryJPA.getId());
        }
        categoryTypeRepository.delete(id);
        documentsCache.invalidateCache();   //categories can control what is displayed on the docs, so we must invalidate cache
    }

}
