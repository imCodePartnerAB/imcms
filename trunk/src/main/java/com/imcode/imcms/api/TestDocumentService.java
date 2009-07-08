package com.imcode.imcms.api;

import imcode.server.MockImcmsServices;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.MockRoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.imcode.db.mock.MockDatabase;
import com.imcode.db.mock.MockResultSet;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.MockDocumentIndex;

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
