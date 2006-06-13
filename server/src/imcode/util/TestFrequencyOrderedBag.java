package imcode.util;

import junit.extensions.ActiveTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

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

    public void testConcurrency() throws InterruptedException {

        ActiveTestSuite suite = new ActiveTestSuite();
        Test test = new Test() {
            private FrequencyOrderedBag bag = new FrequencyOrderedBag();
            public int countTestCases() {
                return 1;
            }

            public void run(TestResult testResult) {
                try {
                    for ( int j = 0; j < 10; ++j ) {
                        for ( int i = 0; i < 10; ++i ) {
                            Integer key = new Integer(i);
                            bag.add(key);
                            assertTrue(bag.getFrequency(key) > 0) ;
                        }
                    }
                } catch ( Exception e ) {
                    testResult.addError(this, e);
                }
            }
        };
        for ( int i = 0; i < 100; ++i ) {
            suite.addTest(test);
        }
        TestResult testResult = new TestResult();
        suite.run(testResult);
        assertEquals(0, testResult.errorCount());
    }

}
