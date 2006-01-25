package imcode.util;

import junit.framework.*;
import imcode.util.LfuMap;

import java.util.HashMap;

public class TestLfuMap extends TestCase {

    public void testSizeOne() throws Exception {
        LfuMap map = new LfuMap(new HashMap(), 1);

        map.put("foo", "foo") ;
        assertTrue(map.containsKey("foo")) ;
        map.put("bar", "bar") ;
        assertTrue(map.containsKey("foo")) ;
        assertFalse(map.containsKey("bar")) ;
        map.get("bar") ;
        map.put("bar", "bar") ;
        assertFalse(map.containsKey("foo")) ;
        assertTrue(map.containsKey("bar")) ;
    }

    public void testSizeTwo() throws Exception {
        LfuMap map = new LfuMap(new HashMap(), 2);

        map.put("foo", "foo") ;
        assertTrue(map.containsKey("foo")) ;
        map.put("bar", "bar") ;
        assertTrue(map.containsKey("foo")) ;
        assertTrue(map.containsKey("bar")) ;
        map.put("quux", "quux") ;
        assertTrue(map.containsKey("foo")) ;
        assertTrue(map.containsKey("bar")) ;
        assertFalse(map.containsKey("quux")) ;

        assertEquals("foo", map.get("foo")) ;
        map.put("quux", "quux") ;
        assertTrue(map.containsKey("foo")) ;
        assertTrue(map.containsKey("bar")) ;
        assertFalse(map.containsKey("quux")) ;

        assertNull(map.get("quux")) ;
        map.put("quux", "quux") ;
        assertTrue(map.containsKey("foo")) ;
        assertFalse(map.containsKey("bar")) ;
        assertTrue(map.containsKey("quux")) ;

        assertNull(map.get("bar")) ;
        map.put("bar", "bar") ;
        assertTrue(map.containsKey("foo")) ;
        assertFalse(map.containsKey("bar")) ;
        assertTrue(map.containsKey("quux")) ;

        assertNull(map.get("bar")) ;
        map.put("bar", "bar") ;
        assertFalse(map.containsKey("foo")) ;
        assertTrue(map.containsKey("bar")) ;
        assertTrue(map.containsKey("quux")) ;

    }
}