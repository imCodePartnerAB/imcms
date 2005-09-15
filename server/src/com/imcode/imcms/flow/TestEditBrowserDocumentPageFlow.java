package com.imcode.imcms.flow;

import com.imcode.db.Database;
import com.imcode.db.mock.MockDatabase;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DatabaseDocumentGetter;
import com.imcode.imcms.mapping.DefaultDocumentMapper;
import com.imcode.imcms.mapping.DocumentPermissionSetMapper;
import com.imcode.test.mock.MockHttpServletRequest;
import imcode.server.Config;
import imcode.server.ImcmsServices;
import imcode.server.document.BrowserDocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.util.Clock;
import junit.framework.TestCase;

import java.util.Map;

public class TestEditBrowserDocumentPageFlow extends TestCase {

    EditBrowserDocumentPageFlow editBrowserDocumentPageFlow;
    private BrowserDocumentDomainObject browserDocument;
    private BrowserDocumentDomainObject.Browser otherBrowser;
    private DefaultDocumentMapper documentMapper;

    protected void setUp() throws Exception {
        super.setUp();
        browserDocument = new BrowserDocumentDomainObject();
        otherBrowser = new BrowserDocumentDomainObject.Browser( 1, "Other", 1 );
        editBrowserDocumentPageFlow = new EditBrowserDocumentPageFlow( browserDocument, null, null );
        documentMapper = new TestEditBrowserDocumentPageFlow.MockDocumentMapper(null, new MockDatabase(), null, null, null, new Config() );
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

    public class MockDocumentMapper extends DefaultDocumentMapper {



        public MockDocumentMapper( ImcmsServices services, Database database,
                                   DocumentPermissionSetMapper documentPermissionSetMapper,
                                   DocumentIndex documentIndex, Clock clock, Config config ) {
            super( services, database, new DatabaseDocumentGetter(database, services), documentPermissionSetMapper, documentIndex, clock, config, new CategoryMapper(database));
        }

        protected BrowserDocumentDomainObject.Browser createBrowserFromSqlRow( String[] sqlRow ) {
            return otherBrowser ;
        }
    }
}