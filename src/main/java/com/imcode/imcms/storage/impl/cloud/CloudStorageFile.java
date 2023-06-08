package com.imcode.imcms.storage.impl.cloud;

import com.amazonaws.services.s3.model.S3Object;
import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

public class CloudStorageFile implements StorageFile {

    private S3Object s3Object;
    private InputStream inputStream;

    CloudStorageFile(S3Object s3Object) {
        this.s3Object = s3Object;
    }

    @Override
    public StoragePath getPath() {
        String key = s3Object.getKey();
        SourceFile.FileType fileType = key.endsWith(StoragePath.PATH_SEPARATOR) ? DIRECTORY : FILE;
        return StoragePath.get(fileType, key);
    }

    @Override
    @SneakyThrows
    public InputStream getContent() {
        if(inputStream == null) {
            inputStream = new BufferedInputStream(s3Object.getObjectContent());
            inputStream.mark(Integer.MAX_VALUE);
        }else{
            inputStream.reset();
        }

        return inputStream;
    }

    @Override
    public long lastModified() {
        return s3Object.getObjectMetadata().getLastModified().getTime();
    }

    @Override
    public long size() {
        return s3Object.getObjectMetadata().getContentLength();
    }

    @Override
    public void close() throws IOException {
        if (s3Object != null) {
            s3Object.close();
            s3Object = null;
        }

        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }
}
