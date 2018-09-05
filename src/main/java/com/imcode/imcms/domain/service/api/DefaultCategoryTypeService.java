package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CategoryTypeDTO;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    DefaultCategoryTypeService(CategoryTypeRepository categoryTypeRepository) {
        this.categoryTypeRepository = categoryTypeRepository;
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
    public CategoryType save(CategoryType saveMe) {
        final CategoryTypeJPA savedCategoryType = categoryTypeRepository.save(new CategoryTypeJPA(saveMe));
        return new CategoryTypeDTO(savedCategoryType);
    }

    @Override
    public void delete(int id) {
        categoryTypeRepository.delete(id);
    }

}