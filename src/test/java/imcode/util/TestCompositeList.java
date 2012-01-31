package imcode.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class TestCompositeList extends TestCase {

    public void testSimple() {
        CompositeList compositeList = new CompositeList();
        assertEquals(0, compositeList.size()) ;
        assertTrue(compositeList.isEmpty());
        assertFalse(compositeList.iterator().hasNext()) ;
        
        List firstList = new ArrayList() ;
        compositeList.addList(firstList);
        assertEquals(0, compositeList.size()) ;
        assertTrue(compositeList.isEmpty());
        assertFalse(compositeList.iterator().hasNext()) ;

        firstList.add("foo") ;
        assertEquals(1, compositeList.size()) ;
        assertFalse(compositeList.isEmpty());
        assertTrue(compositeList.iterator().hasNext()) ;
        assertEquals("foo", compositeList.iterator().next());

        List secondList = new ArrayList();
        compositeList.addList(secondList);
        assertEquals(1, compositeList.size()) ;
        assertFalse(compositeList.isEmpty());
        assertTrue(compositeList.iterator().hasNext()) ;

        secondList.add("bar") ;
        assertEquals(2, compositeList.size()) ;
        assertFalse(compositeList.isEmpty());
        assertTrue(compositeList.iterator().hasNext()) ;

        compositeList.set(1, "baz") ;
        
        assertEquals(1, secondList.size()) ;
        assertEquals("baz", secondList.get(0)) ;
        
        compositeList.remove(0) ;
        assertTrue(firstList.isEmpty()) ;

        compositeList.remove(0) ;
        assertTrue(secondList.isEmpty()) ;

        assertTrue(compositeList.isEmpty());

    }

}