package com.imcode.imcms.mapping;

import com.imcode.imcms.api.CategoryAlreadyExistsException;
import com.imcode.imcms.mapping.jpa.doc.Category;
import com.imcode.imcms.mapping.jpa.doc.CategoryType;
import com.imcode.imcms.repository.CategoryRepository;
import com.imcode.imcms.repository.CategoryTypeRepository;
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

    /*
    static final String SQL__GET_DOCUMENT_CATEGORIES = "SELECT meta_id, category_id"
                                                       + " FROM document_categories"
                                                       + " WHERE meta_id ";
  */

    private int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private CategoryTypeRepository categoryTypeRepository;


    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType) {
        CategoryType docCategoryType = categoryTypeRepository.findOne(categoryType.getId());
        List<Category> categoryList = categoryRepository.findByType(docCategoryType);
        List<CategoryDomainObject> categoryDomainObjectList = new ArrayList<>(categoryList.size());

        for (Category category : categoryList) {
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

    public CategoryDomainObject addCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
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
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
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
            Category docCategory = categoryRepository.findByNameAndType(category.getName(), toJpaObject(category.getType()));

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
        List<Category> categoryList = categoryRepository.findAll(categoryIds);
        Set<CategoryDomainObject> categoryDomainObjectSet = new HashSet<>();

        for (Category category : categoryList) {
            categoryDomainObjectSet.add(toDomainObject(category));
        }

        return categoryDomainObjectSet;
    }

    public List<CategoryDomainObject> getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDomainObject> categoryDomainObjectList = new ArrayList<>(categoryList.size());

        for (Category category : categoryList) {
            categoryDomainObjectList.add(toDomainObject(category));
        }

        return categoryDomainObjectList;
    }

    public Set<CategoryDomainObject> getCategoriesOfType(CategoryTypeDomainObject categoryType, Set<Integer> categoryIds) {
        Set<CategoryDomainObject> categoryDomainObjectSet = getCategories(categoryIds);

        for (Iterator<CategoryDomainObject> i = categoryDomainObjectSet.iterator(); i.hasNext(); ) {
            if (!i.next().getType().equals(categoryType)) i.remove();
        }

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

    private CategoryDomainObject toDomainObject(Category jpaCategory) {
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

    private Category toJpaObject(CategoryDomainObject categoryDO) {
        return new Category(
                categoryDO.getId(), categoryDO.getName(), categoryDO.getDescription(), categoryDO.getImageUrl(), toJpaObject(categoryDO.getType())
        );
    }
}