package com.imcode.imcms.servlet.superadmin;

import junit.framework.TestCase;

import java.io.File;

public class TestFileAdmin extends TestCase {

    File dir = new File( "tmp/TestFileAdmin" );

    public void setUp() {
        deleteRecursively(dir );
        dir.mkdirs();
    }

    public void testFindUniqueFilename() {
        File[] testFiles = new File[202] ;
        testFiles[0] = new File( dir, "test" ) ;
        for ( int i = 1; i < testFiles.length; i++ ) {
            testFiles[i] = new File( dir, "test."+i ) ;
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

