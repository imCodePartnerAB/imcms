package com.imcode.imcms.api;

import com.imcode.db.mock.MockDatabase;
import com.imcode.db.mock.MockResultSet;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.MockDocumentIndex;
import imcode.server.MockImcmsServices;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.MockRoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class TestDocumentService extends TestCase {

    private DocumentService documentService;
    private MockDatabase database;
    private User user;
    private MockImcmsServices imcmsServices;
    private MockContentManagementSystem contentManagementSystem;

    public void setUp() throws Exception {
        super.setUp();
        contentManagementSystem = new MockContentManagementSystem();
        user = new User(new UserDomainObject());
        contentManagementSystem.setCurrentUser( user );
        imcmsServices = new MockImcmsServices();
        imcmsServices.setRoleGetter(new MockRoleGetter());
        database = new MockDatabase();
        DocumentMapper documentMapper = new DocumentMapper(imcmsServices, database);
        documentMapper.setDocumentIndex(new MockDocumentIndex());
        imcmsServices.setDocumentMapper(documentMapper) ;
        imcmsServices.setCategoryMapper(documentMapper.getCategoryMapper());
        contentManagementSystem.setInternal( imcmsServices );
        documentService = new DocumentService(contentManagementSystem) ;
    }

    public void testSaveCategory() throws CategoryAlreadyExistsException, NoPermissionException {
        ResultSet allCategoryTypesResult = new MockResultSet(new Object[][] { { new Integer(1), "test", new Integer(0), new Integer(0), new Integer(0) } });
        database.addExpectedSqlCall( new MockDatabase.MatchesRegexSqlCallPredicate( "SELECT category_types.category_type_id"), allCategoryTypesResult );
        CategoryType categoryType = documentService.getAllCategoryTypes()[0] ;
        assertEquals( false, categoryType.isInherited()) ;
        String categoryName = "name";
        Category category = new Category( categoryName, categoryType );
        category.setDescription( "description" );
        category.setImage( "image" );
        user.addRole( new Role( imcmsServices.getRoleGetter().getRole(RoleId.SUPERADMIN) ) );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "categories" ), new Integer(1) );
        documentService.saveCategory(category);
        database.assertExpectedSqlCalls();

        ResultSet categoryResult = new MockResultSet(new Object[][] { { new Integer(1), category.getName(), category.getDescription(), category.getImage(), new Integer(categoryType.getId()), categoryType.getName(), new Integer(categoryType.getInternal().getMaxChoices()), new Integer(categoryType.isInherited() ? 1 : 0), new Integer(categoryType.isImageArchive() ? 1 : 0)}});
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

    public void testApiWrappingList() {
        List list = new ArrayList() ;
        DocumentService.ApiDocumentWrappingList apiDocumentWrappingList = new DocumentService.ApiDocumentWrappingList(list, contentManagementSystem);
        list.add(new TextDocumentDomainObject()) ;
        assertNotNull(apiDocumentWrappingList.get(0)) ;
        TextDocument document = new TextDocument(new TextDocumentDomainObject(), contentManagementSystem);
        apiDocumentWrappingList.set(0, document) ;
        assertNotNull(apiDocumentWrappingList.get(0)) ;
        assertEquals(document, apiDocumentWrappingList.remove(0)) ;
        assertTrue(list.isEmpty()) ;
    }
}
