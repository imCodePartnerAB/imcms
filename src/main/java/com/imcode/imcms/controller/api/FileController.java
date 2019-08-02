package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.FileService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.regex.Pattern.compile;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final Pattern FILE_NAME_PATTERN = compile("(.*?\\/files\\/)(?<path>.*)");

    private final FileService defaultFileService;

    public FileController(FileService defaultFileService) {
        this.defaultFileService = defaultFileService;
    }

    private String getFileName(String path, String endPointName) throws UnsupportedEncodingException {
        Matcher matcher = FILE_NAME_PATTERN.matcher(path);
        String extractedPath = null;
        if (matcher.matches()) {
            if (endPointName.isEmpty()) {
                extractedPath = matcher.group("path");
            } else {
                extractedPath = matcher.group("path").substring(endPointName.length() - 1);
            }
        }
        if (extractedPath != null) {
            if (!Files.exists(Paths.get(getDecodePath(extractedPath)))) { //if regex or something else remove "/" separator, so need add again
                extractedPath = System.getProperty("file.separator") + extractedPath;
            }
        }
        return extractedPath;
    }

    private String getDecodePath(String path) throws UnsupportedEncodingException {
        return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
    }

    @GetMapping("/**")
    public List<SourceFile> getFiles(HttpServletRequest request) throws IOException {
        String fileURI = getFileName(request.getRequestURI(), "");
        List<SourceFile> files;
        if (null == fileURI || fileURI.isEmpty()) {
            files = defaultFileService.getRootFiles();
        } else {
            files = defaultFileService.getFiles(Paths.get(fileURI));
        }

        return files;
    }

    @GetMapping("/file/**")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "/file/");
        final Path pathFile = Paths.get(getDecodePath(fileURI));
        byte[] content = Files.readAllBytes(pathFile);
        final Path path = defaultFileService.getFile(pathFile);

        return ResponseEntity.ok()
                .contentLength(content.length)
                .header(HttpHeaders.CONTENT_TYPE, new MimetypesFileTypeMap().getContentType(path.toFile()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName())
                .body(content);
    }

    @GetMapping("/docs")
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

            SourceFile sourceFile = defaultFileService.saveFile(target.resolve(fileName), file.getBytes(), CREATE_NEW);

            sourceFiles.add(sourceFile);
        }
        return sourceFiles;//todo upload only file? Check!
    }

    @PostMapping("/**")
    public SourceFile createFile(@RequestBody SourceFile sourceFile) throws IOException {
        boolean isDirectory = sourceFile.getFileType().equals(DIRECTORY);
        return defaultFileService.createFile(sourceFile, isDirectory);
    }

    @PostMapping("/copy/**")
    public List<SourceFile> copyFile(@RequestBody Properties pathParam) throws IOException {
        final List<Path> src = Arrays.stream(pathParam.getProperty("src").split(","))
                .map(Paths::get)
                .collect(Collectors.toList());
        final Path target = Paths.get(pathParam.getProperty("target"));
        return defaultFileService.copyFile(src, target);
    }

    @PutMapping("/**")
    public SourceFile saveFile(@RequestBody Properties propertiesFile) throws IOException {
        final Path path = defaultFileService.getFile(Paths.get(propertiesFile.getProperty("fullPath")));
        final String newContent = propertiesFile.getProperty("content");
        return defaultFileService.saveFile(path, Collections.singletonList(newContent), null);
    }

    @PostMapping("/template/**")
    public Template saveTemplateInGroup(HttpServletRequest request, @RequestBody TemplateGroup templateGroup) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "/template/");
        final Path templatePath = Paths.get(fileURI);
        return defaultFileService.saveTemplateInGroup(templatePath, templateGroup);
    }

    @PutMapping("/move/**")
    public List<SourceFile> moveFile(@RequestBody Properties pathParam) throws IOException {
        final List<Path> src = Arrays.stream(pathParam.getProperty("src").split(","))
                .map(Paths::get)
                .collect(Collectors.toList());
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
    public void deleteFile(@RequestBody SourceFile file) throws IOException {
        final Path path = Paths.get(file.getFullPath());
        defaultFileService.deleteFile(path);
    }
}
