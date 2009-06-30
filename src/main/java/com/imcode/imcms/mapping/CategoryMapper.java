package com.imcode.imcms.mapping;

import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.CategoryAlreadyExistsException;

// TODO: replace document_caegories native queries with 
public class CategoryMapper extends HibernateTemplate {
	
    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;
                
    /*
    static final String SQL__GET_DOCUMENT_CATEGORIES = "SELECT meta_id, category_id"
                                                       + " FROM document_categories"
                                                       + " WHERE meta_id ";
	*/
    
    @Transactional
    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType) {
    	List<CategoryDomainObject> list = findByNamedQuery("Category.getByType", categoryType);
    	
    	return list.toArray(new CategoryDomainObject[] {});
    }

    @Transactional
    public boolean isUniqueCategoryTypeName(String categoryTypeName) {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
            if (categoryType.getName().equalsIgnoreCase(categoryTypeName)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public CategoryTypeDomainObject[] getAllCategoryTypes() {
    	List<CategoryTypeDomainObject> types = 
    		(List<CategoryTypeDomainObject>)getSession().getNamedQuery("CategoryType.getAllTypes")
    		.list();
    		
    	return types.toArray(new CategoryTypeDomainObject[] {});
    }

    @Transactional
    public CategoryDomainObject getCategoryByTypeAndName(CategoryTypeDomainObject categoryType, String categoryName) {
    	return (CategoryDomainObject)getSession().getNamedQuery("Category.getByNameAndType")
    		.setParameter("name", categoryName)
    		.setParameter("type", categoryType)
    		.uniqueResult();
    }

    @Transactional
    public CategoryDomainObject getCategoryById( int categoryId ) {
    	return (CategoryDomainObject)get(CategoryDomainObject.class, categoryId);
    }

    @Transactional
    public CategoryTypeDomainObject getCategoryTypeByName(String categoryTypeName) {
    	return (CategoryTypeDomainObject)getSession().getNamedQuery("CategoryType.getByName")
    		.setParameter("name", categoryTypeName)
    		.uniqueResult();
    }

    @Transactional
    public CategoryTypeDomainObject getCategoryTypeById(int categoryTypeId) {
    	return (CategoryTypeDomainObject)get(CategoryTypeDomainObject.class, categoryTypeId);
    }

    @Transactional
    public void deleteCategoryTypeFromDb(CategoryTypeDomainObject categoryType) {
        String sqlstr = "delete from category_types where category_type_id = ?";
        
        getSession().createSQLQuery(sqlstr).setParameter(0, categoryType.getId())
        	.executeUpdate();
    }

    @Transactional
    public CategoryTypeDomainObject addCategoryTypeToDb(final CategoryTypeDomainObject categoryType) {
        save(categoryType);
        return categoryType;
    }

    @Transactional
    public void updateCategoryType(CategoryTypeDomainObject categoryType) {
    	update(categoryType);
    }

    @Transactional
    public CategoryDomainObject addCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        save(category);
        return category;
    }

    @Transactional
    public void updateCategory(CategoryDomainObject category) {
    	update(category);
    }

    @Transactional
    public void deleteCategoryFromDb(CategoryDomainObject category) {
        delete(category);
    }

    @Transactional
    public String[] getAllDocumentsOfOneCategory(CategoryDomainObject category) {
        String sqlstr = "select meta_id from document_categories where category_id = ? ";
        
        List<Integer> list = (List<Integer>)getSession().createSQLQuery(sqlstr)
    		.setParameter(0, category.getId())
    		.list();
        
        String[] metaIds = new String[list.size()];
        
        for (int i = 0; i < metaIds.length; i++) {
        	metaIds[i] = list.get(i).toString();
        }
        
        return metaIds;
    }

    @Transactional
    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category) {
    	String sql = "DELETE FROM document_categories WHERE meta_id = ? and category_id = ?";
    	
    	getSession().createSQLQuery(sql)
    		.setParameter(0, document.getId())
    		.setParameter(1, category.getId())
    		.executeUpdate();
    }

    @Transactional
    void checkMaxDocumentCategoriesOfType(DocumentDomainObject document)
            throws MaxCategoryDomainObjectsOfTypeExceededException {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
            int maxChoices = categoryType.getMaxChoices();
            Set<CategoryDomainObject> documentCategoriesOfType = getCategoriesOfType(categoryType, document.getCategoryIds());
            if (UNLIMITED_MAX_CATEGORY_CHOICES != maxChoices && documentCategoriesOfType.size() > maxChoices) {
                throw new MaxCategoryDomainObjectsOfTypeExceededException("Document may have at most " + maxChoices
                                                                          + " categories of type '"
                                                                          + categoryType.getName()
                                                                          + "'");
            }
        }
    }

    @Transactional
    public void saveCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        if (0 == category.getId()) {
            CategoryDomainObject categoryInDb = getCategoryByTypeAndName(category.getType(), category.getName());
            if (null != categoryInDb) {
                throw new CategoryAlreadyExistsException("A category with name \"" + category.getName()
                                                         + "\" already exists in category type \""
                                                         + category.getType().getName()
                                                         + "\".");
            }
            addCategory(category);
        } else {
            updateCategory(category);
        }
    }

    @Transactional
    public Set<CategoryDomainObject> getCategories(Collection<Integer> categoryIds) {
        Set<CategoryDomainObject> categories = new HashSet<CategoryDomainObject>() ;
        
        for (Integer categoryId: categoryIds) {
            CategoryDomainObject category = getCategoryById(categoryId.intValue()) ;
            if (null != category) {
                categories.add(category) ;
            }
        }
        
        return categories;
    }

    @Transactional    
    public Set<CategoryDomainObject> getCategoriesOfType(CategoryTypeDomainObject categoryType, Set<Integer> categoryIds) {
        Set<CategoryDomainObject> categories = getCategories(categoryIds) ;
        Set<CategoryDomainObject> categoriesOfType = new HashSet<CategoryDomainObject>();
        
        for (CategoryDomainObject category: categories) {
            if ( categoryType.equals( category.getType() ) ) {
                categoriesOfType.add( category );
            }
        }
        
        return categoriesOfType ;
    }
}
