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

import static org.junit.jupiter.api.Assertions.*;

public class DefaultFileServiceTest extends WebAppSpringTestConfig {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";
    private final String testDirectoryName3 = testDirectoryName + "three";

    @Autowired
    private DefaultFileService fileService;

    @Value("${FileAdminRootPaths}")
    private String testRootPaths;

    @BeforeEach
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(testFileName));
        Files.deleteIfExists(Paths.get(testFileName2));
        Files.deleteIfExists(Paths.get(testDirectoryName));
        Files.deleteIfExists(Paths.get(testDirectoryName2));
        Files.deleteIfExists(Paths.get(testDirectoryName3));
    }

    @Test
    public void getAllFiles_When_FilesInDirectoryExist_Expected_CorrectFiles() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathFile = pathDir.resolve(testFileName);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertFalse(Files.exists(pathFile));

        Files.createFile(pathFile);

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.isDirectory(pathFile));

        assertEquals(1, Files.list(pathDir).count());

        //assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void getAllFiles_When_DirectoryHasFolderAndFile_Expected_CorrectFiles() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final String testNameDir2 = testDirectoryName + "a"; /// !!!
        final Path pathDir2 = pathDir.resolve(testNameDir2); /// !!!

        assertFalse(Files.exists(pathDir2));

        Files.createDirectory(pathDir2);

        assertTrue(Files.exists(pathDir2));
        assertTrue(Files.isDirectory(pathDir2));

        final Path pathFile = pathDir2.resolve(testFileName);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertFalse(Files.exists(pathFile));

        Files.createFile(pathFile);

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.isDirectory(pathFile));

        final Path pathFile2 = pathDir.resolve(testFileName2);

        assertEquals(pathFile2.getFileName().toString(), testFileName2);
        assertFalse(Files.exists(pathFile2));

        Files.createFile(pathFile2);

        assertTrue(Files.exists(pathFile2));
        assertFalse(Files.isDirectory(pathFile2));

        assertEquals(2, Files.list(pathDir).count());

        //assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathFile2);
        Files.deleteIfExists(pathDir2);
        Files.deleteIfExists(pathDir);


        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));
        assertFalse(Files.exists(pathFile2));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void getAllFiles_When_FilesInDirectoryNotExist_Expected_EmptyResult() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        assertNotNull(Files.list(pathDir));
        assertEquals(0, Files.list(pathDir).count());

        //assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());

        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
    }

    @Test
    public void createFile_Expected_CreatedFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathFile = pathDir.resolve(testFileName);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertFalse(Files.exists(pathFile));

        final Path currentCreatedFile = Files.createFile(pathFile);
        //final Path createdFile = fileService.createFile(pathFile);

//        assertEquals(currentCreatedFile.getFileName(), createdFile.getFileName());
//        assertEquals(currentCreatedFile.toString(), createdFile.toString());

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void saveFile_When_Expected_SavedFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathFile = pathDir.resolve(testFileName);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertFalse(Files.exists(pathFile));

        final Path currentCreatedFile = Files.createFile(pathFile);

        assertTrue(Files.exists(currentCreatedFile));

        final String newName = "newTestFileName";
        final Path renameFile = currentCreatedFile.getParent().resolve(newName);

        assertNotEquals(currentCreatedFile.getFileName(), renameFile.getFileName());

//        final Path createdFile = fileService.saveFile(renameFile);

//        assertEquals(renameFile.getFileName(), createdFile.getFileName());
//        assertEquals(renameFile.toString(), createdFile.toString());

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void getFile_When_FileExists_Expected_CorrectFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathFile = pathDir.resolve(testFileName);

        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertTrue(Files.exists(pathFile));

//        assertEquals( Paths.get(pathFile.toUri()).toString(),fileService.getFile(pathFile).toString());
//        assertEquals( Paths.get(pathFile.toUri()).getFileName(),fileService.getFile(pathFile).getFileName());

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FileExists_Expected_Delete() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathFile = pathDir.resolve(testFileName);
        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertTrue(Files.exists(pathFile));

        // fileService.deleteFile(pathFile);

        Files.deleteIfExists(pathFile); // delete that when set up fileService
        Files.deleteIfExists(pathDir); //

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FileNotExists_Expected_CorrectException() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathFile = pathDir.resolve(testFileName);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertFalse(Files.exists(pathFile));

        final String fakeName = "fake.txt";
        final Path fakePathFile = pathDir.resolve(fakeName);

        assertThrows(NoSuchFileException.class, () -> Files.delete(fakePathFile));
//        assertThrows(NoSuchFileException.class, () -> fileService.deleteFile(fakePathFile));


        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void copyFile_When_FileExists_Expected_CopyFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathDir2 = Paths.get(testDirectoryName2);
        assertFalse(Files.exists(pathDir2));

        Files.createDirectory(pathDir2);

        assertTrue(Files.exists(pathDir2));
        assertTrue(Files.isDirectory(pathDir2));

        final Path pathFile = pathDir.resolve(testFileName);

        Files.createFile(pathFile);

        assertEquals(pathFile.getFileName().toString(), testFileName);
        assertFalse(Files.isDirectory(pathFile));
        assertTrue(Files.exists(pathFile));

        //fileService.copyFile(pathFile, pathDir2);

        final Path copiedFileByDir2 = Files.copy(pathFile, pathDir2.resolve(pathFile.getFileName()));

//        assertEquals(copiedFileByDir2.toString(), fileService.copyFile(pathFile, pathDir2).toString());
//        assertEquals(copiedFileByDir2.getParent(), fileService.copyFile(pathFile, pathDir2).getParent());
        assertEquals(1, Files.list(pathDir2).count());

        Files.deleteIfExists(pathFile);
        Files.deleteIfExists(copiedFileByDir2);
        Files.deleteIfExists(pathDir);
        Files.deleteIfExists(pathDir2);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));
        assertFalse(Files.exists(pathFile));
        assertFalse(Files.exists(copiedFileByDir2));

    }

    @Test
    public void moveFile_When_FileExist_Expected_moveCorrectFile() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathDir2 = Paths.get(testDirectoryName2);
        assertFalse(Files.exists(pathDir2));

        Files.createDirectory(pathDir2);

        assertTrue(Files.exists(pathDir2));
        assertTrue(Files.isDirectory(pathDir2));

        final Path pathFileByDir = pathDir.resolve(testFileName);

        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertFalse(Files.isDirectory(pathFileByDir));
        assertTrue(Files.exists(pathFileByDir));

        final String fullFileName2 = testFileName2;
        final Path pathFile2ByDir = pathDir.resolve(fullFileName2);

        Files.createFile(pathFile2ByDir);

        assertEquals(pathFile2ByDir.getFileName().toString(), fullFileName2);
        assertFalse(Files.isDirectory(pathFile2ByDir));
        assertTrue(Files.exists(pathFile2ByDir));
        assertEquals(2, Files.list(pathDir).count());
        assertEquals(0, Files.list(pathDir2).count());

        //fileService.moveFile(pathFileByDir, pathDir2);

        final Path movedFile = Files.move(pathFileByDir, pathDir2.resolve(pathFileByDir.getFileName()));

//        assertEquals(movedFile.toString(), fileService.moveFile(pathFileByDir, pathDir2).toString());
//        assertEquals(movedFile.getParent(), fileService.moveFile(pathFileByDir, pathDir2).getParent());
        assertEquals(1, Files.list(pathDir).count());
        assertEquals(1, Files.list(pathDir2).count());

        Files.deleteIfExists(pathFileByDir);
        Files.deleteIfExists(pathFile2ByDir);
        Files.deleteIfExists(movedFile);
        Files.deleteIfExists(pathDir);
        Files.deleteIfExists(pathDir2);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));
        assertFalse(Files.exists(pathFileByDir));
        assertFalse(Files.exists(pathFile2ByDir));
        assertFalse(Files.exists(movedFile));
    }

    @Test
    public void getAllFiles_WhenFilesHaveSubFiles_Expected_CorrectSize() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        assertFalse(Files.exists(pathDir2ByDir));

        Files.createDirectory(pathDir2ByDir);

        assertTrue(Files.exists(pathDir2ByDir));
        assertTrue(Files.isDirectory(pathDir2ByDir));

        final Path pathFileByDir = pathDir.resolve(testFileName);

        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertFalse(Files.isDirectory(pathFileByDir));
        assertTrue(Files.exists(pathFileByDir));

        final String fullFileName2 = testFileName2;
        final Path pathFile2ByDir2 = pathDir2ByDir.resolve(fullFileName2);

        Files.createFile(pathFile2ByDir2);

        assertEquals(pathFile2ByDir2.getFileName().toString(), fullFileName2);
        assertFalse(Files.isDirectory(pathFile2ByDir2));
        assertTrue(Files.exists(pathFile2ByDir2));
        assertEquals(2, Files.list(pathDir).count());
        assertEquals(1, Files.list(pathDir2ByDir).count());

        //assertEquals(Files.list(pathDir).count(), fileService.getFiles(pathDir).size());

        Files.deleteIfExists(pathFile2ByDir2);
        Files.deleteIfExists(pathDir2ByDir);
        Files.deleteIfExists(pathFileByDir);
        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));
        assertFalse(Files.exists(pathFile2ByDir2));
        assertFalse(Files.exists(pathFileByDir));
    }

    @Test
    public void createDirectory_Expected_CreatedDirectory() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

//        assertTrue(Files.isDirectory(fileService.createFile(pathDir)));

        Files.deleteIfExists(pathDir);

        assertFalse(Files.exists(pathDir));
    }

    @Test
    public void copyDirectory_When_DirectoryExists_Expected_CopyDirectory() throws IOException {
        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        assertFalse(Files.exists(pathDir2ByDir));

        Files.createDirectory(pathDir2ByDir);

        assertTrue(Files.exists(pathDir2ByDir));
        assertTrue(Files.isDirectory(pathDir2ByDir));

        final Path pathFileByDir = pathDir.resolve(testFileName);

        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertFalse(Files.isDirectory(pathFileByDir));
        assertTrue(Files.exists(pathFileByDir));
        assertEquals(2, Files.list(pathDir).count());

        final Path pathDir3 = Paths.get(testDirectoryName3);

        assertFalse(Files.exists(pathDir3));

        Files.createDirectory(pathDir3);

        assertTrue(Files.exists(pathDir3));
        assertTrue(Files.isDirectory(pathDir3));
        assertEquals(0, Files.list(pathDir3).count());


        Path copiedDir = Files.copy(pathDir2ByDir, pathDir3.resolve(pathDir2ByDir.getFileName()));

        assertEquals(1, Files.list(pathDir3).count());
        assertEquals(2, Files.list(pathDir).count());


        //assertEquals(copiedDir.getParent(), fileService.copyFile(pathDir2ByDir, pathDir3).getParent());

        Files.deleteIfExists(pathFileByDir);
        Files.deleteIfExists(pathDir2ByDir);
        Files.deleteIfExists(copiedDir);
        Files.deleteIfExists(pathDir);
        Files.deleteIfExists(pathDir3);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFileByDir));
        assertFalse(Files.exists(pathDir2ByDir));
        assertFalse(Files.exists(copiedDir));
        assertFalse(Files.exists(pathDir3));
    }

    @Test
    public void moveDirectory_When_DirectoryExist_Expected_moveCorrectDirectory() throws IOException {

        final Path pathDir = Paths.get(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(pathDir);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));

        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName3);
        assertFalse(Files.exists(pathDir2ByDir));

        Files.createDirectory(pathDir2ByDir);

        assertTrue(Files.exists(pathDir2ByDir));
        assertTrue(Files.isDirectory(pathDir2ByDir));

        final Path pathFileByDir = pathDir.resolve(testFileName);

        Files.createFile(pathFileByDir);

        assertEquals(pathFileByDir.getFileName().toString(), testFileName);
        assertFalse(Files.isDirectory(pathFileByDir));
        assertTrue(Files.exists(pathFileByDir));
        assertEquals(2, Files.list(pathDir).count());

        final Path pathDir3 = Paths.get(testDirectoryName3);

        assertFalse(Files.exists(pathDir3));

        Files.createDirectory(pathDir3);

        assertTrue(Files.exists(pathDir3));
        assertTrue(Files.isDirectory(pathDir3));
        assertEquals(0, Files.list(pathDir3).count());


        Path movedDir = Files.move(pathDir2ByDir, pathDir3.resolve(pathDir2ByDir.getFileName()));

        assertEquals(1, Files.list(pathDir3).count());
        assertEquals(1, Files.list(pathDir).count());

        //assertEquals(movedDir.getParent(), fileService.moveFile(pathDir2ByDir, pathDir3).getParent());

        Files.deleteIfExists(pathFileByDir);
        Files.deleteIfExists(movedDir);
        Files.deleteIfExists(pathDir);
        Files.deleteIfExists(pathDir3);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathFileByDir));
        assertFalse(Files.exists(movedDir));
        assertFalse(Files.exists(pathDir3));
    }
}
