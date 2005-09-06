package com.imcode.imcms.api;

import imcode.server.MockImcmsServices;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.MockRoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestDocument extends TestCase{

    public void testGetAllRolesMappedToPermissions() {
        final MockContentManagementSystem contentManagementSystem = new MockContentManagementSystem();
        contentManagementSystem.setCurrentInternalUser(new UserDomainObject());
        MockImcmsServices imcmsServices = new MockImcmsServices();
        contentManagementSystem.setInternal(imcmsServices);
        imcmsServices.setRoleGetter(new MockRoleGetter()) ;
        final TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        textDocument.setDocumentPermissionSetTypeForRoleId(RoleId.USERADMIN, DocumentPermissionSetTypeDomainObject.FULL);
        textDocument.setDocumentPermissionSetTypeForRoleId(RoleId.USERS, DocumentPermissionSetTypeDomainObject.FULL);
        textDocument.setDocumentPermissionSetTypeForRoleId(new RoleId(3), DocumentPermissionSetTypeDomainObject.FULL);
        Document doc = new TextDocument(textDocument, contentManagementSystem);

        final Map allRolesMappedToPermissions = doc.getRolesMappedToPermissions();
        Set roles = allRolesMappedToPermissions.keySet();
        assertTrue(CollectionUtils.exists(roles, new RoleIdEqualsPredicate(RoleId.USERADMIN))) ;
        assertTrue(CollectionUtils.exists(roles, new RoleIdEqualsPredicate(RoleId.USERS))) ;
        assertTrue(CollectionUtils.exists(roles, new RoleIdEqualsPredicate(new RoleId(3)))) ;
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

    private static class RoleIdEqualsPredicate implements Predicate {
        private final RoleId roleId;

        RoleIdEqualsPredicate(RoleId roleName) {
            this.roleId = roleName;
        }

        public boolean evaluate(Object o) {
            return ((Role)o).getId() == roleId.intValue() ;
        }
    }

}
