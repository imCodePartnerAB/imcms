package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.api.DefaultFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.util.regex.Pattern.compile;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final Pattern FILE_NAME_PATTERN = compile("(.*?\\/files\\/)(?<path>.*)");

    private final DefaultFileService defaultFileService;

    public FileController(DefaultFileService defaultFileService) {
        this.defaultFileService = defaultFileService;
    }

    private String getFileName(String pathName, String removeStart) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(pathName);
        String path = null;
        if (matcher.matches()) {
            if (removeStart.isEmpty()) {
                path = matcher.group("path");
            } else {
                path = matcher.group("path").substring(removeStart.length() - 1);
            }
        }
        return path;
    }

    @PostMapping("/upload/**")
    public String uploadFile(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        final String destination = getFileName(request.getRequestURI(), "/upload/");
        final Path resolvePath = Paths.get(destination).resolve(file.getOriginalFilename());
        return defaultFileService.saveFile(resolvePath, file.getBytes(), CREATE_NEW).toString();
    }

    @GetMapping("/**")
    public List<Path> getFiles(HttpServletRequest request) throws IOException {
        final String file = getFileName(request.getRequestURI(), "");
        return defaultFileService.getFiles(Paths.get(file));
    }

    @PutMapping("/**")
    public String saveFile(HttpServletRequest request) throws IOException {
        final String file = getFileName(request.getRequestURI(), "");
        final Path path = defaultFileService.getFile(Paths.get(file));
        final byte[] content = Files.readAllBytes(path);

        return defaultFileService.saveFile(path, content, null).toString();
    }

    @PostMapping("/**")
    public String createFile(HttpServletRequest request, @RequestParam boolean isDirectory) throws IOException {
        final String file = getFileName(request.getRequestURI(), "");
        return defaultFileService.createFile(Paths.get(file), isDirectory).toString();
    }

    @GetMapping("/file/**")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) throws IOException {
        final String file = getFileName(request.getRequestURI(), "/file/");
        final Path path = defaultFileService.getFile(Paths.get(file));
        final byte[] content = Files.readAllBytes(path);

        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @DeleteMapping("/**")
    public void deleteFile(HttpServletRequest request) throws IOException {
        final String file = getFileName(request.getRequestURI(), "");
        defaultFileService.deleteFile(Paths.get(file));
    }

    @PutMapping("/move/**")
    public String moveFile(HttpServletRequest request, @RequestParam Path target) throws IOException {
        final String file = getFileName(request.getRequestURI(), "/move/");
        return defaultFileService.moveFile(Paths.get(file), target).toString();
    }

    @PostMapping("/copy/**")
    public String copyFile(HttpServletRequest request, @RequestParam Path target) throws IOException {
        final String file = getFileName(request.getRequestURI(), "/copy/");
        return defaultFileService.copyFile(Paths.get(file), target).toString();
    }
}
