package com.imcode.imcms.mapping;

import com.imcode.imcms.api.CategoryAlreadyExistsException;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryType;
import com.imcode.imcms.persistence.repository.CategoryRepository;
import com.imcode.imcms.persistence.repository.CategoryTypeRepository;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional(rollbackFor = Throwable.class)
public class CategoryMapper {

    private static int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    private final CategoryRepository categoryRepository;
    private final CategoryTypeRepository categoryTypeRepository;

    @Inject
    public CategoryMapper(CategoryRepository categoryRepository, CategoryTypeRepository categoryTypeRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryTypeRepository = categoryTypeRepository;
    }


    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType) {
        CategoryType docCategoryType = categoryTypeRepository.findOne(categoryType.getId());
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
        List<CategoryType> categoryTypeList = categoryTypeRepository.findAll();
        List<CategoryTypeDomainObject> categoryTypeDomainObjectList = new ArrayList<>(categoryTypeList.size());

        for (CategoryType categoryType : categoryTypeList) {
            categoryTypeDomainObjectList.add(toDomainObject(categoryType));
        }

        return categoryTypeDomainObjectList.toArray(new CategoryTypeDomainObject[categoryTypeDomainObjectList.size()]);
    }


    public CategoryDomainObject getCategoryByTypeAndName(CategoryTypeDomainObject categoryType, String categoryName) {
        CategoryType docCategoryType = categoryTypeRepository.findOne(categoryType.getId());
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
        CategoryType docCategoryType = categoryTypeRepository.findOne(categoryType.getId());

        if (docCategoryType != null) categoryTypeRepository.delete(docCategoryType);
    }

    public CategoryTypeDomainObject addCategoryTypeToDb(CategoryTypeDomainObject categoryType) {
        CategoryType docCategoryType = toJpaObject(categoryType);

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

    public String[] getAllDocumentsOfOneCategory(CategoryDomainObject category) {
        return categoryRepository.findCategoryDocIds(category.getId());
    }


    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category) {
        categoryRepository.deleteByDocIdAndCategoryId(document.getId(), category.getId());
    }


    void checkMaxDocumentCategoriesOfType(DocumentDomainObject document)
            throws MaxCategoryDomainObjectsOfTypeExceededException {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for (CategoryTypeDomainObject categoryType : categoryTypes) {
            int maxChoices = categoryType.getMaxChoices();
            Set documentCategoriesOfType = getCategoriesOfType(categoryType, document.getCategoryIds());
            if (UNLIMITED_MAX_CATEGORY_CHOICES != maxChoices && documentCategoriesOfType.size() > maxChoices) {
                throw new MaxCategoryDomainObjectsOfTypeExceededException("Document may have at most " + maxChoices
                        + " categories of type '"
                        + categoryType.getName()
                        + "'");
            }
        }
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

    public Set<CategoryDomainObject> getCategories(Collection<Integer> categoryIds) {
        List<CategoryJPA> categoryList = categoryRepository.findAll(categoryIds);
        Set<CategoryDomainObject> categoryDomainObjectSet = new HashSet<>();

        for (CategoryJPA category : categoryList) {
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

    public Set<CategoryDomainObject> getCategoriesOfType(CategoryTypeDomainObject categoryType, Set<Integer> categoryIds) {
        Set<CategoryDomainObject> categoryDomainObjectSet = getCategories(categoryIds);

        categoryDomainObjectSet.removeIf(categoryDomainObject -> !categoryDomainObject.getType().equals(categoryType));

        return categoryDomainObjectSet;
    }


    private CategoryTypeDomainObject toDomainObject(CategoryType jpaType) {
        return jpaType == null
                ? null
                : new CategoryTypeDomainObject(
                jpaType.getId(),
                jpaType.getName(),
                jpaType.getMaxChoices(),
                jpaType.isInherited(),
                jpaType.isImageArchive());
    }

    private CategoryDomainObject toDomainObject(CategoryJPA jpaCategory) {
        return jpaCategory == null
                ? null
                : new CategoryDomainObject(
                jpaCategory.getId(),
                jpaCategory.getName(),
                jpaCategory.getDescription(),
                jpaCategory.getImageUrl(),
                toDomainObject(jpaCategory.getType()));
    }

    private CategoryType toJpaObject(CategoryTypeDomainObject typeDO) {
        return new CategoryType(
                typeDO.getId(), typeDO.getName(), typeDO.getMaxChoices(), typeDO.isInherited(), typeDO.isImageArchive()
        );
    }

    private CategoryJPA toJpaObject(CategoryDomainObject categoryDO) {
        return new CategoryJPA(
                categoryDO.getId(), categoryDO.getName(), categoryDO.getDescription(), categoryDO.getImageUrl(), toJpaObject(categoryDO.getType())
        );
    }
}