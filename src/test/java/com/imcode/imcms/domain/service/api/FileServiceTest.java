package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import org.apache.uima.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @AfterEach
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

        Files.createDirectories(pathDir2);
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

        Files.createDirectories(pathDir);

        final Path createdFile = fileService.createFile(pathFile, false);

        assertTrue(Files.exists(createdFile));
        assertEquals(pathFile, createdFile);
    }

    @Test
    public void createFile_Expected_CreatedFolder() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(firstRootPath));

        Files.createDirectories(pathDir);

        final Path createdFile = fileService.createFile(pathFile, true);

        assertTrue(Files.exists(createdFile));
        assertEquals(pathFile, createdFile);
    }

    @Test
    public void createFile_When_FileCreateToOutSideRootDir_Expected_CorrectException() {
        final Path pathFile = Paths.get(testFileName);

        assertFalse(Files.exists(pathFile));

        assertThrows(FileAccessDeniedException.class, () -> fileService.createFile(pathFile, false));
        assertThrows(FileAccessDeniedException.class, () -> fileService.createFile(pathFile, true));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void saveFile_When_FileExistAndOverWrite_Expected_SavedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);

        final String testText = "bla-bla-bla";
        final Path saved = fileService.saveFile(pathFile, testText.getBytes(), null);

        assertTrue(Files.exists(saved));
        List<String> lines = Files.readAllLines(saved);
        assertEquals(1, lines.size());
        String savedGetText = lines.get(0);
        assertEquals(testText, savedGetText);
    }

    @Test
    public void saveFile_When_FileExistAndNotOverWrite_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile2 = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createFile(pathFile2);

        final String testText = "bla-bla-bla";
        assertThrows(FileAlreadyExistsException.class, () -> fileService.saveFile(
                pathFile2, testText.getBytes(), StandardOpenOption.CREATE_NEW));
    }

    @Test
    public void getFile_When_FileExists_Expected_CorrectFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.toString(), fileService.getFile(pathFile).toString());
    }

    @Test
    public void getFile_When_PathFileContainsCommandCharacters_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path testPath = Paths.get("../");
        final Path testPath2 = Paths.get("./");
        final Path testPath3 = Paths.get("/~/");
        final Path testPath4 = Paths.get(".././~/../.");

        final Path testPath5 = firstRootPath.resolve("../");
        final Path testPath6 = firstRootPath.resolve("./");
        final Path testPath7 = firstRootPath.resolve("/~/");
        final Path testPath8 = firstRootPath.resolve(".././~/../.");


        Files.createDirectory(firstRootPath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(testPath));
        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(testPath2));
        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(testPath3));
        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(testPath4));

        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(testPath5));
        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(testPath7));
        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(testPath8));

        assertTrue(Files.exists(fileService.getFile(testPath6)));
    }

    @Test
    public void getFile_When_PathFileNotExist_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);

        assertFalse(Files.exists(pathFile));
        assertThrows(NoSuchFileException.class, () -> fileService.getFile(pathFile));
    }

    @Test
    public void deleteFile_When_FileExists_Expected_Delete() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);

        fileService.deleteFile(pathFile);

        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FileNotExists_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);

        final String fakeName = "fake.txt";
        final Path fakePathFile = pathDir.resolve(fakeName);

        assertThrows(NoSuchFileException.class, () -> fileService.deleteFile(fakePathFile));
    }

    @Test
    public void copyFile_When_SrcFileExists_Expected_CopyFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createDirectory(pathDir2);

        List<Path> paths = Collections.singletonList(Files.createFile(pathFile));

        assertEquals(0, Files.list(pathDir2).count());

        fileService.copyFile(paths, pathDir2);

        assertEquals(1, Files.list(pathDir2).count());
    }

    @Test
    public void copyFile_When_TargetFileExists_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFile2 = firstRootPath.resolve(testFileName2);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        List<Path> paths = Collections.singletonList(Files.createFile(pathFile));
        Files.createFile(pathFile2);

        assertThrows(FileSystemException.class, () -> fileService.copyFile(paths, pathFile2));
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
        List<Path> paths = Collections.singletonList(Files.createFile(pathFile));

        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(paths, pathFakeFile2));
        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(Collections.singletonList(pathFakeFile2), pathFile));
    }

    @Test
    public void moveFile_When_FileExist_Expected_moveCorrectFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = firstRootPath.resolve(testFileName);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));

        Files.createDirectories(pathDir2);
        Files.createDirectory(pathDir);

        List<Path> paths = Collections.singletonList(Files.createFile(pathFileByDir));

        assertEquals(3, fileService.getFiles(firstRootPath).size()); // +1 in firstroot
        assertEquals(0, fileService.getFiles(pathDir).size());

        fileService.moveFile(paths, pathDir);

        assertFalse(Files.exists(pathFileByDir));
        assertEquals(2, fileService.getFiles(firstRootPath).size());
        assertEquals(1, fileService.getFiles(pathDir).size());
    }

    @Test
    public void moveFile_When_FileMoveToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFakeDir = Paths.get("outSideDir");
        final Path pathFakeFile2 = pathFakeDir.resolve(testRootPaths.get(0).getFileName());
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        List<Path> paths = Collections.singletonList(Files.createFile(pathFile));

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(paths, pathFakeFile2));

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.exists(pathFakeFile2));

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(Collections.singletonList(pathFakeFile2), pathFile));

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.exists(pathFakeFile2));
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

        Files.createDirectories(pathDir2ByDir);
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);

        fileService.copyFile(Collections.singletonList(pathDir2ByDir), pathDir3);

        assertTrue(Files.exists(pathDir2ByDir));
        assertTrue(Files.exists(pathDir3));

        assertEquals(1, Files.list(pathDir3).count());
    }


    @Test
    public void copyDirectories_When_DirectoryExists_Expected_CopyDirectories() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);

        assertFalse(Files.exists(pathDir3));

        List<Path> src = new ArrayList<>();
        Files.createDirectory(firstRootPath);
        src.add(Files.createDirectory(pathDir));
        src.add(Files.createDirectory(pathDir2));
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);


        fileService.copyFile(src, pathDir3);
        assertTrue(Files.exists(pathDir));
        assertTrue(Files.exists(pathDir2));
        assertTrue(Files.exists(pathDir3));
        assertEquals(2, Files.list(pathDir3).count());
    }

    @Test
    public void moveDirectory_When_DirectoryExist_Expected_moveCorrectDirectory() throws IOException {

        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName3);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);

        assertFalse(Files.exists(firstRootPath));

        Files.createDirectories(pathDir2ByDir);
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);

        fileService.moveFile(Collections.singletonList(pathDir2ByDir), pathDir3);
        assertFalse(Files.exists(pathDir2ByDir));
        assertTrue(Files.exists(pathDir3));
    }

    @Test
    public void moveFiles_When_FilesExist_Expected_moveFiles() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile3ByDir = pathDir.resolve("bla" + testFileName2);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);

        List<Path> src = new ArrayList<>();
        src.add(Files.createDirectory(pathFileByDir));
        src.add(Files.createDirectory(pathFile3ByDir));

        assertEquals(0, Files.list(pathDir2).count());

        fileService.moveFile(src, pathDir2);
        assertFalse(Files.exists(pathFileByDir));
        assertFalse(Files.exists(pathFile3ByDir));
        assertEquals(2, Files.list(firstRootPath).count());
        assertEquals(2, Files.list(pathDir2).count());
    }
}
