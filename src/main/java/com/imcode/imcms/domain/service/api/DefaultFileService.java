package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.FileService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;

    @Value(".")
    private Path rootPath;

    public DefaultFileService(DocumentService<DocumentDTO> documentService) {
        this.documentService = documentService;
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
            log.error("FINALNORMALIZE " + finalNormalize);
            log.error("ROOT PATH ON CHECK! " + rootPaths.get(0));
            log.error("FILE EXIST GET path from client?? " + Files.exists(Paths.get(finalNormalize)));
            log.error("FILE EXIST GET path from WITH / exists?? " + Files.exists(Paths.get("/" + finalNormalize)));
            log.error("ROOT FILE EXIST ??" + Files.exists(rootPaths.get(0)));
            throw new FileAccessDeniedException("File access denied!");
        }
    }

    private SourceFile toSourceFile(Path path) {
        final SourceFile.FileType fileType = Files.isDirectory(path) ? DIRECTORY : FILE;
        return new SourceFile(path.getFileName().toString(), path.toString(), fileType);
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
    public Path getFile(Path file, byte[] content) throws IOException {
        if (isAllowablePath(file) && Files.exists(file)) {
            return file;
        } else {
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public List<DocumentDTO> getDocumentsByTemplatePath(Path template) throws IOException {
        if (isAllowablePath(template) && Files.exists(template)) {
            final String templateName = FilenameUtils.removeExtension(template.getFileName().toString());
            return documentService.getDocumentsByTemplateName(templateName);
        } else {
            throw new NoSuchFileException("File is not exist!");
        }
    }

    @Override
    public void deleteFile(Path file) throws IOException {
        if (isAllowablePath(file)) {
            Files.delete(file);
        }
    }

    @Override
    public List<Path> moveFile(List<Path> src, Path target) throws IOException {
        final List<Path> path = new ArrayList<>();
        for (Path srcPath : src) {
            if (isAllowablePath(srcPath) && isAllowablePath(target)) {
                path.add(Files.move(srcPath, target.resolve(srcPath.getFileName())));
            }
        }
        return path;
    }


    @Override
    public SourceFile moveFile(Path src, Path target) throws IOException {
        SourceFile sourceFile = new SourceFile();
        if (isAllowablePath(src) && isAllowablePath(target)) {
            final Path path = Files.move(src, target);
            sourceFile = toSourceFile(path);
        }
        return sourceFile;
    }

    @Override
    public SourceFile copyFile(Path src, Path target) throws IOException {
        SourceFile newFile = new SourceFile();
        if (isAllowablePath(src) && isAllowablePath(target)) {
            Path path = Files.copy(src, target.resolve(src.getFileName()));
            newFile = toSourceFile(path);
        }
        return newFile;
    }

    @Override
    public Path saveFile(Path location, byte[] content, OpenOption writeMode) throws IOException {
        Path path = null;
        if (isAllowablePath(location)) {
            if (null == writeMode) {
                path = Files.write(location, content);
            } else {
                path = Files.write(location, content, writeMode);
            }
        }
        return path;
    }

    @Override
    public SourceFile createFile(SourceFile file, boolean isDirectory) throws IOException {
        Path filePath = Paths.get(file.getFullPath());
        SourceFile newSrcFile = new SourceFile();

        if (isAllowablePath(filePath)) {
            Path newSrcFilePath;
            if (isDirectory) {
                newSrcFilePath = Files.createDirectory(filePath);
            } else {
                newSrcFilePath = Files.createFile(filePath);
            }
            newSrcFile = toSourceFile(newSrcFilePath);
        }

        return newSrcFile;
    }
}
