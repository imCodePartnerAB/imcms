package imcode.server.document;

import junit.framework.TestCase;
import imcode.server.user.RoleDomainObject;

public class TestDocumentDomainObject extends TestCase {

    DocumentDomainObject document ;

    protected void setUp() throws Exception {
        super.setUp();    // TODO
        document = new DocumentDomainObject() {
            public int getDocumentTypeId() {
                return 0;  // TODO
            }

            public void accept( DocumentVisitor documentVisitor ) {
                // TODO
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
