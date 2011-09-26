package com.imcode.imcms.addon.imagearchive.service;

import java.util.List;

import com.imcode.imcms.addon.imagearchive.dto.RoleCategoriesDto;
import com.imcode.imcms.addon.imagearchive.entity.CategoryRoles;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.CategoryTypes;
import com.imcode.imcms.addon.imagearchive.service.exception.CategoryExistsException;
import org.hibernate.SessionFactory;

@Transactional
public class CategoryService {
    @Autowired
    private SessionFactory factory;
    
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<CategoryTypes> getCategoryTypes() {

        List<CategoryTypes> types = factory.getCurrentSession()
                .createQuery(
                "SELECT ct.id AS id, ct.name AS name FROM CategoryTypes ct WHERE ct.imageArchive IS TRUE ORDER BY ct.name")
                .setResultTransformer(Transformers.aliasToBean(CategoryTypes.class))
                .list();

        return types;
    }

    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<CategoryRoles> findCategoryRoles(Roles role) {
        List<CategoryRoles> categoryRoles = factory.getCurrentSession()
                .createQuery("SELECT cr.categoryId AS categoryId, cr.roleId AS roleId, cr.canUse AS canUse, cr.canChange AS canChange " +
                        "FROM CategoryRoles cr WHERE cr.roleId = :roleId")
                .setInteger("roleId", role.getId())
                .setResultTransformer(Transformers.aliasToBean(CategoryRoles.class))
                .list();

        return categoryRoles;
    }

    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public boolean existsCategory(String categoryName, int categoryTypeId) {
        
        Integer existingCategoryId = (Integer) factory.getCurrentSession()
                .createQuery(
                "SELECT c.id FROM Categories c WHERE c.name = :name AND c.type.id = :typeId")
                .setString("name", categoryName)
                .setInteger("typeId", categoryTypeId)
                .setMaxResults(1)
                .uniqueResult();

        return existingCategoryId != null;
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public boolean existsCategory(int categoryId, String newCategoryName) {

        Integer existingCategoryId = (Integer) factory.getCurrentSession()
                .createQuery(
                "SELECT c.id FROM Categories c " +
                "WHERE c.name = :newName AND c.id <> :categoryId AND c.type.name = 'Images'")
                .setString("newName", newCategoryName)
                .setInteger("categoryId", categoryId)
                .setMaxResults(1)
                .uniqueResult();

        return existingCategoryId != null;
    }
    
    public Categories createCategory(String categoryName, int categoryTypeId) throws CategoryExistsException {
        Session session = factory.getCurrentSession();

        Integer typeId = (Integer) session
                .createQuery("SELECT ct.id FROM CategoryTypes ct WHERE ct.id = :id")
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
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public Categories getCategory(int categoryId) {

        Categories category = (Categories) factory.getCurrentSession()
                .createQuery(
                "SELECT c.id AS id, c.name AS name, c.typeId AS typeId FROM Categories c WHERE c.id = :id")
                .setInteger("id", categoryId)
                .setResultTransformer(Transformers.aliasToBean(Categories.class))
                .uniqueResult();

        return category;
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> getCategories() {
        
        List<Categories> categories = factory.getCurrentSession()
                .createQuery(
                "SELECT c.id AS id, c.name AS name, c.typeId AS typeId FROM Categories c " +
                "WHERE c.type.name = :typeName ORDER BY c.name")
                .setString("typeName", "Images")
                .setResultTransformer(Transformers.aliasToBean(Categories.class))
                .list();

        return categories;
    }
    
    public void deleteCategory(int categoryId) {
        Session session = factory.getCurrentSession();

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
    }
    
    public void updateCategory(int categoryId, String categoryName, int typeId) throws CategoryExistsException {
        if (existsCategory(categoryId, categoryName)) {
            throw new CategoryExistsException();
        }

        factory.getCurrentSession()
                .createQuery(
                "UPDATE Categories c SET c.name = :name, c.typeId = :typeId WHERE c.id = :categoryId")
                .setString("name", categoryName)
                .setInteger("typeId", typeId)
                .setInteger("categoryId", categoryId)
                .executeUpdate();
    }
}
