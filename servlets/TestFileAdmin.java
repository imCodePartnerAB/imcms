
import junit.framework.TestCase;

import java.io.File;

public class TestFileAdmin extends TestCase {

    public void setUp() {
        deleteRecursively(new File( "/tmp/TestFileAdmin" ) );
        new File( "/tmp/TestFileAdmin" ).mkdirs();
    }

    public void testFindUniqueFilename() {
        File[] testFiles = new File[202] ;
        testFiles[0] = new File( "/tmp/TestFileAdmin/test" ) ;
        for ( int i = 1; i < testFiles.length; i++ ) {
            testFiles[i] = new File( "/tmp/TestFileAdmin/test."+i ) ;
        }

        assertEquals(testFiles[0], FileAdmin.findUniqueFilename(testFiles[0]));
        for ( int i = 1; i < testFiles.length; i++ ) {
            testFiles[i-1].mkdir() ;
            assertEquals( testFiles[i], FileAdmin.findUniqueFilename( testFiles[0] ) );
        }
    }

    private void deleteRecursively(File file) {
        deleteRecursively( new File[]{ file } ) ;
    }

    private void deleteRecursively(File[] files) {
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i] ;
            if (file.isDirectory()) {
                deleteRecursively(file.listFiles());
            }
            file.delete() ;
        }
    }

}

