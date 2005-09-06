package imcode.server.document;

import com.imcode.imcms.api.Document;
import imcode.server.user.RoleId;
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
        RoleId roleId = RoleId.SUPERADMIN;
        assertEquals( document.getDocumentPermissionSetTypeForRoleId(roleId), DocumentPermissionSetTypeDomainObject.NONE );
        DocumentDomainObject clone = (DocumentDomainObject)document.clone() ;
        clone.setDocumentPermissionSetTypeForRoleId( roleId, DocumentPermissionSetTypeDomainObject.FULL );
        assertEquals( clone.getDocumentPermissionSetTypeForRoleId( roleId ), DocumentPermissionSetTypeDomainObject.FULL );
        assertEquals( document.getDocumentPermissionSetTypeForRoleId( roleId ), DocumentPermissionSetTypeDomainObject.NONE );
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
