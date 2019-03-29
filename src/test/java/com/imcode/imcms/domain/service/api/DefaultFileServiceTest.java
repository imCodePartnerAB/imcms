package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
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

public class DefaultFileServiceTest extends WebAppSpringTestConfig {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";
    private final String testDirectoryName3 = testDirectoryName + "three";

    @Autowired
    private DefaultFileService fileService;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<String> testRootPaths;

    @BeforeEach
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(testFileName)); // rebuild !
        Files.deleteIfExists(Paths.get(testFileName2));
        Files.deleteIfExists(Paths.get(testDirectoryName));
        Files.deleteIfExists(Paths.get(testDirectoryName2));
        Files.deleteIfExists(Paths.get(testDirectoryName3));
    }

    @Test
    public void getAllFiles_When_FilesInDirectoryExist_Expected_CorrectFiles() throws IOException {
        final Path pathRootDir = Paths.get(testRootPaths.get(0));
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertTrue(Files.exists(pathRootDir)); // !!
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));

        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());

    }

    @Test
    public void getAllFiles_When_DirectoryHasFolderAndFile_Expected_CorrectFiles() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir2.resolve(testFileName);
        final Path pathFile2 = pathDir.resolve(testFileName2);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));

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
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());
    }

    @Test
    public void createFile_Expected_CreatedFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        final Path currentCreatedFile = Files.createFile(pathFile);
        final Path createdFile = fileService.createFile(pathFile);

        assertEquals(currentCreatedFile.getFileName(), createdFile.getFileName());
        assertEquals(currentCreatedFile.toString(), createdFile.toString());
    }

    @Test
    public void saveFile_When_Expected_SavedFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        final Path currentCreatedFile = Files.createFile(pathFile);
        final String newName = "newTestFileName";
        final Path renameFile = currentCreatedFile.getParent().resolve(newName);

        final Path createdFile = fileService.saveFile(renameFile);

        assertNotEquals(currentCreatedFile.getFileName(), createdFile.getFileName());
        assertEquals(renameFile.getFileName(), createdFile.getFileName());
    }

    @Test
    public void getFile_When_FileExists_Expected_CorrectFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        assertEquals(Paths.get(pathFile.toUri()).toString(), fileService.getFile(pathFile).toString());
        assertEquals(Paths.get(pathFile.toUri()).getFileName(), fileService.getFile(pathFile).getFileName());
    }

    @Test
    public void deleteFile_When_FileExists_Expected_Delete() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        fileService.deleteFile(pathFile);

        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FileNotExists_Expected_CorrectException() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);


        assertEquals(pathFile.getFileName().toString(), testFileName);

        final String fakeName = "fake.txt";
        final Path fakePathFile = pathDir.resolve(fakeName);

        assertThrows(NoSuchFileException.class, () -> fileService.deleteFile(fakePathFile));

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void copyFile_When_FileExists_Expected_CopyFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathDir2 = Paths.get(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);

        final Path copiedFileByDir2 = Files.copy(pathFile, pathDir2.resolve(pathFile.getFileName()));

        assertEquals(copiedFileByDir2.toString(), fileService.copyFile(pathFile, pathDir2).toString());
        assertEquals(copiedFileByDir2.getParent(), fileService.copyFile(pathFile, pathDir2).getParent());

        assertEquals(1, Files.list(pathDir2).count());
    }

    @Test
    public void moveFile_When_FileExist_Expected_moveCorrectFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathDir2 = Paths.get(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile2ByDir = pathDir.resolve(testFileName2);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));

        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFileByDir);
        Files.createFile(pathFile2ByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertEquals(pathFile2ByDir.getFileName().toString(), testFileName2);

        assertEquals(2, Files.list(pathDir).count());
        assertEquals(0, Files.list(pathDir2).count());

        final Path movedFile = Files.move(pathFileByDir, pathDir2.resolve(pathFileByDir.getFileName()));

        assertEquals(movedFile.toString(), fileService.moveFile(pathFileByDir, pathDir2).toString());
        assertEquals(movedFile.getParent(), fileService.moveFile(pathFileByDir, pathDir2).getParent());
    }

    @Test
    public void getAllFiles_WhenFilesHaveSubFiles_Expected_CorrectSize() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile2ByDir2 = pathDir2ByDir.resolve(testFileName2);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));

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
    public void createDirectory_Expected_CreatedDirectory() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        assertTrue(Files.isDirectory(fileService.createFile(pathDir)));

        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
    }

    @Test
    public void copyDirectory_When_DirectoryExists_Expected_CopyDirectory() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = Paths.get(testDirectoryName3);
        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));
        assertFalse(Files.exists(pathDir3));

        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createFile(pathFileByDir);
        Files.createDirectory(pathDir3);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);

        Path copiedDir = Files.copy(pathDir2ByDir, pathDir3.resolve(pathDir2ByDir.getFileName()));

        assertEquals(copiedDir.getParent(), fileService.copyFile(pathDir2ByDir, pathDir3).getParent());
    }

    @Test
    public void moveDirectory_When_DirectoryExist_Expected_moveCorrectDirectory() throws IOException {

        final Path pathDir = Paths.get(testDirectoryName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName3);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = Paths.get(testDirectoryName3);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));
        assertFalse(Files.exists(pathDir3));

        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2ByDir);
        Files.createDirectory(pathDir3);


        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);

        assertEquals(0, Files.list(pathDir3).count());
        Path movedDir = Files.move(pathDir2ByDir, pathDir3.resolve(pathDir2ByDir.getFileName()));

        assertEquals(1, Files.list(pathDir3).count());
        assertEquals(movedDir.getParent(), fileService.moveFile(pathDir2ByDir, pathDir3).getParent());
    }
}
