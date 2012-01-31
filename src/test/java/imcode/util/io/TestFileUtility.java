package imcode.util.io;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TestFileUtility extends TestCase {

    private File rootDir;
    private File subDir1;
    private File subDir2;
    private File subSubDir;

    public void setUp() throws Exception {
        rootDir = new File( "test" );
        FileUtils.deleteDirectory( rootDir );
        FileUtils.forceMkdir( rootDir );
        subDir1 = new File( rootDir, "subdir1" );
        FileUtils.forceMkdir( subDir1 );
        subDir2 = new File( rootDir, "subdir2" );
        FileUtils.forceMkdir( subDir2 );
        subSubDir = new File( subDir1, "subsubdir" );
        FileUtils.forceMkdir( subSubDir );
    }

    protected void tearDown() throws Exception {
        FileUtils.forceDelete(rootDir);
    }

    public void testUnescapeFilename() throws Exception {
        assertEquals("Space", " ",FileUtility.unescapeFilename( "_0020")) ;
        assertEquals( "ÅÄÖåäö", "ÅÄÖåäö", FileUtility.unescapeFilename( "_00c5_00c4_00d6_00e5_00e4_00f6" ) );
    }

    public void testEscapeFilename() throws Exception {
        assertEquals( "Space", "_0020", FileUtility.escapeFilename( " " ) );
        assertEquals( "ÅÄÖåäö", "_00c5_00c4_00d6_00e5_00e4_00f6", FileUtility.escapeFilename( "ÅÄÖåäö" ) );
    }

    public void testEscapes() {
        assertEquals( "Space", " ", FileUtility.unescapeFilename( FileUtility.escapeFilename( " " ) ) );
        assertEquals( "ÅÄÖ_åäö", "ÅÄÖ_åäö", FileUtility.unescapeFilename( FileUtility.escapeFilename( "ÅÄÖ_åäö" ) ) );
    }

    public void testDirectoryIsAncestorOfOrEqualTo() throws IOException {
        assertTrue( FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, subSubDir ) );
        assertTrue( FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, rootDir ) );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( subDir1, rootDir ) );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( subSubDir, subDir1 ) );
    }

    public void testRelativizeFile() throws Exception {
        File relativeFile = FileUtility.relativizeFile( rootDir, subSubDir );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, relativeFile ) );
        assertFalse( FileUtility.directoryIsAncestorOfOrEqualTo( subDir1, relativeFile ) );
        assertEquals( subDir1.getName(), relativeFile.getParentFile().getName() );
        assertNull( relativeFile.getParentFile().getParentFile() );
    }

    public void testBackupRename() throws IOException {
        assertTrue(subDir1.exists());
        assertTrue(subDir2.exists());
        FileUtility.backupRename(subDir1,subDir2);
        assertFalse(subDir1.exists());
        assertTrue(subDir2.exists());
    }


}