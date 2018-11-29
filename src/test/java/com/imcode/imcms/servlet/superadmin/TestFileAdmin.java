package com.imcode.imcms.servlet.superadmin;

import imcode.util.io.FileUtility;
import junit.framework.TestCase;

import java.io.File;

public class TestFileAdmin extends TestCase {

    private final File dir = new File("tmp/TestFileAdmin");

    public void setUp() throws Exception {
        super.setUp();

        if (dir.exists()) {
            FileUtility.forceDelete(dir);
        }

        dir.mkdirs();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        FileUtility.forceDelete(dir);
    }

    public void testFindUniqueFilename() {
        File[] testFiles = new File[202];
        testFiles[0] = new File(dir, "test");

        for (int i = 1; i < testFiles.length; i++) {
            testFiles[i] = new File(dir, "test." + i);
        }

        assertEquals(testFiles[0], FileAdmin.findUniqueFilename(testFiles[0]));

        for (int i = 1; i < testFiles.length; i++) {
            testFiles[i - 1].mkdir();
            assertEquals(testFiles[i], FileAdmin.findUniqueFilename(testFiles[0]));
        }
    }

}

