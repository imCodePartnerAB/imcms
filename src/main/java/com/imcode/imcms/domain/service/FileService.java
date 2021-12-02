package com.imcode.imcms.domain.service;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.DocumentDTO;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

public interface FileService {

    List<SourceFile> getRootFolders();

    List<SourceFile> getFiles(Path file) throws IOException;

    SourceFile getFile(Path file) throws IOException;

    List<DocumentDTO> getDocumentsByTemplatePath(Path template) throws IOException;

    void deleteFile(Path file) throws IOException;

    /**
     * @param src    - list paths files
     * @param target - path directory in which need move src
     *               All files will be save their name !
     */
    List<SourceFile> moveFile(List<Path> src, Path target) throws IOException;

    SourceFile renameFile(Path src, String newName) throws IOException;

    // Multiple copy files
    List<SourceFile> copyFile(List<Path> src, Path target) throws IOException;

    /**saveFile with param byte array need for download file any type files, as
     * saveFile with param list can't save any contents different files.
     * For example:  image, gif, pdf and etc..
     *
     * @param location - path where is the file
     * @param contents - current content file
     * @return
     * @throws IOException
     */
    SourceFile saveFile(Path location, byte[] contents, OpenOption writeMode) throws IOException;

    SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException;
}
