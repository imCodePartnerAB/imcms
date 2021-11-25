package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.exception.TemplateFileException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.FileService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static ucar.httpservices.HTTPAuthStore.log;

@Service
@Transactional
public class DefaultFileService implements FileService {

    private final DocumentService<DocumentDTO> documentService;

    private final TemplateService templateService;

    private final BiFunction<Path, Boolean, SourceFile> fileToSourceFile;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;

    @Value("${rootPath}")
    private Path rootPath;

    @Autowired
    public DefaultFileService(DocumentService<DocumentDTO> documentService,
                              TemplateService templateService,
                              BiFunction<Path, Boolean, SourceFile> fileToSourceFile) {
        this.documentService = documentService;
        this.templateService = templateService;
        this.fileToSourceFile = fileToSourceFile;
    }

    private void checkAccessAllowable(Path path) {
        String normalizedPath = path.normalize().toString();
        final String finalNormalize = StringUtils.isBlank(normalizedPath)
                ? rootPath.toString()
                : normalizedPath;

        boolean access = false;
        for (Path pathRoot : rootPaths) {
            try {
                access = Files.walk(pathRoot).anyMatch(pathWalk -> Paths.get(finalNormalize).startsWith(pathWalk));
                if (access) break;
            } catch (IOException e) {
                log.warn("There is no such file in the project. Got path: " + pathRoot);
            }
        }

        if (!access) {
            log.error("File access denied! Got path: " + path);
            throw new FileAccessDeniedException("File access denied!");
        }
    }

    @Override
    public List<SourceFile> getRootFiles() {
        return rootPaths.stream()
                .filter(Files::exists)
                .map(path -> fileToSourceFile.apply(path, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<SourceFile> getFiles(Path path) throws IOException {
        checkAccessAllowable(path);

        List<Path> paths = Files.isDirectory(path) ? Files.list(path).sorted().collect(Collectors.toList()) : Collections.EMPTY_LIST;
        return paths.stream()
                .map(filePath -> {
                    final SourceFile sourceFile = fileToSourceFile.apply(filePath, false);
                    if (isTemplatePath(filePath) && !Files.isDirectory(filePath)) {
                        sourceFile.setNumberOfDocuments(getDocumentsByTemplatePath(filePath).size());
                    }
                    return sourceFile;
                })
                .sorted(Comparator.comparing(SourceFile::getFileType))
                .collect(Collectors.toList());
    }

    @Override
    public SourceFile getFile(Path file) throws IOException {
        checkAccessAllowable(file);
        if (Files.exists(file)) {
            return fileToSourceFile.apply(file, true);
        } else {
            log.error("File doesn't exist: " + file);
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public List<DocumentDTO> getDocumentsByTemplatePath(Path template) {
        return documentService.getDocumentsByTemplateName(FilenameUtils.removeExtension(template.getFileName().toString()));
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        checkAccessAllowable(file);

        if (Files.isDirectory(file)) {
            if(isTemplatePath(file)) throwTemplateException("Folder with templates can't be deleted!  Got path:" + file);

            Files.list(file)
                    .map(Path::toFile)
                    .forEach(FileUtils::deleteRecursive);
            Files.delete(file);
        } else {
            if(isTemplatePath(file)) deleteTemplate(file);

            Files.delete(file);
        }
    }

    @Override
    public List<SourceFile> moveFile(List<Path> src, Path target) throws IOException {
        src.forEach(this::checkAccessAllowable);
        src.forEach(path -> {
            if(isTemplatePath(path)) throwTemplateException("Folder with templates or templates cannot be moved! Got path: " + path);
        });
        checkAccessAllowable(target);
        if(isTemplatePath(target)) throwTemplateException("Files cannot be moved to the folder with templates! Got path: " + target);

        final List<SourceFile> files = new ArrayList<>();
        for (Path srcPath : src) {
            files.add(fileToSourceFile.apply(Files.move(srcPath, target.resolve(srcPath.getFileName())), false));
        }
        return files;
    }

    @Override
    public SourceFile renameFile(Path src, String newName) throws IOException {
        checkAccessAllowable(src);

        if (StringUtils.isNotBlank(src.toString()) && StringUtils.isNotBlank(newName)) {
            final Path path = Files.move(src, Paths.get(src.getParent().toString(), newName));

            try{
                if(Files.isDirectory(path) && isTemplatePath(src)) throwTemplateException("Folder with templates can't be deleted!");
                if(isTemplatePath(src)) renameTemplate(src, newName);
            }catch(TemplateFileException e){
                Files.move(path, src);
                throw e;
            }

            return fileToSourceFile.apply(path, false);
        } else {
            final String errorMessage = "Filepath or filename is empty!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public List<SourceFile> copyFile(List<Path> src, Path target) throws IOException {
        src.forEach(this::checkAccessAllowable);
        src.forEach(path -> {
            if(isTemplatePath(path)) throwTemplateException("Folder with templates or templates cannot be copied! Got path: " + path);
        });
        checkAccessAllowable(target);
        if(isTemplatePath(target)) throwTemplateException("Files cannot be copied to the folder with templates! Got path: " + target);

        final List<SourceFile> files = new ArrayList<>();
        for (Path srcPath : src) {
            files.add(fileToSourceFile.apply(
                    Files.copy(srcPath, target.resolve(srcPath.getFileName())), false));
        }
        return files;
    }

    @Override
    public SourceFile saveFile(Path location, byte[] content, OpenOption writeMode) throws IOException {
        checkAccessAllowable(location);

        if (StringUtils.isNotBlank(location.toString())) {
            Path writeFilePath = writeMode == null ? Files.write(location, content) : Files.write(location, content, writeMode);

            if(isTemplatePath(location) && Files.exists(location)) createAndSaveTemplate(location);

            return fileToSourceFile.apply(writeFilePath, true);
        } else {
            final String errorMessage = "Filepath is empty!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException {
        final Path filePath = Paths.get(file.getFullPath());
        if (StringUtils.isNotBlank(file.getFileName())) {
            checkAccessAllowable(filePath);

            Path newSrcFilePath;
            if (isDirectory) {
                if(isTemplatePath(filePath)) throwTemplateException("Folder cannot be created in the folder with templates");
                newSrcFilePath = Files.createDirectory(filePath);
            } else {
                newSrcFilePath = Files.createFile(filePath);
                if(isTemplatePath(filePath) && Files.exists(newSrcFilePath)) createAndSaveTemplate(filePath);
            }

            return fileToSourceFile.apply(newSrcFilePath, false);
        } else {
            final String errorMessage = "Filename is empty!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    private boolean isTemplatePath(Path path) {
        return path.startsWith(templateService.getTemplateDirectory().toPath());
    }

    private void createAndSaveTemplate(Path path) throws IOException {
        if(templateService.isValidName(path.getFileName().toString())){
            final String templateName = FilenameUtils.removeExtension(path.getFileName().normalize().toString());
            final TemplateJPA templateJPA = new TemplateJPA();
            templateJPA.setName(templateName);
            templateJPA.setHidden(false);

            try{
                templateService.save(templateJPA);
            }catch (DataIntegrityViolationException e){
                Files.delete(path);
                throwTemplateException("Template with the same name already exists!");
            }
        }
    }

    private void renameTemplate(Path path, String newName){
        if(templateService.isValidName(newName)){
            final String oldTemplateName = FilenameUtils.removeExtension(path.getFileName().normalize().toString());
            templateService.renameTemplate(oldTemplateName, FilenameUtils.removeExtension(newName));
        }else{
            deleteTemplate(path);
        }
    }

    private void deleteTemplate(Path path){
        final String orgTemplateName = FilenameUtils.removeExtension(path.getFileName().toString());
        if(documentService.getDocumentsByTemplateName(orgTemplateName).isEmpty()){
            final Template template = templateService.get(orgTemplateName);
            if(template != null) templateService.delete(template.getId());
        }else{
            throwTemplateException("Template is used in documents, cannot be deleted! Got path: " + path);
        }
    }

    private void throwTemplateException(String errorMessage){
        log.error(errorMessage);
        throw new TemplateFileException(errorMessage);
    }
}