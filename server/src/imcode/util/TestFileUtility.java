package imcode.util;

import junit.framework.TestCase;

public class TestFileUtility extends TestCase {

    public void testUnescapeFilename() throws Exception {
        assertEquals("Space", " ",FileUtility.unescapeFilename( "_0020")) ;
        assertEquals( "ִֵײוהצ", "ִֵײוהצ", FileUtility.unescapeFilename( "_00c5_00c4_00d6_00e5_00e4_00f6" ) );
    }

    public void testEscapeFilename() throws Exception {
        assertEquals( "Space", "_0020", FileUtility.escapeFilename( " " ) );
        assertEquals( "ִֵײוהצ", "_00c5_00c4_00d6_00e5_00e4_00f6", FileUtility.escapeFilename( "ִֵײוהצ" ) );
    }

    public void testEscapes() {
        assertEquals( "Space", " ", FileUtility.unescapeFilename( FileUtility.escapeFilename( " " ) ) );
        assertEquals( "ִֵײוהצ", "ִֵײוהצ", FileUtility.unescapeFilename( FileUtility.escapeFilename( "ִֵײוהצ" ) ) );
    }
}