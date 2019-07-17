package com.imcode.imcms.domain.service;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.DocumentDTO;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

public interface FileService {

    List<SourceFile> getRootFiles();

    List<SourceFile> getFiles(Path file) throws IOException;

    Path getFile(Path file, byte[] content) throws IOException;

    List<DocumentDTO> getDocumentsByTemplatePath(Path template) throws IOException;

    void deleteFile(Path file) throws IOException;

    /**
     * @param src    - list paths files
     * @param target - path directory in which need move src
     *               All files will be save their name !
     */
    List<Path> moveFile(List<Path> src, Path target) throws IOException;

    /**
     * @param src    - path file which need rename
     * @param target - this is the final target path there will be a renamed src
     *               the src has the ability to move or just change your name
     */

    SourceFile moveFile(Path src, Path target) throws IOException;

    SourceFile copyFile(Path src, Path target) throws IOException;

    SourceFile saveFile(Path location, List<String> contents, OpenOption writeMode) throws IOException;

    SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException;
}
