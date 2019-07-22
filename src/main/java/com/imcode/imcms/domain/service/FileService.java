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

    Path getFile(Path file) throws IOException;

    List<DocumentDTO> getDocumentsByTemplatePath(Path template) throws IOException;

    void deleteFile(Path file) throws IOException;

    /**
     * @param src    - list paths files
     * @param target - path directory in which need move src
     *               All files will be save their name !
     */
    List<SourceFile> moveFile(List<Path> src, Path target) throws IOException;

    /**
     * @param src    - path file which need rename
     * @param target - this is the final target path there will be a renamed src
     *               the src has the ability to move or just change your name
     */

    SourceFile moveFile(Path src, Path target) throws IOException;

    SourceFile copyFile(Path src, Path target) throws IOException;

    /**
     * saveFile with param List<String> need for edit content files, which could have content.
     * Each rows in content in file will reading like String if it possible to;
     */

    SourceFile saveFile(Path location, List<String> contents, OpenOption writeMode) throws IOException;

    /**saveFile with param byte array need for download file any type files, as
     * saveFile with param List<String> can't save any contents different files.
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
