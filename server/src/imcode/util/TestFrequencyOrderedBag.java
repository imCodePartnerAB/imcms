package imcode.util;

import junit.framework.TestCase;

import java.util.List;

public class TestFrequencyOrderedBag extends TestCase {

    public void testSimple() {
        FrequencyOrderedBag bag = new FrequencyOrderedBag();
        List list = bag.asList() ;

        assertEquals(0, bag.getFrequency("foo")) ;
        assertEquals(0, bag.size()) ;
        assertEquals(0, list.size()) ;

        bag.add("foo") ;
        assertEquals(1, bag.getFrequency("foo")) ;
        assertEquals("foo", list.get(0)) ;
        assertEquals(0, bag.getFrequency("bar")) ;
        assertEquals(1, bag.size()) ;
        assertEquals(1, list.size()) ;

        bag.add("bar") ;
        assertEquals(1, bag.getFrequency("bar")) ;
        assertEquals("bar", list.get(0)) ;
        assertEquals("foo", list.get(1)) ;
        assertEquals(2, bag.size()) ;
        assertEquals(2, list.size()) ;

        bag.add("foo") ;
        assertEquals(2, bag.getFrequency("foo")) ;
        assertEquals("foo", list.get(0)) ;
        assertEquals("bar", list.get(1)) ;
        assertEquals(2, bag.size()) ;
        assertEquals(2, list.size()) ;
    }

}
