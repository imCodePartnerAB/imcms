package com.imcode.imcms.util;

import imcode.util.io.FileUtility;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertTrue;

/**
 * Wrapper for deleting file on close, useful in tests
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 02.10.18.
 */
public class DeleteOnCloseFile extends File implements AutoCloseable {

    public DeleteOnCloseFile(String pathname) {
        super(pathname);
    }

    public DeleteOnCloseFile(String parent, String child) {
        super(parent, child);
    }

    public DeleteOnCloseFile(File parent, String child) {
        super(parent, child);
    }

    public DeleteOnCloseFile(URI uri) {
        super(uri);
    }

    @SneakyThrows
    @Override
    public void close() {
        if (exists()) assertTrue(FileUtility.forceDelete(this));
    }
}
