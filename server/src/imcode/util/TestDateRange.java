package imcode.util ;

import java.util.Date ;

import junit.framework.* ;

public class TestDateRange extends TestCase {

    private Date date10 ;
    private Date date20 ;
    private Date date30 ;
    private Date date40 ;

    private DateRange range10to10 ;
    private DateRange range10to20 ;
    private DateRange range10to30 ;
    private DateRange range10to40 ;
    private DateRange range20to20 ;
    private DateRange range20to30 ;

    public TestDateRange(String name) {
	super(name) ;
    }

    protected void setUp() {

	date10 = new Date(10) ;
	date20 = new Date(20) ;
	date30 = new Date(30) ;
	date40 = new Date(40) ;

	range10to10 = new DateRange(date10,date10) ;
	range10to20 = new DateRange(date10,date20) ;
	range10to30 = new DateRange(date10,date30) ;
	range10to40 = new DateRange(date10,date40) ;
	range20to20 = new DateRange(date20,date20) ;
	range20to30 = new DateRange(date20,date30) ;
    }

    public void testEquals() {
	assertEquals(range10to10,range10to10) ;
	assertEquals(range20to30,range20to30) ;
    }

    public void testGets() {
	assertEquals(range10to10.getStartDate(),date10) ;
	assertEquals(range10to10.getEndDate(),date10) ;
    }

    public void testContains() {
	assertTrue(!range10to10.contains(date10)) ;
	assertTrue(range10to20.contains(date10)) ;
	assertTrue(!range10to20.contains(date20)) ;
	assertTrue(range10to30.contains(date20)) ;
	assertTrue(!range10to30.contains(date30)) ;
	assertTrue(!range10to30.contains(date40)) ;
	assertTrue(!range20to30.contains(date10)) ;
    }

    public void testZeroWidthOverlap() {
	assertTrue(!range10to10.overlap(range10to10)) ;
	assertTrue(!range10to10.overlap(range10to20)) ;
	assertTrue(!range10to10.overlap(range10to30)) ;
	assertTrue(!range10to10.overlap(range10to40)) ;
	assertTrue(!range10to10.overlap(range20to20)) ;
	assertTrue(!range10to10.overlap(range20to30)) ;

	assertTrue(!range10to20.overlap(range10to10)) ;
	assertTrue(!range10to20.overlap(range20to20)) ;

	assertTrue(!range10to30.overlap(range10to10)) ;
	assertTrue(range10to30.overlap(range20to20)) ;

	assertTrue(!range10to40.overlap(range10to10)) ;
	assertTrue(range10to40.overlap(range20to20)) ;

	assertTrue(!range20to20.overlap(range10to10)) ;
	assertTrue(!range20to20.overlap(range10to20)) ;
	assertTrue(range20to20.overlap(range10to30)) ;
	assertTrue(range20to20.overlap(range10to40)) ;
	assertTrue(!range20to20.overlap(range20to20)) ;
	assertTrue(!range20to20.overlap(range20to30)) ;

	assertTrue(!range20to30.overlap(range20to20)) ;
	assertTrue(!range20to30.overlap(range10to10)) ;
    }

    public void testOverlap() {

	assertTrue(range10to20.overlap(range10to20)) ;
	assertTrue(range10to20.overlap(range10to30)) ;
	assertTrue(range10to20.overlap(range10to40)) ;
	assertTrue(!range10to20.overlap(range20to30)) ;

	assertTrue(range10to30.overlap(range10to20)) ;
	assertTrue(range10to30.overlap(range10to30)) ;
	assertTrue(range10to30.overlap(range10to40)) ;
	assertTrue(range10to30.overlap(range20to30)) ;

	assertTrue(range10to40.overlap(range10to20)) ;
	assertTrue(range10to40.overlap(range10to30)) ;
	assertTrue(range10to40.overlap(range10to40)) ;
	assertTrue(range10to40.overlap(range20to30)) ;

	assertTrue(!range20to30.overlap(range10to20)) ;
	assertTrue(range20to30.overlap(range10to30)) ;
	assertTrue(range20to30.overlap(range10to40)) ;
	assertTrue(range20to30.overlap(range20to30)) ;

    }

}
