package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.FileService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static ucar.httpservices.HTTPAuthStore.log;

@Service
public class DefaultFileService implements FileService {

    private final DocumentService<DocumentDTO> documentService;

    private final TemplateRepository templateRepository;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;

    @Value(".")
    private Path rootPath;

    @Autowired
    public DefaultFileService(DocumentService<DocumentDTO> documentService,
                              TemplateRepository templateRepository) {
        this.documentService = documentService;
        this.templateRepository = templateRepository;

    }

    private boolean isAllowablePath(Path path) {
        String normalizedPath = path.normalize().toString();
        final String finalNormalize = StringUtils.isBlank(normalizedPath)
                ? rootPath.toString()
                : normalizedPath;
        long countMatches = 0;
        for (Path pathRoot : rootPaths) {
            try {
                countMatches += Files.walk(pathRoot)
                        .filter(pathWalk -> Paths.get(finalNormalize).startsWith(pathWalk))
                        .count();
            } catch (IOException e) {
                e.getMessage();
                continue;
            }
        }

        if (countMatches > 0) {
            return true;
        } else {
            log.error("File access denied ! Got path: " + path);
            throw new FileAccessDeniedException("File access denied!");
        }
    }

    private SourceFile toSourceFile(Path path) {
        final SourceFile.FileType fileType = Files.isDirectory(path) ? DIRECTORY : FILE;
        List<String> contents;
        try {
            contents = Files.readAllLines(path);
        } catch (IOException e) {
            log.info("File has not content!!!");
            contents = null;
        }
        return new SourceFile(path.getFileName().toString(), path.toString(), fileType, contents);
    }

    private String getPathWithoutExtension(String fileName) {
        return FilenameUtils.removeExtension(fileName);
    }

    @Override
    public List<SourceFile> getRootFiles() {
        return rootPaths.stream()
                .filter(path -> Files.exists(Paths.get(path.toString())))
                .map(this::toSourceFile)
                .collect(Collectors.toList());
    }

    @Override
    public List<SourceFile> getFiles(Path path) throws IOException {
        List<Path> paths;
        if (isAllowablePath(path) && Files.isDirectory(path)) {
            paths = Files.list(path)
                    .collect(Collectors.toList());
        } else {
            paths = Collections.EMPTY_LIST;
        }
        return paths.stream()
                .map(this::toSourceFile)
                .collect(Collectors.toList());
    }

    @Override
    public Path getFile(Path file) throws IOException {
        if (isAllowablePath(file) && Files.exists(file)) {
            return file;
        } else {
            log.error("File doesn't exist: " + file);
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public List<DocumentDTO> getDocumentsByTemplatePath(Path template) throws IOException {
        if (isAllowablePath(template) && Files.exists(template)) {
            final String templateName = getPathWithoutExtension(template.getFileName().toString());
            return documentService.getDocumentsByTemplateName(templateName);
        } else {
            log.error("Template file doesn't exist: " + template);
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        if (isAllowablePath(file) && Files.isDirectory(file)) {
            Files.list(file)
                    .map(Path::toFile)
                    .forEach(FileUtils::deleteRecursive);

            Files.delete(file);
        } else {
            Files.delete(file);
        }
    }

    @Override
    public List<SourceFile> moveFile(List<Path> src, Path target) throws IOException {
        final List<SourceFile> files = new ArrayList<>();
        for (Path srcPath : src) {
            if (isAllowablePath(srcPath) && isAllowablePath(target)) {
                files.add(toSourceFile(
                        Files.move(srcPath, target.resolve(srcPath.getFileName()))
                ));
            }
        }
        return files;
    }


    @Transactional
    @Override
    public SourceFile moveFile(Path src, Path target) throws IOException {
        final String targetFileName = target.getFileName().toString();
        final String srcFileName = src.getFileName().toString();
        final String originalSrcName = getPathWithoutExtension(srcFileName);
        final String originalTargetName = getPathWithoutExtension(targetFileName);


        if (isAllowablePath(src) && isAllowablePath(target) && StringUtils.isNotBlank(targetFileName)) {
            if (null != templateRepository.findByName(originalSrcName)) {
                TemplateJPA template = templateRepository.findByName(originalSrcName);
                template.setName(originalTargetName);
                templateRepository.save(new TemplateJPA(template));
            }
            final Path path = Files.move(src, target);
            return toSourceFile(path);
        } else {
            final String errorMessage = "File couldn't has empty Name !";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public List<SourceFile> copyFile(List<Path> src, Path target) throws IOException {
        final List<SourceFile> files = new ArrayList<>();
        for (Path srcPath : src) {
            if (isAllowablePath(srcPath) && isAllowablePath(target)) {
                files.add(toSourceFile(
                        Files.copy(srcPath, target.resolve(srcPath.getFileName()))
                ));
            }
        }
        return files;
    }

    @Override
    public SourceFile saveFile(Path location, List<String> content, OpenOption writeMode) throws IOException {
        Path writeFilePath = null;
        if (isAllowablePath(location)) {
            if (null == writeMode) {
                writeFilePath = Files.write(location, content);
            } else {
                writeFilePath = Files.write(location, content, writeMode);
            }
        }

        return toSourceFile(writeFilePath);
    }

    @Override
    public SourceFile saveFile(Path location, byte[] content, OpenOption writeMode) throws IOException {
        Path writeFilePath;
        if (isAllowablePath(location)) {
            if (null == writeMode) {
                writeFilePath = Files.write(location, content);
            } else {
                writeFilePath = Files.write(location, content, writeMode);
            }
            return toSourceFile(writeFilePath);
        } else {
            final String errorMessage = "File name is empty! Can't save empty file name!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public Template saveTemplateInGroup(Path template, TemplateGroup templateGroup) {
        final String templateName = template.getFileName().normalize().toString();
        final String originalName = getPathWithoutExtension(templateName);
        final TemplateJPA templateJPA = templateRepository.findByName(originalName);
        if (templateJPA != null) {
            templateJPA.setTemplateGroup(templateGroup);
            return new TemplateDTO(templateRepository.save(templateJPA));
        } else {
            TemplateJPA newTemplateJPA = new TemplateJPA();
            newTemplateJPA.setName(originalName);
            newTemplateJPA.setHidden(false);
            newTemplateJPA.setTemplateGroup(templateGroup);
            return new TemplateDTO(templateRepository.save(newTemplateJPA));
        }
    }


    @Override
    public SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException {
        final Path filePath = Paths.get(file.getFullPath());
        final String fileName = FilenameUtils.removeExtension(filePath.getFileName().normalize().toString());

        if (isAllowablePath(filePath) && StringUtils.isNotBlank(fileName)) {
            Path newSrcFilePath;
            if (isDirectory) {
                newSrcFilePath = Files.createDirectory(filePath);
            } else {
                newSrcFilePath = Files.createFile(filePath);
            }
            return toSourceFile(newSrcFilePath);
        } else {
            final String errorMessage = "File name is empty! Can't create empty file name!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }
}

