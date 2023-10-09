package com.imcode.imcms.domain.service;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.exception.FileOperationFailureException;

import java.io.IOException;
import java.nio.file.Files;
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
    List<SourceFile> moveFile(List<Path> src, Path target) throws FileOperationFailureException, IOException;

    /**
     * Move file with new filename
     * @param src - file path to move
     * @param target - destination folder
     * @return path of moved file
     */
    SourceFile moveFileWithRename(Path src, Path target, String newFilename) throws FileOperationFailureException, IOException;

    SourceFile renameFile(Path src, String newName) throws IOException;

    /**
     * Rename existing file
     * @param path - file path
     * @return renamed file path
     * @throws IOException
     */
    SourceFile defaultRename(Path path) throws IOException;

    // Multiple copy files
    List<SourceFile> copyFile(List<Path> src, Path target) throws IOException, FileOperationFailureException;

    List<SourceFile> copyFile(List<Path> src, Path target, boolean overwrite) throws IOException, FileOperationFailureException;

    /**
     * Copy file with new filename
     */
    SourceFile copyFileWithRename(Path src, Path target, String newFilename) throws FileOperationFailureException, IOException;

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

    /**
     * Check if single file exists
     * @param path to check if exists
     * @return true or false
     */
    default boolean exists(String path) {
        return exists(Path.of(path));
    }
    
    default boolean exists(Path path){
        return Files.exists(path);
    }

    /**
     * Check if list of files exist
     * @param paths to check if exist
     * @return list of path that exist
     */
    List<SourceFile> existsAll(List<String> paths);
    
}
