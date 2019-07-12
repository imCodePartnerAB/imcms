package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.api.DefaultFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.regex.Pattern.compile;
import static ucar.httpservices.HTTPAuthStore.log;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final Pattern FILE_NAME_PATTERN = compile("(.*?\\/files\\/)(?<path>.*)");

    private final DefaultFileService defaultFileService;

    public FileController(DefaultFileService defaultFileService) {
        this.defaultFileService = defaultFileService;
    }

    private String getFileName(String path, String endPointName) {

        log.info("GET FILE NAME uri request: " + path);
        Matcher matcher = FILE_NAME_PATTERN.matcher(path);
        String extractedPath = null;
        if (matcher.matches()) {
            if (endPointName.isEmpty()) {
                extractedPath = matcher.group("path");
            } else {
                extractedPath = matcher.group("path").substring(endPointName.length() - 1);
            }
        }
        log.info("EXTRACT PATH form controller " + extractedPath);
        return extractedPath;
    }

    @GetMapping("/**")
    public List<SourceFile> getFiles(HttpServletRequest request) throws IOException {
        String fileURI = getFileName(request.getRequestURI(), "");
        List<SourceFile> files;
        if (null == fileURI || fileURI.isEmpty()) {
            files = defaultFileService.getRootFiles();
        } else {
            if (!Files.exists(Paths.get(fileURI))) {
                fileURI = "/" + fileURI;
            }
            files = defaultFileService.getFiles(Paths.get(fileURI));
        }

        return files;
    }

    @GetMapping("/file/**")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "/file/");
        final Path pathFile = Paths.get(fileURI);
        byte[] content = Files.readAllBytes(pathFile);
        final Path path = defaultFileService.getFile(pathFile, null);

        return ResponseEntity.ok()
                .contentLength(content.length)
                .header(HttpHeaders.CONTENT_TYPE, new MimetypesFileTypeMap().getContentType(path.toFile()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName())
                .body(content);
    }

    @GetMapping("/documents")
    public List<DocumentDTO> getDocumentsByTemplatePath(@RequestParam Path template) throws IOException {
        return defaultFileService.getDocumentsByTemplatePath(template);
    }

    @PostMapping("/upload/**")
    public List<SourceFile> uploadFile(MultipartHttpServletRequest request) throws IOException {
        final List<MultipartFile> files = new ArrayList<>(request.getFileMap().values());
        final String targetDirectory = request.getParameter("targetDirectory");

        final List<SourceFile> sourceFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            final Path fileName = Paths.get(file.getName());
            final Path target = Paths.get(targetDirectory);
            SourceFile sourceFile = new SourceFile(fileName.toString(), target.resolve(fileName).toString(), FILE);

            defaultFileService.saveFile(Paths.get(sourceFile.getFullPath()), file.getBytes(), CREATE_NEW);

            sourceFiles.add(sourceFile);
        }
        return sourceFiles;
    }

    @PostMapping("/**")
    public SourceFile createFile(@RequestBody SourceFile sourceFile) throws IOException {
        boolean isDirectory = sourceFile.getFileType().equals(DIRECTORY);
        return defaultFileService.createFile(sourceFile, isDirectory);
    }

    @PostMapping("/copy/**")
    public SourceFile copyFile(@RequestBody Properties pathParam) throws IOException {
        final Path src = Paths.get(pathParam.getProperty("src"));
        final Path target = Paths.get(pathParam.getProperty("target"));
        return defaultFileService.copyFile(src, target);
    }

    @PutMapping("/**")
    public String saveFile(HttpServletRequest request, @RequestBody byte[] newContent) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "");
        byte[] content = Files.readAllBytes(Paths.get(fileURI));
        final Path path = defaultFileService.getFile(Paths.get(fileURI), content);
        return defaultFileService.saveFile(path, newContent, null).toString();
    }

    @PutMapping("/move/**")
    public SourceFile moveFile(@RequestBody Properties pathParam) throws IOException {
        final Path src = Paths.get(pathParam.getProperty("src"));
        final Path target = Paths.get(pathParam.getProperty("target"));
        return defaultFileService.moveFile(src, target);
    }

    @PutMapping("/rename/**")
    public SourceFile renameFile(@RequestBody Properties pathParam) throws IOException {
        final Path src = Paths.get(pathParam.getProperty("src"));
        final Path target = Paths.get(pathParam.getProperty("target"));
        return defaultFileService.moveFile(src, target);
    }

    @DeleteMapping("/**")
    public void deleteFile(HttpServletRequest request) throws IOException {
        final String file = getFileName(request.getRequestURI(), "");
        defaultFileService.deleteFile(Paths.get(file));
    }
}
