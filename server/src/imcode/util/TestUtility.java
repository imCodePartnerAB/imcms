package imcode.util;

import junit.framework.TestCase;

public class TestUtility extends TestCase {

    public void testGetHumanReadableTimeLength() throws Exception {
        assertEquals( "1ms", Utility.getHumanReadableTimeLength( 1 ) );
        assertEquals( "2s, 1ms", Utility.getHumanReadableTimeLength( 2001 ) );
        assertEquals( "3m, 1s", Utility.getHumanReadableTimeLength( 181000 ) );
        assertEquals( "4h, 1ms", Utility.getHumanReadableTimeLength( 14400001 ) );
        assertEquals("1h, 1m, 1s, 1ms", Utility.getHumanReadableTimeLength( 3661001 ));
    }
}
