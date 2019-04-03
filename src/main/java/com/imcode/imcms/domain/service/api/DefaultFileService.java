package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.domain.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
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


    private Path isAllowablePath(Path path) {
        Path normalize = path.normalize();
        long count = rootPaths.stream()
                .map(Path::toString)
                .filter(normalize::startsWith)
                .count();
        if (count > 0) {
            return normalize;
        } else {
            throw new FileAccessDeniedException("File access denied!");
        }
    }

    @Override
    public List<Path> getFiles(Path file) throws IOException {
        List<Path> paths = Files.list(isAllowablePath(file)).collect(Collectors.toList());
        return paths;
    }

    @Override
    public Path getFile(Path file) throws IOException {
        if (Files.exists(isAllowablePath(file))) {
            return Paths.get(isAllowablePath(file).toString());
        } else {
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public void deleteFile(Path file) throws IOException {

        Files.delete(isAllowablePath(file));

    }

    @Override
    public Path moveFile(Path src, Path target) throws IOException {
        return Files.move(isAllowablePath(src), isAllowablePath(target));
    }

    @Override
    public Path copyFile(Path src, Path target) throws IOException {
        return Files.copy(isAllowablePath(src), isAllowablePath(target));
    }

    @Override
    public Path saveFile(Path file, boolean canOverWrite) throws IOException {
        if (canOverWrite) {
            byte[] bytesFile = Files.readAllBytes(file);
            return Files.write(isAllowablePath(file), bytesFile);
        } else {
            throw new FileAlreadyExistsException("File already exists!");
        }
    }

    @Override
    public Path createFile(Path file) throws IOException {
        if (Files.isDirectory(isAllowablePath(file))) {
            return Files.createDirectory(file);
        } else {
            return Files.createFile(isAllowablePath(file));
        }
    }
}
