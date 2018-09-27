package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.exception.DirectoryNotEmptyException;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.ImcmsConstants;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.io.File.separator;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ImageFolderServiceTest {

    @Autowired
    private ImageFolderService imageFolderService;
    @Autowired
    private CommonContentService commonContentService;
    @Autowired
    private VersionService versionService;

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;


    @Value("${ImagePath}")
    private File imagesPath;

    @Before
    public void prepareData() {
        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @After
    public void clearTestData() {
        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @Test
    public void getImageFolder_Expected_RootFolderIsReturnedWithImages() {
        final ImageFolderDTO imageFolder = imageFolderService.getImageFolder();

        assertNotNull(imageFolder);
        assertThat(imageFolder.getName(), is("images"));
        assertThat(imageFolder.getPath(), is(""));
    }

    @Test
    public void getImageFolder_Expected_RootFolderWithOutGeneratedFolder() {
        final ImageFolderDTO imageFolder = imageFolderService.getImageFolder();

        assertNotNull(imageFolder);
        assertThat(imageFolder.getName(), is("images"));

        final List<String> imageSubFoldersNames = imageFolder.getFolders().stream()
                .map(ImageFolderDTO::getName)
                .collect(Collectors.toList());

        assertFalse(imageSubFoldersNames.contains(ImcmsConstants.IMAGE_GENERATED_FOLDER));
    }

    @Test
    public void createNewImageFolder_When_FolderNotExistBefore_Expect_FolderCreatedAndIsDirectoryAndReadableAndThenRemoved() throws IOException {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());
        assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
        assertTrue(newFolder.exists());
        assertTrue(newFolder.isDirectory());
        assertTrue(newFolder.canRead());
        assertTrue(FileUtility.forceDelete(newFolder));
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() throws IOException {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());
        assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
        assertTrue(newFolder.exists());

        try {
            imageFolderService.createImageFolder(imageFolderDTO); // exception expected here
            fail("Expected exception wasn't thrown!");

        } catch (FolderAlreadyExistException e) {
            assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() throws IOException {
        final String newFolderName0 = "new_test_folder";
        final String newFolderPath0 = separator + newFolderName0;

        final String newFolderName1 = "nested1";
        final String newFolderPath1 = newFolderPath0 + separator + newFolderName1;

        final String newFolderName2 = "nested2";
        final String newFolderPath2 = newFolderPath1 + separator + newFolderName2;

        final String newFolderName3 = "nested3";
        final String newFolderPath3 = newFolderPath1 + separator + newFolderName3;

        final File newFolder0 = new File(imagesPath, newFolderPath0);
        final File newFolder1 = new File(imagesPath, newFolderPath1);
        final File newFolder2 = new File(imagesPath, newFolderPath2);
        final File newFolder3 = new File(imagesPath, newFolderPath3);

        final ImageFolderDTO imageFolderDTO0 = new ImageFolderDTO(newFolderName0, newFolderPath0);
        final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1, newFolderPath1);
        final ImageFolderDTO imageFolderDTO2 = new ImageFolderDTO(newFolderName2, newFolderPath2);
        final ImageFolderDTO imageFolderDTO3 = new ImageFolderDTO(newFolderName3, newFolderPath3);

        assertFalse(newFolder0.exists());
        assertFalse(newFolder1.exists());
        assertFalse(newFolder2.exists());
        assertFalse(newFolder3.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO0));
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO1));
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO2));
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO3));

            assertTrue(newFolder0.exists());
            assertTrue(newFolder1.exists());
            assertTrue(newFolder2.exists());
            assertTrue(newFolder3.exists());

            assertTrue(newFolder0.isDirectory());
            assertTrue(newFolder1.isDirectory());
            assertTrue(newFolder2.isDirectory());
            assertTrue(newFolder3.isDirectory());

            assertTrue(newFolder0.canRead());
            assertTrue(newFolder1.canRead());
            assertTrue(newFolder2.canRead());
            assertTrue(newFolder3.canRead());

            assertTrue(FileUtility.forceDelete(newFolder3));
            assertTrue(FileUtility.forceDelete(newFolder2));
            assertTrue(FileUtility.forceDelete(newFolder1));
            assertTrue(FileUtility.forceDelete(newFolder0));

        } finally {
            if (newFolder3.exists()) assertTrue(FileUtility.forceDelete(newFolder3));
            if (newFolder2.exists()) assertTrue(FileUtility.forceDelete(newFolder2));
            if (newFolder1.exists()) assertTrue(FileUtility.forceDelete(newFolder1));
            if (newFolder0.exists()) assertTrue(FileUtility.forceDelete(newFolder0));
        }
    }

    @Test
    public void renameFolder_When_FolderExistsInRootImagesDirectory_Expect_True() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());

            final String folderNewName = "new_name";
            final File renamedFolder = new File(imagesPath, folderNewName);
            imageFolderDTO.setName(folderNewName);

            assertFalse(renamedFolder.exists());
            assertTrue(imageFolderService.renameFolder(imageFolderDTO));
            assertTrue(renamedFolder.exists());
            assertFalse(newFolder.exists());
            assertTrue(FileUtility.forceDelete(renamedFolder));

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    @Test(expected = FolderNotExistException.class)
    public void renameFolder_When_FolderNotExist_Expect_CorrectException() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        final String folderNewName = "new_name";
        final File renamedFolder = new File(imagesPath, folderNewName);

        assertFalse(renamedFolder.exists());

        try {
            imageFolderDTO.setName(folderNewName);

            assertFalse(renamedFolder.exists());
            imageFolderService.renameFolder(imageFolderDTO); // exception expected here!

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
            if (renamedFolder.exists()) assertTrue(FileUtility.forceDelete(renamedFolder));
        }
    }

    @Test
    public void renameFolder_When_FolderExistsNestedInRootImagesDirectory_Expect_True() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        final String newNestedFolderName = "nested_folder";
        final String path = newFolderName + separator + newNestedFolderName;
        final File newNestedFolder = new File(imagesPath, path);
        final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(newNestedFolderName, separator + path);

        assertFalse(newNestedFolder.exists());

        final String nestedFolderNewName = "new_name";
        final File renamedFolder = new File(imagesPath, newFolderName + separator + nestedFolderNewName);

        assertFalse(renamedFolder.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());

            assertTrue(imageFolderService.createImageFolder(imageNestedFolderDTO));
            assertTrue(newNestedFolder.exists());

            imageNestedFolderDTO.setName(nestedFolderNewName);

            assertFalse(renamedFolder.exists());
            assertTrue(imageFolderService.renameFolder(imageNestedFolderDTO));
            assertTrue(renamedFolder.exists());
            assertFalse(newNestedFolder.exists());
            assertTrue(FileUtility.forceDelete(renamedFolder));

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
            if (newNestedFolder.exists()) assertTrue(FileUtility.forceDelete(newNestedFolder));
            if (renamedFolder.exists()) assertTrue(FileUtility.forceDelete(renamedFolder));
        }
    }

    @Test(expected = FolderAlreadyExistException.class)
    public void renameFolder_When_FolderWithSuchNameExist_Expect_CorrectException() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        final String newFolderName1 = "new_test_folder1";
        final File newFolder1 = new File(imagesPath, newFolderName1);
        final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1);

        assertFalse(newFolder1.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());

            assertTrue(imageFolderService.createImageFolder(imageFolderDTO1));
            assertTrue(newFolder1.exists());
            assertTrue(newFolder1.isDirectory());
            assertTrue(newFolder1.canRead());

            final String folderNewName = newFolderName;
            final File renamedFolder = new File(imagesPath, folderNewName);
            imageFolderDTO1.setName(folderNewName);

            assertTrue(renamedFolder.exists());
            imageFolderService.renameFolder(imageFolderDTO1); // exception expected here!

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
            if (newFolder1.exists()) assertTrue(FileUtility.forceDelete(newFolder1));
        }
    }

    @Test
    public void deleteFolder_When_FolderExist_Expect_TrueAndFolderDeleted() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try {
            assertFalse(newFolder.exists());
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());

            assertTrue(imageFolderService.deleteFolder(imageFolderDTO));
            assertFalse(newFolder.exists());

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    @Test(expected = FolderNotExistException.class)
    public void deleteFolder_When_FolderNotExist_Expect_CorrectException() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try {
            assertFalse(newFolder.exists());
            imageFolderService.deleteFolder(imageFolderDTO); // exception expected here!

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    @Test
    public void deleteFolderWithEmptySubdirectory_ExpectTrue() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);

        final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
        final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());

        try {
            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isRemoved = imageFolderService.deleteFolder(testImageSubFolderDTO);

            assertTrue(isRemoved);
            assertFalse(testSubdirectory.exists());
        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void deleteDirectoryWithFiles_Expect_FalseAndDirectoryNotEmptyException() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.jpg";

        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testImage = new File(testDirectory, testImageName);

        final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());

        try {
            imageFolderService.createImageFolder(testImageFolderDTO);

            assertTrue(testDirectory.exists());

            final boolean isTestFileCreated = testImage.createNewFile();
            assertTrue(isTestFileCreated);

            final boolean isRemoved = imageFolderService.deleteFolder(testImageFolderDTO);

            assertFalse(isRemoved);
            assertTrue(testDirectory.exists());
        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test
    public void deleteSubdirectoryWithEmptySubdirectoryTree_Expect_True() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
        final File testSubdirectoryLevel2 = new File(testSubdirectory, testSubdirectoryName);

        final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
        final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());
        final ImageFolderDTO testImageSubFolderDTOLevel2 = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName() + File.separator + testSubdirectoryLevel2.getName());

        try {
            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTOLevel2);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());
            assertTrue(testSubdirectoryLevel2.exists());


            final boolean isRemoved = imageFolderService.deleteFolder(testImageSubFolderDTO);

            assertTrue(isRemoved);
            assertFalse(testSubdirectoryLevel2.exists());
            assertFalse(testSubdirectory.exists());
        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void deleteSubdirectoryWithSubdirectoryWithFiles_Expect_FalseAndDirectoryNotEmptyException() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
        final File testImage1 = new File(testDirectory, testImageName);
        final File testImage2 = new File(testSubdirectory, testImageName);

        final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
        final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());

        try {
            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isTestFile1Created = testImage1.createNewFile();
            final boolean isTestFile2Created = testImage2.createNewFile();

            assertTrue(isTestFile1Created);
            assertTrue(isTestFile2Created);

            final boolean isRemoved = imageFolderService.deleteFolder(testImageFolderDTO);

            assertFalse(isRemoved);
            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());
        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test
    public void canDeleteSubdirectoryWithEmptySubdirectoryTree_Expect_True() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
        final File testSubdirectoryLevel2 = new File(testSubdirectory, testSubdirectoryName);

        final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
        final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());
        final ImageFolderDTO testImageSubFolderDTOLevel2 = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName() + File.separator + testSubdirectoryLevel2.getName());

        try {
            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTOLevel2);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());
            assertTrue(testSubdirectoryLevel2.exists());

            assertTrue(imageFolderService.canBeDeleted(testImageFolderDTO));
            assertTrue(imageFolderService.canBeDeleted(testImageSubFolderDTO));

        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void canDeleteSubdirectoryWithSubdirectoryWithFile_Expect_FalseAndDirectoryNotEmptyException() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
        final File testImage2 = new File(testSubdirectory, testImageName);

        final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
        final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());

        try {
            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isTestFile2Created = testImage2.createNewFile();

            assertTrue(isTestFile2Created);

            assertFalse(imageFolderService.canBeDeleted(testImageFolderDTO));
            assertFalse(imageFolderService.canBeDeleted(testImageSubFolderDTO));
        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void canDeleteSubdirectoryWithSubdirectoryWithFilesAtAllDirectories_Expect_FalseAndDirectoryNotEmptyException() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
        final File testImage1 = new File(testDirectory, testImageName);
        final File testImage2 = new File(testSubdirectory, testImageName);

        final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
        final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());

        try {
            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isTestFile1Created = testImage1.createNewFile();
            final boolean isTestFile2Created = testImage2.createNewFile();

            assertTrue(isTestFile1Created);
            assertTrue(isTestFile2Created);

            assertFalse(imageFolderService.canBeDeleted(testImageFolderDTO));
            assertFalse(imageFolderService.canBeDeleted(testImageSubFolderDTO));
        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderNotContainUsedImages_ExpectedEmptyList() throws Exception {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testStubImageFileName = "testStub.jpg";
        final ImageFileDTO imageFileDTOStub = new ImageFileDTO();
        imageFileDTOStub.setPath(testStubImageFileName);

        final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

        commonDocumentDTO.getCommonContents()
                .forEach(commonContent -> commonContent.setMenuImageURL(File.separator + imageFileDTOStub.getPath()));
        commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

        final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
        Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
        final Image imageLatest = imageDataInitializer.createData(1, testStubImageFileName, testStubImageFileName, latestVersion);

        final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
        Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
        final Image imageWorking = imageDataInitializer.createData(1, testStubImageFileName, testStubImageFileName, workingVersion);

        List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);
        assertNotNull(usages);
        assertTrue(usages.isEmpty());
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderContainSingleUsedImage_ExpectedListWithUsage() throws Exception {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testImageFileName = "test.jpg";
        final ImageFileDTO imageFileDTOStub = new ImageFileDTO();
        imageFileDTOStub.setPath(testImageFileName);

        final File testImage = new File(imagesPath, testImageFileName);

        try {
            testImage.createNewFile();

            final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

            commonDocumentDTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(File.separator + imageFileDTOStub.getPath()));
            commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

            final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
            Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
            final Image imageLatest = imageDataInitializer.createData(1, testImageFileName, testImageFileName, latestVersion);

            final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
            Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
            final Image imageWorking = imageDataInitializer.createData(1, testImageFileName, testImageFileName, workingVersion);

            List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);

            assertNotNull(usages);
            assertFalse(usages.isEmpty());
            assertEquals(1, usages.size());
            assertEquals(4, usages.get(0).getUsages().size());
        } finally {
            testImage.delete();
        }
    }


    @Test
    public void checkFolderForImagesUsed_When_FolderContainSeveralUsedImages_ExpectedListWithUsages() throws Exception {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testImage1FileName = "test1.jpg";
        final String testImage2FileName = "test2.jpg";

        final ImageFileDTO imageFile1DTO = new ImageFileDTO();
        imageFile1DTO.setPath(testImage1FileName);

        final File test1Image = new File(imagesPath, testImage1FileName);
        final File test2Image = new File(imagesPath, testImage2FileName);

        try {
            test1Image.createNewFile();
            test2Image.createNewFile();

            final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

            commonDocumentDTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(File.separator + imageFile1DTO.getPath()));
            commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

            final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
            Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
            final Image imageLatest = imageDataInitializer.createData(1, testImage1FileName, testImage1FileName, latestVersion);


            final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
            Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
            final Image imageWorking = imageDataInitializer.createData(1, testImage2FileName, testImage2FileName, workingVersion);

            List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);

            assertNotNull(usages);
            assertFalse(usages.isEmpty());
            assertEquals(2, usages.size());
            assertEquals(3, usages.get(0).getUsages().size());
            assertEquals(1, usages.get(1).getUsages().size());
        } finally {
            test1Image.delete();
            test2Image.delete();
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_SubFolderContainSingleUsedImage_ExpectedListWithUsage() throws Exception {
        final String subDirectoryName = "subDirectory";
        final String testImageFileName = "test.jpg";
        final File testFolder = new File(imagesPath, subDirectoryName);

        final File testFile = new File(testFolder, testImageFileName);

        try {
            testFile.mkdirs();
            testFile.createNewFile();

            final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();
            imageFolderDTO.setPath(File.separator + subDirectoryName + File.separator);

            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(File.separator + subDirectoryName + File.separator + testImageFileName);

            final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

            commonDocumentDTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));
            commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

            final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
            Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
            final Image imageLatest = imageDataInitializer.createData(1, testImageFileName, subDirectoryName + File.separator + testImageFileName, latestVersion);

            final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
            Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
            final Image imageWorking = imageDataInitializer.createData(1, testImageFileName, subDirectoryName + File.separator + testImageFileName, workingVersion);

            List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);

            assertNotNull(usages);
            assertFalse(usages.isEmpty());
            assertEquals(1, usages.size());
            assertEquals(4, usages.get(0).getUsages().size());
        } finally {
            FileUtils.deleteDirectory(testFolder);
        }
    }
}
