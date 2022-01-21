package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.exception.DirectoryNotEmptyException;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.util.DeleteOnCloseFile;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ImageFolderServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private ImageFolderService imageFolderService;
	@Autowired
	private ImageService imageService;
    @Autowired
    private VersionService versionService;

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Value("${ImagePath}")
    private File imagesPath;

    @BeforeEach
    public void prepareData() {
        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @AfterEach
    public void clearTestData() {
        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @Test
    public void getImageFolder_Expected_RootFolderIsReturnedWithImages() {
        final ImageFolderDTO imageFolder = imageFolderService.getImageFolder();

        assertNotNull(imageFolder);
        assertEquals("images", imageFolder.getName());
        assertEquals("", imageFolder.getPath());
    }

    @Test
    public void getImageFolder_Expected_RootFolderWithOutGeneratedFolder() {
        final ImageFolderDTO imageFolder = imageFolderService.getImageFolder();

        assertNotNull(imageFolder);
        assertEquals("images", imageFolder.getName());

        final List<String> imageSubFoldersNames = imageFolder.getFolders().stream()
                .map(ImageFolderDTO::getName)
                .collect(Collectors.toList());

        assertFalse(imageSubFoldersNames.contains(ImcmsConstants.IMAGE_GENERATED_FOLDER));
    }

    @Test
    public void createNewImageFolder_When_FolderNotExistBefore_Expect_FolderCreatedAndIsDirectoryAndReadableAndThenRemoved() {
        final String newFolderName = "new_test_folder";

        try (DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, newFolderName)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

            assertFalse(newFolder.exists());
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());
        }
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() {
        final String newFolderName = "new_test_folder";

        try (DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, newFolderName)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

            assertFalse(newFolder.exists());
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());

            assertThrows(FolderAlreadyExistException.class, () -> imageFolderService.createImageFolder(imageFolderDTO));
        }
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() {
        final String newFolderName0 = "new_test_folder";
        final String newFolderPath0 = separator + newFolderName0;

        final String newFolderName1 = "nested1";
        final String newFolderPath1 = newFolderPath0 + separator + newFolderName1;

        final String newFolderName2 = "nested2";
        final String newFolderPath2 = newFolderPath1 + separator + newFolderName2;

        final String newFolderName3 = "nested3";
        final String newFolderPath3 = newFolderPath1 + separator + newFolderName3;

        try (DeleteOnCloseFile newFolder3 = new DeleteOnCloseFile(imagesPath, newFolderPath3);
             DeleteOnCloseFile newFolder2 = new DeleteOnCloseFile(imagesPath, newFolderPath2);
             DeleteOnCloseFile newFolder1 = new DeleteOnCloseFile(imagesPath, newFolderPath1);
             DeleteOnCloseFile newFolder0 = new DeleteOnCloseFile(imagesPath, newFolderPath0)) {
            final ImageFolderDTO imageFolderDTO0 = new ImageFolderDTO(newFolderName0, newFolderPath0);
            final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1, newFolderPath1);
            final ImageFolderDTO imageFolderDTO2 = new ImageFolderDTO(newFolderName2, newFolderPath2);
            final ImageFolderDTO imageFolderDTO3 = new ImageFolderDTO(newFolderName3, newFolderPath3);

            assertFalse(newFolder0.exists());
            assertFalse(newFolder1.exists());
            assertFalse(newFolder2.exists());
            assertFalse(newFolder3.exists());

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
        }
    }

	@Test
	public void renameFolder_When_FolderExistsInRootImagesDirectory_Expect_True_And_UpdatedImageUrl() {
		setUserDomainObject();

		final String folderName = "test_folder";
		final String newFolderName = "new_name";

		final String testImageName = "testImage";
		final String testImagePath = folderName + separator + "testImage";
		final String newTestImagePath = newFolderName + separator + "testImage";

		final Integer testDocumentId = documentDataInitializer.createData().getId();
	    final Version version = versionService.create(testDocumentId, 1);
	    final ImageJPA imageJPA = imageDataInitializer.createData(1, testImageName, testImagePath, version);

        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(folderName);
        imageFolderDTO.setName(newFolderName);

        try (DeleteOnCloseFile renamedFolder = new DeleteOnCloseFile(imagesPath, newFolderName);
             DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, folderName)) {
            assertFalse(newFolder.exists());

            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());

            assertFalse(renamedFolder.exists());
            assertTrue(imageFolderService.renameFolder(imageFolderDTO));
            assertTrue(renamedFolder.exists());
            assertFalse(newFolder.exists());

	        final ImageDTO imageDTO = imageService.getImage(testDocumentId, imageJPA.getIndex(), imageJPA.getLanguage().getCode(), imageJPA.getLoopEntryRef());
	        assertEquals(newTestImagePath, imageDTO.getPath());
        }

		removeUserDomainObject();
    }

    @Test
    public void renameFolder_When_FolderNotExist_Expect_CorrectException() {
        final String newFolderName = "new_test_folder";
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);
        final String folderNewName = "new_name";

        try (DeleteOnCloseFile renamedFolder = new DeleteOnCloseFile(imagesPath, folderNewName);
             DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, newFolderName)) {
            assertFalse(renamedFolder.exists());
            assertFalse(newFolder.exists());

            imageFolderDTO.setName(folderNewName);
            assertFalse(renamedFolder.exists());
            assertThrows(FolderNotExistException.class, () -> imageFolderService.renameFolder(imageFolderDTO));
        }
    }

    @Test
    public void renameFolder_When_FolderExistsNestedInRootImagesDirectory_Expect_True_And_UpdatedImageUrl() throws Exception {
	    setUserDomainObject();

        final String newFolderName = "new_test_folder";
        final String newNestedFolderName = "nested_folder";
        final String path = newFolderName + separator + newNestedFolderName;
        final String nestedFolderNewName = "new_name";
        final String renamedFolderPath = newFolderName + separator + nestedFolderNewName;

	    final String testImageName = "testImage";
	    final String testImagePath = newFolderName + separator + newNestedFolderName + separator + "testImage";
	    final String newTestImagePath = renamedFolderPath + separator + "testImage";

	    final Integer testDocumentId = documentDataInitializer.createData().getId();
	    final Version version = versionService.create(testDocumentId, 1);
	    final ImageJPA imageJPA = imageDataInitializer.createData(1, testImageName, testImagePath, version);

        try (DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, newFolderName);
             DeleteOnCloseFile newNestedFolder = new DeleteOnCloseFile(imagesPath, path);
             DeleteOnCloseFile renamedFolder = new DeleteOnCloseFile(imagesPath, renamedFolderPath)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

            assertFalse(newFolder.exists());

            final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(newNestedFolderName, separator + path);

            assertFalse(newNestedFolder.exists());
            assertFalse(renamedFolder.exists());

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

	        final ImageDTO imageDTO = imageService.getImage(testDocumentId, imageJPA.getIndex(), imageJPA.getLanguage().getCode(), imageJPA.getLoopEntryRef());
	        assertEquals(newTestImagePath, imageDTO.getPath());
        }
	    removeUserDomainObject();
    }

    @Test
    public void renameFolder_When_FolderWithSuchNameExist_Expect_CorrectException() {
        final String newFolderName = "new_test_folder";
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        final String newFolderName1 = "new_test_folder1";
        final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1);

        try (DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, newFolderName);
             DeleteOnCloseFile newFolder1 = new DeleteOnCloseFile(imagesPath, newFolderName1)) {
            assertFalse(newFolder.exists());
            assertFalse(newFolder1.exists());

            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());

            assertTrue(imageFolderService.createImageFolder(imageFolderDTO1));
            assertTrue(newFolder1.exists());
            assertTrue(newFolder1.isDirectory());
            assertTrue(newFolder1.canRead());

            final File renamedFolder = new File(imagesPath, newFolderName);
            imageFolderDTO1.setName(newFolderName);

            assertTrue(renamedFolder.exists());
            assertThrows(FolderAlreadyExistException.class, () -> imageFolderService.renameFolder(imageFolderDTO1));
        }
    }

    @Test
    public void deleteFolder_When_FolderExist_Expect_TrueAndFolderDeleted() throws Exception {
        final String newFolderName = "new_test_folder";
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try (DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, newFolderName)) {
            assertFalse(newFolder.exists());
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());

            assertTrue(imageFolderService.deleteFolder(imageFolderDTO));
            assertFalse(newFolder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderNotExist_Expect_CorrectException() {
        final String newFolderName = "new_test_folder";
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try (DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, newFolderName)) {
            assertFalse(newFolder.exists());
            assertThrows(FolderNotExistException.class, () -> imageFolderService.deleteFolder(imageFolderDTO));
        }
    }

    @Test
    public void deleteFolderWithEmptySubdirectory_ExpectTrue() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName);
             DeleteOnCloseFile testSubdirectory = new DeleteOnCloseFile(testDirectory, testSubdirectoryName)) {
            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectory.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isRemoved = imageFolderService.deleteFolder(testImageSubFolderDTO);

            assertTrue(isRemoved);
            assertFalse(testSubdirectory.exists());
        }
    }

    @Test
    public void deleteDirectoryWithFiles_Expect_FalseAndDirectoryNotEmptyException() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.jpg";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testImage = new File(testDirectory, testImageName);
            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());

            imageFolderService.createImageFolder(testImageFolderDTO);

            assertTrue(testDirectory.exists());

            final boolean isTestFileCreated = testImage.createNewFile();
            assertTrue(isTestFileCreated);

            assertThrows(DirectoryNotEmptyException.class, () -> imageFolderService.deleteFolder(testImageFolderDTO));
            assertTrue(testDirectory.exists());
        }
    }

    @Test
    public void deleteSubdirectoryWithEmptySubdirectoryTree_Expect_True() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
            final File testSubdirectoryLevel2 = new File(testSubdirectory, testSubdirectoryName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectory.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);
            final String name1 = testDirectory.getName() + separator + testSubdirectory.getName() + separator
                    + testSubdirectoryLevel2.getName();
            final ImageFolderDTO testImageSubFolderDTOLevel2 = new ImageFolderDTO(name1);

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
        }
    }

    @Test
    public void deleteSubdirectoryWithSubdirectoryWithFiles_Expect_FalseAndDirectoryNotEmptyException() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
            final File testImage1 = new File(testDirectory, testImageName);
            final File testImage2 = new File(testSubdirectory, testImageName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectory.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isTestFile1Created = testImage1.createNewFile();
            final boolean isTestFile2Created = testImage2.createNewFile();

            assertTrue(isTestFile1Created);
            assertTrue(isTestFile2Created);

            assertThrows(DirectoryNotEmptyException.class, () -> imageFolderService.deleteFolder(testImageFolderDTO));

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());
        }
    }

    @Test
    public void canDeleteSubdirectoryWithEmptySubdirectoryTree_Expect_True() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
            final File testSubdirectoryLevel2 = new File(testSubdirectory, testSubdirectoryName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectory.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);
            final String name1 = testDirectory.getName() + separator + testSubdirectory.getName() + separator
                    + testSubdirectoryLevel2.getName();
            final ImageFolderDTO testImageSubFolderDTOLevel2 = new ImageFolderDTO(name1);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTOLevel2);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());
            assertTrue(testSubdirectoryLevel2.exists());

            assertTrue(imageFolderService.canBeDeleted(testImageFolderDTO));
            assertTrue(imageFolderService.canBeDeleted(testImageSubFolderDTO));
        }
    }

    @Test
    public void canDeleteSubdirectoryWithSubdirectoryWithFile_Expect_FalseAndDirectoryNotEmptyException() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
            final File testImage2 = new File(testSubdirectory, testImageName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectory.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isTestFile2Created = testImage2.createNewFile();

            assertTrue(isTestFile2Created);

            assertThrows(DirectoryNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageFolderDTO));
            assertThrows(DirectoryNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageSubFolderDTO));
        }
    }

    @Test
    public void canDeleteSubdirectoryWithSubdirectoryWithFilesAtAllDirectories_Expect_FalseAndDirectoryNotEmptyException() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
            final File testImage1 = new File(testDirectory, testImageName);
            final File testImage2 = new File(testSubdirectory, testImageName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectory.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            final boolean isTestFile1Created = testImage1.createNewFile();
            final boolean isTestFile2Created = testImage2.createNewFile();

            assertTrue(isTestFile1Created);
            assertTrue(isTestFile2Created);

            assertThrows(DirectoryNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageFolderDTO));
            assertThrows(DirectoryNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageSubFolderDTO));
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderNotContainUsedImages_ExpectedEmptyList() {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testStubImageName = "testStub.jpg";

        imageDataInitializer.createAllAvailableImageContent(
                true, testStubImageName, testStubImageName
        );

        List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);
        assertNotNull(usages);
        assertTrue(usages.isEmpty());
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderContainSingleUsedImage_ExpectedListWithUsage() throws Exception {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testImageName = "test.jpg";

        try (DeleteOnCloseFile testImage = new DeleteOnCloseFile(imagesPath, testImageName)) {
            assertFalse(testImage.exists());
            assertTrue(testImage.createNewFile());
            assertTrue(testImage.exists());

            imageDataInitializer.createAllAvailableImageContent(
                    false, testImageName, testImageName
            );

            List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);

            assertNotNull(usages);
            assertFalse(usages.isEmpty());
            assertEquals(1, usages.size());
            assertEquals(2, usages.get(0).getUsages().size());
            assertEquals(testImageName, usages.get(0).getImageName());
            assertEquals(imageFolderDTO.getPath(), usages.get(0).getFilePath());
        }
    }


    @Test
    public void checkFolderForImagesUsed_When_FolderContainSeveralUsedImages_ExpectedListWithUsages() throws Exception {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testImage1Name = "test1.jpg";
        final String testImage2Name = "test2.jpg";

        try (DeleteOnCloseFile testImage1File = new DeleteOnCloseFile(imagesPath, testImage1Name);
             DeleteOnCloseFile testImage2File = new DeleteOnCloseFile(imagesPath, testImage2Name)) {
            assertFalse(testImage1File.exists());
            assertTrue(testImage1File.createNewFile());
            assertTrue(testImage1File.exists());
            assertFalse(testImage2File.exists());
            assertTrue(testImage2File.createNewFile());
            assertTrue(testImage2File.exists());

            imageDataInitializer.createAllAvailableImageContent(
                    false, testImage1Name, testImage2Name
            );

            List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);

            assertNotNull(usages);
            assertFalse(usages.isEmpty());
            assertEquals(2, usages.size());
            assertEquals(1, usages.get(0).getUsages().size());
            assertEquals(1, usages.get(1).getUsages().size());
//            assertEquals(testImage1Name, usages.get(0).getImageName());
            assertEquals(testImage2Name, usages.get(1).getImageName());
            assertEquals(imageFolderDTO.getPath(), usages.get(0).getFilePath());
            assertEquals(imageFolderDTO.getPath(), usages.get(1).getFilePath());
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_SubFolderContainSingleUsedImage_ExpectedListWithUsage() throws Exception {
        final String subDirectoryName = "subDirectory";
        final String testImageName = "test.jpg";
        final String testImageUrl = subDirectoryName + separator + testImageName;

        try (DeleteOnCloseFile testSubDirectory = new DeleteOnCloseFile(imagesPath, subDirectoryName)) {
            final File testImageFile = new File(testSubDirectory, testImageName);

            assertTrue(testSubDirectory.mkdirs());

            assertFalse(testImageFile.exists());
            assertTrue(testImageFile.createNewFile());
            assertTrue(testImageFile.exists());

            imageDataInitializer.createAllAvailableImageContent(
                    true, testImageUrl, testImageUrl
            );

            final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();
            imageFolderDTO.setPath(separator + subDirectoryName + separator);
            List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);

            assertNotNull(usages);
            assertFalse(usages.isEmpty());
            assertEquals(1, usages.size());
            assertEquals(2, usages.get(0).getUsages().size());
            assertEquals(testImageName, usages.get(0).getImageName());
            assertEquals(imageFolderDTO.getPath(), usages.get(0).getFilePath());
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_ImageUsedAtIntermediateDocumentVersion_Expect_ExpectedEmptyList() {
        final String testImageName = "test.jpg";
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testStubImageName = "testStub.jpg";
        final String subDirectoryName = "subDirectory";

        final Integer testDocumentId = documentDataInitializer.createData().getId();
        final Version intermediateVersion = versionService.create(testDocumentId, 1);

        // fixme: check usage
        final ImageJPA imageIntermediate = imageDataInitializer.createData(1, "", testImageName, intermediateVersion);

        final Version latestVersion = versionService.create(testDocumentId, 1);
        // fixme: check usage
        final ImageJPA imageLatest = imageDataInitializer.createData(
                1, "", subDirectoryName + separator + testStubImageName, latestVersion
        );

        List<ImageFolderItemUsageDTO> usages = imageFolderService.checkFolder(imageFolderDTO);

        assertNotNull(usages);
        assertTrue(usages.isEmpty());
    }

	private void setUserDomainObject() {
		final UserDomainObject user = new UserDomainObject(1);
		user.setRoleIds(Collections.singleton(Roles.SUPER_ADMIN.getId()));
		user.setLanguageIso639_2("eng"); // user lang should exist in common content
		Imcms.setUser(user);
	}

	private void removeUserDomainObject() {
		Imcms.removeUser();
	}
}
