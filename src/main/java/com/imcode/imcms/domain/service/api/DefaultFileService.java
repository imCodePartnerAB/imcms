package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        return count > 0;
    }

    @Override
    public List<Path> getFiles(Path file) throws IOException {
        if (isAllowablePath(file)) {
            List<Path> paths = Files.list(file).collect(Collectors.toList());
            return paths;
        } else {
            throw new AccessDeniedException("Files not allowed show!");
        }
    }

    @Override
    public Path getFile(Path file) throws IOException {
        if (isAllowablePath(file)) {
            return Paths.get(file.toString());
        } else {
            throw new AccessDeniedException("File not allowed shows!");
        }
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        if (isAllowablePath(file)) {
            Files.delete(file);
        } else {
            throw new AccessDeniedException("File not allowed delete!");
        }
    }

    @Override
    public Path moveFile(Path src, Path target) throws IOException {
        if (isAllowablePath(src) && isAllowablePath(target)) {
            return Files.move(src, target);
        } else {
            throw new AccessDeniedException("File not allowed move!");
        }
    }

    @Override
    public Path copyFile(Path src, Path target) throws IOException {
        if (isAllowablePath(src) && isAllowablePath(target)) {
            return Files.copy(src, target);
        } else {
            throw new AccessDeniedException("File not allowed copy!");
        }
    }

    @Override
    public Path saveFile(Path file) { // todo fix
        return file.resolve(file.getFileName());
    }

    @Override
    public Path createFile(Path file) throws IOException {
        if (isAllowablePath(file)) {
            if (Files.isDirectory(file)) {
                return Files.createDirectory(file);
            } else {
                return Files.createFile(file);
            }
        } else {
            throw new AccessDeniedException("Do not allow to create file!");
        }
    }
}
