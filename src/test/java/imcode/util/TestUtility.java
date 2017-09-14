package imcode.util;

import junit.framework.TestCase;

public class TestUtility extends TestCase {

//    public void testCreateQueryStringFromParameterMultiMap() {
//        MultiMap map1 = new MultiValueMap();
//        map1.put( "foo", "bar" ) ;
//        assertEquals( "foo=bar", Utility.createQueryStringFromParameterMultiMap( map1 ) ) ;
//        map1.put( "foo", "baz" ) ;
//        assertEquals( "foo=bar&foo=baz", Utility.createQueryStringFromParameterMultiMap( map1 ) );
//        map1.put( "bar", "foo" ) ;
//        assertEquals( "foo=bar&foo=baz&bar=foo", Utility.createQueryStringFromParameterMultiMap( map1 ) );
//    }

//    public void testIsValidEmail() throws Exception {
//        assertTrue(Utility.isValidEmail( "test@test.test") ) ;
//        assertFalse( Utility.isValidEmail( "test" ) );
//    }

    public void testThrowableContainsMessageContaining() {
        assertTrue(Utility.throwableContainsMessageContaining(new Throwable("test"), "e"));
        assertTrue(Utility.throwableContainsMessageContaining(new Throwable(new Throwable("test")), "t"));
        assertFalse(Utility.throwableContainsMessageContaining(new Throwable(new Throwable("test")), "p"));
        assertFalse(Utility.throwableContainsMessageContaining(new Throwable(new Throwable((String) null)), "t"));
    }
}
