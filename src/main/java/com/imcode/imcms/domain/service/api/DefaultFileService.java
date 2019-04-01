package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultFileService implements FileService {

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;


    private boolean isAllowablePath(Path path) {
        List<String> rootFilesName = rootPaths.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        for (String fileName : rootFilesName) {
            if (path.toString().startsWith(fileName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Path> getFiles(Path file) throws IOException {
        List<Path> paths = Files.list(file).collect(Collectors.toList());
        return paths;
    }

    @Override
    public Path getFile(Path file) {
        return Paths.get(file.toString());
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        Files.delete(file);
    }

    @Override
    public Path moveFile(Path src, Path target) throws IOException {
        if (isAllowablePath(src) && isAllowablePath(target)) {
            return Files.move(src, target);
        } else {
            throw new NoSuchFileException("File not allowed move!");
        }
    }

    @Override
    public Path copyFile(Path src, Path target) throws IOException {
        if (isAllowablePath(src) && isAllowablePath(target)) {
            return Files.copy(src, target);
        } else {
            throw new NoSuchFileException("File not allowed copy!");
        }
    }

    @Override
    public Path saveFile(Path file) { // fix

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
            throw new NoSuchFileException("Do not allow to create file!");
        }
    }
}
