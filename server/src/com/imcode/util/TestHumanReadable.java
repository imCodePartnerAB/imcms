package com.imcode.util;

import junit.framework.*;
import org.apache.commons.lang.time.DateUtils;

public class TestHumanReadable extends TestCase {

    public void testGetHumanReadableTimeLength() throws Exception {
        assertEquals( "1ms", HumanReadable.getHumanReadableTimeLength( 1 ) );
        assertEquals( "2s, 1ms", HumanReadable.getHumanReadableTimeLength( 2 * DateUtils.MILLIS_IN_SECOND + 1 ) );
        assertEquals( "3m, 1s", HumanReadable.getHumanReadableTimeLength( 3 * DateUtils.MILLIS_IN_MINUTE
                                                                       + 1 * DateUtils.MILLIS_IN_SECOND ) );
        assertEquals( "4h, 1ms", HumanReadable.getHumanReadableTimeLength( 4 * DateUtils.MILLIS_IN_HOUR + 1 ) );
        assertEquals( "1h, 1m, 1s, 1ms", HumanReadable.getHumanReadableTimeLength( DateUtils.MILLIS_IN_HOUR
                                                                                + DateUtils.MILLIS_IN_MINUTE
                                                                                + DateUtils.MILLIS_IN_SECOND
                                                                                + 1 ) );
    }

    public void testGetHumanReadableByteSize() throws Exception {
        assertEquals( "1023 B", HumanReadable.getHumanReadableByteSize( 1023 ) );
        assertEquals( "1 kB", HumanReadable.getHumanReadableByteSize( 1024 ) );
        assertEquals( "1.5 kB", HumanReadable.getHumanReadableByteSize( 1024+512 )) ;
        assertEquals( "1.5 MB", HumanReadable.getHumanReadableByteSize( 1048576+524288 )) ;
        assertEquals( "1 GB", HumanReadable.getHumanReadableByteSize( 1048576*1024 )) ;
    }
}
