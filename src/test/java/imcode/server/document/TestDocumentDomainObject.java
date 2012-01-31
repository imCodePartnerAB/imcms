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
    
    public void testDeepClonePermissionSets() throws CloneNotSupportedException {
        assertFalse(document.getPermissionSets().getRestricted1().getEditDocumentInformation()) ;

        DocumentDomainObject clone = (DocumentDomainObject)document.clone() ;
        assertNotSame(document.attributes, clone.attributes) ;
        assertNotSame(document.getPermissionSets(), clone.getPermissionSets());
        assertNotSame(document.getPermissionSets().getRestricted1(), clone.getPermissionSets().getRestricted1());
        
        assertFalse(clone.getPermissionSets().getRestricted1().getEditDocumentInformation()) ;

        clone.getPermissionSets().getRestricted1().setEditDocumentInformation(true);
        assertTrue(clone.getPermissionSets().getRestricted1().getEditDocumentInformation()) ;
        assertFalse(document.getPermissionSets().getRestricted1().getEditDocumentInformation()) ;
    }

    public void testGetLifeCyclePhaseAtTime() throws Exception {

        assertLifeCyclePhase(LifeCyclePhase.NEW);

        document.setPublicationEndDatetime( new Date( 0 ) );
        assertLifeCyclePhase(LifeCyclePhase.NEW);

        document.setPublicationStatus( Document.PublicationStatus.DISAPPROVED );
        assertLifeCyclePhase(LifeCyclePhase.DISAPPROVED);

        document.setPublicationStatus( Document.PublicationStatus.APPROVED );
        assertLifeCyclePhase(LifeCyclePhase.UNPUBLISHED);

        document.setPublicationEndDatetime( null );
        assertLifeCyclePhase(LifeCyclePhase.APPROVED);

        document.setArchivedDatetime( new Date( 0 ) );
        assertLifeCyclePhase(LifeCyclePhase.APPROVED);

        document.setPublicationStartDatetime( new Date( 0 ) );
        assertLifeCyclePhase(LifeCyclePhase.ARCHIVED);

        document.setArchivedDatetime( null );
        assertLifeCyclePhase(LifeCyclePhase.PUBLISHED);

        document.setPublicationEndDatetime( new Date( 0 ) );
        assertLifeCyclePhase(LifeCyclePhase.UNPUBLISHED);
    }

    private void assertLifeCyclePhase(LifeCyclePhase lifeCyclePhase) {
        assertEquals( lifeCyclePhase, document.getLifeCyclePhaseAtTime(new Date(1)) );
    }

    public void testGetDocumentProperties() throws Exception {

        document.setProperty("imcms:document:alias", "test");
        document.setProperty("imcms:document:foo", "baa");
        document.setProperty("imcms:document:fide", "bla");
        assertEquals(document.getProperty("imcms:document:alias"), "test");
        assertEquals(document.getProperty("imcms:document:foo"), "baa");
        assertNotSame(document.getProperty("imcms:document:fide"), "hide");
        assertNull(document.getProperty("hej"));
        assertTrue(document.getProperties().size()==3);
    }
}