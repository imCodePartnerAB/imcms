package imcode.util;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class TestUtility extends TestCase {

    private File rootDir;
    private File subDir;
    private File subSubDir;

    public void setUp() throws Exception {
        rootDir = new File("test") ;
        FileUtils.deleteDirectory( rootDir );
        FileUtils.forceMkdir( rootDir );
        subDir = new File(rootDir, "subdir") ;
        FileUtils.forceMkdir( subDir );
        subSubDir = new File(subDir, "subsubdir") ;
        FileUtils.forceMkdir( subSubDir );
    }

    public void testDirectoryIsAncestorOfOrEqualTo() throws IOException {
        assertTrue(FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, subSubDir ));
        assertTrue(FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, rootDir ));
        assertFalse(FileUtility.directoryIsAncestorOfOrEqualTo( subDir, rootDir ));
        assertFalse(FileUtility.directoryIsAncestorOfOrEqualTo( subSubDir, subDir ));
    }

    public void testRelativizeFile() throws Exception {
        File relativeFile = FileUtility.relativizeFile( rootDir, subSubDir );
        assertFalse(FileUtility.directoryIsAncestorOfOrEqualTo( rootDir, relativeFile)) ;
        assertFalse(FileUtility.directoryIsAncestorOfOrEqualTo( subDir, relativeFile)) ;
        assertEquals( subDir.getName(), relativeFile.getParentFile().getName()) ;
        assertNull( relativeFile.getParentFile().getParentFile()) ;
    }
}
