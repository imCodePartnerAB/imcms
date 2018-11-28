package com.imcode.imcms.mapping;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.mapping.container.VersionRef;
import org.junit.jupiter.api.Test;

import static com.imcode.imcms.api.DocumentVersion.WORKING_VERSION_NO;
import static com.imcode.imcms.mapping.DocumentStoringVisitor.getFilenameForFileDocumentFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileDocFilenameSpec extends WebAppSpringTestConfig {

    @Test
    public void version_is_working_and_file_id_is_blank() {
        // should be just docId
        assertEquals("1001", getFilenameForFileDocumentFile(VersionRef.of(1001, WORKING_VERSION_NO), null));
        assertEquals("1111", getFilenameForFileDocumentFile(VersionRef.of(1111, WORKING_VERSION_NO), ""));
    }

    @Test
    public void version_is_working_and_file_id_is_not_blank() {
        // should be docId.fileId
        assertEquals("1001.10", getFilenameForFileDocumentFile(VersionRef.of(1001, WORKING_VERSION_NO), "10"));
        assertEquals("1234.56", getFilenameForFileDocumentFile(VersionRef.of(1234, WORKING_VERSION_NO), "56"));
        assertEquals("1212.ok", getFilenameForFileDocumentFile(VersionRef.of(1212, WORKING_VERSION_NO), "ok"));
    }

    @Test
    public void version_is_not_working_and_file_id_is_blank() {
        // should be docId_versionNo
        assertEquals("1001_30", getFilenameForFileDocumentFile(VersionRef.of(1001, 30), null));
        assertEquals("1111_5", getFilenameForFileDocumentFile(VersionRef.of(1111, 5), ""));
    }

    @Test
    public void version_is_not_working_and_file_id_is_not_blank() {
        // should be docId_docVersionNo.fileId
        assertEquals("1001_4.2", getFilenameForFileDocumentFile(VersionRef.of(1001, 4), "2"));
        assertEquals("3210_6.11", getFilenameForFileDocumentFile(VersionRef.of(3210, 6), "11"));
        assertEquals("3412_2.abc", getFilenameForFileDocumentFile(VersionRef.of(3412, 2), "abc"));
    }
}