package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.service.FileService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class DefaultFileService implements FileService {


    @Override
    public List<Path> getFiles(Path file) {
        return null;
    }

    @Override
    public Path getFile(Path file) {
        return null;
    }

    @Override
    public void deleteFile(Path file) {

    }

    @Override
    public Path moveFile(Path src, Path target) {
        return null;
    }

    @Override
    public Path copyFile(Path src, Path target) {
        return null;
    }

    @Override
    public Path saveFile(Path file) {
        return null;
    }

    @Override
    public Path createFile(Path file) {
        return null;
    }
}
