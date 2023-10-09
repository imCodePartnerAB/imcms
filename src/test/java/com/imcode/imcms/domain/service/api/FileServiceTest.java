package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.exception.FileOperationFailureException;
import com.imcode.imcms.domain.exception.TemplateFileException;
import com.imcode.imcms.domain.service.FileService;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class FileServiceTest extends WebAppSpringTestConfig {

    private final String testFileName = "fileNameTest.jsp";
    private final String testFileName2 = "fileNameTest2.txt";
    private final String testTemplateName = "templateTest.jsp";
    private final String testDirectoryName = "dirNameTest";
    private final String testDirectoryName2 = testDirectoryName + "two";
    private final String testDirectoryName3 = testDirectoryName + "three";
    private final String newNameFile = "newNameTest.jsp";
    private final String newNameDirectory = "newNameDirectory";

    private final String notValidNewTemplateName = "newNameTest.txt";
    private final String notAllowedExtensionFile = "notAllowedExtensionFile.xml";
    private final Path outsideFilePath = Paths.get(testFileName);

    @Autowired
    private FileService fileService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateDataInitializer templateDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private BiFunction<Path, Boolean, SourceFile> fileToSourceFile;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> testRootPaths;
    @Value("WEB-INF/templates")
    private Path templateDirectoryFromProperties;
    @Value("WEB-INF/templates/text")
    private Path templateTextDirectory;
    @Value("${rootPath}")
    private Path rootPath;

    @BeforeEach
    @AfterEach
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(fileService, "rootPaths", testRootPaths);  //nonexistent paths are removed when creating the service

        templateDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();

	    Files.deleteIfExists(outsideFilePath);
	    testRootPaths.stream()
			    .filter(path -> !path.toString().contains(templateDirectoryFromProperties.toString()))
			    .map(Path::toFile)
			    .forEach(FileSystemUtils::deleteRecursively);
	    deleteFilesInTemplateDirectory(testFileName, testFileName2, testTemplateName, newNameFile, notValidNewTemplateName);
        Files.createDirectories(templateTextDirectory);
    }

    private void deleteFilesInTemplateDirectory(String... names) throws IOException {
        for(String name: names){
            Files.deleteIfExists(templateTextDirectory.resolve(name));
        }
    }

    @Test
    public void getDocumentsByTemplateName_When_TemplateHasDocuments_Expected_CorrectSize() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path template = firstRootPath.resolve(testTemplateName);
        final String templateName = FilenameUtils.removeExtension(template.getFileName().toString());
        DocumentDTO document = documentDataInitializer.createData();
        Files.createDirectory(firstRootPath);
        Files.createFile(template);
        templateDataInitializer.createData(document.getId(), templateName, templateName);

        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(template);
        assertEquals(1, documents.size());
    }

    @Test
    public void getDocumentsByTemplateName_When_TemplateHasNotDocuments_Expected_EmptyResult() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path template = firstRootPath.resolve(testTemplateName);
        final String templateName = template.getFileName().toString();
        Files.createDirectory(firstRootPath);
        Files.createFile(template);
        templateDataInitializer.createData(templateName);

        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(template);
        assertTrue(documents.isEmpty());
    }

    @Test
    public void getDocumentsByTemplateName_When_TemplateNotFileButNameExists_Expected_CorrectSize() throws IOException {
        final String testTemplateName = "test";
        DocumentDTO document = documentDataInitializer.createData();
        templateDataInitializer.createData(document.getId(), testTemplateName, testTemplateName);

        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(Paths.get(testTemplateName));
        assertEquals(1, documents.size());
    }

    @Test
    public void getDocumentsByTemplateName_When_GetImage_Expected_EmptyResult() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path imagePath = firstRootPath.resolve("image.png");
        Files.createDirectory(firstRootPath);
        Files.createFile(imagePath);

        assertTrue(fileService.getDocumentsByTemplatePath(imagePath).isEmpty());
    }

    @Test
    public void getFiles_When_FilesInDirectoryExist_Expected_CorrectFiles() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        final List<SourceFile> files = Collections.singletonList(fileToSourceFile.apply(pathFile, false));

        final List<SourceFile> foundFiles = fileService.getFiles(pathDir);

        assertEquals(files.size(), foundFiles.size());
        assertEquals(files, foundFiles);
    }

    @Test
    public void getFiles_When_DirectoryHasFolderAndFile_Expected_CorrectSize() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);

        Files.createDirectories(pathDir2);
        Files.createFile(pathFile);

        final List<SourceFile> expectedFiles = Arrays.asList(
                fileToSourceFile.apply(pathDir2, false),
                fileToSourceFile.apply(pathFile, false)
        );
        final List<SourceFile> foundFiles = fileService.getFiles(pathDir);

        assertEquals(expectedFiles.size(), foundFiles.size());
    }

    @Test
    public void getFiles_When_FilesInDirectoryNotExist_Expected_EmptyResult() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);

        assertEquals(0, fileService.getFiles(pathDir).size());
    }

    @Test
    public void getFiles_When_FilesInTemplateTextDir_Expected_ListWithCorrectValuesNumberOfDocuments() throws IOException {
        final Path pathFile = templateTextDirectory.resolve(testFileName);
        Files.createFile(pathFile);

        final String testTemplateName = FilenameUtils.removeExtension(testFileName);
        DocumentDTO document = documentDataInitializer.createData();
        templateDataInitializer.createData(document.getId(), testTemplateName, testTemplateName);

        final SourceFile expectedSourceFile = fileToSourceFile.apply(pathFile, false);
        expectedSourceFile.setNumberOfDocuments(fileService.getDocumentsByTemplatePath(Paths.get(testTemplateName)).size());
        final SourceFile sourceFile = fileService.getFiles(templateTextDirectory).stream()
                .filter(source -> source.getFullPath().equals(pathFile.toString()))
                .findAny().get();

        assertEquals(expectedSourceFile.getNumberOfDocuments(), sourceFile.getNumberOfDocuments());
    }

    @Test
    public void createFile_WhenFileNameEmpty_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir);

        final Path pathNewFile = pathDir.resolve(" ");
        final SourceFile newFile = fileToSourceFile.apply(pathNewFile, true);

        assertThrows(EmptyFileNameException.class, () -> fileService.createFile(newFile, false));

    }

    @Test
    public void createFile_WhenFileNotExist_Expected_CreatedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir);

        final Path pathNewFile = pathDir.resolve(testFileName);
        final SourceFile newFile = fileToSourceFile.apply(pathNewFile, false);

        final SourceFile createdFile = fileService.createFile(newFile, false);

        assertTrue(Files.exists(pathNewFile));
        assertEquals(newFile, createdFile);
    }


    @Test
    public void createFile_When_FileAndDirCreateToOutSideRootDir_And_FileWithNotAllowedExtension_Expected_CorrectException() throws IOException {
        final Path rootDir = Files.createDirectory(testRootPaths.get(0));
        final Path rootDirParent = rootDir.getParent();

        final Path outSidePathDir = rootDirParent.resolve(testDirectoryName);
        final Path outSidePathFile = rootDirParent.resolve(testFileName);
        final Path notAllowedExtension = rootDir.resolve(notAllowedExtensionFile);

        assertFalse(Files.exists(outSidePathDir));
        assertFalse(Files.exists(outSidePathFile));
        assertFalse(Files.exists(notAllowedExtension));

        final SourceFile outSideNewDir = fileToSourceFile.apply(outSidePathDir, false);
        final SourceFile outSideNewFile = fileToSourceFile.apply(outSidePathFile, false);
        final SourceFile notAllowedExtensionNewFile = fileToSourceFile.apply(notAllowedExtension, false);

        assertThrows(FileAccessDeniedException.class, () -> fileService.createFile(outSideNewDir, true));
        assertThrows(FileAccessDeniedException.class, () -> fileService.createFile(outSideNewFile, false));
        assertThrows(FileAccessDeniedException.class, () -> fileService.createFile(notAllowedExtensionNewFile, false));

        assertFalse(Files.exists(outSidePathDir));
        assertFalse(Files.exists(outSidePathFile));
        assertFalse(Files.exists(notAllowedExtension));
    }

    @Test
    public void createFile_When_TemplateTextDir_And_TemplateNameIsValid_Expected_AddedTemplateEntity() throws IOException {
        assertTrue(templateRepository.findAll().isEmpty());

        final Path pathNewFile = templateTextDirectory.resolve(testFileName);
        final SourceFile newFile = fileToSourceFile.apply(pathNewFile, false);
        final SourceFile createdFile = fileService.createFile(newFile, false);
        assertTrue(Files.exists(pathNewFile));

        final List<TemplateJPA> templates = templateRepository.findAll();
        assertEquals(1, templates.size());
        assertEquals(FilenameUtils.removeExtension(createdFile.getFileName()), templates.get(0).getName());
    }

    @Test
    public void createFile_When_TemplateTextDir_And_TemplateNameIsNotValid_Expected_NotAddedTemplateEntity() throws IOException {
        assertTrue(templateRepository.findAll().isEmpty());

        final Path pathNewFile = templateTextDirectory.resolve(testFileName2);
        final SourceFile newFile = fileToSourceFile.apply(pathNewFile, false);
        final SourceFile createdFile = fileService.createFile(newFile, false);
        assertTrue(Files.exists(pathNewFile));

        assertTrue(templateRepository.findAll().isEmpty());
    }

    @Test
    public void createDir_When_PathToTemplateTextDir_Expected_CorrectException() {
        final Path pathNewFile = templateTextDirectory.resolve(testDirectoryName);
        final SourceFile newFile = fileToSourceFile.apply(pathNewFile, false);
        assertThrows(TemplateFileException.class, () -> fileService.createFile(newFile, true));
        assertFalse(Files.exists(pathNewFile));
    }

    @Test
    public void saveFile_When_FileDoesNotExist_Expected_CreatedAndSavedFile() throws IOException{
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectories(pathDir);
        assertFalse(Files.exists(pathFile));

        final String testText = "bla-bla-bla";
        final SourceFile saved = fileService.saveFile(pathFile, testText.getBytes(), null);

        assertNotNull(saved);
        assertTrue(Files.exists(Paths.get(saved.getFullPath())));
        String savedContent = new String(saved.getContents()).trim();
        assertEquals(testText, savedContent);
    }

    @Test
    public void saveFile_When_FileExistAndOverWrite_Expected_SavedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);


        final String testText = "bla-bla-bla";
        final SourceFile saved = fileService.saveFile(pathFile, testText.getBytes(), null);

        assertNotNull(saved);
        assertTrue(Files.exists(Paths.get(saved.getFullPath())));
        String savedContent = new String(saved.getContents()).trim();
        assertEquals(testText, savedContent);
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
                pathFile2, testText.getBytes(), StandardOpenOption.CREATE_NEW)
        );
    }

    @Test
    public void saveFile_When_FileToOutsideRootDir_Expected_CorrectException(){
        final String testText = "bla-bla-bla";
        assertThrows(FileAccessDeniedException.class, () ->
                fileService.saveFile(outsideFilePath, testText.getBytes(), null));
    }

    @Test
    public void saveFile_When_FileWithNotAllowedExtension_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        Files.createDirectories(firstRootPath);

        final String testText = "bla-bla-bla";
        assertThrows(FileAccessDeniedException.class, () ->
                fileService.saveFile(firstRootPath.resolve(notAllowedExtensionFile), testText.getBytes(), null));
    }

    @Test
    public void saveFile_When_PathToTemplateTextDir_And_TemplateNameIsValid_Expected_SavedTemplateEntity() throws IOException {
        final Path pathFile = templateTextDirectory.resolve(testFileName);

        assertTrue(templateRepository.findAll().isEmpty());

        final String testText = "bla-bla-bla";
        final SourceFile saved = fileService.saveFile(pathFile, testText.getBytes(), null);

        assertTrue(Files.exists(Paths.get(saved.getFullPath())));
        assertEquals(1, templateRepository.findAll().size());
    }

    @Test
    public void saveFile_When_PathToTemplateTextDir_And_TemplateNameIsNotValid_Expected_NotSavedTemplateEntity() throws IOException {
        final Path pathFile = templateTextDirectory.resolve(testFileName2);

        assertTrue(templateRepository.findAll().isEmpty());

        final String testText = "bla-bla-bla";
        final SourceFile saved = fileService.saveFile(pathFile, testText.getBytes(), null);

        assertTrue(Files.exists(Paths.get(saved.getFullPath())));
        assertTrue(templateRepository.findAll().isEmpty());
    }

    @Test
    public void getFile_When_FileExists_Expected_CorrectFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);

        assertEquals(pathFile.toString(), fileService.getFile(pathFile).getFullPath());
        assertEquals(new String(Files.readAllBytes(pathFile)), new String(fileService.getFile(pathFile).getContents()));
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

        assertTrue(Files.exists(Paths.get(fileService.getFile(testPath6).getFullPath())));
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
    public void getFile_When_PathFileFromToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path outDirRoot = Paths.get(firstRootPath.getParent().toString());

        Files.createDirectory(firstRootPath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(outDirRoot));
    }

    @Test
    public void getFile_When_FileWithNotAllowedExtension_Expected_NoException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path notAllowedExtensionPath = firstRootPath.resolve(notAllowedExtensionFile);

        Files.createDirectory(firstRootPath);
        Files.createFile(notAllowedExtensionPath);

        assertDoesNotThrow(() -> fileService.getFile(notAllowedExtensionPath));
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
    public void deleteDir_When_DirHasFiles_Expected_Delete() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFile2 = pathDir.resolve(testFileName2);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathFile3 = pathDir2.resolve("test" + testFileName2);

        Files.createDirectories(pathDir2);
        Files.createFile(pathFile);
        Files.createFile(pathFile2);
        Files.createFile(pathFile3);

        fileService.deleteFile(pathDir);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));
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
    public void deleteDir_When_DirIsEmpty_Expected_Deleted() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        assertEquals(1, Files.list(firstRootPath).count());
        fileService.deleteFile(pathDir);
        assertEquals(0, Files.list(firstRootPath).count());
    }

    @Test
    public void deleteFile_When_FileInTemplateTextDir_And_TemplateIsNotUsedDocuments_Expected_Deleted() throws IOException {
        final Path template = templateTextDirectory.resolve(testTemplateName);
        Files.createFile(template);
        assertTrue(Files.exists(template));

        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(template);
        assertTrue(documents.isEmpty());

        fileService.deleteFile(template);
        assertFalse(Files.exists(template));
    }

    @Test
    public void deleteFile_When_FileInTemplateTextDir_And_TemplateIsUsedByDocuments_Expected_CorrectException() throws IOException {
        final Path template = templateTextDirectory.resolve(testTemplateName);
        final String templateName = FilenameUtils.removeExtension(template.getFileName().toString());
        Files.createFile(template);
        assertTrue(Files.exists(template));

        DocumentDTO document = documentDataInitializer.createData();
        templateDataInitializer.createData(document.getId(), templateName, templateName);

        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(template);
        assertEquals(1, documents.size());
        assertThrows(TemplateFileException.class, () -> fileService.deleteFile(templateTextDirectory));
        assertTrue(Files.exists(template));
    }

    @Test
    public void deleteDir_When_DirIsTemplateTextDir_Expected_CorrectException() {
        assertTrue(Files.exists(templateTextDirectory));
        assertThrows(TemplateFileException.class, () -> fileService.deleteFile(templateTextDirectory));
    }

    @Test
    public void deleteFile_When_FileFromOutsideRootDir_Expected_CorrectException() {
        assertThrows(FileAccessDeniedException.class, () -> fileService.deleteFile(outsideFilePath));
    }

    @Test
    public void deleteDir_When_DirFromOutsideRootDir_Expected_CorrectException() throws IOException{
        final Path firstRootPath = testRootPaths.get(0);
        Files.createDirectories(firstRootPath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.deleteFile(firstRootPath.getParent()));
    }

    @Test
    public void deleteFile_When_FileWithNotAllowedExtension_Expected_CorrectException() throws IOException{
        final Path firstRootPath = testRootPaths.get(0);
        final Path notAllowedExtensionFilePath = firstRootPath.resolve(notAllowedExtensionFile);

        Files.createDirectories(firstRootPath);
        Files.createFile(notAllowedExtensionFilePath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.deleteFile(notAllowedExtensionFilePath));
    }

    @Test
    public void copyFile_When_SrcFileExists_Expected_CopyFile() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFile);

        assertEquals(0, Files.list(pathDir2).count());
        fileService.copyFile(Collections.singletonList(pathFile), pathDir2);
        assertEquals(1, Files.list(pathDir).count());
        assertEquals(1, Files.list(pathDir2).count());
    }


    @Test
    public void copyFiles_When_FilesExist_Expected_CopyFiles() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileInDir = pathDir.resolve(testFileName);
        final Path pathFile2InDir = pathDir.resolve(testFileName2);
        final Path pathDirTarget = firstRootPath.resolve(testDirectoryName2);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createDirectory(pathDirTarget);
        List<Path> paths = new ArrayList<>();
        paths.add(Files.createFile(pathFileInDir));
        paths.add(Files.createFile(pathFile2InDir));

        assertEquals(2, Files.list(pathDir).count());
        assertEquals(0, Files.list(pathDirTarget).count());
        fileService.copyFile(paths, pathDirTarget);
        assertEquals(2, Files.list(pathDir).count());
        assertEquals(2, Files.list(pathDirTarget).count());
    }

    @Test
    public void copyFile_When_FileCopyToAndFromOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path outsideRootPath = firstRootPath.getParent();
        final Path fileInRootPath = firstRootPath.resolve(testFileName2);

        Files.createDirectory(firstRootPath);
        Files.createFile(fileInRootPath);
        Files.createFile(outsideFilePath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(
                Collections.singletonList(outsideFilePath), firstRootPath));
        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(Collections.singletonList(
                fileInRootPath), outsideRootPath));
    }

    @Test
    public void copyFile_When_DirCopyToAndFromOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path secondOutsideRootPath = testRootPaths.get(1).getParent();

        Files.createDirectory(firstRootPath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(
                Collections.singletonList(firstRootPath), secondOutsideRootPath));
        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(Collections.singletonList(
                secondOutsideRootPath), firstRootPath));
    }

    @Test
    public void copyFile_When_FileWithNotAllowedExtension_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path secondRootPath = testRootPaths.get(1);
        final Path notAllowedExtensionFilePath = firstRootPath.resolve(notAllowedExtensionFile);
        Files.createDirectory(firstRootPath);
        Files.createDirectory(secondRootPath);
        Files.createFile(notAllowedExtensionFilePath);

        assertThrows(FileAccessDeniedException.class,
                () -> fileService.copyFile(Collections.singletonList(notAllowedExtensionFilePath), secondRootPath));
    }

    @Test
    public void copyFile_When_FileInTemplateTextDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        assertThrows(TemplateFileException.class, () -> fileService.copyFile(Collections.singletonList(pathFile), templateTextDirectory));
    }

    @Test
    public void copyFile_When_FileFromTemplateTextDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path templateFile = templateTextDirectory.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(templateFile);

        assertThrows(TemplateFileException.class, () -> fileService.copyFile(Collections.singletonList(templateFile), firstRootPath));
    }

    @Test
    public void copyDir_When_DirIsTemplateTextDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        Files.createDirectory(firstRootPath);

        assertThrows(TemplateFileException.class, () -> fileService.copyFile(Collections.singletonList(templateTextDirectory), firstRootPath));
    }

    @Test
    public void moveFiles_When_FilesExist_Expected_MoveFiles() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileInDir = pathDir.resolve(testFileName);
        final Path pathFile2InDir = pathDir.resolve(testFileName2);
        final Path pathDirTarget = firstRootPath.resolve(testDirectoryName2);
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createDirectory(pathDirTarget);

        List<Path> paths = new ArrayList<>();
        paths.add(Files.createFile(pathFileInDir));
        paths.add(Files.createFile(pathFile2InDir));

        assertEquals(2, Files.list(pathDir).count());
        assertEquals(0, Files.list(pathDirTarget).count());
        fileService.moveFile(paths, pathDirTarget);
        assertEquals(0, Files.list(pathDir).count());
        assertEquals(2, Files.list(pathDirTarget).count());
    }

    @Test
    public void moveFile_When_FileExist_Expected_moveCorrectFile() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = firstRootPath.resolve(testFileName);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));

        Files.createDirectories(pathDir2);
        Files.createDirectory(pathDir);

        List<Path> paths = Collections.singletonList(Files.createFile(pathFileByDir));

        assertEquals(3, fileService.getFiles(firstRootPath).size());
        assertEquals(0, fileService.getFiles(pathDir).size());

        fileService.moveFile(paths, pathDir);

        assertFalse(Files.exists(pathFileByDir));
        assertEquals(2, fileService.getFiles(firstRootPath).size());
        assertEquals(1, fileService.getFiles(pathDir).size());
    }

    @Test
    public void moveFile_When_FilesMoveToFromOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path outsideRootPath = firstRootPath.getParent();
        final Path fileInRootPath = firstRootPath.resolve(testFileName2);

        Files.createDirectory(firstRootPath);
        Files.createFile(fileInRootPath);
        Files.createFile(outsideFilePath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(
                Collections.singletonList(outsideFilePath), firstRootPath));
        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(Collections.singletonList(
                fileInRootPath), outsideRootPath));
    }

    @Test
    public void moveFile_When_FileIsRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path secondRootPath = testRootPaths.get(1);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(secondRootPath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(
                Collections.singletonList(secondRootPath), firstRootPath));
    }

    @Test
    public void moveFile_When_DirMoveToFromOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path secondOutsideRootPath = testRootPaths.get(1).getParent();

        Files.createDirectory(firstRootPath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(
                Collections.singletonList(firstRootPath), secondOutsideRootPath));
        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(Collections.singletonList(
                secondOutsideRootPath), firstRootPath));
    }

    @Test
    public void moveFile_When_FileWithNotAllowedExtension_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path secondRootPath = testRootPaths.get(1);
        final Path notAllowedExtensionFilePath = firstRootPath.resolve(notAllowedExtensionFile);
        Files.createDirectory(firstRootPath);
        Files.createDirectory(secondRootPath);
        Files.createFile(notAllowedExtensionFilePath);

        assertThrows(FileAccessDeniedException.class,
                () -> fileService.moveFile(Collections.singletonList(notAllowedExtensionFilePath), secondRootPath));
    }

    @Test
    public void moveFile_When_FileInTemplateTextDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);
        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);

        assertThrows(TemplateFileException.class, () -> fileService.moveFile(Collections.singletonList(pathFile), templateTextDirectory));
    }

    @Test
    public void moveFile_When_FileFromTemplateTextDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path templateFile = templateTextDirectory.resolve(testFileName);
        Files.createDirectory(firstRootPath);
        Files.createFile(templateFile);

        assertThrows(TemplateFileException.class, () -> fileService.moveFile(Collections.singletonList(templateFile), firstRootPath));
    }

    @Test
    public void moveDir_When_DirIsTemplateTextDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        Files.createDirectory(firstRootPath);

        assertThrows(TemplateFileException.class, () -> fileService.moveFile(Collections.singletonList(templateTextDirectory), firstRootPath));
    }

    @Test
    public void getFiles_WhenFilesHaveSubFiles_Expected_CorrectSize() throws IOException {
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
    public void getFiles_When_OrderNotCorrect_Expected_CorrectOrder() throws IOException {
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

        List<SourceFile> receivedFiles = fileService.getFiles(firstRootPath);

        assertEquals(DIRECTORY, receivedFiles.get(0).getFileType());
        assertEquals(DIRECTORY, receivedFiles.get(1).getFileType());
        assertEquals(FILE, receivedFiles.get(2).getFileType());
        assertEquals(FILE, receivedFiles.get(3).getFileType());
    }

    @Test
    public void copyFiles_When_FilesExists_Expected_CopyFiles() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile2ByDir = pathDir.resolve(testFileName2);
        final Path targetDir = pathDir.resolve(testDirectoryName2);

        Files.createDirectories(pathDir);
        Files.createDirectory(targetDir);

        List<Path> paths = new ArrayList<>();
        paths.add(Files.createFile(pathFileByDir));
        paths.add(Files.createFile(pathFile2ByDir));

        assertEquals(3, Files.list(pathDir).count());
        assertEquals(0, Files.list(targetDir).count());
        fileService.copyFile(paths, targetDir);
        assertEquals(3, Files.list(pathDir).count());
        assertEquals(2, Files.list(targetDir).count());
    }

    @Test
    public void copyDirectory_When_DirectoryIsNotEmpty_Expected_CopyDirectory() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFile2ByDir = pathDir2ByDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);

        assertFalse(Files.exists(firstRootPath));

        Files.createDirectories(pathDir2ByDir);
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);
        Files.createFile(pathFile2ByDir);

        fileService.copyFile(Collections.singletonList(pathDir), pathDir3);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.exists(pathDir2ByDir));
        assertTrue(Files.exists(pathFileByDir));
        assertTrue(Files.exists(pathFile2ByDir));

        final Path newPathDir = pathDir3.resolve(pathDir.getFileName());
        assertTrue(Files.exists(newPathDir));
        assertTrue(Files.exists(newPathDir.resolve(pathFileByDir.getFileName())));
        assertTrue(Files.exists(newPathDir.resolve(pathDir2ByDir.getFileName())));
        assertTrue(Files.exists(newPathDir.resolve(pathFile2ByDir.getFileName())));
    }

    @Test
    public void copyFileWithOverwrite_When_TargetFileExist_Expected_FileOverwritten() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);
        final Path pathFileByDir3 = pathDir3.resolve(testFileName);

        assertFalse(Files.exists(firstRootPath));

        Files.createDirectories(pathDir2ByDir);
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);
        Files.createFile(pathFileByDir3);

        fileService.copyFile(Collections.singletonList(pathDir), pathDir3, true);

        assertTrue(Files.exists(pathDir));
        assertTrue(Files.exists(pathDir2ByDir));
        assertTrue(Files.exists(pathFileByDir));
        assertTrue(Files.exists(pathFileByDir3));
    }

    @Test
    public void copyFileWithRename_Expect_FileRenamed() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);

        String filename = "newFilename.jsp";

        final SourceFile sourceFile = fileService.copyFileWithRename(pathFileByDir, pathDir3, filename);

        assertTrue(Files.exists(Path.of(sourceFile.getFullPath())));
        assertTrue(Files.exists(pathFileByDir));
    }

    @Test
    public void moveDirectory_When_DirectoryNotEmpty_Expected_moveCorrectDirectory() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathDir2ByDir = pathDir.resolve(testDirectoryName2);
        final Path pathFile2ByDir = pathDir2ByDir.resolve(testFileName);
        final Path pathDir3 = firstRootPath.resolve(testDirectoryName3);

        assertFalse(Files.exists(firstRootPath));

        Files.createDirectories(pathDir2ByDir);
        Files.createDirectory(pathDir3);
        Files.createFile(pathFileByDir);
        Files.createFile(pathFile2ByDir);

        fileService.moveFile(Collections.singletonList(pathDir), pathDir3);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2ByDir));
        assertFalse(Files.exists(pathFileByDir));
        assertFalse(Files.exists(pathFile2ByDir));

        final Path newPathDir = pathDir3.resolve(pathDir.getFileName());
        assertTrue(Files.exists(newPathDir));
        assertTrue(Files.exists(newPathDir.resolve(pathFileByDir.getFileName())));
        assertTrue(Files.exists(newPathDir.resolve(pathDir2ByDir.getFileName())));
        assertTrue(Files.exists(newPathDir.resolve(pathFile2ByDir.getFileName())));
    }

    @Test
    public void moveDirectory_When_SelectedTwoDirectories_Expected_moveDirectories() throws IOException, FileOperationFailureException {

        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = pathDir.resolve(testDirectoryName2);
        final Path pathDirTarget = firstRootPath.resolve(testDirectoryName3);
        final Path pathFileByDir = pathDir.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createDirectory(pathDirTarget);
        Files.createFile(pathFileByDir);

        List<Path> testPaths = new ArrayList<>();
        testPaths.add(pathDir2);
        testPaths.add(pathDir);

        assertEquals(2, Files.list(firstRootPath).count());
        assertEquals(0, Files.list(pathDirTarget).count());

        fileService.moveFile(testPaths, pathDirTarget);

        assertFalse(Files.exists(pathDir));
        assertFalse(Files.exists(pathDir2));
        assertEquals(1, Files.list(firstRootPath).count());
        assertEquals(2, Files.list(pathDirTarget).count());
    }

    @Test
    public void moveFiles_When_FilesExist_Expected_moveFiles() throws IOException, FileOperationFailureException {
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

    @Test
    public void moveFiles_When_TargetFilesAlreadyExist_Expected_CorrectException() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final Path pathFile3ByDir = pathDir.resolve("bla" + testFileName2);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);


        List<Path> src = new ArrayList<>();
        src.add(Files.createFile(pathFileByDir));
        src.add(Files.createFile(pathFile3ByDir));

        assertEquals(0, Files.list(pathDir2).count());

        fileService.moveFile(src, pathDir2);
        assertEquals(2, Files.list(pathDir2).count());

        Files.createFile(pathFileByDir);
        Files.createFile(pathFile3ByDir);

        assertThrows(FileOperationFailureException.class, () -> fileService.moveFile(src, pathDir2));
        assertEquals(2, Files.list(pathDir2).count());
    }

    @Test
    public void moveFileWithRename_Expected_MovedFileWithNewFilename() throws IOException, FileOperationFailureException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final String newFilename = "newFilename.jsp";

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFileByDir);

        assertEquals(0, Files.list(pathDir2).count());

        fileService.moveFileWithRename(pathFileByDir, pathDir2, newFilename);
        assertEquals(1, Files.list(pathDir2).count());
    }

    @Test
    public void moveFileWithRename_When_TargetFileAlreadyExist_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);
        final String newFilename = "fileNameTest.jsp";

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);

        assertEquals(0, Files.list(pathDir2).count());

        Files.createFile(pathFileByDir);
        Files.createFile(pathDir2.resolve("fileNameTest.jsp"));

        assertThrows(FileOperationFailureException.class, () -> fileService.moveFileWithRename(pathFileByDir, pathDir2, newFilename));
        assertEquals(1, Files.list(pathDir2).count());
    }

    @Test
    public void moveFileWithRename_When_MoveDirectory_Expect_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final String newFilename = "newFilename";

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);

        assertThrows(FileOperationFailureException.class, () -> fileService.moveFileWithRename(pathDir, pathDir2, newFilename));
    }

    @Test
    public void renameFile_When_FileExistsAndNewNameIsNotEmpty_Expected_RenameFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);
        assertTrue(Files.exists(pathFile));

        final Path expectedPathFile = firstRootPath.resolve(newNameFile);
        fileService.renameFile(pathFile, newNameFile);
        assertFalse(Files.exists(pathFile));
        assertTrue(Files.exists(expectedPathFile));
    }

    @Test
    public void renameFile_When_FileDoesNotExist_Expected_CorrectException() {
        final Path firstRootPath = testRootPaths.get(0);
        final Path nonExistentPath = firstRootPath.resolve(testFileName);

        assertFalse(Files.exists(nonExistentPath));

        assertThrows(FileAccessDeniedException.class, () -> fileService.renameFile(nonExistentPath, newNameFile));
    }

    @Test
    public void renameFile_When_NewNameIsEmpty_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);
        assertTrue(Files.exists(pathFile));

        final String newName = "";
        assertThrows(EmptyFileNameException.class, () -> fileService.renameFile(pathFile, newName));
    }

    @Test
    public void renameFile_When_FileFromOutsideRootDir_Expected_CorrectException() {
        assertThrows(FileAccessDeniedException.class, () -> fileService.renameFile(outsideFilePath, newNameFile));
    }

    @Test
    public void renameFile_When_DirIsRootDir_And_DirFromOutsideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootDir = testRootPaths.get(0);
        Files.createDirectory(firstRootDir);
        assertThrows(FileAccessDeniedException.class, () -> fileService.renameFile(firstRootDir, newNameDirectory));
        assertThrows(FileAccessDeniedException.class, () -> fileService.renameFile(firstRootDir.getParent(), newNameDirectory));
    }

    @Test
    public void renameFile_When_FileWithNotAllowedExtension_Expected_CorrectException() throws IOException {
        final Path firstRootDir = testRootPaths.get(0);
        final Path notAllowedExtensionFilePath = firstRootDir.resolve(notAllowedExtensionFile);
        Files.createDirectory(firstRootDir);
        Files.createFile(notAllowedExtensionFilePath);
        assertThrows(FileAccessDeniedException.class, () -> fileService.renameFile(notAllowedExtensionFilePath, newNameFile));
    }

    @Test
    public void renameFile_When_FileInTemplateTextDir_And_TemplateIsUsedByDocuments_And_TemplateNameIsValid_Expected_RenameFileAndEntity() throws IOException {
        final Path templatePath = templateTextDirectory.resolve(testFileName);
        final String templateName = FilenameUtils.removeExtension(templatePath.getFileName().toString());
        Files.createFile(templatePath);
        assertTrue(Files.exists(templatePath));

        DocumentDTO document = documentDataInitializer.createData();
        templateDataInitializer.createData(document.getId(), templateName, templateName);
        assertNotNull(templateRepository.findByName(templateName));

        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(templatePath);
        assertEquals(1, documents.size());

        fileService.renameFile(templatePath, newNameFile);
        assertNull(templateRepository.findByName(testFileName));
        assertNotNull(templateRepository.findByName(FilenameUtils.removeExtension(newNameFile)));

        final Path expectedTemplatePath = templateTextDirectory.resolve(newNameFile);
        assertTrue(Files.exists(expectedTemplatePath));
        assertFalse(Files.exists(templatePath));

        documents = fileService.getDocumentsByTemplatePath(templatePath);
        assertTrue(documents.isEmpty());
        List<DocumentDTO> expectedDocuments = fileService.getDocumentsByTemplatePath(expectedTemplatePath);
        assertEquals(1, expectedDocuments.size());
    }

    @Test
    public void renameFile_When_FileInTemplateTextDir_And_TemplateIsUsedByDocuments_And_TemplateNameIsNotValid_Expected_CorrectException() throws IOException {
        final Path templatePath = templateTextDirectory.resolve(testFileName);
        final String templateName = FilenameUtils.removeExtension(templatePath.getFileName().toString());
        Files.createFile(templatePath);
        assertTrue(Files.exists(templatePath));

        DocumentDTO document = documentDataInitializer.createData();
        templateDataInitializer.createData(document.getId(), templateName, templateName);
        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(templatePath);
        assertEquals(1, documents.size());

        assertThrows(TemplateFileException.class, () -> fileService.renameFile(templatePath, notValidNewTemplateName));

        assertTrue(Files.exists(templatePath));
        documents = fileService.getDocumentsByTemplatePath(templatePath);
        assertEquals(1, documents.size());
    }

    @Test
    public void renameFile_When_FileInTemplateTextDir_And_TemplateIsNotUsedDocuments_And_TemplateNameIsNotValid_Expected_RenameFile_And_DeleteTemplateEntity() throws IOException {
        final Path templatePath = templateTextDirectory.resolve(testFileName);
        Files.createFile(templatePath);
        assertTrue(Files.exists(templatePath));

        final String templateName = FilenameUtils.removeExtension(templatePath.getFileName().toString());
        templateRepository.save(new TemplateJPA(null, templateName, true));
        templateRepository.save(new TemplateJPA(null, templateName + "2", true));     //Prevent a single template deletion exception

        List<DocumentDTO> documents = fileService.getDocumentsByTemplatePath(templatePath);
        assertTrue(documents.isEmpty());

        final Path expectedFilePath = templateTextDirectory.resolve(notValidNewTemplateName);
        fileService.renameFile(templatePath, notValidNewTemplateName);
        assertTrue(Files.exists(expectedFilePath));
        assertFalse(Files.exists(templatePath));

        assertNull(templateRepository.findByName(templateName));
    }

    @Test
    public void renameFile_When_FileIsTemplateTextDir_Expected_CorrectException() {
        assertTrue(Files.exists(templateTextDirectory));
        assertThrows(TemplateFileException.class, () -> fileService.renameFile(templateTextDirectory, "newDirectoryName"));
    }

    @Test
    public void defaultRenameFile_Expected_RenameFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFile);
        assertTrue(Files.exists(pathFile));

        final SourceFile sourceFile = fileService.defaultRename(pathFile);

        assertTrue(Files.notExists(pathFile));
        assertTrue(Files.exists(Path.of(sourceFile.getFullPath())));
    }

    @Test
    public void defaultRename_When_FileDoesNotExist_And_FileIsTemplate_Expect_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFile = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        assertTrue(Files.notExists(pathFile));

        assertThrows(FileAccessDeniedException.class, () -> fileService.defaultRename(pathFile));
    }

    @Test
    public void existsFile_When_FileExist_Expected_True() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFileByDir);

        assertTrue(fileService.exists(firstRootPath));
        assertTrue(fileService.exists(pathDir));
        assertTrue(fileService.exists(pathDir2));
        assertTrue(fileService.exists(pathFileByDir));
    }

    @Test
    public void existsFile_When_FileNotExist_Expected_False(){
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);

        assertFalse(fileService.exists(firstRootPath));
        assertFalse(fileService.exists(pathDir));
        assertFalse(fileService.exists(pathDir2));
        assertFalse(fileService.exists(pathFileByDir));
    }

    @Test
    public void existsAllFiles_When_FilesExist_Expected_ListWithExistingFiles() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createDirectory(pathDir2);
        Files.createFile(pathFileByDir);

        final List<String> paths = Stream.of(firstRootPath, pathDir, pathDir2, pathFileByDir).map(path ->path.toAbsolutePath().toString()).toList();
        final List<SourceFile> sourceFiles = fileService.existsAll(paths);

        assertFalse(sourceFiles.isEmpty());
        assertEquals(4, sourceFiles.size());
    }

    @Test
    public void existsAllFiles_When_FilesNotExist_Expected_EmptyList(){
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathDir2 = firstRootPath.resolve(testDirectoryName2);
        final Path pathFileByDir = pathDir.resolve(testFileName);

        final List<String> paths = Stream.of(firstRootPath, pathDir, pathDir2, pathFileByDir).map(path ->path.toAbsolutePath().toString()).toList();

        assertTrue(fileService.existsAll(paths).isEmpty());

    }

}
