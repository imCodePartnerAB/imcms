package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.apache.uima.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class FileControllerTest extends AbstractControllerTest {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";
    private final String testDirectoryName3 = testDirectoryName + "three";


    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> testRootPaths;

    @Autowired
    private FileController fileController;

    @BeforeEach
    @AfterEach
    public void setUp() {
        testRootPaths.stream().map(Path::toFile).forEach(FileUtils::deleteRecursive);
    }

    @Override
    protected String controllerPath() {
        return "/files";
    }

    @Test
    public void getAllFiles_When_FilesExist_Expected_HttpStatusOk() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir);
        fileController.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = get(
                controllerPath() + "/paths/").param("file", "" + pathDir);

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getAllFiles_When_FilesExist_Expected_CorrectList() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir);
        fileController.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = get(
                controllerPath() + "/paths/").param("file", "" + pathDir);

        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<Path> files = fromJson(jsonResponse, new TypeReference<List<Path>>() {
        });

        assertNotNull(files);
        assertFalse(files.isEmpty());
        assertEquals(files.size(), fileController.getFiles(pathDir).size());
    }

    @Test
    public void getAllFiles_When_FileHasNotFiles_Expected_OkAndEmptyList() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir2);
        fileController.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = get(
                controllerPath() + "/paths/").param("file", "" + pathDir2);

        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<Path> files = fromJson(jsonResponse, new TypeReference<List<Path>>() {
        });

        assertNotNull(files);
        assertTrue(files.isEmpty());
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void createFile_When_FileNotExists_Expected_OkAndCreatedFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        fileController.createDir(firstRootPath);

        final MockHttpServletRequestBuilder requestBuilder = post(
                controllerPath() + "/file").param("file", "" + pathFile);

        assertFalse(Files.exists(pathFile));
        performRequestBuilderExpectedOk(requestBuilder);
        assertTrue(Files.exists(pathFile));
    }

    @Test
    public void createFile_When_FolderNotExists_Expected_OkAndCreatedFolder() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        fileController.createDir(firstRootPath);

        final MockHttpServletRequestBuilder requestBuilder = post(
                controllerPath() + "/directory").param("file", "" + pathDir);

        assertFalse(Files.exists(pathDir));
        performRequestBuilderExpectedOk(requestBuilder);
        assertTrue(Files.exists(pathDir));
    }

    @Test
    public void deleteFile_When_FileExists_Expected_OkAndDeleteFile() throws Exception {

    }

    @Test
    public void deleteFile_When_FolderExists_Expected_OkAndDeleteFolder() throws Exception {

    }

    @Test
    public void deleteFile_When_FileNotExists_Expected_CorrectException() throws Exception { //folder too

    }

    @Test
    public void getFile_When_FileExists_Expected_OkAndCorrectFile() throws Exception {

    }

    @Test
    public void copyFile_When_SrcFileExist_Expected_OkAndCopyFile() throws Exception {

    }

    @Test
    public void copyFile_When_TargetFileExist_Expected_CorrectException() throws Exception {

    }

    @Test
    public void moveFile_When_SrcFileExist_Expected_OkAndMoveFile() throws Exception {

    }

    @Test
    public void moveFile_When_TargetFileExists_Expected_CorrectException() throws Exception {

    }

    @Test
    public void moveFile_When_TargetFileNotExists_Expected_CorrectException() throws Exception { //todo need that ?

    }


}
