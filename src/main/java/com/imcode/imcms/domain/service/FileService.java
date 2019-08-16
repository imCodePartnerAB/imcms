package com.imcode.imcms.domain.service;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.model.Template;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

public interface FileService {

    List<SourceFile> getRootFiles();

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

    /**
     * @param src    - path file which need rename
     * @param target - this is the final target path there will be a renamed src
     *               the src has the ability to move or just change your name
     */

    SourceFile moveFile(Path src, Path target) throws IOException;

    // Multiple copy files
    List<SourceFile> copyFile(List<Path> src, Path target) throws IOException;

    /**
     * saveFile with param list need for edit content files, which could have content.
     * Each rows in content in file will reading like String if it possible to;
     */

    SourceFile saveFile(Path location, List<String> contents, OpenOption writeMode) throws IOException;

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

    /** saveTemplateInGroup was created for save template file in template group,
     * if template file does not exist in db , so will create new templateJPA and will save in db,
     * with current set template group, else just will change template group from template file
     *
     * saved only original file name without extension!
     *
     * @param template - path template file
     * @param templateGroupName - template group name which will be save template file
     *
     */
    Template saveTemplateInGroup(Path template, String templateGroupName) throws IOException;

    SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException;

    void replaceDocsOnNewTemplate(Path oldTemplate, Path newTemplate);
}
