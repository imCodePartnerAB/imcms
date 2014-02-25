package com.imcode.imcms.mapping;

import com.imcode.imcms.api.CategoryAlreadyExistsException;
import com.imcode.imcms.mapping.dao.DocCategoryDao;
import com.imcode.imcms.mapping.dao.DocCategoryTypeDao;
import com.imcode.imcms.mapping.orm.DocCategory;
import com.imcode.imcms.mapping.orm.DocCategoryType;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;

@Transactional(rollbackOn = Throwable.class)
public class CategoryMapper {

    /*
    static final String SQL__GET_DOCUMENT_CATEGORIES = "SELECT meta_id, category_id"
                                                       + " FROM document_categories"
                                                       + " WHERE meta_id ";
  */

    private int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    @Inject
    private DocCategoryDao docCategoryDao;

    @Inject
    private DocCategoryTypeDao docCategoryTypeDao;


    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType) {
        DocCategoryType docCategoryType = docCategoryTypeDao.findOne(categoryType.getId());
        List<DocCategory> docCategoryList = docCategoryDao.findByType(docCategoryType);
        List<CategoryDomainObject> categoryDomainObjectList = new ArrayList<>(docCategoryList.size());

        for (DocCategory docCategory : docCategoryList) {
            categoryDomainObjectList.add(OrmToApi.toApi(docCategory));
        }

        return categoryDomainObjectList.toArray(new CategoryDomainObject[categoryDomainObjectList.size()]);
    }


    public boolean isUniqueCategoryTypeName(String categoryTypeName) {
        return docCategoryTypeDao.findByNameIgnoreCase(categoryTypeName) == null;
    }

    public CategoryTypeDomainObject[] getAllCategoryTypes() {
        List<DocCategoryType> docCategoryTypeList = docCategoryTypeDao.findAll();
        List<CategoryTypeDomainObject> categoryTypeDomainObjectList = new ArrayList<>(docCategoryTypeList.size());

        for (DocCategoryType docCategoryType : docCategoryTypeList) {
            categoryTypeDomainObjectList.add(OrmToApi.toApi(docCategoryType));
        }

        return categoryTypeDomainObjectList.toArray(new CategoryTypeDomainObject[categoryTypeDomainObjectList.size()]);
    }


    public CategoryDomainObject getCategoryByTypeAndName(CategoryTypeDomainObject categoryType, String categoryName) {
        DocCategoryType docCategoryType = docCategoryTypeDao.findOne(categoryType.getId());
        DocCategory docCategory = docCategoryDao.findByNameAndType(categoryName, docCategoryType);

        return docCategory == null ? null : OrmToApi.toApi(docCategory);
    }


    public CategoryDomainObject getCategoryById(int categoryId) {
        DocCategory docCategory = docCategoryDao.findOne(categoryId);

        return docCategory == null ? null : OrmToApi.toApi(docCategory);
    }

    public CategoryTypeDomainObject getCategoryTypeByName(String categoryTypeName) {
        DocCategoryType docCategoryType = docCategoryTypeDao.findByNameIgnoreCase(categoryTypeName);

        return docCategoryType == null ? null : OrmToApi.toApi(docCategoryType);
    }

    public CategoryTypeDomainObject getCategoryTypeById(int categoryTypeId) {
        DocCategoryType docCategoryType = docCategoryTypeDao.findOne(categoryTypeId);

        return docCategoryType == null ? null : OrmToApi.toApi(docCategoryType);
    }


    public void deleteCategoryTypeFromDb(CategoryTypeDomainObject categoryType) {
        DocCategoryType docCategoryType = docCategoryTypeDao.findOne(categoryType.getId());

        if (docCategoryType != null) docCategoryTypeDao.delete(docCategoryType);
    }

    public CategoryTypeDomainObject addCategoryTypeToDb(CategoryTypeDomainObject categoryType) {
        DocCategoryType docCategoryType = OrmToApi.toOrm(categoryType);

        docCategoryType.setId(null);

        return OrmToApi.toApi(docCategoryTypeDao.saveAndFlush(docCategoryType));
    }

    public void updateCategoryType(CategoryTypeDomainObject categoryType) {
        docCategoryTypeDao.saveAndFlush(OrmToApi.toOrm(categoryType));
    }

    public void saveCategoryType(CategoryTypeDomainObject categoryType) {
        docCategoryTypeDao.saveAndFlush(OrmToApi.toOrm(categoryType));
    }

    public CategoryDomainObject addCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        return OrmToApi.toApi(docCategoryDao.saveAndFlush(OrmToApi.toOrm(category)));
    }

    public void updateCategory(CategoryDomainObject category) {
        docCategoryDao.save(OrmToApi.toOrm(category));
    }

    public void deleteCategoryFromDb(CategoryDomainObject category) {
        docCategoryDao.delete(category.getId());
    }

    public String[] getAllDocumentsOfOneCategory(CategoryDomainObject category) {
        return docCategoryDao.findCategoryDocIds(category.getId());
    }


    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category) {
        docCategoryDao.deleteByDocIdAndCategoryId(document.getId(), category.getId());
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
                        + "'" );
            }
        }
    }

    public CategoryDomainObject saveCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        if (category.getId() == 0) {
            DocCategory docCategory = docCategoryDao.findByNameAndType(category.getName(), OrmToApi.toOrm(category.getType()));

            if (docCategory != null) {
                throw new CategoryAlreadyExistsException("A category with name \"" + category.getName()
                        + "\" already exists in category type \""
                        + category.getType().getName()
                        + "\"." );
            }
        }

        return OrmToApi.toApi(docCategoryDao.saveAndFlush(OrmToApi.toOrm(category)));
    }

    public Set<CategoryDomainObject> getCategories(Collection<Integer> categoryIds) {
        List<DocCategory> docCategoryList = docCategoryDao.findAll(categoryIds);
        Set<CategoryDomainObject> categoryDomainObjectSet = new HashSet<>();

        for (DocCategory docCategory : docCategoryList) {
            categoryDomainObjectSet.add(OrmToApi.toApi(docCategory));
        }

        return categoryDomainObjectSet;
    }

    public List<CategoryDomainObject> getAllCategories() {
        List<DocCategory> docCategoryList = docCategoryDao.findAll();
        List<CategoryDomainObject> categoryDomainObjectList = new ArrayList<>(docCategoryList.size());

        for (DocCategory docCategory : docCategoryList) {
            categoryDomainObjectList.add(OrmToApi.toApi(docCategory));
        }

        return categoryDomainObjectList;
    }

    public Set<CategoryDomainObject> getCategoriesOfType(CategoryTypeDomainObject categoryType, Set<Integer> categoryIds) {
        Set<CategoryDomainObject> categoryDomainObjectSet = getCategories(categoryIds);

        for (Iterator<CategoryDomainObject> i = categoryDomainObjectSet.iterator(); i.hasNext(); ) {
            if (i.next().getType().equals(categoryType)) i.remove();
        }

        return categoryDomainObjectSet;
    }
}