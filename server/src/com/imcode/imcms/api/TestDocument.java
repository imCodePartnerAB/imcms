package com.imcode.imcms.api;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.util.*;

public class TestDocument extends TestCase{

    public void testGetAllRolesMappedToPermissions() throws NoPermissionException {
        final MockContentManagementSystem contentManagementSystem = new MockContentManagementSystem();
        contentManagementSystem.setCurrentInternalUser(new UserDomainObject());
        final TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        textDocument.setPermissionSetIdForRole(RoleDomainObject.USERADMIN, 0);
        textDocument.setPermissionSetIdForRole(RoleDomainObject.USERS, 0);
        textDocument.setPermissionSetIdForRole(new RoleDomainObject(3, "test", 0), 0);
        Document doc = new TextDocument(textDocument, contentManagementSystem);

        final Map allRolesMappedToPermissions = doc.getAllRolesMappedToPermissions();
        final Iterator iterator = allRolesMappedToPermissions.keySet().iterator();
        String roleName = (String) iterator.next();
        assertEquals(RoleDomainObject.USERADMIN.getName(), roleName);
        roleName = (String) iterator.next();
        assertEquals("Users", roleName);
        roleName = (String) iterator.next();
        assertEquals("test", roleName);

    }

    public void testSearchDisabled() {
        TextDocumentDomainObject documentDO = new TextDocumentDomainObject();
        TextDocument document = new TextDocument(documentDO, null);
        assertFalse(document.isSearchDisabled()) ;
        documentDO.setSearchDisabled(true);
        assertTrue(document.isSearchDisabled()) ;
        document.setSearchDisabled(false);
        assertFalse(documentDO.isSearchDisabled()) ;
    }

    public void testKeywords() {
        TextDocumentDomainObject documentDO = new TextDocumentDomainObject();
        TextDocument document = new TextDocument(documentDO, null);
        Set keywords = new HashSet();
        keywords.add("foo") ;
        documentDO.setKeywords(keywords);
        assertTrue(document.getKeywords().contains("foo")) ;
    }

    public void testLinkableByOtherUsers() {
        TextDocumentDomainObject documentDO = new TextDocumentDomainObject();
        TextDocument document = new TextDocument(documentDO, null);
        assertFalse(document.isLinkableByOtherUsers()) ;
        documentDO.setLinkableByOtherUsers(true);
        assertTrue(document.isLinkableByOtherUsers()) ;
        document.setLinkableByOtherUsers(false);
        assertFalse(documentDO.isLinkableByOtherUsers()) ;
    }
}
