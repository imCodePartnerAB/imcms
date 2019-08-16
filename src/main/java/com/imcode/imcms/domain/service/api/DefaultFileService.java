package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.FileService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import com.imcode.imcms.persistence.entity.TemplateGroupJPA;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateGroupRepository;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.util.FileUtils;
import org.modelmapper.ModelMapper;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static java.nio.file.StandardOpenOption.CREATE;
import static ucar.httpservices.HTTPAuthStore.log;

@Service
@Transactional
public class DefaultFileService implements FileService {

    private final DocumentService<DocumentDTO> documentService;

    private final TemplateRepository templateRepository; //todo maybe use only repo or service?

    private final TemplateGroupRepository templateGroupRepository;

    private final ModelMapper modelMapper;

    private final TemplateService templateService;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;

    @Value("${rootPath}")
    private Path rootPath;

    @Autowired
    public DefaultFileService(DocumentService<DocumentDTO> documentService,
                              TemplateRepository templateRepository,
                              TemplateGroupRepository templateGroupRepository,
                              ModelMapper modelMapper,
                              TemplateService templateService) {
        this.documentService = documentService;
        this.templateRepository = templateRepository;
        this.templateGroupRepository = templateGroupRepository;
        this.modelMapper = modelMapper;
        this.templateService = templateService;
    }

    private boolean isAllowableToAccess(Path path) {
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

        final Template receivedByTemplate = templateRepository.findByName(normalizedPath);
        if (countMatches > 0 || receivedByTemplate != null) {
            return true;
        } else {
            log.error("File access denied ! Got path: " + path);
            throw new FileAccessDeniedException("File access denied!");
        }
    }

    private String getPhysicalPath(Path path) {
        return path.toAbsolutePath().toString().substring(rootPath.toString().length());
    }

    private SourceFile toSourceFile(Path path, boolean withContent) {
        final SourceFile.FileType fileType = Files.isDirectory(path) ? DIRECTORY : FILE;
        final String physicalPath = getPhysicalPath(path);
        List<String> contents = fileType == DIRECTORY ? null : Collections.EMPTY_LIST;
        try {
            if (withContent) contents = Files.readAllLines(path);
        } catch (IOException e) {
            log.info("File has not content!!!");
            contents = null;
        }
        return new SourceFile(path.getFileName().toString(), physicalPath, path.toString(), fileType, contents);
    }

    private String getPathWithoutExtension(String fileName) {
        return FilenameUtils.removeExtension(fileName);
    }

    @Override
    public List<SourceFile> getRootFiles() {
        return rootPaths.stream()
                .filter(path -> Files.exists(Paths.get(path.toString())))
                .map(path -> toSourceFile(path, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<SourceFile> getFiles(Path path) throws IOException {
        List<Path> paths;
        if (isAllowableToAccess(path) && Files.isDirectory(path)) {
            paths = Files.list(path)
                    .collect(Collectors.toList());
        } else {
            paths = Collections.EMPTY_LIST;
        }
        return paths.stream()
                .map(filePath -> toSourceFile(filePath, false))
                .sorted(Comparator.comparing(SourceFile::getFileType))
                .collect(Collectors.toList());
    }

    @Override
    public SourceFile getFile(Path file) throws IOException {
        if (isAllowableToAccess(file) && Files.exists(file)) {
            return toSourceFile(file, true);
        } else {
            log.error("File doesn't exist: " + file);
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public List<DocumentDTO> getDocumentsByTemplatePath(Path template) throws IOException {
        final String templateName = template.getFileName().toString();
        final Template receivedTemplate = templateRepository.findByName(templateName);
        if (receivedTemplate != null && template.getParent() == null) {
            return documentService.getDocumentsByTemplateName(templateName);
        } else if (isAllowableToAccess(template) && Files.exists(template)) {
            final String originalTemplateName = getPathWithoutExtension(templateName);
            return documentService.getDocumentsByTemplateName(originalTemplateName);
        } else {
            log.error("Template file doesn't exist: " + template);
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        if (isAllowableToAccess(file) && Files.isDirectory(file)) {
            Files.list(file)
                    .map(Path::toFile)
                    .forEach(FileUtils::deleteRecursive);

            Files.delete(file);
        } else {
            final String orgTemplateName = getPathWithoutExtension(file.getFileName().toString());
            final Optional<Template> receivedTemplate = templateService.get(orgTemplateName);
            receivedTemplate.ifPresent(template -> {
                final Path pathInTemplateDir = templateService.getPhysicalPath(template.getName());
                if (pathInTemplateDir != null && pathInTemplateDir.equals(file)) {
                    templateService.delete(template.getId());
                }
            });
            Files.delete(file);
        }
    }

    @Override
    public List<SourceFile> moveFile(List<Path> src, Path target) throws IOException {
        final List<SourceFile> files = new ArrayList<>();
        for (Path srcPath : src) {
            if (isAllowableToAccess(srcPath) && isAllowableToAccess(target)) {
                files.add(toSourceFile(
                        Files.move(srcPath, target.resolve(srcPath.getFileName())), false
                ));
            }
        }
        return files;
    }


    @Override
    public SourceFile moveFile(Path src, Path target) throws IOException {
        final String targetFileName = target.getFileName().toString();
        final String srcFileName = src.getFileName().toString();
        final String originalSrcName = getPathWithoutExtension(srcFileName);
        final String originalTargetName = getPathWithoutExtension(targetFileName);
        final Template receivedTemplate = templateRepository.findByName(originalSrcName);
        final Path physicalTemplatePath = templateService.getPhysicalPath(originalSrcName);
        if (isAllowableToAccess(src) && isAllowableToAccess(target) && StringUtils.isNotBlank(targetFileName)) {
            if (null != receivedTemplate && src.equals(physicalTemplatePath)) {
                TemplateGroup gotTemplateGroup = receivedTemplate.getTemplateGroup();
                if (gotTemplateGroup != null) {
                    gotTemplateGroup.setTemplates(Collections.EMPTY_SET); // in order to avoid recursive error
                }
                receivedTemplate.setId(receivedTemplate.getId());
                receivedTemplate.setName(originalTargetName);
                receivedTemplate.setHidden(receivedTemplate.isHidden());
                receivedTemplate.setTemplateGroup(gotTemplateGroup);
                templateRepository.saveAndFlush(modelMapper.map(receivedTemplate, TemplateJPA.class));
            }
            final Path path = Files.move(src, target);
            return toSourceFile(path, false);
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
            if (isAllowableToAccess(srcPath) && isAllowableToAccess(target)) {
                files.add(toSourceFile(
                        Files.copy(srcPath, target.resolve(srcPath.getFileName())), false
                ));
            }
        }
        return files;
    }

    @Override
    public SourceFile saveFile(Path location, List<String> content, OpenOption writeMode) throws IOException {
        Path writeFilePath = null;
        if (isAllowableToAccess(location)) {
            if (null == writeMode) {
                writeFilePath = Files.write(location, content);
            } else {
                writeFilePath = Files.write(location, content, writeMode);
            }
        }

        return toSourceFile(writeFilePath, true);
    }

    @Override
    public SourceFile saveFile(Path location, byte[] content, OpenOption writeMode) throws IOException {
        Path writeFilePath;
        if (isAllowableToAccess(location)) {
            if (null == writeMode) {
                writeFilePath = Files.write(location, content);
            } else {
                writeFilePath = Files.write(location, content, writeMode);
            }
            return toSourceFile(writeFilePath, true);
        } else {
            final String errorMessage = "File name is empty! Can't save empty file name!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public Template saveTemplateInGroup(Path template, String templateGroupName) throws IOException {
        final String templateName = template.getFileName().normalize().toString();
        final String originalTemplateName = getPathWithoutExtension(templateName);
        if (isAllowableToAccess(template) && StringUtils.isNotBlank(templateName)) {
            final TemplateJPA receivedTemplate = templateRepository.findByName(originalTemplateName);
            final TemplateGroupJPA receivedTemplateGroup = templateGroupRepository.findByName(templateGroupName);
            byte[] contentTemplate = Files.readAllBytes(template);
            if (receivedTemplateGroup != null)
                receivedTemplateGroup.setTemplates(Collections.EMPTY_SET);// in order to avoid recursive error
            if (receivedTemplate != null) {
                receivedTemplate.setTemplateGroup(receivedTemplateGroup);
                templateService.saveTemplateFile(receivedTemplate, contentTemplate, CREATE);
                return new TemplateDTO(templateRepository.save(receivedTemplate));
            } else {
                TemplateJPA newTemplateJPA = new TemplateJPA();
                newTemplateJPA.setName(originalTemplateName);
                newTemplateJPA.setHidden(false);
                newTemplateJPA.setTemplateGroup(receivedTemplateGroup);
                templateService.saveTemplateFile(newTemplateJPA, contentTemplate, CREATE);
                return new TemplateDTO(templateRepository.saveAndFlush(newTemplateJPA));
            }
        } else {
            final String errorMessage = "File name is empty! Can't save empty file name template in group!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }


    @Override
    public SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException {
        final Path filePath = Paths.get(file.getFullPath());
        final String fileName = FilenameUtils.removeExtension(filePath.getFileName().normalize().toString());

        if (isAllowableToAccess(filePath) && StringUtils.isNotBlank(fileName)) {
            Path newSrcFilePath;
            if (isDirectory) {
                newSrcFilePath = Files.createDirectory(filePath);
            } else {
                newSrcFilePath = Files.createFile(filePath);
            }
            return toSourceFile(newSrcFilePath, false);
        } else {
            final String errorMessage = "File name is empty! Can't create empty file name!";
            log.error(errorMessage);
            throw new EmptyFileNameException(errorMessage);
        }
    }

    @Override
    public void replaceDocsOnNewTemplate(Path oldTemplate, Path newTemplate) {
        if (isAllowableToAccess(oldTemplate) && isAllowableToAccess(newTemplate)) {
            templateService.replaceTemplateFile(oldTemplate, newTemplate);
        }
    }
}

