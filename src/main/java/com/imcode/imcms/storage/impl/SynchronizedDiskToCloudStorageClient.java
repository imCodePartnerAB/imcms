package com.imcode.imcms.storage.impl;

import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.storage.impl.cloud.CloudStorageClient;
import com.imcode.imcms.storage.impl.disk.DiskStorageClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Synchronizes disk storage with s3. Main storage is s3.
 * Saves file to disk storage and s3. Gives file from disk.
 * If there is no required file on disk, pulls it from s3 and save to disk.
 */
public class SynchronizedDiskToCloudStorageClient implements StorageClient {

    private final DiskStorageClient diskStorageClient;
    private final CloudStorageClient cloudStorageClient;

    private final ExecutorService syncExecutor = Executors.newSingleThreadExecutor();

    private static final Logger log = LogManager.getLogger(SynchronizedDiskToCloudStorageClient.class);

    public SynchronizedDiskToCloudStorageClient(DiskStorageClient diskStorageClient,
                                                CloudStorageClient cloudStorageClient) {
        this.diskStorageClient = diskStorageClient;
        this.cloudStorageClient = cloudStorageClient;
    }

    @Override
    public StorageFile getFile(StoragePath path) throws StorageFileNotFoundException {
        try{
            return diskStorageClient.getFile(path);
        }catch (StorageFileNotFoundException e){
            final StorageFile file = cloudStorageClient.getFile(path);
            syncExecutor.submit(() -> {
                StoragePath parentPath = path.getParentPath();
                if(!diskStorageClient.exists(parentPath)) diskStorageClient.create(parentPath);
                diskStorageClient.put(path, file.getContent());
            });

            return cloudStorageClient.getFile(path);
        }
    }

    @Override
    public List<StoragePath> listPaths(StoragePath path) {
        return cloudStorageClient.listPaths(path);
    }

    @Override
    public List<StoragePath> walk(StoragePath path) {
        return cloudStorageClient.walk(path);
    }

    @Override
    public boolean exists(StoragePath path) {
        return diskStorageClient.exists(path) || cloudStorageClient.exists(path);
    }

    @Override
    public void create(StoragePath path) {
        diskStorageClient.create(path);
        cloudStorageClient.create(path);
    }

    @Override
    public void put(StoragePath path, InputStream inputStream) {
        diskStorageClient.put(path, inputStream);
        cloudStorageClient.create(path);
        syncExecutor.submit(() -> {
            try(StorageFile file = diskStorageClient.getFile(path)){
                cloudStorageClient.put(path, file.getContent());
            } catch (Exception e) {
                log.error("Exception while syncing file to cloud", e);
            }
        });
    }

    @Override
    public void move(StoragePath fromPath, StoragePath toPath) {
        if(diskStorageClient.exists(fromPath)){
            diskStorageClient.move(fromPath, toPath);
        }
        cloudStorageClient.move(fromPath, toPath);
    }

    @Override
    public void copy(StoragePath sourcePath, StoragePath toPath) {
        if(diskStorageClient.exists(sourcePath)){
            diskStorageClient.copy(sourcePath, toPath);
        }

        cloudStorageClient.copy(sourcePath, toPath);
    }

    @Override
    public boolean canPut(StoragePath path) {
        return diskStorageClient.canPut(path) && cloudStorageClient.canPut(path);
    }

    @Override
    public void delete(StoragePath path, boolean force) {
        if(diskStorageClient.exists(path)){
            diskStorageClient.delete(path, force);
        }
        cloudStorageClient.delete(path, force);
    }

}
