package imcode.util;

import junit.framework.*;
import imcode.util.CompositeList;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

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

    }

}