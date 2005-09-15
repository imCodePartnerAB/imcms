package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryDatabaseCommand;
import com.imcode.db.commands.UpdateTableWhereColumnEqualsDatabaseCommand;
import com.imcode.db.handlers.ObjectArrayResultSetHandler;
import com.imcode.db.handlers.ObjectFromRowFactory;
import com.imcode.imcms.api.CategoryAlreadyExistsException;
import com.imcode.imcms.db.DatabaseUtils;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper {
    private Database database;
    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    private static final String SQL__CATEGORY__COLUMNS = "categories.category_id, categories.name, categories.description, categories.image";
    public static final String SQL__CATEGORY_TYPE__COLUMNS = "category_types.category_type_id, category_types.name, category_types.max_choices, category_types.inherited";
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
    private static final String SQL__GET_DOCUMENT_CATEGORIES = "SELECT "+SQL__CATEGORY__COLUMNS+", " +
                                                               SQL__CATEGORY_TYPE__COLUMNS
                                                               + " FROM document_categories"
                                                               + " JOIN categories"
                                                               + "  ON document_categories.category_id = categories.category_id"
                                                               + " JOIN category_types"
                                                               + "  ON categories.category_type_id = category_types.category_type_id"
                                                               + " WHERE document_categories.meta_id = ?";

    public CategoryMapper(Database database) {
        this.database = database ;
    }

    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType
    ) {
        String sqlQuery = SQL_GET_ALL_CATEGORIES_OF_TYPE;
        String[] parameters = new String[]{"" + categoryType.getId()};
        String[][] sqlResult = DatabaseUtils.execute2dStringArrayQuery(database, sqlQuery, parameters);
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
        return (CategoryTypeDomainObject[]) DatabaseUtils.executeObjectArrayQuery(database, SQL__GET_ALL_CATEGORY_TYPES, new String[0], new CategoryTypeFromRowFactory());
    }

    public CategoryDomainObject getCategoryByTypeAndName(CategoryTypeDomainObject categoryType, String categoryName) {
        return (CategoryDomainObject) DatabaseUtils.executeObjectQuery(database, SQL__GET_CATEGORY_BY_NAME_AND_CATEGORY_TYPE_ID, new String[]{categoryName, ""+categoryType.getId()}, new CategoryFromRowFactory()) ;
    }

    public CategoryDomainObject getCategoryById( int categoryId ) {
        return (CategoryDomainObject) DatabaseUtils.executeObjectQuery(database, SQL__GET_CATEGORY_BY_ID, new String[]{"" + categoryId}, new CategoryFromRowFactory()) ;
    }

    public CategoryTypeDomainObject getCategoryTypeByName(String categoryTypeName) {
        return (CategoryTypeDomainObject) DatabaseUtils.executeObjectQuery(database, SQL__GET_CATEGORY_TYPE_BY_NAME, new String[] { categoryTypeName }, new CategoryTypeFromRowFactory());
    }

    public CategoryTypeDomainObject getCategoryTypeById(int categoryTypeId) {
        return (CategoryTypeDomainObject) DatabaseUtils.executeObjectQuery(database, SQL__GET_CATEGORY_TYPE_BY_ID, new String[] { "" + categoryTypeId }, new CategoryTypeFromRowFactory());
    }

    public void deleteCategoryTypeFromDb(CategoryTypeDomainObject categoryType) {
        String sqlstr = "delete from category_types where category_type_id = ?";
        String[] params = new String[]{categoryType.getId() + ""};
        DatabaseUtils.executeUpdate(database, sqlstr, params);
    }

    public CategoryTypeDomainObject addCategoryTypeToDb(final CategoryTypeDomainObject categoryType
    ) {
        Number newId = (Number) database.executeCommand(new InsertIntoTableDatabaseCommand("category_types", getColumnNamesAndValuesForCategoryType(categoryType))) ;
        return getCategoryTypeById(newId.intValue());
    }

    private Object[][] getColumnNamesAndValuesForCategoryType(CategoryTypeDomainObject categoryType) {
        return new Object[][] {
                { "name", categoryType.getName() },
                { "max_choices", new Integer(categoryType.getMaxChoices()) },
                { "inherited", new Integer(categoryType.isInherited() ? 1 : 0) },
        };
    }

    public void updateCategoryType(CategoryTypeDomainObject categoryType) {
        database.executeCommand(new UpdateTableWhereColumnEqualsDatabaseCommand("category_types", "category_type_id", new Integer(categoryType.getId()), getColumnNamesAndValuesForCategoryType(categoryType))) ;
    }

    public CategoryDomainObject addCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        Number newId = (Number) database.executeCommand(new InsertIntoTableDatabaseCommand("categories", getColumnNamesAndValuesForCategory(category))) ;
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
        database.executeCommand(new UpdateTableWhereColumnEqualsDatabaseCommand("categories", "category_id", new Integer(category.getId()), getColumnNamesAndValuesForCategory(category))) ;
    }

    public void deleteCategoryFromDb(CategoryDomainObject category) {
        String sqlstr = "delete from categories where category_id = ?";
        String[] params = new String[]{category.getId() + ""};
        DatabaseUtils.executeUpdate(database, sqlstr, params);
    }

    void updateDocumentCategories(DocumentDomainObject document) {
        removeAllCategoriesFromDocument(document);
        CategoryDomainObject[] categories = document.getCategories();
        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject category = categories[i];
            addCategoryToDocument(category, document);
        }
    }

    private void addCategoryToDocument(CategoryDomainObject category, DocumentDomainObject document) {
        int categoryId = category.getId();
        String[] params = new String[]{"" + document.getId(), "" + categoryId};
        DatabaseUtils.executeUpdate(database, "INSERT INTO document_categories (meta_id, category_id) VALUES(?,?)", params);
    }

    public String[] getAllDocumentsOfOneCategory(CategoryDomainObject category) {

        String sqlstr = "select meta_id from document_categories where category_id = ? ";
        String[] params = new String[]{category.getId() + ""};

        return DatabaseUtils.executeStringArrayQuery(database, sqlstr, params);
    }

    private void removeAllCategoriesFromDocument(DocumentDomainObject document) {
        String[] params = new String[]{"" + document.getId()};
        DatabaseUtils.executeUpdate(database, "DELETE FROM document_categories WHERE meta_id = ?", params);
    }

    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category
    ) {
        String[] params = new String[]{document.getId() + "", category.getId() + ""};
        DatabaseUtils.executeUpdate(database, "DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", params);
    }

    void checkMaxDocumentCategoriesOfType(DocumentDomainObject document)
            throws MaxCategoryDomainObjectsOfTypeExceededException {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
            int maxChoices = categoryType.getMaxChoices();
            CategoryDomainObject[] documentCategoriesOfType = document.getCategoriesOfType(categoryType);
            if (UNLIMITED_MAX_CATEGORY_CHOICES != maxChoices && documentCategoriesOfType.length > maxChoices) {
                throw new MaxCategoryDomainObjectsOfTypeExceededException("Document may have at most " + maxChoices
                                                                          + " categories of type '"
                                                                          + categoryType.getName()
                                                                          + "'");
            }
        }
    }

    public void initDocumentCategories(DatabaseConnection connection, DocumentDomainObject document) {
        CategoryDomainObject[] documentCategories = getDocumentCategories(document, connection);

        for ( int i = 0; i < documentCategories.length; i++ ) {
            CategoryDomainObject category = documentCategories[i];
            document.addCategory(category);
        }

    }

    private CategoryDomainObject[] getDocumentCategories(DocumentDomainObject document,
                                                         DatabaseConnection connection) {
        Object result = new SqlQueryDatabaseCommand(SQL__GET_DOCUMENT_CATEGORIES, new String[] {
                "" + document.getId() }, new ObjectArrayResultSetHandler(new CategoryFromRowFactory())).executeOn(connection);
        return (CategoryDomainObject[]) result;
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

    private static class CategoryTypeFromRowFactory implements ObjectFromRowFactory {

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
            return new CategoryTypeDomainObject(id, name, maxChoices, inherited) ;
        }

        public Class getClassOfCreatedObjects() {
            return CategoryTypeDomainObject.class;
        }
    }

    private static class CategoryFromRowFactory implements ObjectFromRowFactory {

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
