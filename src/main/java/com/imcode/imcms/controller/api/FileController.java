package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.SourceFile;
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

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.regex.Pattern.compile;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final Pattern FILE_NAME_PATTERN = compile("(.*?\\/files\\/)(?<path>.*)");

    private static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    private final DefaultFileService defaultFileService;

    public FileController(DefaultFileService defaultFileService) {
        this.defaultFileService = defaultFileService;
    }

    private String getFileName(String path, String endPointName) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(path);
        String extractedPath = null;
        if (matcher.matches()) {
            if (endPointName.isEmpty()) {
                extractedPath = matcher.group("path");
            } else {
                extractedPath = matcher.group("path").substring(endPointName.length() - 1);
            }
        }
        return extractedPath;
    }

    @GetMapping("/**")
    public List<SourceFile> getFiles(HttpServletRequest request) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "");
        List<Path> files;
        List<SourceFile> sourceFiles = new ArrayList<>();
        if (null == fileURI) {
            files = defaultFileService.getRootFiles();
        } else {
            files = defaultFileService.getFiles(Paths.get(fileURI));
        }

        for (Path path : files) {
            if (Files.isDirectory(path)) {
                sourceFiles.add(
                        new SourceFile(path.getFileName().toString(),
                                path.toString(),
                                SourceFile.FileType.DIRECTORY)
                );
            } else {
                sourceFiles.add(
                        new SourceFile(path.getFileName().toString(),
                                path.toString(),
                                FILE)
                );
            }
        }

        return sourceFiles;

    }

    @GetMapping("/file/**")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "/file/");
        final Path path = defaultFileService.getFile(Paths.get(fileURI));

        byte[] content = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .contentLength(content.length)
                .header(HttpHeaders.CONTENT_TYPE, new MimetypesFileTypeMap().getContentType(path.toFile()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName())
                .body(content);
    }

    @PostMapping("/upload/**")
    public String uploadFile(HttpServletRequest request,
                             @RequestParam MultipartFile file) throws IOException {

        final String destination = getFileName(request.getRequestURI(), "/upload/");
        final Path resolvePath = Paths.get(destination).resolve(file.getOriginalFilename());
        return defaultFileService.saveFile(resolvePath, file.getBytes(), CREATE_NEW).toString();
    }

    @PostMapping("/**")
    public SourceFile createFile(@RequestBody SourceFile sourceFile) throws IOException {
        boolean isDirectory = sourceFile.getFileType().equals(DIRECTORY);
        return defaultFileService.createFile(sourceFile, isDirectory);
    }

    @PostMapping("/copy/**")
    public String copyFile(@RequestBody Properties pathParam) throws IOException {
        final Path src = Paths.get(pathParam.getProperty("src"));
        final Path target = Paths.get(pathParam.getProperty("target"));
        return defaultFileService.copyFile(Collections.singletonList(src), target).toString();
    }

    @PutMapping("/**")
    public String saveFile(HttpServletRequest request, @RequestBody byte[] newContent) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "");
        final Path path = defaultFileService.getFile(Paths.get(fileURI));
        return defaultFileService.saveFile(path, newContent, null).toString();
    }

    @PutMapping("/move/**")
    public String moveFile(@RequestBody Properties pathParam) throws IOException {
        final Path src = Paths.get(pathParam.getProperty("src"));
        final Path target = Paths.get(pathParam.getProperty("target"));
        return defaultFileService.moveFile(Collections.singletonList(src), target).toString();
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
