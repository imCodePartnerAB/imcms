package com.imcode.imcms.storage.impl.disk;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

public class DiskStorageFile implements StorageFile {

    private Path path;
    private InputStream inputStream;

    DiskStorageFile(Path path){
        this.path = path;
    }

    @Override
    public StoragePath getPath() {
        SourceFile.FileType fileType = Files.isDirectory(path) ? DIRECTORY : FILE;
        return StoragePath.get(fileType, path.toString());
    }

    @Override
    @SneakyThrows
    public InputStream getContent() {
        if(inputStream != null) inputStream.close();

        inputStream = new FileInputStream(path.toString());
        return inputStream;
    }

    @Override
    @SneakyThrows
    public long lastModified() {
        return Files.getLastModifiedTime(path).toMillis();
    }

    @Override
    @SneakyThrows
    public long size() {
        return Files.size(path);
    }

    @Override
    public void close() throws IOException {
        if(inputStream != null) inputStream.close();
        path = null;
    }
}
