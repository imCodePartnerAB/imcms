package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.FileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultFileService implements FileService {


    @Override
    public List<Path> getFiles(Path file) throws IOException {
        List<Path> paths = new ArrayList<>();
        Files.list(file).forEach(paths::add);
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
        return Files.move(src, target.resolve(src.getFileName()));
    }

    @Override
    public Path copyFile(Path src, Path target) throws IOException {
        return Files.copy(src, target.resolve(src.getFileName()));
    }

    @Override
    public Path saveFile(Path file) {

        return file.resolve(file.getFileName());
    }

    @Override
    public Path createFile(Path file) throws IOException {
        if (Files.isDirectory(file)) {
            return Files.createDirectory(file);
        } else {
            return Files.createFile(file);
        }
    }
}
