package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.apache.uima.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class FileControllerTest extends AbstractControllerTest {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";

    @Value("classpath:img1.jpg")
    private File testImageFile;

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
        return "/files/";
    }

    @Test
    public void getAllFiles_When_FilesExist_Expected_HttpStatusOk() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);


        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                controllerPath() + pathDir);

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getFile_When_FileOutSideRoot_Expected_CorrectException() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path testPath = Paths.get("../");
        final Path testPath2 = Paths.get("./");
        final Path testPath3 = Paths.get("/~/");
        final Path testPath4 = Paths.get(".././~/../.");

        final Path testPath5 = firstRootPath.resolve("../");
        final Path testPath6 = firstRootPath.resolve("/~/");
        final Path testPath7 = firstRootPath.resolve(".././~/../.");


        Files.createDirectory(firstRootPath);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + testPath.toString());
        final MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders.get(controllerPath() + testPath2.toString());
        final MockHttpServletRequestBuilder requestBuilder3 = MockMvcRequestBuilders.get(controllerPath() + testPath3.toString());
        final MockHttpServletRequestBuilder requestBuilder4 = MockMvcRequestBuilders.get(controllerPath() + testPath4.toString());
        final MockHttpServletRequestBuilder requestBuilder5 = MockMvcRequestBuilders.get(controllerPath() + testPath5.toString());
        final MockHttpServletRequestBuilder requestBuilder6 = MockMvcRequestBuilders.get(controllerPath() + testPath6.toString());
        final MockHttpServletRequestBuilder requestBuilder7 = MockMvcRequestBuilders.get(controllerPath() + testPath7.toString());

        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder2);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder3);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder4);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder5);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder6);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder7);
    }

    @Test
    public void getAllFiles_When_FilesExist_Expected_CorrectSize() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = get(
                controllerPath() + pathDir);

        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<Path> files = fromJson(jsonResponse, new TypeReference<List<Path>>() {
        });

        assertNotNull(files);
        assertFalse(files.isEmpty());
        assertEquals(1, files.size());
    }

    @Test
    public void getAllFiles_When_FileHasNotFiles_Expected_OkAndEmptyList() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir2);
        Files.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = get(
                controllerPath() + pathDir2);

        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<Path> files = fromJson(jsonResponse, new TypeReference<List<Path>>() {
        });

        assertNotNull(files);
        assertTrue(files.isEmpty());
        assertEquals(0, files.size());
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void createFile_When_FileNotExists_Expected_OkAndCreatedFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);
        final Path pathFile2 = firstRootPath.resolve(testFileName2);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile2);

        assertFalse(Files.exists(pathFile));

        final MockHttpServletRequestBuilder requestBuilder = post(
                controllerPath() + pathFile).param("isDirectory", "" + false);

        performRequestBuilderExpectedOk(requestBuilder);

        assertTrue(Files.exists(pathFile));
    }

    @Test
    public void createFile_When_FolderNotExists_Expected_OkAndCreatedFolder() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectory(firstRootPath);

        final MockHttpServletRequestBuilder requestBuilder = post(
                controllerPath() + pathDir).param("isDirectory", "" + true);

        assertFalse(Files.exists(pathDir));
        performRequestBuilderExpectedOk(requestBuilder);
        assertTrue(Files.exists(pathDir));
        assertTrue(Files.isDirectory(pathDir));
    }

    @Test
    public void saveFile_When_FileExists_Expected_OkAndSavedFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + pathFile.toString());

        final List<String> linesFile = Files.readAllLines(pathFile);
        assertEquals(0, linesFile.size());

        final String testText = "tesstttt text";
        final String saved = fileController.saveFile(request, testText.getBytes());

        assertEquals(testText, Files.readAllLines(pathFile).get(0));

        final Path path = Paths.get(saved);
        final String testText2 = "test2";
        final MockHttpServletRequestBuilder requestBuilder = put(controllerPath() + path)
                .content(testText2);

        performRequestBuilderExpectedOk(requestBuilder);
        final String newTestLine = Files.readAllLines(path).get(0);
        assertNotEquals(testText, newTestLine);
        assertEquals(testText2, newTestLine);
    }

    @Test
    public void deleteFile_When_FileExists_Expected_OkAndDeleteFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = delete(
                controllerPath() + pathFile);

        assertTrue(Files.exists(pathFile));
        performRequestBuilderExpectedOk(requestBuilder);
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FolderExists_Expected_OkAndDeleteFolder() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir);

        final MockHttpServletRequestBuilder requestBuilder = delete(
                controllerPath() + pathDir);

        assertTrue(Files.exists(pathDir));
        performRequestBuilderExpectedOk(requestBuilder);
        assertFalse(Files.exists(pathDir));
    }

    @Test
    public void copyFile_When_SrcFileExist_Expected_OkAndCopyFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFile2 = firstRootPath.resolve(testFileName);

        Files.createDirectories(pathDir2);
        Files.createFile(pathFile);

        assertFalse(Files.exists(pathFile2));

        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + pathDir.toString());
        assertEquals(2, fileController.getFiles(request).size());

        final MockHttpServletRequestBuilder requestBuilder = post(controllerPath() + "/copy/" + pathFile)
                .param("target", "" + pathFile2.getParent());

        performRequestBuilderExpectedOk(requestBuilder);

        assertTrue(Files.exists(pathFile));
        assertEquals(2, fileController.getFiles(request).size());
        assertTrue(Files.exists(pathFile2));
    }

    @Test
    public void moveFile_When_SrcFileExist_Expected_OkAndMoveFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFile2 = firstRootPath.resolve(testFileName);

        Files.createDirectories(pathDir2);
        Files.createFile(pathFile);

        assertFalse(Files.exists(pathFile2));

        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + pathFile2.getParent().toString());
        assertEquals(1, fileController.getFiles(request).size());

        final MockHttpServletRequestBuilder requestBuilder = put(controllerPath() + "/move/" + pathFile)
                .param("target", "" + pathFile2.getParent());

        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(2, fileController.getFiles(request).size());
        assertFalse(Files.exists(pathFile));
        assertTrue(Files.exists(pathFile2));
    }

    @Test
    public void renameFile_When_FileExists_Expected_OkAndReNameFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final String anotherName = "another.txt";
        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + firstRootPath);

        final MockHttpServletRequestBuilder requestBuilder = put(controllerPath() + "/rename/" + pathFile)
                .param("name", anotherName);

        performRequestBuilderExpectedOk(requestBuilder);
        final Path renamedPath = fileController.getFiles(request).get(0);
        assertNotNull(renamedPath);
        assertTrue(Files.exists(pathFile.getParent().resolve(anotherName)));
        assertFalse(Files.exists(pathFile));
        assertEquals(anotherName, renamedPath.getFileName().toString());
    }

    @Test
    public void downloadFile_When_FileExists_Expected_Ok() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final String testText = "some text...";
        Files.write(pathFile, testText.getBytes());

        final MockHttpServletRequestBuilder fileDownloadRequestBuilder = get(controllerPath() + "/file/" + pathFile);

        performRequestBuilderExpectedOkAndContentByteEquals(fileDownloadRequestBuilder, testText.getBytes());
    }

    @Test
    public void uploadFile_When_FileExistsAndUploadInRoot_Expected_OkAndUploadFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final byte[] imageFileBytes = Files.readAllBytes(testImageFile.toPath());
        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        Files.createDirectory(firstRootPath);

        final MockMultipartHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath() + "/upload/" + firstRootPath)
                .file(file);

        assertFalse(Files.exists(firstRootPath.resolve(file.getOriginalFilename())));
        performRequestBuilderExpectedOk(fileUploadRequestBuilder);
        assertTrue(Files.exists(firstRootPath.resolve(file.getOriginalFilename())));
    }
}