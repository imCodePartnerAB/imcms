package com.imcode.imcms;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.DocumentStoringVisitor;
import imcode.server.Imcms;
import imcode.util.io.FileUtility;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;

/**
 *
 */
public class FileDaoTest {


    @Test
    public void getFilenameForFileDocumentFile() {
        assertEquals("Working version filename; fileId is null.",
                     "1001",
                     DocumentStoringVisitor.getFilenameForFileDocumentFile(1001, DocumentVersion.WORKING_VERSION_NO, null));

        assertEquals("Working version filename; fileId is empty.",
                     "1001",
                     DocumentStoringVisitor.getFilenameForFileDocumentFile(1001, DocumentVersion.WORKING_VERSION_NO, null));

        assertEquals("Working version filename.",
                     "1001.txt",
                     DocumentStoringVisitor.getFilenameForFileDocumentFile(1001, DocumentVersion.WORKING_VERSION_NO, "txt"));


        assertEquals("Custom version filename; fileId is null.",
                     "1001_3",
                     DocumentStoringVisitor.getFilenameForFileDocumentFile(1001, 3, null));

        assertEquals("Custom version filename; fileId is empty.",
                     "1001_3",
                     DocumentStoringVisitor.getFilenameForFileDocumentFile(1001, 3, ""));

        assertEquals("Custom version filename",
                     "1001_3.txt",
                     DocumentStoringVisitor.getFilenameForFileDocumentFile(1001, 3, "txt"));
    }
}
