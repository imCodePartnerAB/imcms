package imcode.util;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TestFileUtility extends TestCase {

    private File rootDir;
    private File subDir;
    private File subSubDir;

    public void setUp() throws Exception {
        rootDir = new File( "test" );
        FileUtils.deleteDirectory( rootDir );
        FileUtils.forceMkdir( rootDir );
        subDir = new File( rootDir, "subdir" );
        FileUtils.forceMkdir( subDir );
        subSubDir = new File( subDir, "subsubdir" );
        FileUtils.forceMkdir( subSubDir );
    }

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
        assertEquals( "ִֵײ_והצ", "ִֵײ_והצ", FileUtility.unescapeFilename( FileUtility.escapeFilename( "ִֵײ_והצ" ) ) );
    }

    public void testDirectoryIsAncestorOfOrEqualTo() throws IOException {
        assertTrue( FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, subSubDir ) );
        assertTrue( FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, rootDir ) );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( subDir, rootDir ) );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( subSubDir, subDir ) );
    }

    public void testRelativizeFile() throws Exception {
        File relativeFile = FileUtility.relativizeFile( rootDir, subSubDir );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, relativeFile ) );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( subDir, relativeFile ) );
        assertEquals( subDir.getName(), relativeFile.getParentFile().getName() );
        assertNull( relativeFile.getParentFile().getParentFile() );
    }


}