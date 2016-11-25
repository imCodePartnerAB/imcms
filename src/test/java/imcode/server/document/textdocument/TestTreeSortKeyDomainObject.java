package imcode.server.document.textdocument;

import junit.framework.TestCase;

public class TestTreeSortKeyDomainObject extends TestCase {

    TreeSortKeyDomainObject treeSortKeyDomainObject;

    public void testPrefix() {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( ".1" );
        assertEquals( 1, treeSortKeyDomainObject.getLevelCount() ) ;
    }

    public void testSuffix() {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( "1." );
        assertEquals( 1, treeSortKeyDomainObject.getLevelCount() ) ;
    }

    public void testEmpty() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( "" );
        assertEquals( 0, treeSortKeyDomainObject.getLevelCount() ) ;
    }

    public void testUntrimmed() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( " 1 " );
        assertEquals( 1, treeSortKeyDomainObject.getLevelCount() );
    }

    public void testWhitespace() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( "   " );
        assertEquals( 0, treeSortKeyDomainObject.getLevelCount() );
    }

    public void testSeparators() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( "1,2.3 4");
        assertEquals( 1, treeSortKeyDomainObject.getLevelKey( 0 ) ) ;
        assertEquals( 2, treeSortKeyDomainObject.getLevelKey( 1 ) );
        assertEquals( 3, treeSortKeyDomainObject.getLevelKey( 2 ) );
        assertEquals( 4, treeSortKeyDomainObject.getLevelKey( 3 ) );
    }
}