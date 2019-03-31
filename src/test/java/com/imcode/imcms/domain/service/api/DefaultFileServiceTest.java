package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import org.apache.uima.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultFileServiceTest extends WebAppSpringTestConfig {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";
    private final String testDirectoryName3 = testDirectoryName + "three";

    @Autowired
    private DefaultFileService fileService;

    @Value("#{imcmsProperties.FileAdminRootPaths}")
    private List<Path> testRootPaths;


    @AfterEach
    public void setUp() {
        for (Path path : testRootPaths) {
            String[] paths = path.toString().split(";");
            for (String pathFile : paths) {
                FileUtils.deleteRecursive(new File(pathFile));
            }
        }
    }

    @Test
    public void getAllFiles_When_FilesInDirectoryExist_Expected_CorrectFiles() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());
    }

    @Test
    public void getAllFiles_When_DirectoryHasFolderAndFile_Expected_CorrectFiles() throws IOException {
        final Path secondRootPath = testRootPaths.get(1);
        final Path pathRootDir = Paths.get(secondRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir2.resolve(testFileName);
        final Path pathFile2 = pathDir.resolve(testFileName2);

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFile);
        Files.createFile(pathFile2);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertEquals(pathFile2.getFileName().toString(), testFileName2);

        assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());
    }

    @Test
    public void getAllFiles_When_FilesInDirectoryNotExist_Expected_EmptyResult() throws IOException {
        final Path secondRootPath = testRootPaths.get(1);
        final Path pathRootDir = Paths.get(secondRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);

        assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size()); // 0
    }

    @Test
    public void createFile_Expected_CreatedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathRootDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        final Path createdFile = fileService.createFile(pathFile);

        assertEquals(pathFile.getParent(), createdFile.getParent());
        assertEquals(pathFile.getFileName(), createdFile.getFileName());
    }

    @Test
    public void saveFile_When_Expected_SavedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        final Path currentCreatedFile = fileService.createFile(pathFile);
        final String newName = "newTestFileName";
        final Path renameFile = currentCreatedFile.getParent().resolve(newName);


        assertNotEquals(currentCreatedFile.getFileName(), fileService.saveFile(renameFile).getFileName());
        assertEquals(renameFile.getFileName(), fileService.saveFile(renameFile).getFileName());
    }

    @Test
    public void getFile_When_FileExists_Expected_CorrectFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        assertEquals(Paths.get(pathFile.toString()).toString(), fileService.getFile(pathFile).toString());
        assertEquals(Paths.get(pathFile.toString()).getFileName(), fileService.getFile(pathFile).getFileName());
    }

    @Test
    public void deleteFile_When_FileExists_Expected_Delete() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        fileService.deleteFile(pathFile);

        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FileNotExists_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        final String fakeName = "fake.txt";
        final Path fakePathFile = pathDir.resolve(fakeName);

        assertThrows(NoSuchFileException.class, () -> fileService.deleteFile(fakePathFile));

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);
        Files.deleteIfExists(pathRootDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
        assertFalse(Files.exists(pathRootDir));
    }

    @Test
    public void copyFile_When_FileExists_Expected_CopyFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathDir2 = pathRootDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        final Path copiedFile = fileService.copyFile(pathFile, pathDir2);

        assertEquals(pathFile.getFileName(), copiedFile.getFileName());
        assertEquals(1, fileService.getFiles(pathDir2).size());
    }

    @Test
    public void moveFile_When_FileExist_Expected_moveCorrectFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathDir2 = pathRootDir.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile2ByDir = pathDir.resolve(testFileName2);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFileByDir);
        Files.createFile(pathFile2ByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertEquals(pathFile2ByDir.getFileName().toString(), testFileName2);

        assertEquals(2, Files.list(pathDir).count());
        assertEquals(0, Files.list(pathDir2).count());
        final Path movedFile = fileService.moveFile(pathFileByDir, pathDir2);

        assertEquals(1, Files.list(pathDir).count());
        assertEquals(1, Files.list(pathDir2).count());
        assertEquals(pathFileByDir.getFileName(), movedFile.getFileName());
    }

    @Test
    public void getAllFiles_WhenFilesHaveSubFiles_Expected_CorrectSize() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile2ByDir2 = pathDir2ByDir.resolve(testFileName2);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createFile(pathFileByDir);
        Files.createFile(pathFile2ByDir2);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertEquals(pathFile2ByDir2.getFileName().toString(), testFileName2);

        assertFalse(fileService.getFiles(pathDir).isEmpty());
        assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());
    }

    @Test
    public void copyDirectory_When_DirectoryExists_Expected_CopyDirectory() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = pathRootDir.resolve(testDirectoryName3);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));
        assertFalse(Files.exists(pathDir3));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertEquals(pathDir3.getFileName(), fileService.copyFile(pathDir2ByDir, pathDir3).getFileName());
        assertEquals(pathRootDir.getFileName(), fileService.copyFile(pathDir2ByDir, pathDir3).getParent().getFileName());
    }

    @Test
    public void moveDirectory_When_DirectoryExist_Expected_moveCorrectDirectory() throws IOException {

        final Path firstRootPath = testRootPaths.get(0);
        final Path pathRootDir = Paths.get(firstRootPath.toString());
        final Path pathDir = pathRootDir.resolve(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName3);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = pathRootDir.resolve(testDirectoryName3);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));
        assertFalse(Files.exists(pathDir3));

        Files.createDirectory(pathRootDir);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);

        assertEquals(0, Files.list(pathDir3).count());
        assertEquals(pathDir2ByDir.getFileName(), fileService.moveFile(pathDir2ByDir, pathDir3).getFileName());
        assertEquals(1, fileService.getFiles(pathDir3).size());
    }
}
