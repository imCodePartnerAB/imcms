package imcode.server.document;

import junit.framework.TestCase;

public class TestDocumentPermissionSetDomainObject extends TestCase {

    private static final DocumentPermission PERMISSION = new DocumentPermission("test");
    DocumentPermissionSetDomainObject documentPermissionSet = new DocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_1) {
        public void setFromBits(
                int permissionBits) {
        }
    };

    public void testPermissionSetNone() {
        DocumentPermissionSetDomainObject.NONE.setPermission(PERMISSION, true);
        assertFalse(DocumentPermissionSetDomainObject.NONE.hasPermission(PERMISSION));
    }

    public void testPermissionSetRead() {
        DocumentPermissionSetDomainObject.READ.setPermission(PERMISSION, true);
        assertFalse(DocumentPermissionSetDomainObject.READ.hasPermission(PERMISSION));
    }

    public void testPermissionSetFull() {
        DocumentPermissionSetDomainObject full = DocumentPermissionSetDomainObject.FULL;
        full.setPermission(PERMISSION, false);
        assertTrue(full.hasPermission(PERMISSION));
    }

    public void testSetPermissionTrue() throws Exception {
        documentPermissionSet.setPermission(PERMISSION, true);
        assertTrue(documentPermissionSet.hasPermission(PERMISSION));
    }

    public void testSetPermissionFalse() throws Exception {
        documentPermissionSet.setPermission(PERMISSION, false);
        assertFalse(documentPermissionSet.hasPermission(PERMISSION));
    }

    public void testHasPermission() throws Exception {
        assertFalse(documentPermissionSet.hasPermission(PERMISSION));
    }

}