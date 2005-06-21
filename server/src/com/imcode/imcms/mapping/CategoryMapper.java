package com.imcode.imcms.mapping;

import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.ConvenienceDatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.MaxCategoryDomainObjectsOfTypeExceededException;
import com.imcode.imcms.api.CategoryAlreadyExistsException;

public class CategoryMapper {
    private Database database;
    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;
    public static final String SQL_GET_ALL_CATEGORIES_OF_TYPE = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
                                                                + "FROM categories\n"
                                                                + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                                                                + "WHERE categories.category_type_id = ?\n"
                                                                + "ORDER BY categories.name";
    public static final String SQL_GET_CATEGORY = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
                                                  + "FROM categories\n"
                                                  + "JOIN category_types\n"
                                                  + "ON categories.category_type_id = category_types.category_type_id\n"
                                                  + "WHERE category_types.name = ?\n"
                                                  + "AND categories.name = ?";
    public static final String SQL__CATEGORY_TYPE__COLUMNS = "category_types.category_type_id, category_types.name, category_types.max_choices, category_types.inherited";

    public CategoryMapper(Database database) {
        this.database = database ;
    }

    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType
    ) {
        String sqlQuery = SQL_GET_ALL_CATEGORIES_OF_TYPE;
        String[] parameters = new String[]{"" + categoryType.getId()};
        String[][] sqlResult = database.execute2dArrayQuery(sqlQuery, parameters);
        CategoryDomainObject[] categoryDomainObjects = new CategoryDomainObject[sqlResult.length];
        for (int i = 0; i < sqlResult.length; i++) {
            int categoryId = Integer.parseInt(sqlResult[i][0]);
            String categoryName = sqlResult[i][1];
            String categoryDescription = sqlResult[i][2];
            String categoryImage = sqlResult[i][3];

            categoryDomainObjects[i] =
            new CategoryDomainObject(categoryId, categoryName, categoryDescription, categoryImage, categoryType);
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
        String sqlQuery = "SELECT " + SQL__CATEGORY_TYPE__COLUMNS + " FROM category_types ORDER BY name";
        String[][] sqlResult = database.execute2dArrayQuery(sqlQuery, new String[0]);

        CategoryTypeDomainObject[] categoryTypes = new CategoryTypeDomainObject[sqlResult.length];
        for ( int i = 0; i < categoryTypes.length; i++ ) {
            CategoryTypeDomainObject categoryType = createCategoryTypeFromSqlResult( sqlResult[i], 0 );
            categoryTypes[i] = categoryType;
        }

        return categoryTypes;
    }

    public CategoryDomainObject getCategory(CategoryTypeDomainObject categoryType, String categoryName
    ) {
        String sqlQuery = SQL_GET_CATEGORY;
        String[] params = new String[]{categoryType.getName(), categoryName};
        String[] sqlResult = database.executeArrayQuery(sqlQuery, params);
        if (0 != sqlResult.length) {
            final int categoryId = Integer.parseInt(sqlResult[0]);
            final String categoryNameFromDb = sqlResult[1];
            final String categoryDescription = sqlResult[2];
            final String categoryImge = sqlResult[3];

            return new CategoryDomainObject(categoryId, categoryNameFromDb, categoryDescription, categoryImge,
                                            categoryType);
        } else {
            return null;
        }
    }

    public CategoryDomainObject getCategoryById( int categoryId ) {
        String sqlQuery = "SELECT categories.name, categories.description, categories.image, "+SQL__CATEGORY_TYPE__COLUMNS +"\n"
                          + "FROM categories\n"
                          + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                          + "WHERE categories.category_id = ?";

        String[] params = new String[]{"" + categoryId};
        String[] categorySqlResult = database.executeArrayQuery(sqlQuery, params);

        if (0 != categorySqlResult.length) {
            String categoryName = categorySqlResult[0];
            String categoryDescription = categorySqlResult[1];
            String categoryImage = categorySqlResult[2];
            CategoryTypeDomainObject categoryType = createCategoryTypeFromSqlResult( categorySqlResult, 3 ) ;

            return new CategoryDomainObject(categoryId, categoryName, categoryDescription, categoryImage, categoryType);
        } else {
            return null;
        }
    }

    public CategoryTypeDomainObject getCategoryType(String categoryTypeName) {
        String sqlStr = "SELECT "+SQL__CATEGORY_TYPE__COLUMNS +"\n"
                        + "FROM category_types\n"
                        + "WHERE category_types.name = ?";
        String[] sqlResult = database.executeArrayQuery(sqlStr, new String[]{categoryTypeName});

        if (null == sqlResult || 0 == sqlResult.length) {
            return null;
        } else {
            return createCategoryTypeFromSqlResult( sqlResult, 0 ) ;
        }
    }

    public CategoryTypeDomainObject getCategoryTypeById(int categoryTypeId) {
        String sqlStr = "select "+SQL__CATEGORY_TYPE__COLUMNS +" from category_types where category_type_id = ? ";
        String[] sqlResult = database.executeArrayQuery(sqlStr, new String[]{"" + categoryTypeId});

        if (null == sqlResult || 0 == sqlResult.length) {
            return null;
        } else {
            return createCategoryTypeFromSqlResult( sqlResult, 0 ) ;
        }
    }

    public void deleteCategoryTypeFromDb(CategoryTypeDomainObject categoryType) {
        String sqlstr = "delete from category_types where category_type_id = ?";
        String[] params = new String[]{categoryType.getId() + ""};
        database.executeUpdateQuery(sqlstr, params);
    }

    public CategoryTypeDomainObject addCategoryTypeToDb(final CategoryTypeDomainObject categoryType
    ) {
        DatabaseCommand addCategoryTypeCommand = new DatabaseCommand() {
            public Object executeOn(DatabaseConnection connection) throws DatabaseException {
                String sqlstr = "insert into category_types (name, max_choices, inherited) values(?,?,?)";
                String[] params = new String[]{categoryType.getName(), categoryType.getMaxChoices() + "", (categoryType.isInherited() ? "1" : "0")};
                return connection.executeUpdateAndGetGeneratedKey(sqlstr, params) ;
            }
        };
        Number newId = (Number) database.executeCommand(addCategoryTypeCommand) ;
        return getCategoryTypeById(newId.intValue());
    }

    public void updateCategoryType(CategoryTypeDomainObject categoryType) {
        String sqlstr = "update category_types set name= ?, max_choices= ?  where category_type_id = ? ";
        String[] params = new String[]{
            categoryType.getName(), categoryType.getMaxChoices() + "",
            categoryType.getId() + ""
        };
        database.executeUpdateQuery(sqlstr, params);
    }

    public CategoryDomainObject addCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        String sqlstr = "insert into categories  (category_type_id, name, description, image) values(?,?,?,?) SELECT @@IDENTITY";
        String[] params = new String[]{
            category.getType().getId() + "", category.getName(),
            category.getDescription(), category.getImageUrl()
        };
        String newId = database.executeStringQuery(sqlstr, params);
        int categoryId = Integer.parseInt(newId);
        category.setId(categoryId);
        return getCategoryById(categoryId);
    }

    public void updateCategory(CategoryDomainObject category) {
        String sqlstr = "update categories set category_type_id = ?, name= ?, description = ?, image = ?  where category_id = ? ";
        String[] params = new String[]{
            category.getType().getId() + "", category.getName(), category.getDescription(),
            category.getImageUrl(),
            category.getId() + ""
        };
        database.executeUpdateQuery(sqlstr, params);
    }

    public void deleteCategoryFromDb(CategoryDomainObject category) {
        String sqlstr = "delete from categories where category_id = ?";
        String[] params = new String[]{category.getId() + ""};
        database.executeUpdateQuery(sqlstr, params);
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
        database.executeUpdateQuery("INSERT INTO document_categories (meta_id, category_id) VALUES(?,?)", params);
    }

    public String[] getAllDocumentsOfOneCategory(CategoryDomainObject category) {

        String sqlstr = "select meta_id from document_categories where category_id = ? ";
        String[] params = new String[]{category.getId() + ""};
        String[] res = database.executeArrayQuery(sqlstr, params);

        return res;
    }

    private void removeAllCategoriesFromDocument(DocumentDomainObject document) {
        String[] params = new String[]{"" + document.getId()};
        database.executeUpdateQuery("DELETE FROM document_categories WHERE meta_id = ?", params);
    }

    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category
    ) {
        String[] params = new String[]{document.getId() + "", category.getId() + ""};
        database.executeUpdateQuery("DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", params);
    }

    static CategoryTypeDomainObject createCategoryTypeFromSqlResult( String[] sqlRow, int offset ) {
        int categoryTypeId = Integer.parseInt( sqlRow[offset+0] );
        String typeName = sqlRow[offset+1];
        int maxChoices = Integer.parseInt( sqlRow[offset+2] );
        boolean inherited = 0 != Integer.parseInt( sqlRow[offset+3] ) ;
        CategoryTypeDomainObject categoryTypeDomainObject = new CategoryTypeDomainObject( categoryTypeId, typeName, maxChoices, inherited );
        return categoryTypeDomainObject;
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

    public static void initDocumentCategories(ConvenienceDatabaseConnection connection, DocumentDomainObject document) {
        String[][] categories = connection.execute2dArrayQuery( "SELECT categories.category_id, categories.name, categories.image, categories.description, "+
                                                                SQL__CATEGORY_TYPE__COLUMNS
                                                                + " FROM document_categories"
                                                                + " JOIN categories"
                                                                + "  ON document_categories.category_id = categories.category_id"
                                                                + " JOIN category_types"
                                                                + "  ON categories.category_type_id = category_types.category_type_id"
                                                                + " WHERE document_categories.meta_id = ?",
                                                                new String[]{"" + document.getId()} );
        for (int i = 0; i < categories.length; i++) {
            String[] categoryArray = categories[i];

            int categoryId = Integer.parseInt(categoryArray[0]);
            String categoryName = categoryArray[1];
            String categoryImage = categoryArray[2];
            String categoryDescription = categoryArray[3];

            CategoryTypeDomainObject categoryType = createCategoryTypeFromSqlResult( categoryArray, 4 ) ;
            CategoryDomainObject category = new CategoryDomainObject(categoryId, categoryName, categoryDescription,
                                                                     categoryImage, categoryType);
            document.addCategory(category);
        }

    }
}
