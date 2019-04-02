package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import org.apache.uima.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileServiceTest extends WebAppSpringTestConfig {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";
    private final String testDirectoryName3 = testDirectoryName + "three";

    @Autowired
    private DefaultFileService fileService;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> testRootPaths;

    @BeforeEach
    public void setUp() {
        testRootPaths.stream().map(Path::toFile).forEach(FileUtils::deleteRecursive);
    }


    @Test
    public void getAllFiles_When_FilesInDirectoryExist_Expected_CorrectFiles() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(1, fileService.getFiles(pathDir).size());
    }

    @Test
    public void getAllFiles_When_DirectoryHasFolderAndFile_Expected_CorrectFiles() throws IOException {
        final Path secondRootPath = testRootPaths.get(1);
        final Path pathDir = secondRootPath.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir2.resolve(testFileName);
        final Path pathFile2 = pathDir.resolve(testFileName2);

        Files.createDirectory(secondRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFile);
        Files.createFile(pathFile2);

        assertEquals(2, fileService.getFiles(pathDir).size());
    }

    @Test
    public void getAllFiles_When_FilesInDirectoryNotExist_Expected_EmptyResult() throws IOException {
        final Path secondRootPath = testRootPaths.get(1);
        final Path pathDir = secondRootPath.resolve(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(secondRootPath);
        Files.createDirectory(pathDir);

        assertEquals(0, fileService.getFiles(pathDir).size());
    }

    @Test
    public void createFile_Expected_CreatedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(firstRootPath));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);

        final Path createdFile = fileService.createFile(pathFile);

        assertTrue(Files.exists(createdFile));
        assertEquals(pathFile, createdFile);
    }

    @Test
    public void createFile_When_FileCreateToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path pathFile = Paths.get(testFileName);

        assertFalse(Files.exists(pathFile));

        assertThrows(NoSuchFileException.class, () -> fileService.createFile(pathFile));
        assertFalse(Files.exists(pathFile));
        Files.deleteIfExists(pathFile);
    }

    @Test
    public void saveFile_When_Expected_SavedFile() throws IOException {  //todo fix in service too
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
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
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.toString(), fileService.getFile(pathFile).toString());
    }

    @Test
    public void deleteFile_When_FileExists_Expected_Delete() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        fileService.deleteFile(pathFile);

        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FileNotExists_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);

        final String fakeName = "fake.txt";
        final Path fakePathFile = pathDir.resolve(fakeName);

        assertThrows(NoSuchFileException.class, () -> fileService.deleteFile(fakePathFile));

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);
        Files.deleteIfExists(firstRootPath);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
        assertFalse(Files.exists(firstRootPath));
    }

    @Test
    public void copyFile_When_FileExists_Expected_CopyFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile2 = firstRootPath.resolve(testFileName2);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        final Path copiedFile = fileService.copyFile(pathFile, pathFile2);

        assertEquals(pathFile2.toAbsolutePath(), copiedFile.toAbsolutePath());
    }

    @Test
    public void copyFile_When_FileCopyToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFakeFile2 = Paths.get("test");
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);


        assertThrows(NoSuchFileException.class, () -> fileService.copyFile(pathFile, pathFakeFile2));

        Files.deleteIfExists(pathFakeFile2);
    }

    @Test
    public void moveFile_When_FileExist_Expected_moveCorrectFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = firstRootPath.resolve(testFileName);
        final Path pathFile2ByDir2 = pathDir2.resolve(testFileName2);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertEquals(pathFile2ByDir2.getFileName().toString(), testFileName2);

        assertEquals(3, fileService.getFiles(firstRootPath).size());
        assertEquals(0, fileService.getFiles(pathDir2).size());
        final Path movedFile = fileService.moveFile(pathFileByDir, pathFile2ByDir2);

        assertEquals(2, fileService.getFiles(firstRootPath).size());
        assertEquals(1, fileService.getFiles(pathDir2).size());
        assertEquals(pathFile2ByDir2.toAbsolutePath(), movedFile.toAbsolutePath());
    }

    @Test
    public void moveFile_When_FileMoveToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFakeDir = Paths.get("outSideDir");
        final Path pathFakeFile2 = pathFakeDir.resolve(testRootPaths.get(0).getFileName());
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathFakeDir);
        Files.createFile(pathFile);

        assertThrows(NoSuchFileException.class, () -> fileService.moveFile(pathFile, pathFakeFile2));

        Files.deleteIfExists(pathFakeDir);
    }

    @Test
    public void getAllFiles_WhenFilesHaveSubFiles_Expected_CorrectSize() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile2ByDir2 = pathDir2ByDir.resolve(testFileName2);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createFile(pathFileByDir);
        Files.createFile(pathFile2ByDir2);

        assertFalse(fileService.getFiles(pathDir).isEmpty());
        assertEquals(2, fileService.getFiles(pathDir).size());
    }

    @Test
    public void copyDirectory_When_DirectoryExists_Expected_CopyDirectory() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);

        assertFalse(Files.exists(firstRootPath));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createFile(pathFileByDir);

        assertEquals(pathDir3.toString(), fileService.copyFile(pathDir2ByDir, pathDir3).toString());
    }

    @Test
    public void moveDirectory_When_DirectoryExist_Expected_moveCorrectDirectory() throws IOException {

        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName3);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);

        assertFalse(Files.exists(firstRootPath));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createFile(pathFileByDir);

        assertEquals(pathDir3.toString(), fileService.moveFile(pathDir2ByDir, pathDir3).toString());
    }
}
