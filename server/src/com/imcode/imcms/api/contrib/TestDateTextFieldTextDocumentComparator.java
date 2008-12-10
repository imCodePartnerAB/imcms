package com.imcode.imcms.api.contrib;

import com.imcode.imcms.api.DocumentService;
import com.imcode.imcms.api.MockContentManagementSystem;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.MockImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

public class TestDateTextFieldTextDocumentComparator extends TestCase {

    private DateTextFieldTextDocumentComparator comparator;
    private TextDocument d1;
    private TextDocument d2;
    public static final int TEXT_FIELD_INDEX = 1;

    protected void setUp() throws Exception {
        super.setUp();
        this.comparator = new DateTextFieldTextDocumentComparator( TEXT_FIELD_INDEX ) ;
        MockContentManagementSystem contentManagementSystem = new MockContentManagementSystem();
        UserDomainObject internalUser = new UserDomainObject();
        internalUser.addRoleId( RoleId.SUPERADMIN );
        contentManagementSystem.setCurrentInternalUser( internalUser );
        MockImcmsServices imcmsServices = new MockImcmsServices();
        imcmsServices.setDocumentMapper( new DocumentMapper( imcmsServices, null) {
            public DocumentDomainObject getDocument( Integer metaId ) {
                TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
                textDocument.setId( metaId.intValue() );
                return textDocument;
            }
        });
        contentManagementSystem.setInternal( imcmsServices );
        DocumentService documentService = new DocumentService( contentManagementSystem );
        this.d1 = (TextDocument)documentService.getDocument( 1001 ) ;
        this.d2 = (TextDocument)documentService.getDocument( 1002 ) ;
    }

    public void testCompare() {
        assertEquals( -1, comparator.compare( d1, d2 ) ) ;
        d1.setPlainTextField( TEXT_FIELD_INDEX, "2000-01-01");
        assertEquals( 1, comparator.compare( d1, d2 ) ) ;
        d2.setPlainTextField( TEXT_FIELD_INDEX, "2000-01-01");
        assertEquals( -1, comparator.compare( d1, d2 ) ) ;
        d1.setPlainTextField( TEXT_FIELD_INDEX, "2001-01-01");
        assertEquals( 1, comparator.compare( d1, d2 ) ) ;
    }
}
