package com.imcode.imcms.storage;

import com.imcode.imcms.storage.exception.StorageFileNotFoundException;

import java.io.InputStream;
import java.util.List;

/**
 * Provides an interface for working with some storage (adding, reading, deleting, etc).
 */
public interface StorageClient {

    StorageFile getFile(StoragePath path) throws StorageFileNotFoundException;

    /**
     * @param path - folder
     * @return list paths of elements from a folder
     */
    List<StoragePath> listPaths(StoragePath path);

    /**
     * @param path - folder
     * @return list of all nested element and folder paths
     */
    List<StoragePath> walk(StoragePath path);

    boolean exists(StoragePath path);

    /**
     * Create an empty file or folder
     */
    void create(StoragePath path);

    void put(StoragePath path, InputStream inputStream);

    void move(StoragePath fromPath, StoragePath toPath);

    void copy(StoragePath sourcePath, StoragePath toPath);

    default boolean canPut(StoragePath path){
        return true;
    }

    void delete(StoragePath path, boolean force);
}
