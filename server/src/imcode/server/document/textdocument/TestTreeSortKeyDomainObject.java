package imcode.server.document.textdocument;

import junit.framework.TestCase;

public class TestTreeSortKeyDomainObject extends TestCase {

    TreeSortKeyDomainObject treeSortKeyDomainObject;

    public void testEmptyTreeSortKeyDomainObject() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( "" );
        assertEquals( 0, treeSortKeyDomainObject.getLevelCount() ) ;
    }

    public void testUntrimmedTreeSortKeyDomainObject() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( " 1 " );
        assertEquals( 1, treeSortKeyDomainObject.getLevelCount() );
    }

    public void testWhitespaceTreeSortKeyDomainObject() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( "   " );
        assertEquals( 0, treeSortKeyDomainObject.getLevelCount() );
    }

    public void testTreeSortKeyDomainObject() throws Exception {
        treeSortKeyDomainObject = new TreeSortKeyDomainObject( "1,2.3 4");
        assertEquals( 1, treeSortKeyDomainObject.getLevelKey( 0 ) ) ;
        assertEquals( 2, treeSortKeyDomainObject.getLevelKey( 1 ) );
        assertEquals( 3, treeSortKeyDomainObject.getLevelKey( 2 ) );
        assertEquals( 4, treeSortKeyDomainObject.getLevelKey( 3 ) );
    }
}