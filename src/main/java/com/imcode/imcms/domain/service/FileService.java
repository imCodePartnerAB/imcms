package com.imcode.imcms.domain.service;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

public interface FileService {

    List<Path> getRootFiles();

    List<Path> getFiles(Path file) throws IOException;

    Path getFile(Path file) throws IOException;

    void deleteFile(Path file) throws IOException;

    List<Path> moveFile(List<Path> src, Path target, String newName) throws IOException;

    List<Path> copyFile(List<Path> src, Path target) throws IOException;

    Path saveFile(Path location, byte[] content, OpenOption writeMode) throws IOException;

    Path createFile(Path file, boolean isDirectory) throws IOException;
}
