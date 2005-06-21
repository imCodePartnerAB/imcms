package com.imcode.imcms.api;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class TestDocument extends TestCase{

    public void testGetAllRolesMappedToPermissions() {
        final MockContentManagementSystem contentManagementSystem = new MockContentManagementSystem();
        contentManagementSystem.setCurrentInternalUser(new UserDomainObject());
        final TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        textDocument.setPermissionSetIdForRole(RoleDomainObject.USERADMIN, 0);
        textDocument.setPermissionSetIdForRole(RoleDomainObject.USERS, 0);
        textDocument.setPermissionSetIdForRole(new RoleDomainObject(3, "test", 0), 0);
        Document doc = new TextDocument(textDocument, contentManagementSystem);

        final Map allRolesMappedToPermissions = doc.getRolesMappedToPermissions();
        Set roles = allRolesMappedToPermissions.keySet();
        assertTrue(CollectionUtils.exists(roles, new RoleNameEqualsPredicate(RoleDomainObject.USERADMIN.getName()))) ;
        assertTrue(CollectionUtils.exists(roles, new RoleNameEqualsPredicate(RoleDomainObject.USERS.getName()))) ;
        assertTrue(CollectionUtils.exists(roles, new RoleNameEqualsPredicate("test"))) ;
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

    private static class RoleNameEqualsPredicate implements Predicate {
        private final String roleName;

        public RoleNameEqualsPredicate(String roleName) {
            this.roleName = roleName;
        }

        public boolean evaluate(Object o) {
            return ((Role)o).getName().equals(roleName) ;
        }
    }
}
