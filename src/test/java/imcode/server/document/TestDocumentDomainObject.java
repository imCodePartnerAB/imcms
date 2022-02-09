package imcode.server.document;

import com.imcode.imcms.api.Document;
import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

public class TestDocumentDomainObject extends TestCase {

    DocumentDomainObject document;

    protected void setUp() throws Exception {
        super.setUp();
        document = new DocumentDomainObject() {
            public DocumentTypeDomainObject getDocumentType() {
                return null;
            }

            public void accept(DocumentVisitor documentVisitor) {
            }
        };
    }

    public void testGetLifeCyclePhaseAtTime() {

        assertLifeCyclePhase(LifeCyclePhase.NEW);

        document.setPublicationEndDatetime(new Date(0));
        assertLifeCyclePhase(LifeCyclePhase.NEW);

        document.setPublicationStatus(Document.PublicationStatus.DISAPPROVED);
        assertLifeCyclePhase(LifeCyclePhase.DISAPPROVED);

        document.setPublicationStatus(Document.PublicationStatus.APPROVED);
        assertLifeCyclePhase(LifeCyclePhase.UNPUBLISHED);

        document.setPublicationEndDatetime(null);
        assertLifeCyclePhase(LifeCyclePhase.UNPUBLISHED);

        document.setArchivedDatetime(new Date(0));
        assertLifeCyclePhase(LifeCyclePhase.UNPUBLISHED);

        document.setPublicationStartDatetime(new Date(0));
        assertLifeCyclePhase(LifeCyclePhase.ARCHIVED);

	    document.setPublicationEndDatetime(new Date());
	    assertLifeCyclePhase(LifeCyclePhase.UNPUBLISHED);

		document.setPublicationEndDatetime(null);
        document.setArchivedDatetime(null);
        assertLifeCyclePhase(LifeCyclePhase.PUBLISHED);

        document.setPublicationEndDatetime(new Date(0));
        assertLifeCyclePhase(LifeCyclePhase.PUBLISHED);
    }

    private void assertLifeCyclePhase(LifeCyclePhase lifeCyclePhase) {
	    Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, 1);
	    assertEquals(lifeCyclePhase, DocumentDomainObject.getLifeCyclePhaseAtTime(document, date.getTime()));
    }

    public void testGetDocumentProperties() {

        document.setProperty("imcms:document:alias", "test");
        document.setProperty("imcms:document:foo", "baa");
        document.setProperty("imcms:document:fide", "bla");
        assertEquals(document.getProperty("imcms:document:alias"), "test");
        assertEquals(document.getProperty("imcms:document:foo"), "baa");
        assertNotSame(document.getProperty("imcms:document:fide"), "hide");
        assertNull(document.getProperty("hej"));
        assertEquals(3, document.getProperties().size());
    }
}