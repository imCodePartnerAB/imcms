package com.imcode.imcms.mapping;

import com.imcode.imcms.api.exception.CategoryAlreadyExistsException;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(rollbackFor = Throwable.class)
public class CategoryMapper {

    private final CategoryRepository categoryRepository;
    private final CategoryTypeRepository categoryTypeRepository;

    public CategoryMapper(CategoryRepository categoryRepository, CategoryTypeRepository categoryTypeRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryTypeRepository = categoryTypeRepository;
    }

    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType) {
        CategoryTypeJPA docCategoryType = categoryTypeRepository.findOne(categoryType.getId());
        List<CategoryJPA> categoryList = categoryRepository.findByType(docCategoryType);
        List<CategoryDomainObject> categoryDomainObjectList = new ArrayList<>(categoryList.size());

        for (CategoryJPA category : categoryList) {
            categoryDomainObjectList.add(toDomainObject(category));
        }

        return categoryDomainObjectList.toArray(new CategoryDomainObject[categoryDomainObjectList.size()]);
    }

    public boolean isUniqueCategoryTypeName(String categoryTypeName) {
        return categoryTypeRepository.findByNameIgnoreCase(categoryTypeName) == null;
    }

    public CategoryTypeDomainObject[] getAllCategoryTypes() {
        List<CategoryTypeJPA> categoryTypeList = categoryTypeRepository.findAll();
        List<CategoryTypeDomainObject> categoryTypeDomainObjectList = new ArrayList<>(categoryTypeList.size());

        for (CategoryTypeJPA categoryType : categoryTypeList) {
            categoryTypeDomainObjectList.add(toDomainObject(categoryType));
        }

        return categoryTypeDomainObjectList.toArray(new CategoryTypeDomainObject[categoryTypeDomainObjectList.size()]);
    }


    public CategoryDomainObject getCategoryByTypeAndName(CategoryTypeDomainObject categoryType, String categoryName) {
        CategoryTypeJPA docCategoryType = categoryTypeRepository.findOne(categoryType.getId());
        return toDomainObject(categoryRepository.findByNameAndType(categoryName, docCategoryType));
    }


    public CategoryDomainObject getCategoryById(int categoryId) {
        return toDomainObject(categoryRepository.findOne(categoryId));
    }

    public CategoryTypeDomainObject getCategoryTypeByName(String categoryTypeName) {
        return toDomainObject(categoryTypeRepository.findByNameIgnoreCase(categoryTypeName));
    }

    public CategoryTypeDomainObject getCategoryTypeById(int categoryTypeId) {
        return toDomainObject(categoryTypeRepository.findOne(categoryTypeId));
    }


    public void deleteCategoryTypeFromDb(CategoryTypeDomainObject categoryType) {
        CategoryTypeJPA docCategoryType = categoryTypeRepository.findOne(categoryType.getId());

        if (docCategoryType != null) categoryTypeRepository.delete(docCategoryType);
    }

    public CategoryTypeDomainObject addCategoryTypeToDb(CategoryTypeDomainObject categoryType) {
        CategoryTypeJPA docCategoryType = toJpaObject(categoryType);

        docCategoryType.setId(null);

        return toDomainObject(categoryTypeRepository.saveAndFlush(docCategoryType));
    }

    public void updateCategoryType(CategoryTypeDomainObject categoryType) {
        categoryTypeRepository.saveAndFlush(toJpaObject(categoryType));
    }

    public void saveCategoryType(CategoryTypeDomainObject categoryType) {
        categoryTypeRepository.saveAndFlush(toJpaObject(categoryType));
    }

    public CategoryDomainObject addCategory(CategoryDomainObject category) {
        return toDomainObject(categoryRepository.saveAndFlush(toJpaObject(category)));
    }

    public void updateCategory(CategoryDomainObject category) {
        categoryRepository.save(toJpaObject(category));
    }

    public void deleteCategoryFromDb(CategoryDomainObject category) {
        categoryRepository.delete(category.getId());
    }

    public List<Integer> getAllDocumentsOfOneCategory(CategoryDomainObject category) {
        return categoryRepository.findCategoryDocIds(category.getId());
    }


    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category) {
        categoryRepository.deleteByDocIdAndCategoryId(document.getId(), category.getId());
    }

    public CategoryDomainObject saveCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        if (category.getId() == 0) {
            CategoryJPA docCategory = categoryRepository.findByNameAndType(category.getName(), toJpaObject(category.getType()));

            if (docCategory != null) {
                throw new CategoryAlreadyExistsException("A category with name \"" + category.getName()
                        + "\" already exists in category type \""
                        + category.getType().getName()
                        + "\".");
            }
        }

        return toDomainObject(categoryRepository.saveAndFlush(toJpaObject(category)));
    }

    public Set<CategoryDomainObject> getCategories(Collection<Category> categories) {
        Set<CategoryDomainObject> categoryDomainObjectSet = new HashSet<>();

        for (Category category : categories) {
            categoryDomainObjectSet.add(toDomainObject(category));
        }

        return categoryDomainObjectSet;
    }

    public List<CategoryDomainObject> getAllCategories() {
        List<CategoryJPA> categoryList = categoryRepository.findAll();
        List<CategoryDomainObject> categoryDomainObjectList = new ArrayList<>(categoryList.size());

        for (CategoryJPA category : categoryList) {
            categoryDomainObjectList.add(toDomainObject(category));
        }

        return categoryDomainObjectList;
    }

    public Set<CategoryDomainObject> getCategoriesOfType(CategoryTypeDomainObject categoryType, Set<Category> categories) {
        Set<CategoryDomainObject> categoryDomainObjectSet = getCategories(categories);

        categoryDomainObjectSet.removeIf(categoryDomainObject -> !categoryDomainObject.getType().equals(categoryType));

        return categoryDomainObjectSet;
    }


    private CategoryTypeDomainObject toDomainObject(CategoryType jpaType) {
        return jpaType == null
                ? null
                : new CategoryTypeDomainObject(
                jpaType.getId(),
                jpaType.getName(),
                jpaType.isMultiSelect(),
                jpaType.isInherited(),
                jpaType.isVisible());
    }

    private CategoryDomainObject toDomainObject(Category jpaCategory) {
        return jpaCategory == null
                ? null
                : new CategoryDomainObject(
                jpaCategory.getId(),
                jpaCategory.getName(),
                jpaCategory.getDescription(),
                toDomainObject(jpaCategory.getType()));
    }

    private CategoryTypeJPA toJpaObject(CategoryType typeDO) {
        return new CategoryTypeJPA(
                typeDO.getId(), typeDO.getName(), typeDO.isMultiSelect(), typeDO.isInherited(), typeDO.isVisible());
    }

    private CategoryJPA toJpaObject(CategoryDomainObject categoryDO) {
        return new CategoryJPA(
                categoryDO.getId(), categoryDO.getName(), categoryDO.getDescription(), toJpaObject(categoryDO.getType())
        );
    }
}