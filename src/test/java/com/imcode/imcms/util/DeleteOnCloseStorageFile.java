package com.imcode.imcms.util;

import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import imcode.util.io.FileUtility;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DeleteOnCloseStorageFile implements AutoCloseable {

    final StoragePath path;
    final StorageClient storageClient;

    public DeleteOnCloseStorageFile(StoragePath path, StorageClient storageClient){
        this.path = path;
        this.storageClient = storageClient;
    }

    public boolean exists(){
        return storageClient.exists(path);
    }

    public String getName(){
        return path.getName();
    }

    public boolean create(){
        storageClient.create(path);
        return exists();
    }

    public void put(InputStream inputStream){
        storageClient.put(path, inputStream);
    }

    public boolean delete(boolean force){
        storageClient.delete(path, force);
        return !exists();
    }

    @SneakyThrows
    @Override
    public void close() {
        if (exists()) assertTrue(delete(true));
    }
}
