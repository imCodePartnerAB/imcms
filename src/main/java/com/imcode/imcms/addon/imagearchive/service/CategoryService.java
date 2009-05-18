package com.imcode.imcms.addon.imagearchive.service;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.CategoryTypes;
import com.imcode.imcms.addon.imagearchive.service.exception.CategoryExistsException;

@Transactional
public class CategoryService {
    @Autowired
    @Qualifier("hibernateTemplate")
    private HibernateTemplate template;
    
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<CategoryTypes> getCategoryTypes() {
        return (List<CategoryTypes>) template.executeFind(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<CategoryTypes> types = session.createQuery(
                        "SELECT ct.id AS id, ct.name AS name FROM CategoryTypes ct WHERE ct.imageArchive IS TRUE ORDER BY ct.name")
                        .setResultTransformer(Transformers.aliasToBean(CategoryTypes.class))
                        .list();
                
                return types;
            }
        });
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public boolean existsCategory(final String categoryName, final int categoryTypeId) {
        return (Boolean) template.execute(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Integer existingCategoryId = (Integer) session.createQuery(
                        "SELECT c.id FROM Categories c WHERE c.name = :name AND c.type.id = :typeId")
                        .setString("name", categoryName)
                        .setInteger("typeId", categoryTypeId)
                        .setMaxResults(1)
                        .uniqueResult();
                
                return existingCategoryId != null;
            }
        });
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public boolean existsCategory(final int categoryId, final String newCategoryName) {
        return (Boolean) template.execute(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Integer existingCategoryId = (Integer) session.createQuery(
                        "SELECT c.id FROM Categories c " +
                        "WHERE c.name = :newName AND c.id <> :categoryId AND c.type.imageArchive IS TRUE")
                        .setString("newName", newCategoryName)
                        .setInteger("categoryId", categoryId)
                        .setMaxResults(1)
                        .uniqueResult();
                
                return existingCategoryId != null;
            }
        });
    }
    
    public Categories createCategory(final String categoryName, final int categoryTypeId) throws CategoryExistsException {
        
        return (Categories) template.execute(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Integer typeId = (Integer) session.createQuery("SELECT ct.id FROM CategoryTypes ct WHERE ct.id = :id")
                        .setInteger("id", categoryTypeId)
                        .uniqueResult();
                
                if (typeId == null) {
                    return null;
                }
                
                if (existsCategory(categoryName, categoryTypeId)) {
                    throw new CategoryExistsException();
                }
                
                Categories category = new Categories();
                category.setName(categoryName);
                category.setTypeId(categoryTypeId);
                category.setDescription("");
                category.setImage("");
                
                session.persist(category);
                session.flush();
                
                return category;
            }
        });
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public Categories getCategory(final int categoryId) {
        return (Categories) template.execute(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Categories category = (Categories) session.createQuery(
                        "SELECT c.id AS id, c.name AS name, c.typeId AS typeId FROM Categories c WHERE c.id = :id")
                        .setInteger("id", categoryId)
                        .setResultTransformer(Transformers.aliasToBean(Categories.class))
                        .uniqueResult();
                
                return category;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> getCategories() {
        return (List<Categories>) template.executeFind(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<Categories> categories = session.createQuery(
                        "SELECT c.id AS id, c.name AS name, c.typeId AS typeId FROM Categories c " +
                        "WHERE c.type.imageArchive IS TRUE ORDER BY c.name")
                        .setResultTransformer(Transformers.aliasToBean(Categories.class))
                        .list();
                
                return categories;
            }
        });
    }
    
    public void deleteCategory(final int categoryId) {
        template.execute(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.createQuery("DELETE FROM ImageCategories ic WHERE ic.categoryId = :id")
                        .setInteger("id", categoryId)
                        .executeUpdate();
                
                session.createQuery("DELETE FROM CategoryRoles cr WHERE cr.categoryId = :id")
                        .setInteger("id", categoryId)
                        .executeUpdate();
                
                session.createSQLQuery("DELETE FROM document_categories WHERE category_id = :id")
                        .setInteger("id", categoryId)
                        .executeUpdate();
                
                
                session.createQuery("DELETE FROM Categories c WHERE c.id = :id")
                        .setInteger("id", categoryId)
                        .executeUpdate();
                
                return null;
            }
        });
    }
    
    public void updateCategory(final int categoryId, final String categoryName, final int typeId) throws CategoryExistsException {
        template.execute(new HibernateCallback() {
        
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                if (existsCategory(categoryId, categoryName)) {
                    throw new CategoryExistsException();
                }
                
                session.createQuery(
                        "UPDATE Categories c SET c.name = :name, c.typeId = :typeId WHERE c.id = :categoryId")
                        .setString("name", categoryName)
                        .setInteger("typeId", typeId)
                        .setInteger("categoryId", categoryId)
                        .executeUpdate();
                
                return null;
            }
        });
    }
}
