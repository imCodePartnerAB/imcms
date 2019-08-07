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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
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

    @BeforeEach
    @AfterEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
        testRootPaths.stream().map(Path::toFile).forEach(FileUtils::deleteRecursive);
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
    public void getDocumentsByTemplateName_When_TemplateToOutSideRootDir_Expected_CorrectException() {
        final Path pathOutSide = Paths.get(testTemplateName);
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
                new SourceFile(firstRootDir.getFileName().toString(), firstRootDir.toString(), DIRECTORY, null),
                new SourceFile(secondRootDir.getFileName().toString(), secondRootDir.toString(), DIRECTORY, null)
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

        final List<SourceFile> files = Collections.singletonList(
                new SourceFile(pathFile.getFileName().toString(), pathFile.toString(), FILE, Collections.EMPTY_LIST)
        );

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
                new SourceFile(pathDir2.getFileName().toString(), pathDir2.toString(), DIRECTORY, null),
                new SourceFile(pathFile.getFileName().toString(), pathFile.toString(), FILE, Collections.EMPTY_LIST)
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
        final SourceFile newFile = new SourceFile(
                pathNewFile.getFileName().toString(), pathNewFile.toString(), FILE, Collections.EMPTY_LIST
        );

        assertThrows(EmptyFileNameException.class, () -> fileService.createFile(newFile, false));

    }

    @Test
    public void createFile_WhenFileNotExist_Expected_CreatedFile() throws IOException {
        final Path firstRootPath = testRootPaths.get(0);
        final Path pathDir = firstRootPath.resolve(testDirectoryName);

        Files.createDirectories(pathDir);

        final Path pathNewFile = pathDir.resolve(testFileName);
        final SourceFile newFile = new SourceFile(pathNewFile.getFileName().toString(), pathNewFile.toString(), FILE, Collections.EMPTY_LIST);

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

        final SourceFile newFile = new SourceFile(pathFile.getFileName().toString(), pathFile.toString(), FILE, Collections.EMPTY_LIST);
        final SourceFile newDir = new SourceFile(pathDir.getFileName().toString(), pathDir.toString(), DIRECTORY, null);

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
    public void renameTemplateFile_When_TemplateUseDocuments_Expected_Rename() throws IOException {
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
        assertTrue(documentTemplateService.getByTemplateName(namePathTest).isEmpty());
        assertFalse(docsByTemplNameTarget.isEmpty());
        assertEquals(1, docsByTemplNameTarget.size());
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
}
