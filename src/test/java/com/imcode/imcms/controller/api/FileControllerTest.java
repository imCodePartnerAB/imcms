package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.service.FileService;
import imcode.server.Imcms;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static com.imcode.imcms.domain.service.api.DefaultFileService.RENAMED_FILE_FORMAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@Transactional
public class FileControllerTest extends AbstractControllerTest {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";

    @Value("classpath:img1.jpg")
    private File testImageFile;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> testRootPaths;

    @Value("WEB-INF/templates")
    private Path templateDirectory;

    @Value("${rootPath}")
    private Path rootPath;

    @Autowired
    private FileController fileController;

    @Autowired
    private BiFunction<Path, Boolean, SourceFile> fileToSourceFile;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @AfterEach
    public void setUp() throws IOException {
	    ReflectionTestUtils.setField(Imcms.getServices().getManagedBean(FileService.class), "rootPaths", testRootPaths);  //nonexistent paths are removed when creating the service
	    testRootPaths.stream().filter(path -> !path.toString().contains(templateDirectory.toString())).map(Path::toFile).forEach(FileSystemUtils::deleteRecursively);
    }

    @Override
    protected String controllerPath() {
        return "/files/";
    }

    @Test
    public void getFile_When_FilesExist_Expected_HttpStatusOkAndCorrectFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final SourceFile sourceFileTest = fileToSourceFile.apply(pathFile, true);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/get-file")
                .param("path", pathFile.toString());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(sourceFileTest));
    }

    @Test
    public void getFile_When_FilesInOutSideRootDir_Expected_CorrectException() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.getParent().resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/get-file")
                .param("path", pathFile.toString());

        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder);
        Files.delete(pathFile);
    }

    @Test
    public void getFiles_When_FilesExist_Expected_HttpStatusOk_And_CorrectSize() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);


        final MockHttpServletRequestBuilder requestBuilder = get(
                controllerPath() + pathDir);

        performRequestBuilderExpectedOk(requestBuilder);

        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<SourceFile> files = fromJson(jsonResponse, new TypeReference<List<SourceFile>>() {
        });

        assertNotNull(files);
        assertFalse(files.isEmpty());
        assertEquals(1, files.size());
    }

    @Test
    public void getFiles_When_FileOutSideRoot_Expected_CorrectException() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path testPath = Paths.get("../");
        final Path testPath2 = Paths.get("./");
        final Path testPath3 = Paths.get("/~/");
        final Path testPath4 = Paths.get(".././~/../.");

        final Path testPath5 = firstRootPath.resolve("../");
        final Path testPath6 = firstRootPath.resolve("/~/");
        final Path testPath7 = firstRootPath.resolve(".././~/../.");

        Files.createDirectory(firstRootPath);

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + testPath.toString());
        final MockHttpServletRequestBuilder requestBuilder2 = get(controllerPath() + testPath2.toString());
        final MockHttpServletRequestBuilder requestBuilder3 = get(controllerPath() + testPath3.toString());
        final MockHttpServletRequestBuilder requestBuilder4 = get(controllerPath() + testPath4.toString());
        final MockHttpServletRequestBuilder requestBuilder5 = get(controllerPath() + testPath5.toString());
        final MockHttpServletRequestBuilder requestBuilder6 = get(controllerPath() + testPath6.toString());
        final MockHttpServletRequestBuilder requestBuilder7 = get(controllerPath() + testPath7.toString());

        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder2);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder3);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder4);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder5);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder6);
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder7);
    }

    @Test
    public void getFiles_When_FileHasNotFiles_Expected_OkAndEmptyList() throws Exception {
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
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getFiles_When_OrderNotCorrect_Expected_CorrectOrder() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);

        Files.createDirectory(firstRootPath);

        final Path file1 = firstRootPath.resolve(testFileName);
        final Path file2 = firstRootPath.resolve(testFileName2);
        final Path directory1 = firstRootPath.resolve(testDirectoryName);
        final Path directory2 = firstRootPath.resolve(testDirectoryName2);

        Files.createFile(file1);
        Files.createDirectory(directory1);
        Files.createFile(file2);
        Files.createDirectory(directory2);

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + firstRootPath);

        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<SourceFile> receivedFiles = fromJson(jsonResponse, new TypeReference<List<SourceFile>>() {
        });

        assertEquals(DIRECTORY, receivedFiles.get(0).getFileType());
        assertEquals(DIRECTORY, receivedFiles.get(1).getFileType());
        assertEquals(FILE, receivedFiles.get(2).getFileType());
        assertEquals(FILE, receivedFiles.get(3).getFileType());
    }

    @Test
    public void createFile_When_FileNotExists_Expected_OkAndCreatedFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);
        final Path pathFile2 = firstRootPath.resolve(testFileName2);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile2);

        assertFalse(Files.exists(pathFile));

        SourceFile sourceFile = fileToSourceFile.apply(pathFile, false);

        performPostWithContentExpectOk(sourceFile);

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.isDirectory(pathFile));
    }

    @Test
    public void createFile_When_FolderNotExists_Expected_OkAndCreatedFolder() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectory(firstRootPath);

        final SourceFile sourceFile = fileToSourceFile.apply(pathDir, false);
        sourceFile.setFileType(DIRECTORY);

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

        final List<String> linesFile = Files.readAllLines(pathFile);
        assertEquals(0, linesFile.size());

        final String testText = "tesstttt text";

        final MockHttpServletRequestBuilder saveFileRequestBuilder = multipart(controllerPath())
                .with(request -> {
                    request.setMethod(HttpMethod.PUT.name());
                return request;
                })
                .param("fullPath", pathFile.toString())
                .param("content", testText);

        final SourceFile saved = objectMapper.readValue(performRequestBuilderExpectedOk(saveFileRequestBuilder).andReturn().getResponse().getContentAsByteArray(), SourceFile.class);

        assertEquals(testText, Files.readAllLines(pathFile).get(0));

        final Path path = Paths.get(saved.getFullPath());
        final String changedText = "test2";

        final MockHttpServletRequestBuilder requestBuilder = multipart(controllerPath())
                .with(request -> {
                    request.setMethod(HttpMethod.PUT.name());
                    return request;
                })
                .param("fullPath", pathFile.toString())
                .param("content", changedText);

        performRequestBuilderExpectedOk(requestBuilder);
        final String newTestLine = Files.readAllLines(path).get(0);
        assertNotEquals(testText, newTestLine);
        assertEquals(changedText, newTestLine);
    }

    @Test
    public void deleteFile_When_FileExists_Expected_OkAndDeleteFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final SourceFile file = fileToSourceFile.apply(pathFile, false);

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(file);

        assertTrue(Files.exists(pathFile));
        performRequestBuilderExpectedOk(requestBuilder);
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void deleteFile_When_FolderExists_Expected_OkAndDeleteFolder() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir);

        final SourceFile directory = fileToSourceFile.apply(pathDir, false);

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(directory);

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
    public void copyFileWitnRename_Expected_OkAndCopyFileWithNewFilename() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName);
        final String filename = "testFilename24.text";

        Files.createDirectories(pathDir2);
        Files.createFile(pathFile);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + pathDir.toString());
        assertEquals(2, fileController.getFiles(request).size());

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithoutContent( "copy/rename/")
                .param("src", pathFile.toString())
                .param("target", pathDir3.toString())
                .param("newFilename", filename);

        performRequestBuilderExpectedOk(requestBuilder);

        assertTrue(Files.exists(pathFile));
        assertEquals(3, fileController.getFiles(request).size());
        assertTrue(Files.exists(pathDir3));
        assertTrue(Files.exists(pathDir.resolve(filename)));
    }

    @Test
    public void moveFile_When_SrcFileExist_Expected_OkAndMoveFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDirTarget = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDirTarget);
        Files.createFile(pathFile);


        Properties properties = new Properties();
        properties.setProperty("src", pathFile.toString());
        properties.setProperty("target", pathDirTarget.toString());

        assertEquals(2, Files.list(pathDir).count());
        assertEquals(0, Files.list(pathDirTarget).count());
        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(properties, "move");

        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(1, Files.list(pathDir).count());
        assertEquals(1, Files.list(pathDirTarget).count());
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void moveFileWithNewFilename_Expected_OKAndMoveFileWithNewFilename() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDirTarget = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);
        final String fileName = "testName24.txt";

        Files.createDirectories(pathDirTarget);
        Files.createFile(pathFile);

        assertEquals(2, Files.list(pathDir).count());
        assertEquals(0, Files.list(pathDirTarget).count());
        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithoutContent("move/rename/")
                .param("src", pathFile.toString())
                .param("target", pathDirTarget.toString())
                .param("newFilename", fileName);

        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(1, Files.list(pathDir).count());
        assertEquals(1, Files.list(pathDirTarget).count());
        assertFalse(Files.exists(pathFile));
    }

    @Test
    public void renameFile_When_FileExists_Expected_OkAndReNameFile() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);
        final String newFileName = testFileName2;

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn(controllerPath() + firstRootPath);

        Properties properties = new Properties();
        properties.setProperty("src", pathFile.toString());
        properties.setProperty("newName", newFileName);

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(properties, "rename");

        performRequestBuilderExpectedOk(requestBuilder);
        final String renamedPath = fileController.getFiles(request).get(0).getFullPath();
        assertNotNull(renamedPath);
        assertTrue(Files.exists(pathFile.getParent().resolve(newFileName)));
        assertFalse(Files.exists(pathFile));
        assertEquals(newFileName, Paths.get(renamedPath).getFileName().toString());
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
        final MockMultipartFile file = new MockMultipartFile("file.jpg", "img1-test.jpg", null, imageFileBytes);
        Files.createDirectory(firstRootPath);

        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath() + "/upload/" + firstRootPath)
                .file(file)
                .param("targetDirectory", firstRootPath.toString());

        assertFalse(Files.exists(firstRootPath.resolve(file.getName())));
        performRequestBuilderExpectedOk(fileUploadRequestBuilder);
        assertTrue(Files.exists(firstRootPath.resolve(file.getName())));
    }

    @Test
    public void defaultRenameFile_Expected_OkAndFileRenamed() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDirTarget = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDirTarget);
        Files.createFile(pathFile);

        Properties properties = new Properties();
        properties.setProperty("path", pathFile.toString());

        assertEquals(2, Files.list(pathDir).count());
        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(properties, "rename/default/");

        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(2, Files.list(pathDir).count());

        assertFalse(Files.exists(pathFile));
        assertTrue(Files.exists(pathDir.resolve(RENAMED_FILE_FORMAT.formatted("fileName", 1, "txt"))));
    }

    @Test
    public void existsFile_Expected_OkAndTrue() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDirTarget = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDirTarget);
        Files.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath()+"exists/")
                .param("path", pathFile.toString());

        performRequestBuilderExpectedOk(requestBuilder).andExpect(MockMvcResultMatchers.content().string("true"));
    }

    @Test
    public void existsFile_When_FileNotExist_Expected_OkAndFalse() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDirTarget = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDirTarget);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath()+"/exists/")
                .param("path", pathFile.toString());

        performRequestBuilderExpectedOk(requestBuilder).andExpect(MockMvcResultMatchers.content().string("false"));
    }

    @Test
    public void existsFiles_Expected_OkAndFilesExist() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDirTarget = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDirTarget);
        Files.createFile(pathFile);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath()+"exists/all/")
                .param("paths", pathFile.toString());

        performRequestBuilderExpectedOk(requestBuilder);
        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<SourceFile> sourceFiles = fromJson(jsonResponse, new TypeReference<>() {
		});

        assertEquals(1, sourceFiles.size());
    }

    @Test
    public void existsFiles_When_FilesNotExist_Expected_OkAndEmptyResponse() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDirTarget = pathDir.resolve(testDirectoryName2);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDirTarget);

		final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() +"exists/all/")
                .param("paths", pathFile.toString());

        performRequestBuilderExpectedOk(requestBuilder);
        final String jsonResponse = getJsonResponse(requestBuilder);
        final List<SourceFile> sourceFiles = fromJson(jsonResponse, new TypeReference<>() {
        });

        assertEquals(0, sourceFiles.size());
    }
}
