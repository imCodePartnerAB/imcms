package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    DefaultCategoryService(CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
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
        return new CategoryDTO(categoryRepository.save(new CategoryJPA(saveMe)));
    }

    @Override
    public Category update(Category updateMe) {
        Integer id = updateMe.getId();
        String name = updateMe.getName();
        CategoryType categoryType = updateMe.getType();
        String description = updateMe.getDescription();
        String imageUrl = updateMe.getImageUrl();

        Category receivedCategory = categoryRepository.findOne(id);

        for (Category category : getAll()) {
            if (receivedCategory.getName().equals(category.getName())) {
                throw new IllegalArgumentException();
            } else if (receivedCategory.getName().isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                receivedCategory.setName(name);
                receivedCategory.setType(categoryType);
                receivedCategory.setDescription(description);
                receivedCategory.setImageUrl(imageUrl);
            }
        }
        final Category updatedCategory = categoryRepository.save(new CategoryJPA(receivedCategory));
        return new CategoryDTO(updatedCategory);
    }

    @Override
    public void delete(int id) {
        List<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(id);
        if (categoryDocIds.isEmpty()) {
            categoryRepository.deleteDocumentCategory(id);
            categoryRepository.delete(id);
        } else {
            throw new EmptyResultDataAccessException(id);
        }
    }
}
