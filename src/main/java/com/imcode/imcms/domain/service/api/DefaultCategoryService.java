package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.DataUseCategoryException;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Autowired
    DefaultCategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {

        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> getById(int id) {
        return Optional.ofNullable(categoryRepository.findOne(id)).map(CategoryDTO::new);
    }

    @Override
    public Category save(Category saveMe) {
        return new CategoryDTO(categoryRepository.save(modelMapper.map(saveMe, CategoryJPA.class)));
    }

    @Override
    public Category update(Category updateMe) {
        final Category receivedCategory = categoryRepository.findOne(updateMe.getId());
        receivedCategory.setId(updateMe.getId());
        receivedCategory.setName(updateMe.getName());
        receivedCategory.setDescription(updateMe.getDescription());
        receivedCategory.setType(updateMe.getType());
        final Category updatedCategory = categoryRepository.saveAndFlush(modelMapper.map(receivedCategory, CategoryJPA.class));
        return new CategoryDTO(updatedCategory);
    }

    @Override
    public void delete(int id) {
        List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(id);
        if (categoryDocIds.isEmpty()) {
            categoryRepository.delete(id);
        } else {
            throw new DataUseCategoryException("Category has documents!");
        }
    }

    @Override
    public List<CategoryJPA> getCategoriesByCategoryType(Integer id) {
        return categoryRepository.findById(id);
    }
}
