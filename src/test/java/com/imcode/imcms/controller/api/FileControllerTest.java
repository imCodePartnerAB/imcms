package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.api.SourceFile;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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
        final List<SourceFile> files = fromJson(jsonResponse, new TypeReference<List<SourceFile>>() {
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
        final List<SourceFile> files = fromJson(jsonResponse, new TypeReference<List<SourceFile>>() {
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

        SourceFile sourceFile = new SourceFile(
                pathFile.getFileName().toString(),
                pathFile.toString(),
                FILE);

        performPostWithContentExpectOk(sourceFile);

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.isDirectory(pathFile));
    }

    @Test
    public void createFile_When_FolderNotExists_Expected_OkAndCreatedFolder() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectory(firstRootPath);

        SourceFile sourceFile = new SourceFile(
                pathDir.getFileName().toString(),
                pathDir.toString(),
                DIRECTORY);

        assertFalse(Files.exists(pathDir));
        performPostWithContentExpectOk(sourceFile);
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
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir2);
        Files.createFile(pathFile);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + pathDir.toString());
        assertEquals(2, fileController.getFiles(request).size());

        Properties properties = new Properties();
        properties.setProperty("src", pathFile.toString());
        properties.setProperty("target", pathDir3.toString());

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(properties, "copy");

        performRequestBuilderExpectedOk(requestBuilder);

        assertTrue(Files.exists(pathFile));
        assertEquals(2, fileController.getFiles(request).size());
        assertTrue(Files.exists(pathDir3));
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

        Properties properties = new Properties();
        properties.setProperty("src", pathFile.toString());
        properties.setProperty("target", pathFile2.toString());

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(properties, "move");

        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(2, fileController.getFiles(request).size());
        assertFalse(Files.exists(pathFile));
        assertTrue(Files.exists(pathFile2));
    }

    @Test
    public void renameFile_When_FileExists_Expected_OkAndReNameFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);
        final Path pathFile2 = firstRootPath.resolve(testFileName2);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + firstRootPath);

        Properties properties = new Properties();
        properties.setProperty("src", pathFile.toString());
        properties.setProperty("target", pathFile2.toString());

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(properties, "rename");

        performRequestBuilderExpectedOk(requestBuilder);
        final String renamedPath = fileController.getFiles(request).get(0).getFullPath();
        assertNotNull(renamedPath);
        assertTrue(Files.exists(pathFile.getParent().resolve(pathFile2.getFileName())));
        assertFalse(Files.exists(pathFile));
        assertEquals(pathFile2.getFileName(), Paths.get(renamedPath).getFileName());
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
        final Path target = firstRootPath.resolve(testDirectoryName);
        final byte[] imageFileBytes = Files.readAllBytes(testImageFile.toPath());
        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        Files.createDirectory(firstRootPath);
        Files.createDirectory(target);

        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath() + "/upload/" + firstRootPath)
                .file(file)
                .param("targetDirectory", firstRootPath.toString());

        assertFalse(Files.exists(firstRootPath.resolve(file.getName())));
        performRequestBuilderExpectedOk(fileUploadRequestBuilder);
        assertTrue(Files.exists(firstRootPath.resolve(file.getName())));
    }
}
