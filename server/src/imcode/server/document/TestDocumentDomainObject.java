package imcode.server.document;

import imcode.server.user.RoleDomainObject;
import junit.framework.TestCase;

public class TestDocumentDomainObject extends TestCase {

    DocumentDomainObject document ;

    protected void setUp() throws Exception {
        super.setUp();
        document = new DocumentDomainObject() {
            public DocumentTypeDomainObject getDocumentType() {
                return null;
            }
            public void accept( DocumentVisitor documentVisitor ) {
            }
        };
    }

    public void testDeepCloneRoles() throws CloneNotSupportedException {
        RoleDomainObject role = RoleDomainObject.SUPERADMIN;
        assertEquals( document.getPermissionSetIdForRole(role), DocumentPermissionSetDomainObject.TYPE_ID__NONE );
        DocumentDomainObject clone = (DocumentDomainObject)document.clone() ;
        clone.setPermissionSetIdForRole( role, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        assertEquals( clone.getPermissionSetIdForRole( role ), DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        assertEquals( document.getPermissionSetIdForRole( role ), DocumentPermissionSetDomainObject.TYPE_ID__NONE );
    }

}
