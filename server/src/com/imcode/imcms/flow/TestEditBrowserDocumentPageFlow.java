package com.imcode.imcms.flow;

import com.imcode.db.Database;
import com.imcode.db.mock.MockDatabase;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.test.mock.MockHttpServletRequest;
import imcode.server.ImcmsServices;
import imcode.server.MockImcmsServices;
import imcode.server.document.BrowserDocumentDomainObject;
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
        documentMapper = new TestEditBrowserDocumentPageFlow.MockDocumentMapper(new MockImcmsServices(), new MockDatabase());
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



        public MockDocumentMapper(ImcmsServices services, Database database
        ) {
            super( services, database);
        }

        protected BrowserDocumentDomainObject.Browser createBrowserFromSqlRow( String[] sqlRow ) {
            return otherBrowser ;
        }
    }
}