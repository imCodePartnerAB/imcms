package imcode.server.document;

import junit.framework.TestCase;

public class TestUrlDocumentDomainObject extends TestCase {

    UrlDocumentDomainObject urlDocument;
    private static final String HOSTNAME = "example.invalid";

    protected void setUp() throws Exception {
        super.setUp() ;
        urlDocument = new UrlDocumentDomainObject();
    }

    public void testLocalPath() {
        urlDocument.setUrl("/foo/bar") ;
        assertEquals( "/foo/bar", urlDocument.getUrl()) ;
        urlDocument.setUrl( "http:/foo/bar" );
        assertEquals( "http:/foo/bar", urlDocument.getUrl() );
    }

    public void testRelativeLocalPath() {
        urlDocument.setUrl("./foo/bar") ;
        assertEquals( "./foo/bar", urlDocument.getUrl()) ;
        urlDocument.setUrl( "http:foo/bar" );
        assertEquals( "http:foo/bar", urlDocument.getUrl() );
    }

    public void testHttpUrl() {
        urlDocument.setUrl("http://" + HOSTNAME) ;
        assertEquals( "http://"+HOSTNAME, urlDocument.getUrl() );
    }

    public void testFtpUrl() {
        urlDocument.setUrl( "ftp://"+HOSTNAME );
        assertEquals( "ftp://"+HOSTNAME, urlDocument.getUrl() );
    }

    public void testUrlWithoutScheme() {
        urlDocument.setUrl(HOSTNAME) ;
        assertEquals("http://"+HOSTNAME, urlDocument.getUrl()) ;
    }

    public void testFtpUrlWithoutScheme() {
        urlDocument.setUrl( "ftp."+HOSTNAME );
        assertEquals( "ftp://ftp."+HOSTNAME, urlDocument.getUrl() );
    }
}