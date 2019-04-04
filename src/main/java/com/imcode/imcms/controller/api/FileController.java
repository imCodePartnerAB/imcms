package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.api.DefaultFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final String DOWNLOAD_FOLDER = "/";
    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> rootPaths;

    private final DefaultFileService defaultFileService;

    public FileController(DefaultFileService defaultFileService) {
        this.defaultFileService = defaultFileService;
    }

    @GetMapping("/paths/")
    public List<Path> getFiles(@RequestParam Path file) throws IOException {
        return defaultFileService.getFiles(file);
    }

    @GetMapping("/getFile/")
    public Path getFile(@RequestParam Path file) throws IOException {
        return defaultFileService.getFile(file);
    }

    @PostMapping("/file")
    public Path createFile(@RequestParam Path file) throws IOException {
        return defaultFileService.createFile(file, false);
    }

    @PostMapping("/directory")
    public Path createDir(@RequestParam Path file) throws IOException {
        return defaultFileService.createFile(file, true);
    }

    @PostMapping("/upload") // TODO: 04.04.19 fix
    public Path uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        Path path = Paths.get(DOWNLOAD_FOLDER, Objects.requireNonNull(multipartFile.getOriginalFilename()));

        return defaultFileService.saveFile(path, multipartFile.getBytes(), null);
    }

    @PostMapping("/download") // TODO: 04.04.19 fix
    public Path downloadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        Path path = Paths.get(DOWNLOAD_FOLDER, Objects.requireNonNull(multipartFile.getOriginalFilename()));

        return defaultFileService.saveFile(path, multipartFile.getBytes(), null);
    }

    @DeleteMapping("/delete")
    public void deleteFile(@RequestParam Path file) throws IOException {
        defaultFileService.deleteFile(file);
    }

    @PutMapping("/move/{src}/{target}")
    public Path moveFile(@PathVariable Path src, @PathVariable Path target) throws IOException {
        return defaultFileService.moveFile(src, target);
    }

    @PutMapping("/copy/{src}/{target}")
    public Path copyFile(@PathVariable Path src, @PathVariable Path target) throws IOException {
        return defaultFileService.copyFile(src, target);
    }
}
