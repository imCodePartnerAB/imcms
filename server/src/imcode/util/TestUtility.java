package imcode.util;

import junit.framework.TestCase;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

public class TestUtility extends TestCase {

    public void testGetHumanReadableTimeLength() throws Exception {
        assertEquals( "1ms", Utility.getHumanReadableTimeLength( 1 ) );
        assertEquals( "2s, 1ms", Utility.getHumanReadableTimeLength( 2001 ) );
        assertEquals( "3m, 1s", Utility.getHumanReadableTimeLength( 181000 ) );
        assertEquals( "4h, 1ms", Utility.getHumanReadableTimeLength( 14400001 ) );
        assertEquals("1h, 1m, 1s, 1ms", Utility.getHumanReadableTimeLength( 3661001 ));
    }

    public void testCreateQueryStringFromParameterMultiMap() {
        MultiMap map1 = new MultiHashMap();
        map1.put( "foo", "bar" ) ;
        assertEquals( "foo=bar", Utility.createQueryStringFromParameterMultiMap( map1 ) ) ;
        map1.put( "foo", "baz" ) ;
        assertEquals( "foo=bar&foo=baz", Utility.createQueryStringFromParameterMultiMap( map1 ) );
        map1.put( "bar", "foo" ) ;
        assertEquals( "foo=bar&foo=baz&bar=foo", Utility.createQueryStringFromParameterMultiMap( map1 ) );
    }

    public void testIsValidEmail() throws Exception {
        assertTrue(Utility.isValidEmail( "test@test.test") ) ;
        assertFalse( Utility.isValidEmail( "test" ) );
    }
}
