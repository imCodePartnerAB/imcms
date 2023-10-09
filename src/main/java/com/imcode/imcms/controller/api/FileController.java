package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.exception.FileOperationFailureException;
import com.imcode.imcms.domain.service.FileService;
import org.apache.tika.Tika;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private static final String FILENAME_ALREADY_IN_USE_MSG="File with same name already exists!";

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
            files = defaultFileService.getRootFolders();
        } else {
            files = defaultFileService.getFiles(Paths.get(fileURI));
        }

        return files;
    }

    @GetMapping("/get-file")
    public SourceFile getFile(@RequestParam Path path) throws IOException {
        return defaultFileService.getFile(path);
    }

    @GetMapping("/file/**")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) throws IOException {
        final String fileURI = getFileName(request.getRequestURI(), "/file/");
        final Path pathFile = Paths.get(getDecodePath(fileURI));
        byte[] content = Files.readAllBytes(pathFile);
        final SourceFile sourceFile = defaultFileService.getFile(pathFile);

        return ResponseEntity.ok()
                .contentLength(content.length)
                .header(HttpHeaders.CONTENT_TYPE, new Tika().detect(pathFile))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + sourceFile.getFileName())
                .body(content);
    }

    @GetMapping("/docs")
    public List<DocumentDTO> getDocumentsByTemplatePath(@RequestParam Path template) throws IOException {
        return defaultFileService.getDocumentsByTemplatePath(template);
    }

    @PostMapping("/upload/**")
    public ResponseEntity<List<?>> uploadFile(MultipartHttpServletRequest request) throws IOException {
        final List<MultipartFile> files = new ArrayList<>(request.getFileMap().values());
        final String targetDirectory = request.getParameter("targetDirectory");

        final List<SourceFile> sourceFiles = new ArrayList<>();
        final List<String> conflictFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            final Path fileName = Paths.get(file.getName());
            final Path target = Paths.get(targetDirectory);

            try {
                SourceFile sourceFile = defaultFileService.saveFile(target.resolve(fileName), file.getBytes(), CREATE_NEW);

                sourceFiles.add(sourceFile);
            } catch (EmptyFileNameException | FileAlreadyExistsException e) {
                conflictFiles.add(file.getName());
            }
        }

        if (!conflictFiles.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictFiles);
        }

        return ResponseEntity.ok(sourceFiles);
    }

    @PostMapping("/**")
    public SourceFile createFile(@RequestBody SourceFile sourceFile) throws IOException {
        boolean isDirectory = sourceFile.getFileType().equals(DIRECTORY);
        try {
            return defaultFileService.createFile(sourceFile, isDirectory);
        } catch (FileAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, FILENAME_ALREADY_IN_USE_MSG);
        }
    }

    @PostMapping("/copy/**")
    public ResponseEntity<List<?>> copyFile(@RequestBody Properties pathParam) throws IOException {
        final List<Path> src = Arrays.stream(pathParam.getProperty("src").split(","))
                .map(Paths::get)
                .collect(Collectors.toList());
        final Path target = Paths.get(pathParam.getProperty("target"));
        final boolean overwrite = Boolean.parseBoolean(pathParam.getProperty("overwrite"));

        try {
            return ResponseEntity.ok(defaultFileService.copyFile(src, target, overwrite));
        } catch (FileOperationFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getConflictFiles());
        }
    }

    @PostMapping("/copy/rename/**")
    public ResponseEntity<?> copyWithRename(HttpServletRequest request) throws IOException {
        final Path src = Path.of(request.getParameter("src"));
        final Path target = Path.of(request.getParameter("target"));
        final String newFilename = request.getParameter("newFilename");

        try {
            return ResponseEntity.ok(defaultFileService.copyFileWithRename(src, target, newFilename));
        } catch (FileOperationFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getConflictFiles());
        }
    }

    @PutMapping("/**")
    public SourceFile saveFile(MultipartHttpServletRequest request) throws IOException {
        final String filename = request.getParameter("filename");
        final Path path = Paths.get(request.getParameter("fullPath"));
        final String content = request.getParameter("content");
        final byte[] newContent = content != null ? content.getBytes() : request.getFile(filename).getBytes();

        try {
            return defaultFileService.saveFile(path, newContent, null);
        } catch (FileAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, FILENAME_ALREADY_IN_USE_MSG);
        }
    }

    @PutMapping("/move/**")
    public ResponseEntity<List<?>> moveFile(@RequestBody Properties pathParam) throws IOException {
        final List<Path> src = Arrays.stream(pathParam.getProperty("src").split(","))
                .map(Paths::get)
                .collect(Collectors.toList());
        final Path target = Paths.get(pathParam.getProperty("target"));

        try {
            return ResponseEntity.ok(defaultFileService.moveFile(src, target));
        } catch (FileOperationFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getConflictFiles());
        }
    }

    @PutMapping("/move/rename/**")
    public ResponseEntity<?> moveWithRename(HttpServletRequest request) throws IOException {
        final Path src = Path.of(request.getParameter("src"));
        final Path target = Path.of(request.getParameter("target"));
        final String newFilename = request.getParameter("newFilename");

        try {
            return ResponseEntity.ok(defaultFileService.moveFileWithRename(src, target, newFilename));
        } catch (FileOperationFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getConflictFiles());
        }
    }

    @PutMapping("/rename/**")
    public SourceFile renameFile(@RequestBody Properties pathParam) throws IOException {
        final Path src = Paths.get(pathParam.getProperty("src"));
        final String newName = pathParam.getProperty("newName");

        try {
            return defaultFileService.renameFile(src, newName);
        } catch (EmptyFileNameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filename can`t be empty!");
        } catch (FileAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, FILENAME_ALREADY_IN_USE_MSG);
        }
    }

    @PutMapping("/rename/default/**")
    public SourceFile defaultRename(@RequestBody Properties properties) throws IOException {
        return defaultFileService.defaultRename(Path.of(properties.getProperty("path")));
    }

    @DeleteMapping("/**")
    public void deleteFile(@RequestBody SourceFile file) throws IOException {
        final Path path = Paths.get(file.getFullPath());
        defaultFileService.deleteFile(path);
    }

    @GetMapping("/exists/**")
    public boolean exists(@RequestParam String path){
        return defaultFileService.exists(path);
    }

    @GetMapping("/exists/all/**")
    public List<SourceFile> existsAll(@RequestParam List<String> paths){
        return defaultFileService.existsAll(paths);
    }
}
