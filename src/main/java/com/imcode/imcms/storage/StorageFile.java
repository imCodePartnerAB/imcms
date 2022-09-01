package com.imcode.imcms.storage;

import java.io.Closeable;
import java.io.InputStream;

/**
 * Retrieved file from storage. Must be closed at the end!
 */
public interface StorageFile extends Closeable {

    StoragePath getPath();

    /**
     * InputStream can be received several times, but not at the same time.
     */
    InputStream getContent();

    long lastModified();

    long size();
}
