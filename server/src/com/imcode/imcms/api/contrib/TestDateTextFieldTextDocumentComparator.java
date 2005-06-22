package com.imcode.imcms.api.contrib;

import com.imcode.imcms.api.*;
import junit.framework.TestCase;
import imcode.server.MockImcmsServices;
import imcode.server.Config;
import imcode.server.user.UserDomainObject;
import imcode.server.user.RoleDomainObject;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentId;
import com.imcode.imcms.mapping.DatabaseDocumentGetter;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.document.textdocument.TextDocumentDomainObject;

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
        internalUser.addRole( RoleDomainObject.SUPERADMIN );
        contentManagementSystem.setCurrentInternalUser( internalUser );
        MockImcmsServices imcmsServices = new MockImcmsServices();
        imcmsServices.setDocumentMapper( new DocumentMapper( imcmsServices, null, new DatabaseDocumentGetter(null, imcmsServices), null, null, null, new Config(), new CategoryMapper(null)) {
            public DocumentDomainObject getDocument( DocumentId metaId ) {
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
