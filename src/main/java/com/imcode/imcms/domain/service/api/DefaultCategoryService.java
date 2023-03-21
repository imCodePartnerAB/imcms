package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.DataUseCategoryException;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final DocumentMapper documentMapper;

    private final ModelMapper modelMapper;

    private final Logger logger = LogManager.getLogger(DefaultCategoryService.class);

    @Autowired
    DefaultCategoryService(CategoryRepository categoryRepository, DocumentMapper documentMapper, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.documentMapper = documentMapper;
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
        return categoryRepository.findById(id).map(CategoryDTO::new);
    }

	@Override
	public Optional<Category> getByName(String name) {
		return categoryRepository.getByName(name).map(CategoryDTO::new);
	}

	@Override
    public Category save(Category saveMe) {
        saveMe.setName(saveMe.getName());
        return new CategoryDTO(categoryRepository.save(modelMapper.map(saveMe, CategoryJPA.class)));
    }

    @Override
    public Category update(Category updateMe) {
	    final Category receivedCategory = categoryRepository.getOne(updateMe.getId());
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
	        categoryRepository.deleteById(id);
        } else {
            throw new DataUseCategoryException("Category has documents!");
        }
    }

    @Override
    public Collection<Integer> deleteForce(int id){
        final Collection<Integer> categoryDocIds = categoryRepository.findCategoryDocIds(id);

	    categoryRepository.deleteDocumentCategory(id);
	    categoryRepository.deleteById(id);

        return categoryDocIds;
    }

    @Override
    public List<Category> getCategoriesByCategoryType(Integer id) {
        return categoryRepository.findByTypeId(id).stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

	@Override
	public boolean existsByName(String name) {
		return categoryRepository.existsByName(name);
	}
}
