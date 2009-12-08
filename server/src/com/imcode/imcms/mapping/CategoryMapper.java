package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.UpdateTableWhereColumnEqualsDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.db.handlers.SingleObjectHandler;
import com.imcode.db.handlers.ObjectArrayHandler;
import com.imcode.imcms.api.CategoryAlreadyExistsException;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;
import imcode.util.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.collections.map.LRUMap;

public class CategoryMapper {

    private static final int CACHE_SIZE = 100;

    private Database database;

    private Map categoryCache = Collections.synchronizedMap(new LRUMap(CACHE_SIZE));
    private Map categoryTypeCache = Collections.synchronizedMap(new LRUMap(CACHE_SIZE));
    private Map categoryTypeByNameCache = Collections.synchronizedMap(new LRUMap(CACHE_SIZE));
    private Map categoryByTypeAndNameCache = Collections.synchronizedMap(new LRUMap(CACHE_SIZE));

    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    private static final String SQL__CATEGORY__COLUMNS = "categories.category_id, categories.name, categories.description, categories.image";
    public static final String SQL__CATEGORY_TYPE__COLUMNS = "category_types.category_type_id, category_types.name, category_types.max_choices, category_types.inherited, category_types.is_image_archive";
    public static final String SQL_GET_ALL_CATEGORIES_OF_TYPE = "SELECT "+SQL__CATEGORY__COLUMNS+"\n"
                                                                + "FROM categories\n"
                                                                + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                                                                + "WHERE categories.category_type_id = ?\n"
                                                                + "ORDER BY categories.name";
    public static final String SQL__GET_CATEGORY_BY_NAME_AND_CATEGORY_TYPE_ID = "SELECT " + SQL__CATEGORY__COLUMNS +", "
                                                                                + SQL__CATEGORY_TYPE__COLUMNS + "\n"
                                                                                + "FROM categories\n"
                                                                                + "JOIN category_types\n"
                                                                                + "ON categories.category_type_id = category_types.category_type_id\n"
                                                                                + "WHERE categories.name = ?\n"
                                                                                + "AND category_types.category_type_id = ?";

    private static final String SQL__GET_ALL_CATEGORY_TYPES = "SELECT " + SQL__CATEGORY_TYPE__COLUMNS + " FROM category_types ORDER BY name";
    private static final String SQL__GET_CATEGORY_TYPE_BY_NAME = "SELECT "+SQL__CATEGORY_TYPE__COLUMNS +"\n"
                                                                 + "FROM category_types\n"
                                                                 + "WHERE category_types.name = ?";
    private static final String SQL__GET_CATEGORY_TYPE_BY_ID = "select "+SQL__CATEGORY_TYPE__COLUMNS +" from category_types where category_type_id = ? ";
    private static final String SQL__GET_CATEGORY_BY_ID = "SELECT "+SQL__CATEGORY__COLUMNS+", "+
                                                          SQL__CATEGORY_TYPE__COLUMNS +"\n"
                                                          + "FROM categories\n"
                                                          + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                                                          + "WHERE categories.category_id = ?";
    static final String SQL__GET_DOCUMENT_CATEGORIES = "SELECT meta_id, category_id"
                                                       + " FROM document_categories"
                                                       + " WHERE meta_id ";
    private static final SingleObjectHandler SINGLE_CATEGORY_TYPE_HANDLER = new SingleObjectHandler(new CategoryTypeFromRowFactory());
    private static final SingleObjectHandler SINGLE_CATEGORY_HANDLER = new SingleObjectHandler(new CategoryFromRowFactory());
    private static final ObjectArrayHandler CATEGORY_TYPE_ARRAY_HANDLER = new ObjectArrayHandler(new CategoryTypeFromRowFactory());

    public CategoryMapper(Database database) {
        this.database = database ;
    }

    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType
    ) {
        String sqlQuery = SQL_GET_ALL_CATEGORIES_OF_TYPE;
        String[] parameters = new String[]{"" + categoryType.getId()};
        String[][] sqlResult = (String[][]) database.execute(new SqlQueryCommand(sqlQuery, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        CategoryDomainObject[] categoryDomainObjects = new CategoryDomainObject[sqlResult.length];
        for (int i = 0; i < sqlResult.length; i++) {
            int categoryId = Integer.parseInt(sqlResult[i][0]);
            String categoryName = sqlResult[i][1];
            String categoryDescription = sqlResult[i][2];
            String categoryImage = sqlResult[i][3];

            categoryDomainObjects[i] = new CategoryDomainObject(categoryId, categoryName, categoryDescription, categoryImage, categoryType);
        }
        return categoryDomainObjects;
    }

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

    public CategoryTypeDomainObject[] getAllCategoryTypes() {
        String[] parameters = new String[0];
        return (CategoryTypeDomainObject[]) database.execute(new SqlQueryCommand(SQL__GET_ALL_CATEGORY_TYPES, parameters, CATEGORY_TYPE_ARRAY_HANDLER));
    }

    private String getCacheKeyByTypeAndName(CategoryTypeDomainObject categoryType, String categoryName) {
        return categoryType.getId() + "-" + categoryName;
    }

    public CategoryDomainObject getCategoryByTypeAndName(CategoryTypeDomainObject categoryType, String categoryName) {
        String cacheKey = getCacheKeyByTypeAndName(categoryType, categoryName);
        CategoryDomainObject category = (CategoryDomainObject)categoryByTypeAndNameCache.get(cacheKey);

        if (category == null) {
            String[] parameters = new String[]{categoryName, ""+categoryType.getId()};
            category = (CategoryDomainObject) database.execute(new SqlQueryCommand(SQL__GET_CATEGORY_BY_NAME_AND_CATEGORY_TYPE_ID, parameters, SINGLE_CATEGORY_HANDLER)) ;
            categoryByTypeAndNameCache.put(cacheKey, category);
        }

        return category;
    }

    public CategoryDomainObject getCategoryById( int categoryId ) {
        CategoryDomainObject category = (CategoryDomainObject)categoryCache.get(categoryId);
        if (category == null) {
            String[] parameters = new String[]{"" + categoryId};
            category = (CategoryDomainObject) database.execute(new SqlQueryCommand(SQL__GET_CATEGORY_BY_ID, parameters, SINGLE_CATEGORY_HANDLER)) ;
            categoryCache.put(categoryId, category);
        }

        return category;
    }

    public CategoryTypeDomainObject getCategoryTypeByName(String categoryTypeName) {
        CategoryTypeDomainObject categoryType = (CategoryTypeDomainObject) categoryTypeByNameCache.get(categoryTypeName);
        if (categoryType == null) {
            String[] parameters = new String[] { categoryTypeName };
            categoryType = (CategoryTypeDomainObject) database.execute(new SqlQueryCommand(SQL__GET_CATEGORY_TYPE_BY_NAME, parameters, SINGLE_CATEGORY_TYPE_HANDLER));
            categoryTypeByNameCache.put(categoryTypeName, categoryType);
        }

        return categoryType;
    }

    public CategoryTypeDomainObject getCategoryTypeById(int categoryTypeId) {
        CategoryTypeDomainObject categoryType = (CategoryTypeDomainObject)categoryTypeCache.get(categoryTypeId);
        if (categoryType == null) {
            String[] parameters = new String[] { "" + categoryTypeId };
            categoryType = (CategoryTypeDomainObject) database.execute(new SqlQueryCommand(SQL__GET_CATEGORY_TYPE_BY_ID, parameters, SINGLE_CATEGORY_TYPE_HANDLER));
            categoryTypeCache.put(categoryTypeId, categoryType);
        }
        return categoryType;
    }

    public void deleteCategoryTypeFromDb(CategoryTypeDomainObject categoryType) {
        String sqlstr = "delete from category_types where category_type_id = ?";
        String[] params = new String[]{categoryType.getId() + ""};
        database.execute(new SqlUpdateCommand(sqlstr, params));
        
        invalidateCategoryType(categoryType);
    }

    public CategoryTypeDomainObject addCategoryTypeToDb(final CategoryTypeDomainObject categoryType
    ) {
        Number newId = (Number) database.execute(new InsertIntoTableDatabaseCommand("category_types", getColumnNamesAndValuesForCategoryType(categoryType))) ;
        return getCategoryTypeById(newId.intValue());
    }

    private Object[][] getColumnNamesAndValuesForCategoryType(CategoryTypeDomainObject categoryType) {
        return new Object[][] {
                { "name", categoryType.getName() },
                { "max_choices", new Integer(categoryType.getMaxChoices()) },
                { "inherited", new Integer(categoryType.isInherited() ? 1 : 0) },
                { "is_image_archive", new Integer(categoryType.isImageArchive() ? 1 : 0) }, 
        };
    }

    public void updateCategoryType(CategoryTypeDomainObject categoryType) {
        database.execute(new UpdateTableWhereColumnEqualsDatabaseCommand("category_types", "category_type_id", new Integer(categoryType.getId()), getColumnNamesAndValuesForCategoryType(categoryType))) ;
        invalidateCategoryType(categoryType);
    }

    public CategoryDomainObject addCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        Number newId = (Number) database.execute(new InsertIntoTableDatabaseCommand("categories", getColumnNamesAndValuesForCategory(category))) ;
        int categoryId = newId.intValue();
        category.setId(categoryId);
        return getCategoryById(categoryId);
    }

    private Object[][] getColumnNamesAndValuesForCategory(CategoryDomainObject category) {
        return new Object[][] {
                { "category_type_id", new Integer(category.getType().getId()) },
                { "name", category.getName() },
                { "description", category.getDescription() },
                { "image", category.getImageUrl() }
        };
    }

    public void updateCategory(CategoryDomainObject category) {
        database.execute(new UpdateTableWhereColumnEqualsDatabaseCommand("categories", "category_id", new Integer(category.getId()), getColumnNamesAndValuesForCategory(category))) ;
        invalidateCategory(category);
    }

    public void deleteCategoryFromDb(CategoryDomainObject category) {
        String sqlstr = "delete from categories where category_id = ?";
        String[] params = new String[]{category.getId() + ""};
        database.execute(new SqlUpdateCommand(sqlstr, params));
        invalidateCategory(category);
    }

    void updateDocumentCategories(DocumentDomainObject document) {
        Set categoryIds = document.getCategoryIds();
        removeAllCategoriesFromDocument(document);
        for ( Iterator iterator = categoryIds.iterator(); iterator.hasNext(); ) {
            Integer categoryId = (Integer) iterator.next();
            addCategoryToDocument(categoryId.intValue(), document);
        }
    }

    private void addCategoryToDocument(int categoryId, DocumentDomainObject document) {
        String[] params = new String[]{"" + document.getId(), "" + categoryId};
        database.execute(new SqlUpdateCommand("INSERT INTO document_categories (meta_id, category_id) VALUES(?,?)", params));
    }

    public String[] getAllDocumentsOfOneCategory(CategoryDomainObject category) {

        String sqlstr = "select meta_id from document_categories where category_id = ? ";
        String[] params = new String[]{category.getId() + ""};

        return (String[]) database.execute(new SqlQueryCommand(sqlstr, params, Utility.STRING_ARRAY_HANDLER));
    }

    private void removeAllCategoriesFromDocument(DocumentDomainObject document) {
        String[] params = new String[]{"" + document.getId()};
        database.execute(new SqlUpdateCommand("DELETE FROM document_categories WHERE meta_id = ?", params));
    }

    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category
    ) {
        String[] params = new String[]{document.getId() + "", category.getId() + ""};
        database.execute(new SqlUpdateCommand("DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", params));
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

    public Set getCategories(Collection categoryIds) {
        Set categories = new HashSet() ;
        for ( Iterator iterator = categoryIds.iterator(); iterator.hasNext(); ) {
            Integer categoryId = (Integer) iterator.next();
            CategoryDomainObject category = getCategoryById(categoryId.intValue()) ;
            if (null != category) {
                categories.add(category) ;
            }
        }
        return categories;
    }

    public Set getCategoriesOfType(CategoryTypeDomainObject categoryType, Set categoryIds) {
        Set categories = getCategories(categoryIds) ;
        Set categoriesOfType = new HashSet();
        for ( Iterator iterator = categories.iterator(); iterator.hasNext(); ) {
            CategoryDomainObject category = (CategoryDomainObject) iterator.next();
            if ( categoryType.equals( category.getType() ) ) {
                categoriesOfType.add( category );
            }
        }
        return categoriesOfType ;
    }

    private void invalidateCategory(CategoryDomainObject category) {
        categoryCache.remove(category.getId());
        categoryByTypeAndNameCache.remove(getCacheKeyByTypeAndName(category.getType(), category.getName()));
    }

    private void invalidateCategoryType(CategoryTypeDomainObject categoryType) {
        categoryTypeCache.remove(categoryType.getId());
        categoryTypeByNameCache.remove(categoryType.getName());
    }

    private static class CategoryTypeFromRowFactory implements RowTransformer {

        private final int offset;

        CategoryTypeFromRowFactory() {
            this(0) ;
        }

        CategoryTypeFromRowFactory(int offset) {
            this.offset = offset;
        }

        public Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
            int id = resultSet.getInt(offset+1);
            String name = resultSet.getString(offset+2);
            int maxChoices = resultSet.getInt(offset+3);
            boolean inherited = 0 != resultSet.getInt(offset+4);
            boolean imageArchive = 0 != resultSet.getInt(offset+5);
            return new CategoryTypeDomainObject(id, name, maxChoices, inherited, imageArchive);
        }

        public Class getClassOfCreatedObjects() {
            return CategoryTypeDomainObject.class;
        }
    }

    private static class CategoryFromRowFactory implements RowTransformer {

        public Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
            int categoryId = resultSet.getInt(1);
            String categoryName = resultSet.getString(2);
            String categoryDescription = resultSet.getString(3);
            String categoryImage = resultSet.getString(4);

            CategoryTypeDomainObject categoryType = (CategoryTypeDomainObject) new CategoryTypeFromRowFactory(4).createObjectFromResultSetRow(resultSet) ;

            return new CategoryDomainObject(categoryId, categoryName, categoryDescription, categoryImage, categoryType);
        }

        public Class getClassOfCreatedObjects() {
            return CategoryDomainObject.class ;
        }
    }

}
