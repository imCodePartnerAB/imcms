package com.imcode.imcms.domain.service;

import java.nio.file.Path;
import java.util.List;

public interface FileService {
    List<Path> getFiles(Path file);

    Path getFile(Path file);

    void deleteFile(Path file);

    Path moveFile(Path src, Path target);

    Path copyFile(Path src, Path target);

    Path saveFile(Path file);

    Path createFile(Path file);
}
