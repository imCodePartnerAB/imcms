package imcode.server.document;

import imcode.server.user.RoleDomainObject;
import junit.framework.TestCase;

import java.util.Date;

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

    public void testGetPublicationStatus() throws Exception {
        assertEquals( DocumentDomainObject.PublicationStatus.NEW, document.getPublicationStatus() ) ;
        document.setStatus( DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED );
        assertEquals( DocumentDomainObject.PublicationStatus.DISAPPROVED, document.getPublicationStatus() );
        document.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
        assertEquals( DocumentDomainObject.PublicationStatus.APPROVED, document.getPublicationStatus() );
        document.setPublicationStartDatetime( new Date( 0 ) );
        assertEquals( DocumentDomainObject.PublicationStatus.PUBLISHED, document.getPublicationStatus() );
        document.setArchivedDatetime( new Date( 0 ) );
        assertEquals( DocumentDomainObject.PublicationStatus.ARCHIVED, document.getPublicationStatus() );
        document.setPublicationEndDatetime( new Date( 0 ) );
        assertEquals( DocumentDomainObject.PublicationStatus.UNPUBLISHED, document.getPublicationStatus() );
    }
}
