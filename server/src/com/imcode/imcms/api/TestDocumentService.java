package com.imcode.imcms.api;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DefaultDocumentMapper;
import imcode.server.Config;
import imcode.server.MockImcmsServices;
import imcode.server.db.impl.MockDatabase;
import imcode.server.db.impl.MockResultSet;
import imcode.server.user.MockRoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.sql.ResultSet;

public class TestDocumentService extends TestCase {

    private DocumentService documentService;
    private MockDatabase database;
    private User user;
    private MockImcmsServices imcmsServices;

    public void setUp() throws Exception {
        super.setUp();
        MockContentManagementSystem contentManagementSystem = new MockContentManagementSystem();
        user = new User(new UserDomainObject());
        contentManagementSystem.setCurrentUser( user );
        imcmsServices = new MockImcmsServices();
        imcmsServices.setRoleGetter(new MockRoleGetter());
        database = new MockDatabase();
        DefaultDocumentMapper documentMapper = new DefaultDocumentMapper(imcmsServices, database, null, null, null, null, new Config(), new CategoryMapper(database));
        imcmsServices.setDocumentMapper(documentMapper) ;
        imcmsServices.setCategoryMapper(new CategoryMapper(database));
        contentManagementSystem.setInternal( imcmsServices );
        documentService = new DocumentService(contentManagementSystem) ;
    }

    public void testSaveCategory() throws CategoryAlreadyExistsException, NoPermissionException {
        ResultSet allCategoryTypesResult = new MockResultSet(new Object[][] { { new Integer(1), "test", new Integer(0), new Integer(0) } });
        database.addExpectedSqlCall( new MockDatabase.MatchesRegexSqlCallPredicate( "SELECT category_types.category_type_id"), allCategoryTypesResult );
        CategoryType categoryType = documentService.getAllCategoryTypes()[0] ;
        assertEquals( false, categoryType.isInherited()) ;
        String categoryName = "name";
        Category category = new Category( categoryName, categoryType );
        category.setDescription( "description" );
        category.setImage( "image" );
        try {
            documentService.saveCategory(category);
            fail() ;
        } catch( NoPermissionException e ) {}
        user.addRole( new Role( imcmsServices.getRoleGetter().getRole(RoleId.SUPERADMIN) ) );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "categories" ), new Integer(1) );
        documentService.saveCategory(category);
        database.assertExpectedSqlCalls();

        ResultSet categoryResult = new MockResultSet(new Object[][] { { new Integer(1), category.getName(), category.getDescription(), category.getImage(), new Integer(categoryType.getId()), categoryType.getName(), new Integer(categoryType.getInternal().getMaxChoices()), new Integer(categoryType.isInherited() ? 1 : 0)}});
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate(CategoryMapper.SQL__GET_CATEGORY_BY_NAME_AND_CATEGORY_TYPE_ID), categoryResult );
        Category otherCategory = new Category( categoryName, categoryType );
        try {
            documentService.saveCategory( otherCategory );
            fail() ;
        } catch ( CategoryAlreadyExistsException e ) {
        }
        database.assertExpectedSqlCalls();

        String otherName = "other name";
        category.setName( otherName );
        category.setDescription( "other description");
        category.setImage( "other image");
        documentService.saveCategory( category );
        database.assertCalled( new MockDatabase.UpdateTableSqlCallPredicate( "categories", otherName ));
    }

}
