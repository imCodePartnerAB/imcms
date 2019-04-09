package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.domain.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultFileService implements FileService {

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;


    private boolean isAllowablePath(Path path) {
        Path normalize = path.normalize();
        long count = rootPaths.stream()
                .map(Path::toString)
                .filter(normalize::startsWith)
                .count();
        if (count > 0) {
            return true;
        } else {
            throw new FileAccessDeniedException("File access denied!");
        }
    }

    @Override
    public List<Path> getFiles(Path file) throws IOException {
        List<Path> paths;
        if (isAllowablePath(file)) {
            paths = Files.list(file).collect(Collectors.toList());
        } else {
            paths = Collections.EMPTY_LIST;
        }
        return paths;
    }

    @Override
    public Path getFile(Path file) throws IOException { //todo should we return byte[]
        if (isAllowablePath(file) && Files.exists(file)) {
            return file;
        } else {
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        if (isAllowablePath(file)) {
            Files.delete(file);
        }
    }

    @Override
    public Path moveFile(Path src, Path target) throws IOException {
        Path path = null;
        if (isAllowablePath(src) && isAllowablePath(target)) {
            path = Files.move(src, target);
        }
        return path;
    }

    @Override
    public Path copyFile(Path src, Path target) throws IOException {
        Path path = null;
        if (isAllowablePath(src) && isAllowablePath(target)) {
            path = Files.copy(src, target);
        }
        return path;
    }

    @Override
    public Path saveFile(Path location, byte[] content, OpenOption writeMode) throws IOException {
        Path path = null;
        if (isAllowablePath(location)) {
            if (null == writeMode) {
                path = Files.write(location, content);
            } else {
                path = Files.write(location, content, writeMode);
            }
        }
        return path;
    }

    @Override
    public Path createFile(Path file, boolean isDirectory) throws IOException {
        Path path = null;
        if (isAllowablePath(file)) {
            if (isDirectory) {
                path = Files.createDirectory(file);
            } else {
                path = Files.createFile(file);
            }
        }

        return path;
    }
}
