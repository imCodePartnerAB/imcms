package com.imcode.imcms.flow;

import com.imcode.test.mock.MockHttpServletRequest;
import imcode.server.Config;
import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.impl.MockDatabase;
import imcode.server.document.BrowserDocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.util.Clock;
import junit.framework.TestCase;

import java.util.Map;

public class TestEditBrowserDocumentPageFlow extends TestCase {

    EditBrowserDocumentPageFlow editBrowserDocumentPageFlow;
    private BrowserDocumentDomainObject browserDocument;
    private BrowserDocumentDomainObject.Browser otherBrowser;
    private DocumentMapper documentMapper;

    protected void setUp() throws Exception {
        super.setUp();
        browserDocument = new BrowserDocumentDomainObject();
        otherBrowser = new BrowserDocumentDomainObject.Browser( 1, "Other", 1 );
        editBrowserDocumentPageFlow = new EditBrowserDocumentPageFlow( browserDocument, null, null );
        documentMapper = new TestEditBrowserDocumentPageFlow.MockDocumentMapper(null, new MockDatabase(), null, null, null, null, new Config() );
    }

    public void testGetAddedBrowsersFromRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter(EditBrowserDocumentPageFlow.REQUEST_PARAMETER__BROWSERS, new String[0] );
        request.setupAddParameter( EditBrowserDocumentPageFlow.REQUEST_PARAMETER_PREFIX__DESTINATION + BrowserDocumentDomainObject.Browser.DEFAULT.getId(), "1001" ) ;
        request.setupAddParameter( EditBrowserDocumentPageFlow.REQUEST_PARAMETER_PREFIX__DESTINATION
                                   + otherBrowser.getId(), "1002" );
        Map addedBrowsers = editBrowserDocumentPageFlow.getAddedBrowsersFromRequest(request, documentMapper ) ;
        assertEquals( new Integer( 1002 ), addedBrowsers.get( otherBrowser ) );
        assertEquals( new Integer( 1001 ), addedBrowsers.get( BrowserDocumentDomainObject.Browser.DEFAULT ));
    }

    public class MockDocumentMapper extends DocumentMapper {



        public MockDocumentMapper( ImcmsServices services, Database database,
                                   ImcmsAuthenticatorAndUserAndRoleMapper userRegistry,
                                   DocumentPermissionSetMapper documentPermissionSetMapper,
                                   DocumentIndex documentIndex, Clock clock, Config config ) {
            super( services, database, userRegistry, documentPermissionSetMapper, documentIndex, clock, config );
        }

        protected BrowserDocumentDomainObject.Browser createBrowserFromSqlRow( String[] sqlRow ) {
            return otherBrowser ;
        }
    }
}