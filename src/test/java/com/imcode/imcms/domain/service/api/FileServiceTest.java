package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.SourceFile;
import com.imcode.imcms.api.exception.FileAccessDeniedException;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.exception.EmptyFileNameException;
import com.imcode.imcms.domain.service.FileService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class FileServiceTest extends WebAppSpringTestConfig {

    private final String testFileName = "fileName.jsp";
    private final String testFileName2 = "fileName2.txt";
    private final String testTemplateName = "templateTest";
    private final String testDirectoryName = "dirName";
    private final String testDirectoryName2 = testDirectoryName + "two";
    private final String testDirectoryName3 = testDirectoryName + "three";

    @Autowired
    private FileService fileService;

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private TextDocumentTemplateService documentTemplateService;

    @Autowired
    private TemplateRepository templateRepository;

    @Value("#{'${FileAdminRootPaths}'.split(';')}")
    private List<Path> testRootPaths;

    @Value("WEB-INF/templates/text")
    private Path templateDirectory;

    @Value("${rootPath}")
    private Path rootPath;

    @BeforeEach
    @AfterEach
    public void setUp() throws IOException {
        templateDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
        testRootPaths.stream().map(Path::toFile).forEach(FileUtils::deleteRecursive);
        Files.deleteIfExists(templateDirectory.resolve(testFileName));
    }

    private SourceFile toSourceFile(Path filePath, SourceFile.FileType fileType) {
        return new SourceFile(
                filePath.getFileName().toString(),
                getRelatedPath(filePath),
                filePath.toString(),
                fileType,
                fileType == DIRECTORY ? null : Collections.EMPTY_LIST
        );
    }

    private SourceFile toSourceFile(Path filePath) {
        final SourceFile.FileType fileType = filePath.toFile().isDirectory() ? DIRECTORY : FILE;
        return toSourceFile(filePath, fileType);
    }

    private String getRelatedPath(Path path) {
        return path.toAbsolutePath().toString().substring(rootPath.toString().length());
    }

    @Test
    public void getDocumentsByTemplateName_When_TemplateHasDocuments_Expected_CorrectSize() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path template = firstRootPath.resolve(testTemplateName);
        final String templateName = template.getFileName().toString();
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
        assertEquals(0, documents.size());
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
    public void getDocumentsByTemplateName_When_TemplateNotFileButNameNotExists_Expected_CorrectException() {
        final String testTemplateName = "fakeTest";
        assertThrows(FileAccessDeniedException.class, () -> fileService.getDocumentsByTemplatePath(Paths.get(testTemplateName)));
    }

    @Test
    public void getDocumentsByTemplateName_When_TemplateToOutSideRootDir_Expected_CorrectException() {
        final Path pathOutSide = testRootPaths.get(0).resolve(testTemplateName);
        final String templateName = pathOutSide.getFileName().toString();
        DocumentDTO document = documentDataInitializer.createData();
        templateDataInitializer.createData(document.getId(), templateName, templateName);

        assertThrows(FileAccessDeniedException.class, () -> fileService.getDocumentsByTemplatePath(pathOutSide));
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
    public void getRootPaths_When_PathsCorrect_Expected_CorrectSourceFiles() throws IOException {
        final Path firstRootDir = testRootPaths.get(0);
        final Path secondRootDir = testRootPaths.get(1);
        Files.createDirectory(firstRootDir);
        Files.createDirectory(secondRootDir);
        final List<SourceFile> files = Arrays.asList(
                toSourceFile(firstRootDir),
                toSourceFile(secondRootDir)
        );

        assertEquals(files.size(), fileService.getRootFiles().size());
        assertEquals(files, fileService.getRootFiles());
    }

    @Test
    public void getFiles_When_FilesInDirectoryExist_Expected_CorrectFiles() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        final List<SourceFile> files = Collections.singletonList(toSourceFile(pathFile));

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
                toSourceFile(pathDir2),
                toSourceFile(pathFile)
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
    public void createFile_WhenFileNameEmpty_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir);

        final Path pathNewFile = pathDir.resolve(" ");
        final SourceFile newFile = toSourceFile(pathNewFile, FILE);

        assertThrows(EmptyFileNameException.class, () -> fileService.createFile(newFile, false));

    }

    @Test
    public void createFile_WhenFileNotExist_Expected_CreatedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir);

        final Path pathNewFile = pathDir.resolve(testFileName);
        final SourceFile newFile = toSourceFile(pathNewFile, FILE);

        final SourceFile createdFile = fileService.createFile(newFile, false);

        assertTrue(Files.exists(pathNewFile));
        assertEquals(newFile, createdFile);
    }


    @Test
    public void createFile_When_FileCreateToOutSideRootDir_Expected_CorrectException() throws IOException {
        Files.createDirectory(testRootPaths.get(0));
        final Path pathFile = Paths.get(testFileName);
        final Path pathDir = Paths.get(testFileName);

        assertFalse(Files.exists(pathFile));

        final SourceFile newFile = toSourceFile(pathFile);
        final SourceFile newDir = toSourceFile(pathDir);

        assertThrows(FileAccessDeniedException.class, () -> fileService.createFile(newFile, false));
        assertThrows(FileAccessDeniedException.class, () -> fileService.createFile(newDir, true));

        assertFalse(Files.exists(pathFile));
        assertFalse(Files.exists(pathDir));
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
        final SourceFile saved = fileService.saveFile(pathFile, Collections.singletonList(testText), null);

        assertNotNull(saved);
        assertTrue(Files.exists(Paths.get(saved.getFullPath())));
        List<String> line = saved.getContents();
        assertEquals(1, line.size());
        String savedContent = line.get(0);
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
                pathFile2, Collections.singletonList(testText), StandardOpenOption.CREATE_NEW));
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
    public void getFile_When_PathFileFromToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path outDirRoot = Paths.get(firstRootPath.getParent().toString());

        Files.createDirectory(firstRootPath);

        assertThrows(FileAccessDeniedException.class, () -> fileService.getFile(outDirRoot));
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
    public void copyFile_When_SrcFileExists_Expected_CopyFile() throws IOException {
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
    public void copyFiles_When_FilesExist_Expected_CopyFiles() throws IOException {
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
    public void copyFile_When_FileCopyToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFakeFile2 = Paths.get("test");
        assertFalse(Files.exists(pathDir));

        Files.createDirectory(firstRootPath);
        Files.createDirectory(pathDir);
        Files.createFile(pathFile);

        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(
                Collections.singletonList(pathFile), pathFakeFile2));
        assertThrows(FileAccessDeniedException.class, () -> fileService.copyFile(Collections.singletonList(
                pathFakeFile2), pathFile));
    }

    @Test
    public void moveFiles_When_FilesExist_Expected_MoveFiles() throws IOException {
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

        assertEquals(3, fileService.getFiles(firstRootPath).size());
        assertEquals(0, fileService.getFiles(pathDir).size());

        fileService.moveFile(paths, pathDir);

        assertFalse(Files.exists(pathFileByDir));
        assertEquals(2, fileService.getFiles(firstRootPath).size());
        assertEquals(1, fileService.getFiles(pathDir).size());
    }

    @Test
    public void moveFile_When_FileExist_Expected_RenameFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFileByDir = firstRootPath.resolve(testFileName);
        final Path pathFile2ByDir = pathDir.resolve(testFileName2);

        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createFile(pathFileByDir);


        assertEquals(2, fileService.getFiles(firstRootPath).size());
        assertEquals(0, fileService.getFiles(pathDir).size());

        SourceFile moved = fileService.moveFile(pathFileByDir, pathFile2ByDir);

        assertFalse(Files.exists(pathFileByDir));
        assertEquals(1, fileService.getFiles(firstRootPath).size());
        assertEquals(1, fileService.getFiles(pathDir).size());
        assertNotEquals(pathFileByDir.getFileName(), moved.getFileName());

    }

    @Test
    public void moveFile_When_FilesMoveToOutSideRootDir_Expected_CorrectException() throws IOException {
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

        assertThrows(FileAccessDeniedException.class,
                () -> fileService.moveFile(Collections.singletonList(pathFakeFile2), pathFile));

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.exists(pathFakeFile2));
    }

    @Test
    public void moveFile_When_FileMoveToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFakeDir = Paths.get("outSideDir");
        final Path pathFakeFile2 = pathFakeDir.resolve(testRootPaths.get(0).getFileName());

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(pathFile, pathFakeFile2));

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.exists(pathFakeFile2));

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(pathFakeFile2, pathFile));

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.exists(pathFakeFile2));
    }

    @Test
    public void moveFile_When_FileRenameToOutSideRootDir_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);
        final Path pathFile = pathDir.resolve(testFileName);
        final Path pathFakeDir = Paths.get("outSideDir");
        final Path pathFakeFile2 = pathFakeDir.resolve(testRootPaths.get(0).getFileName());
        assertFalse(Files.exists(pathDir));

        Files.createDirectories(pathDir);
        Files.createFile(pathFile);

        assertThrows(FileAccessDeniedException.class, () -> fileService.moveFile(pathFile, pathFakeFile2));

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.exists(pathFakeFile2));

        assertThrows(FileAccessDeniedException.class,
                () -> fileService.moveFile(pathFakeFile2, pathFile));

        assertTrue(Files.exists(pathFile));
        assertFalse(Files.exists(pathFakeFile2));
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
    public void copyFiles_When_FilesExists_Expected_CopyFiles() throws IOException {
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
    public void moveDirectory_When_DirectoryNotEmpty_Expected_moveCorrectDirectory() throws IOException {

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
    public void moveDirectory_When_SelectedTwoDirectories_Expected_moveDirectories() throws IOException {

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

    @Test
    public void renameFile_When_FileNameEmpty_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathTest = firstRootPath.resolve(testDirectoryName);
        final Path pathTarget = firstRootPath.resolve(" ");

        Files.createDirectories(pathTest);

        assertThrows(EmptyFileNameException.class, () -> fileService.moveFile(pathTest, pathTarget));

        assertFalse(Files.exists(pathTarget));

    }

    @Test
    public void renameTemplateFile_When_TemplateUseDocumentsButLocatedNotDirTemplate_Expected_RenameFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathTest = firstRootPath.resolve(testDirectoryName);
        final Path pathTarget = firstRootPath.resolve(testDirectoryName2);
        final String namePathTest = pathTest.getFileName().toString();
        final String namePathTarget = pathTarget.getFileName().toString();
        final DocumentDTO document = documentDataInitializer.createData();
        final Template template = templateDataInitializer.createData(namePathTest);
        templateDataInitializer.createData(document.getId(), template.getName(), template.getName());

        Files.createDirectories(pathTest);

        fileService.moveFile(pathTest, pathTarget);

        final List<TextDocumentTemplateDTO> docsByTemplNameTarget = documentTemplateService.getByTemplateName(namePathTarget);
        final List<TextDocumentTemplateDTO> docsByTemplateSrc = documentTemplateService.getByTemplateName(namePathTest);
        assertFalse(documentTemplateService.getByTemplateName(namePathTest).isEmpty());
        assertTrue(docsByTemplNameTarget.isEmpty());
        assertEquals(1, docsByTemplateSrc.size());
        assertFalse(Files.exists(pathTest));
    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileNotExistsInGroup_Expected_ChangeTemplateGroup() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFileByRoot = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFileByRoot);

        final String originalFileName = FilenameUtils.removeExtension(pathFileByRoot.getFileName().toString());

        Template template = templateDataInitializer.createData(originalFileName);
        TemplateGroup testGroup = templateDataInitializer.createData("testGroup", 2, false);

        assertNull(template.getTemplateGroup());

        Template savedTemplate = fileService.saveTemplateInGroup(pathFileByRoot, testGroup.getName());

        assertNotNull(savedTemplate.getTemplateGroup());
        assertEquals(testGroup.getName(), savedTemplate.getTemplateGroup().getName());

    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileExistsInGroup_Expected_ChangeTemplateGroup() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathFileByRoot = firstRootPath.resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathFileByRoot);

        final String originalFileName = FilenameUtils.removeExtension(pathFileByRoot.getFileName().toString());

        TemplateJPA template = new TemplateJPA(templateDataInitializer.createData(originalFileName));
        List<TemplateGroup> testGroups = templateDataInitializer.createTemplateGroups(2);
        TemplateGroup testTemplateGroup = testGroups.get(0);
        template.setTemplateGroup(testTemplateGroup);
        TemplateJPA saved = templateRepository.save(template);

        assertNotNull(saved.getTemplateGroup());
        assertEquals(testTemplateGroup.getName(), saved.getTemplateGroup().getName());

        final TemplateGroup expectedTemplateGroup = testGroups.get(1);
        final Template changedTemplate = fileService.saveTemplateInGroup(pathFileByRoot, expectedTemplateGroup.getName());

        assertNotNull(changedTemplate.getTemplateGroup());
        assertNotEquals(testTemplateGroup.getName(), changedTemplate.getTemplateGroup().getName());
    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileNameEmpty_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path emptyFileName = firstRootPath.resolve(" ");
        Files.createDirectory(firstRootPath);
        final TemplateGroup testGroup = templateDataInitializer.createData(
                "testGroup", 2, false);

        assertThrows(EmptyFileNameException.class, () -> fileService.saveTemplateInGroup(emptyFileName, testGroup.getName()));
        assertEquals(2, testGroup.getTemplates().size());
    }

    @Test
    public void saveTemplateFileInGroup_When_templateFileInOutSideRoot_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathTemplateFile = firstRootPath.getParent().resolve(testFileName);

        Files.createDirectory(firstRootPath);
        Files.createFile(pathTemplateFile);
        final TemplateGroup testGroup = templateDataInitializer.createData(
                "testGroup", 2, false);

        assertThrows(FileAccessDeniedException.class, () -> fileService.saveTemplateInGroup(pathTemplateFile, testGroup.getName()));
        Files.delete(pathTemplateFile);
    }

    @Test
    public void replaceTemplate_When_oldTemplateFileNameExists_Expected_ReplacedTemplateAndPathExist() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path oldTemplatePath = firstRootPath.resolve(testTemplateName);
        final Path newTemplatePath = firstRootPath.resolve(testTemplateName + "2");
        final String oldTemplateName = oldTemplatePath.getFileName().toString();
        final String replaceTemplateName = newTemplatePath.getFileName().toString();
        final DocumentDTO document = documentDataInitializer.createData();
        Files.createDirectory(firstRootPath);
        Files.createFile(oldTemplatePath);
        Files.createFile(newTemplatePath);
        templateDataInitializer.createData(oldTemplateName);
        templateDataInitializer.createData(replaceTemplateName);
        templateDataInitializer.createData(document.getId(), oldTemplateName, oldTemplateName);

        assertEquals(1, fileService.getDocumentsByTemplatePath(oldTemplatePath).size());
        assertEquals(0, fileService.getDocumentsByTemplatePath(newTemplatePath).size());

        fileService.replaceDocsOnNewTemplate(oldTemplatePath, newTemplatePath);
        assertEquals(1, fileService.getDocumentsByTemplatePath(newTemplatePath).size());
        assertNotNull(templateRepository.findByName(oldTemplateName));
        assertTrue(Files.exists(oldTemplatePath));
    }

    @Test
    public void replaceTemplate_When_oldTemplateNotFileButNameExists_Expected_ReplacedTemplate() throws IOException {
        final Template oldTemplate = templateDataInitializer.createData(testTemplateName);
        final Template newTemplate = templateDataInitializer.createData(testTemplateName + "2");
        final DocumentDTO document = documentDataInitializer.createData();
        templateDataInitializer.createData(document.getId(), oldTemplate.getName(), oldTemplate.getName());
        final Path oldTemplatePath = Paths.get(oldTemplate.getName());
        final Path newTemplatePath = Paths.get(newTemplate.getName());
        assertEquals(1, fileService.getDocumentsByTemplatePath(Paths.get(oldTemplate.getName())).size());
        assertEquals(0, fileService.getDocumentsByTemplatePath(Paths.get(newTemplate.getName())).size());

        fileService.replaceDocsOnNewTemplate(oldTemplatePath, newTemplatePath);
        assertEquals(1, fileService.getDocumentsByTemplatePath(newTemplatePath).size());
        assertNotNull(templateRepository.findByName(oldTemplate.getName()));
    }

    @Test
    public void replaceTemplate_When_oldTemplateFileInOutSideRoot_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path oldTemplatePath = firstRootPath.getParent().resolve(testTemplateName);
        final Path newTemplatePath = firstRootPath.resolve(testTemplateName + "2");
        final String oldTemplateName = oldTemplatePath.getFileName().toString();
        final DocumentDTO document = documentDataInitializer.createData();
        Files.createDirectory(firstRootPath);
        Files.createFile(oldTemplatePath);
        Files.createFile(newTemplatePath);
        templateDataInitializer.createData(document.getId(), oldTemplateName, oldTemplateName);

        assertThrows(FileAccessDeniedException.class, () -> fileService.replaceDocsOnNewTemplate(oldTemplatePath, newTemplatePath));
        Files.delete(oldTemplatePath);
    }

    @Test
    public void replaceTemplate_When_newTemplateFileInOutSideRoot_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path oldTemplatePath = firstRootPath.resolve(testTemplateName);
        final Path newTemplatePath = firstRootPath.getParent().resolve(testTemplateName + "2");
        final String replaceTemplateName = newTemplatePath.getFileName().toString();
        final DocumentDTO document = documentDataInitializer.createData();
        Files.createDirectory(firstRootPath);
        Files.createFile(oldTemplatePath);
        Files.createFile(newTemplatePath);
        templateDataInitializer.createData(document.getId(), replaceTemplateName, replaceTemplateName);

        assertThrows(FileAccessDeniedException.class, () -> fileService.replaceDocsOnNewTemplate(oldTemplatePath, newTemplatePath));
        Files.delete(newTemplatePath);
    }

    @Test
    public void replaceTemplate_When_oldTemplateFileButNameNotExists_Expected_CorrectException() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path oldTemplatePath = firstRootPath.resolve("fakeTest");
        final Path newTemplatePath = firstRootPath.resolve(testTemplateName);
        final String replaceTemplateName = newTemplatePath.getFileName().toString();
        final DocumentDTO document = documentDataInitializer.createData();

        Files.createDirectory(firstRootPath);
        Files.createFile(newTemplatePath);
        templateDataInitializer.createData("testTemplateName");
        templateDataInitializer.createData(replaceTemplateName);
        templateDataInitializer.createData(document.getId(), replaceTemplateName, replaceTemplateName);

        assertThrows(EmptyResultDataAccessException.class, () -> fileService.replaceDocsOnNewTemplate(oldTemplatePath, newTemplatePath));
    }

    @Test
    public void replaceTemplate_When_newTemplateFileButNameNotExists_Expected_Exception() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path oldTemplatePath = firstRootPath.resolve("fakeTest");
        final Path newTemplatePath = firstRootPath.resolve(testTemplateName);
        final String oldTemplateName = oldTemplatePath.getFileName().toString();
        final DocumentDTO document = documentDataInitializer.createData();

        Files.createDirectory(firstRootPath);
        Files.createFile(oldTemplatePath);
        templateDataInitializer.createData("testTemplateName");
        templateDataInitializer.createData(document.getId(), oldTemplateName, oldTemplateName);

        assertEquals(1, fileService.getDocumentsByTemplatePath(oldTemplatePath).size());

        assertThrows(EmptyResultDataAccessException.class, () -> fileService.replaceDocsOnNewTemplate(oldTemplatePath, newTemplatePath));

        assertEquals(1, fileService.getDocumentsByTemplatePath(oldTemplatePath).size());
    }

}
