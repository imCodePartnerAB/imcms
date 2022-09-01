package com.imcode.imcms.storage.impl.disk;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.ConflictFileTypeException;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

public class DiskStorageClient implements StorageClient {

    final Path rootPath;

    public DiskStorageClient(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public StorageFile getFile(StoragePath path) throws StorageFileNotFoundException {
        Path filePath = resolveRoot(path);

        if(!Files.exists(filePath)){
            throw new StorageFileNotFoundException(path);
        }

        return new DiskStorageFile(filePath);
    }

    @Override
    @SneakyThrows
    public List<StoragePath> listPaths(StoragePath path) {
        if(!exists(path)) throw new StorageFileNotFoundException(path);
        try(Stream<Path> list = Files.list(resolveRoot(path))){
            return list.map(this::pathToStoragePath).collect(Collectors.toList());
        }
    }

    @Override
    @SneakyThrows
    public List<StoragePath> walk(StoragePath path) {
        if(!exists(path)) throw new StorageFileNotFoundException(path);

        try(Stream<Path> walk = Files.walk(resolveRoot(path))){
            return walk.map(this::pathToStoragePath).collect(Collectors.toList());
        }
    }

    @Override
    public boolean exists(StoragePath path) {
        return Files.exists(resolveRoot(path));
    }

    @Override
    @SneakyThrows
    public void create(StoragePath path) {
        Path filePath = resolveRoot(path);

        if(path.getType() == DIRECTORY){
            Files.createDirectory(filePath);
        }else{
            Files.createFile(filePath);
        }
    }

    @Override
    @SneakyThrows
    public void put(StoragePath path, InputStream inputStream) {
        FileUtils.copyInputStreamToFile(inputStream, resolveRoot(path).toFile());
    }

    @Override
    @SneakyThrows
    public void move(StoragePath fromPath, StoragePath toPath) {
        if (fromPath.getType() != toPath.getType()) throw new ConflictFileTypeException();
        Files.move(resolveRoot(fromPath), resolveRoot(toPath));
    }

    @Override
    @SneakyThrows
    public void copy(StoragePath sourcePath, StoragePath toPath) {
        if (sourcePath.getType() != toPath.getType()) throw new ConflictFileTypeException();

        if(sourcePath.getType() == DIRECTORY){
            FileUtils.copyDirectory(resolveRoot(sourcePath).toFile(), resolveRoot(toPath).toFile());
        }else{
            Files.copy(resolveRoot(sourcePath), resolveRoot(toPath));
        }
    }

    @Override
    public boolean canPut(StoragePath path) {
        return Files.isWritable(resolveRoot(path));
    }

    @Override
    @SneakyThrows
    public void delete(StoragePath path, boolean force) {
        Path filePath = resolveRoot(path);

        if(Files.isDirectory(filePath) && force){
            FileUtils.forceDelete(filePath.toFile());
        }else{
            Files.delete(filePath);
        }
    }

    private Path resolveRoot(StoragePath path){
        return rootPath.resolve(path.toString());
    }

    private StoragePath pathToStoragePath(Path path){
        SourceFile.FileType type = Files.isDirectory(path) ? DIRECTORY : FILE;
        return StoragePath.get(type, rootPath.relativize(path).toString());
    }
}
