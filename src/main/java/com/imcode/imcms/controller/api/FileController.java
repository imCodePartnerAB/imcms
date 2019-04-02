package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.api.DefaultFileService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private final DefaultFileService defaultFileService;

    public FileController(DefaultFileService defaultFileService) {
        this.defaultFileService = defaultFileService;
    }

    @GetMapping("/paths")
    public List<Path> getFiles(@RequestBody Path file) throws IOException {
        return defaultFileService.getFiles(file);
    }

    @GetMapping
    public Path getFile(@RequestBody Path file) throws IOException {
        return defaultFileService.getFile(file);
    }

    @PostMapping("/file")
    public Path createFile(@RequestBody Path file) throws IOException {
        return defaultFileService.createFile(file);
    }

    @PostMapping("/directory")
    public Path createDir(@RequestBody Path file) throws IOException {
        return defaultFileService.createFile(file);
    }

    @PutMapping
    public Path saveFile(@RequestBody Path file) {
        return defaultFileService.saveFile(file);
    }

    @DeleteMapping
    public void deleteFile(@RequestBody Path file) throws IOException {
        defaultFileService.deleteFile(file);
    }

    @PutMapping("/move")
    public Path moveFile(@RequestBody Path src, @RequestBody Path target) throws IOException {
        return defaultFileService.moveFile(src, target);
    }

    @PutMapping("/copy")
    public Path copyFile(@RequestBody Path src, @RequestBody Path target) throws IOException {
        return defaultFileService.copyFile(src, target);
    }
}
