package imcode.server.document;

import imcode.server.user.RoleDomainObject;
import junit.framework.TestCase;

import java.util.Date;

import com.imcode.imcms.api.Document;

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
        assertEquals( DocumentDomainObject.LifeCyclePhase.NEW, document.getLifeCyclePhase() ) ;
        document.setPublicationStatus( Document.PublicationStatus.DISAPPROVED );
        assertEquals( DocumentDomainObject.LifeCyclePhase.DISAPPROVED, document.getLifeCyclePhase() );
        document.setPublicationStatus( Document.PublicationStatus.APPROVED );
        assertEquals( DocumentDomainObject.LifeCyclePhase.APPROVED, document.getLifeCyclePhase() );
        document.setPublicationStartDatetime( new Date( 0 ) );
        assertEquals( DocumentDomainObject.LifeCyclePhase.PUBLISHED, document.getLifeCyclePhase() );
        document.setArchivedDatetime( new Date( 0 ) );
        assertEquals( DocumentDomainObject.LifeCyclePhase.ARCHIVED, document.getLifeCyclePhase() );
        document.setPublicationEndDatetime( new Date( 0 ) );
        assertEquals( DocumentDomainObject.LifeCyclePhase.UNPUBLISHED, document.getLifeCyclePhase() );
    }
}
