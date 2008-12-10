package com.imcode.imcms.mapping;

import com.imcode.db.mock.MockDatabase;
import com.imcode.db.mock.MockResultSet;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class TestTextDocumentInitializer extends TestCase {

    public void testInitDocumentsMenuItems() throws Exception {
        List documentIds = Arrays.asList(new Integer[] {
                new Integer(1001),
                new Integer(1002),
        });
        MockDatabase database = new MockDatabase();
        database.addExpectedSqlCall(new MockDatabase.StartsWithSqlCallPredicate(TextDocumentInitializer.SQL_GET_MENU_ITEMS),
                                    new MockResultSet(new Object[][] { 
                                            { new Integer(1001), new Integer(1), new Integer(1), new Integer(1), new Integer(1002), new Integer(500), ""},
                                            { new Integer(1001), new Integer(1), new Integer(1), new Integer(1), new Integer(1003), new Integer(510), ""},
                                            { new Integer(1001), new Integer(1), new Integer(1), new Integer(1), new Integer(1005), new Integer(500), ""},
                                            { new Integer(1001), new Integer(2), new Integer(2), new Integer(1), new Integer(1002), new Integer(500), ""},
                                    }));

        MapDocumentGetter documentGetter = new MapDocumentGetter(new DocumentDomainObject[] {
                new TextDocumentDomainObject(1001),
                new TextDocumentDomainObject(1002),
                new TextDocumentDomainObject(1003),
                new TextDocumentDomainObject(1004),
        });
        TextDocumentInitializer initializer = new TextDocumentInitializer(database, documentGetter, documentIds);
        TextDocumentDomainObject textDocument = (TextDocumentDomainObject) documentGetter.getDocument(new Integer(1001));
        initializer.initialize(textDocument);
        assertEquals(2, textDocument.getMenus().size()) ;

        MenuDomainObject menu1 = textDocument.getMenu(1) ;
        assertEquals(2, menu1.getMenuItems().length) ;

        MenuDomainObject menu2 = textDocument.getMenu(2) ;
        assertEquals(1, menu2.getMenuItems().length) ;
        
    }
}