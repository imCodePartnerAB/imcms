package com.imcode.imcms.storage.impl.cloud;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.ConflictFileTypeException;
import com.imcode.imcms.storage.exception.FolderNotEmptyException;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import imcode.server.Imcms;
import org.apache.tika.Tika;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

/**
 * Provides Amazon Simple Storage Service (s3).
 */
public class CloudStorageClient implements StorageClient {

    private final AmazonS3 s3Client;
    private final String bucketName;

    //files are saved, moved and copied with such access by default
    private final CannedAccessControlList defaultAccess;

    public CloudStorageClient(AmazonS3 s3Client, String bucketName) {
        this(s3Client, bucketName, CannedAccessControlList.Private);
    }

    public CloudStorageClient(AmazonS3 s3Client, String bucketName, CannedAccessControlList defaultAccess) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.defaultAccess = defaultAccess;

        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        }
    }

    @Override
    public StorageFile getFile(StoragePath path) throws StorageFileNotFoundException {
        if(!exists(path)){
            throw new StorageFileNotFoundException(path);
        }
        return new CloudStorageFile(s3Client.getObject(bucketName, path.toString()));
    }

    @Override
    public List<StoragePath> listPaths(StoragePath folderPath) {
        if (!exists(folderPath)) throw new StorageFileNotFoundException();

        return getAllObjectsWithPrefix(folderPath.toString()).parallelStream()
                .filter(s3ObjectSummary -> s3ObjectSummary.getKey().matches(folderPath + "((\\w+/)|[^/]+)"))
                .map(s3ObjectSummary -> {
                    String key = s3ObjectSummary.getKey();
                    SourceFile.FileType fileType = key.endsWith(StoragePath.PATH_SEPARATOR) ? DIRECTORY : FILE;
                    return StoragePath.get(fileType, key);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StoragePath> walk(StoragePath folderPath) {
        if(!exists(folderPath)) throw new StorageFileNotFoundException();

        return getAllObjectsWithPrefix(folderPath.toString()).parallelStream()
                .map(s3ObjectSummary -> {
                    String key = s3ObjectSummary.getKey();
                    SourceFile.FileType fileType = key.endsWith(StoragePath.PATH_SEPARATOR) ? DIRECTORY : FILE;
                    return StoragePath.get(fileType, key);
                })
                .collect(Collectors.toList());
    }

    private List<S3ObjectSummary> getAllObjectsWithPrefix(String prefix){
        ObjectListing listing = s3Client.listObjects(bucketName, prefix);
        final List<S3ObjectSummary> objectSummaries = new ArrayList<>(listing.getObjectSummaries());

        while (listing.isTruncated()) {
            listing = s3Client.listNextBatchOfObjects(listing);
            objectSummaries.addAll(listing.getObjectSummaries());
        }

        return objectSummaries;
    }

    @Override
    public void create(StoragePath path){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0L);
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path.toString(), inputStream, metadata);
        s3Client.putObject(putObjectRequest);
    }

    @Override
    public void put(StoragePath path, InputStream inputStream) {
        put(path, inputStream, defaultAccess);
    }

    public void put(StoragePath path, InputStream inputStream, CannedAccessControlList access) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(new Tika().detect(path.toString()));
        objectMetadata.setContentEncoding(Imcms.UTF_8_ENCODING);

        final PutObjectRequest request = new PutObjectRequest(bucketName, path.toString(), inputStream, objectMetadata)
                .withCannedAcl(access);
        s3Client.putObject(request);
    }

    @Override
    public void move(StoragePath fromPath, StoragePath toPath) {
        move(fromPath, toPath, defaultAccess);
    }

    public void move(StoragePath fromPath, StoragePath toPath, CannedAccessControlList access) {
        copy(fromPath, toPath, access);
        delete(fromPath, true);
    }

    @Override
    public void copy(StoragePath sourcePath, StoragePath toPath) {
        copy(sourcePath, toPath, defaultAccess);
    }

    public void copy(StoragePath sourcePath, StoragePath toPath, CannedAccessControlList access) {
        if (sourcePath.getType() != toPath.getType()) throw new ConflictFileTypeException();

        if (sourcePath.getType() == DIRECTORY){
            final List<S3ObjectSummary> objectSummaries = s3Client.listObjectsV2(bucketName, sourcePath.toString()).getObjectSummaries();
            for(S3ObjectSummary objectSummary: objectSummaries){
                String key = objectSummary.getKey();
                String newKey = key.replaceFirst(sourcePath.toString(), toPath.toString());
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, key, bucketName, newKey)
                        .withCannedAccessControlList(access);
                s3Client.copyObject(copyObjectRequest);
            }
        }else{
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, sourcePath.toString(), bucketName, toPath.toString())
                    .withCannedAccessControlList(access);
            s3Client.copyObject(copyObjectRequest);
        }
    }

    @Override
    public boolean exists(StoragePath storagePath) {
        return s3Client.doesObjectExist(bucketName, storagePath.toString());
    }

    @Override
    public void delete(StoragePath path, boolean force) {
        if (path.getType() == DIRECTORY) {
            final List<S3ObjectSummary> objectSummaries = s3Client.listObjectsV2(bucketName, path.toString()).getObjectSummaries();

            if (!force && objectSummaries.size() > 1) throw new FolderNotEmptyException("Path: " + path);

            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                    .withKeys(objectSummaries.stream()
                            .map(objectSummary -> new DeleteObjectsRequest.KeyVersion(objectSummary.getKey()))
                            .collect(Collectors.toList()));
            s3Client.deleteObjects(deleteObjectsRequest);
        }else{
            s3Client.deleteObject(bucketName, path.toString());
        }

    }

}
