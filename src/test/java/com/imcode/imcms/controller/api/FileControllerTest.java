package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.uima.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@Transactional
public class FileControllerTest extends AbstractControllerTest {

    private final String testFileName = "fileName.txt";
    private final String testFileName2 = "fileName2.txt";
    private final String testTemplateName = "fileName.jsp";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";

    @Value("classpath:img1.jpg")
    private File testImageFile;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> testRootPaths;

    @Autowired
    private FileController fileController;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @BeforeEach
    @AfterEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();
        testRootPaths.stream().map(Path::toFile).forEach(FileUtils::deleteRecursive);
    }

    @Override
    protected String controllerPath() {
        return "/files/";
    }

    @Test
    public void getFiles_When_FilesExist_Expected_HttpStatusOk() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);


        final MockHttpServletRequestBuilder requestBuilder = get(
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
    public void getFiles_When_FilesExist_Expected_CorrectSize() throws Exception {
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
        assertEquals(0, files.size());
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

        SourceFile sourceFile = new SourceFile(
                pathFile.getFileName().toString(),
                pathFile.toString(),
                FILE,
                Collections.EMPTY_LIST);

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
                DIRECTORY,
                null);

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
        Properties properties = new Properties();
        properties.setProperty("fullPath", pathFile.toString());
        properties.setProperty("content", testText);
        final SourceFile saved = fileController.saveFile(properties);

        assertEquals(testText, Files.readAllLines(pathFile).get(0));

        final Path path = Paths.get(saved.getFullPath());
        final String changedText = "test2";
        properties.setProperty("content", changedText);
        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(properties, controllerPath());

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

        final SourceFile file = new SourceFile(
                pathFile.getFileName().toString(), pathFile.toString(), FILE, Collections.EMPTY_LIST
        );

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

        final SourceFile directory = new SourceFile(
                pathDir.getFileName().toString(), pathDir.toString(), DIRECTORY, null
        );

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
        final byte[] imageFileBytes = Files.readAllBytes(testImageFile.toPath());
        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        Files.createDirectory(firstRootPath);

        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath() + "/upload/" + firstRootPath)
                .file(file)
                .param("targetDirectory", firstRootPath.toString());

        assertFalse(Files.exists(firstRootPath.resolve(file.getName())));
        performRequestBuilderExpectedOk(fileUploadRequestBuilder);
        assertTrue(Files.exists(firstRootPath.resolve(file.getName())));
    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileNotExistsInGroup_Expected_OkAndChangeTemplateGroup() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathTemplateFile = firstRootPath.resolve(testTemplateName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathTemplateFile);

        final TemplateGroup testGroup = templateDataInitializer.createData("testGroup", 2, false);

        final Properties data = new Properties();
        data.setProperty("templatePath", pathTemplateFile.toString());
        data.setProperty("templateGroupName", testGroup.getName());

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(data, "template");
        performRequestBuilderExpectedOk(requestBuilder);

        final String originalFileName = FilenameUtils.removeExtension(pathTemplateFile.getFileName().toString());
        final Optional<Template> received = templateService.get(originalFileName);
        assertTrue(received.isPresent());

        assertNotNull(received.get().getTemplateGroup());
        assertEquals(testGroup.getName(), received.get().getTemplateGroup().getName());
    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileExistsInGroup_Expected_ChangeTemplateGroup() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathTemplateFile = firstRootPath.resolve(testTemplateName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathTemplateFile);

        List<TemplateGroup> testGroups = templateDataInitializer.createTemplateGroups(2);

        final String originalFileName = FilenameUtils.removeExtension(pathTemplateFile.getFileName().toString());
        final TemplateGroup defaultGroup = testGroups.get(0);

        final TemplateJPA template = new TemplateJPA(templateDataInitializer.createData(originalFileName));

        assertNull(template.getTemplateGroup());

        template.setTemplateGroup(defaultGroup);
        final TemplateJPA savedTemplate = templateRepository.save(template);

        assertNotNull(savedTemplate.getTemplateGroup());
        assertEquals(defaultGroup.getName(), savedTemplate.getTemplateGroup().getName());

        final TemplateGroup newGroup = testGroups.get(1);

        final Properties data = new Properties();
        data.setProperty("templatePath", pathTemplateFile.toString());
        data.setProperty("templateGroupName", newGroup.getName());

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(data, "template");
        performRequestBuilderExpectedOk(requestBuilder);

        final Optional<Template> receivedTemplate = templateService.get(originalFileName);
        assertTrue(receivedTemplate.isPresent());

        assertNotNull(receivedTemplate.get().getTemplateGroup());
        assertEquals(newGroup.getName(), receivedTemplate.get().getTemplateGroup().getName());
    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileNameEmpty_Expected_CorrectException() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path emptyFileName = firstRootPath.resolve(" ");

        Files.createDirectory(firstRootPath);

        final TemplateGroup testGroup = templateDataInitializer.createData(
                "testGroup", 2, false
        );

        final Properties data = new Properties();
        data.setProperty("templatePath", emptyFileName.toString());
        data.setProperty("templateGroupName", testGroup.getName());

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(data, "template");
        performRequestBuilderExpectException(EmptyFileNameException.class, requestBuilder);

        assertEquals(2, testGroup.getTemplates().size());
    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileInOutSideRoot_Expected_CorrectException() throws Exception {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathTemplateFile = firstRootPath.getParent().resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathTemplateFile);

        final TemplateGroup testGroup = templateDataInitializer.createData(
                "testGroup", 2, false);

        final Properties data = new Properties();
        data.setProperty("templatePath", pathTemplateFile.toString());
        data.setProperty("templateGroupName", testGroup.getName());

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(data, "template");
        performRequestBuilderExpectException(FileAccessDeniedException.class, requestBuilder);

        Files.delete(pathTemplateFile);
    }
}
