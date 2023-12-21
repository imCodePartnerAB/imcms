package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.domain.component.ImageFolderCacheManager;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.FolderNotEmptyException;
import com.imcode.imcms.storage.exception.ForbiddenDeleteStorageFileException;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.storage.exception.SuchStorageFileExistsException;
import com.imcode.imcms.util.DeleteOnCloseStorageFile;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ImageFolderServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private ImageFolderService imageFolderService;
    @Autowired
    private ImageFolderCacheManager imageFolderCacheManager;
	@Autowired
	private ImageService imageService;
    @Autowired
    private VersionService versionService;

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    @Qualifier("imageStorageClient")
    private StorageClient storageClient;

    @Value("${ImagePath}")
    private String imagesPath;

    @BeforeEach
    public void prepareData() {
        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @AfterEach
    public void clearTestData() {
        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
        clearImageFolderCache();
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

        final StoragePath newFolderPath = StoragePath.get(DIRECTORY, imagesPath, newFolderName);
        try (DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(newFolderPath, storageClient)) {
            assertFalse(newFolder.exists());

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);
            imageFolderService.createImageFolder(imageFolderDTO);

            assertTrue(newFolder.exists());
        }
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() {
        final String newFolderName = "new_test_folder";

        final StoragePath newFolderPath = StoragePath.get(DIRECTORY, imagesPath, newFolderName);
        try (DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(newFolderPath, storageClient)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

            assertFalse(newFolder.exists());
            imageFolderService.createImageFolder(imageFolderDTO);
            assertTrue(newFolder.exists());

            assertThrows(SuchStorageFileExistsException.class, () -> imageFolderService.createImageFolder(imageFolderDTO));
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

        try (DeleteOnCloseStorageFile newFolder3 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderPath3), storageClient);
             DeleteOnCloseStorageFile newFolder2 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderPath2), storageClient);
             DeleteOnCloseStorageFile newFolder1 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderPath1), storageClient);
             DeleteOnCloseStorageFile newFolder0 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderPath0), storageClient)) {
            final ImageFolderDTO imageFolderDTO0 = new ImageFolderDTO(newFolderName0, newFolderPath0);
            final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1, newFolderPath1);
            final ImageFolderDTO imageFolderDTO2 = new ImageFolderDTO(newFolderName2, newFolderPath2);
            final ImageFolderDTO imageFolderDTO3 = new ImageFolderDTO(newFolderName3, newFolderPath3);

            assertFalse(newFolder0.exists());
            assertFalse(newFolder1.exists());
            assertFalse(newFolder2.exists());
            assertFalse(newFolder3.exists());

            imageFolderService.createImageFolder(imageFolderDTO0);
            imageFolderService.createImageFolder(imageFolderDTO1);
            imageFolderService.createImageFolder(imageFolderDTO2);
            imageFolderService.createImageFolder(imageFolderDTO3);

            assertTrue(newFolder0.exists());
            assertTrue(newFolder1.exists());
            assertTrue(newFolder2.exists());
            assertTrue(newFolder3.exists());
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

        try (DeleteOnCloseStorageFile renamedFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderName), storageClient);
             DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, folderName), storageClient)) {
            assertFalse(newFolder.exists());

            imageFolderService.createImageFolder(imageFolderDTO);
            assertTrue(newFolder.exists());

            assertFalse(renamedFolder.exists());
            imageFolderService.renameFolder(imageFolderDTO);
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

        try (DeleteOnCloseStorageFile renamedFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, folderNewName), storageClient);
             DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderName), storageClient)) {
            assertFalse(renamedFolder.exists());
            assertFalse(newFolder.exists());

            imageFolderDTO.setName(folderNewName);
            assertFalse(renamedFolder.exists());
            assertThrows(StorageFileNotFoundException.class, () -> imageFolderService.renameFolder(imageFolderDTO));
        }
    }

    @Test
    public void renameFolder_When_FolderExistsNestedInRootImagesDirectory_Expect_True_And_UpdatedImageUrl() {
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

        try (DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderName), storageClient);
             DeleteOnCloseStorageFile newNestedFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, path), storageClient);
             DeleteOnCloseStorageFile renamedFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, renamedFolderPath), storageClient)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

            assertFalse(newFolder.exists());

            final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(newNestedFolderName, separator + path);

            assertFalse(newNestedFolder.exists());
            assertFalse(renamedFolder.exists());

            imageFolderService.createImageFolder(imageFolderDTO);
            assertTrue(newFolder.exists());

            imageFolderService.createImageFolder(imageNestedFolderDTO);
            assertTrue(newNestedFolder.exists());

            imageNestedFolderDTO.setName(nestedFolderNewName);

            assertFalse(renamedFolder.exists());
            imageFolderService.renameFolder(imageNestedFolderDTO);
            assertTrue(renamedFolder.exists());
            assertFalse(newNestedFolder.exists());
            assertTrue(renamedFolder.delete(true));

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

        try (DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderName), storageClient);
             DeleteOnCloseStorageFile newFolder1 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderName1), storageClient)) {
            assertFalse(newFolder.exists());
            assertFalse(newFolder1.exists());

            imageFolderService.createImageFolder(imageFolderDTO);
            assertTrue(newFolder.exists());

            imageFolderService.createImageFolder(imageFolderDTO1);
            assertTrue(newFolder1.exists());

            imageFolderDTO1.setName(newFolderName);

            assertThrows(SuchStorageFileExistsException.class, () -> imageFolderService.renameFolder(imageFolderDTO1));
        }
    }

    @Test
    public void deleteFolder_When_FolderExist_Expect_TrueAndFolderDeleted() throws Exception {
        final String newFolderName = "new_test_folder";
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try (DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderName), storageClient)) {
            assertFalse(newFolder.exists());
            imageFolderService.createImageFolder(imageFolderDTO);
            assertTrue(newFolder.exists());

            imageFolderService.deleteFolder(imageFolderDTO);
            assertFalse(newFolder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderNotExist_Expect_CorrectException() {
        final String newFolderName = "new_test_folder";
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try (DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, newFolderName), storageClient)) {
            assertFalse(newFolder.exists());
            assertThrows(StorageFileNotFoundException.class, () -> imageFolderService.deleteFolder(imageFolderDTO));
        }
    }

    @Test
    public void deleteFolderWithEmptySubdirectory_ExpectTrue() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testDirectoryName), storageClient);
             DeleteOnCloseStorageFile testSubdirectory = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testDirectoryName, testSubdirectoryName), storageClient)) {
            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectory.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(testSubdirectory.exists());

            imageFolderService.deleteFolder(testImageSubFolderDTO);

            assertFalse(testSubdirectory.exists());
        }
    }

    @Test
    public void deleteDirectoryWithFiles_Expect_FalseAndFolderNotEmptyException() {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.jpg";

        StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testImagePath = testDirectoryPath.resolve(FILE, testImageName);
            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());

            imageFolderService.createImageFolder(testImageFolderDTO);

            assertTrue(testDirectory.exists());

            storageClient.create(testImagePath);
            final boolean isTestFileCreated = storageClient.exists(testImagePath);
            assertTrue(isTestFileCreated);

            assertThrows(FolderNotEmptyException.class, () -> imageFolderService.deleteFolder(testImageFolderDTO));
            assertTrue(testDirectory.exists());
        }
    }

    @Test
    public void deleteSubdirectoryWithEmptySubdirectoryTree_Expect_True() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testSubdirectory = testDirectoryPath.resolve(DIRECTORY, testSubdirectoryName);
            final StoragePath testSubdirectoryLevel2 = testSubdirectory.resolve(DIRECTORY, testSubdirectoryName);

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
            assertTrue(storageClient.exists(testSubdirectory));
            assertTrue(storageClient.exists(testSubdirectoryLevel2));

            imageFolderService.deleteFolder(testImageSubFolderDTO);

            assertFalse(storageClient.exists(testSubdirectory));
            assertFalse(storageClient.exists(testSubdirectoryLevel2));
        }
    }

    @Test
    public void deleteSubdirectoryWithSubdirectoryWithFiles_Expect_FalseAndFolderNotEmptyException() {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testSubdirectoryPath = testDirectoryPath.resolve(DIRECTORY, testSubdirectoryName);
            final StoragePath testImagePath1 = testDirectoryPath.resolve(FILE, testImageName);
            final StoragePath testImagePath2 = testSubdirectoryPath.resolve(FILE, testImageName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectoryPath.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(storageClient.exists(testSubdirectoryPath));

            storageClient.create(testImagePath1);
            storageClient.create(testImagePath2);
            assertTrue(storageClient.exists(testImagePath1));
            assertTrue(storageClient.exists(testImagePath2));

            assertThrows(FolderNotEmptyException.class, () -> imageFolderService.deleteFolder(testImageFolderDTO));

            assertTrue(testDirectory.exists());
            assertTrue(storageClient.exists(testSubdirectoryPath));
        }
    }

    @Test
    public void canDeleteSubdirectoryWithEmptySubdirectoryTree_Expect_True() throws IOException {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testSubdirectoryPath = testDirectoryPath.resolve(DIRECTORY, testSubdirectoryName);
            final StoragePath testSubdirectoryLevelPath2 = testSubdirectoryPath.resolve(DIRECTORY, testSubdirectoryName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectoryPath.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);
            final String name1 = testDirectory.getName() + separator + testSubdirectoryPath.getName() + separator
                    + testSubdirectoryLevelPath2.getName();
            final ImageFolderDTO testImageSubFolderDTOLevel2 = new ImageFolderDTO(name1);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTOLevel2);

            assertTrue(testDirectory.exists());
            assertTrue(storageClient.exists(testSubdirectoryPath));
            assertTrue(storageClient.exists(testSubdirectoryLevelPath2));

            assertTrue(imageFolderService.canBeDeleted(testImageFolderDTO));
            assertTrue(imageFolderService.canBeDeleted(testImageSubFolderDTO));
        }
    }

    @Test
    public void canDeleteSubdirectoryWithSubdirectoryWithFile_Expect_FalseAndFolderNotEmptyException() {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testSubdirectoryPath = testDirectoryPath.resolve(DIRECTORY, testSubdirectoryName);
            final StoragePath testImagePath2 = testSubdirectoryPath.resolve(FILE, testImageName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectoryPath.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(storageClient.exists(testSubdirectoryPath));

            storageClient.create(testImagePath2);
            assertTrue(storageClient.exists(testImagePath2));

            assertThrows(FolderNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageFolderDTO));
            assertThrows(FolderNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageSubFolderDTO));
        }
    }

    @Test
    public void canDeleteSubdirectoryWithSubdirectoryWithFilesAtAllDirectories_Expect_FalseAndFolderNotEmptyException() {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubDirectory";
        final String testImageName = "test.jpg";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testSubdirectoryPath = testDirectoryPath.resolve(DIRECTORY, testSubdirectoryName);
            final StoragePath testImagePath1 = testDirectoryPath.resolve(FILE, testImageName);
            final StoragePath testImagePath2 = testSubdirectoryPath.resolve(FILE, testImageName);

            final ImageFolderDTO testImageFolderDTO = new ImageFolderDTO(testDirectory.getName());
            final String name = testDirectory.getName() + separator + testSubdirectoryPath.getName();
            final ImageFolderDTO testImageSubFolderDTO = new ImageFolderDTO(name);

            imageFolderService.createImageFolder(testImageFolderDTO);
            imageFolderService.createImageFolder(testImageSubFolderDTO);

            assertTrue(testDirectory.exists());
            assertTrue(storageClient.exists(testSubdirectoryPath));

            storageClient.create(testImagePath1);
            storageClient.create(testImagePath2);
            assertTrue(storageClient.exists(testImagePath1));
            assertTrue(storageClient.exists(testImagePath2));

            assertThrows(FolderNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageFolderDTO));
            assertThrows(FolderNotEmptyException.class, () -> imageFolderService.canBeDeleted(testImageSubFolderDTO));
        }
    }

    @Test
    public void canDelete_When_DirectoryForGeneratedImages_Expect_CorrectException() {
        final ImageFolderDTO generatedImageFolderDTO = new ImageFolderDTO(ImcmsConstants.IMAGE_GENERATED_FOLDER);
        assertThrows(ForbiddenDeleteStorageFileException.class, () -> imageFolderService.canBeDeleted(generatedImageFolderDTO));
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
    public void checkFolderForImagesUsed_When_FolderContainSingleUsedImage_ExpectedListWithUsage() {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testImageName = "test.jpg";

        try (DeleteOnCloseStorageFile testImage = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, testImageName), storageClient)) {
            assertFalse(testImage.exists());
            assertTrue(testImage.create());
            assertTrue(testImage.exists());

            clearImageFolderCache();

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
    public void checkFolderForImagesUsed_When_FolderContainSeveralUsedImages_ExpectedListWithUsages() {
        final ImageFolderDTO imageFolderDTO = imageFolderService.getImageFolder();

        final String testImage1Name = "test1.jpg";
        final String testImage2Name = "test2.jpg";

        try (DeleteOnCloseStorageFile testImage1File = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, testImage1Name), storageClient);
             DeleteOnCloseStorageFile testImage2File = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, testImage2Name), storageClient)) {
            assertFalse(testImage1File.exists());
            assertTrue(testImage1File.create());
            assertTrue(testImage1File.exists());
            assertFalse(testImage2File.exists());
            assertTrue(testImage2File.create());
            assertTrue(testImage2File.exists());

            clearImageFolderCache();

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
    public void checkFolderForImagesUsed_When_SubFolderContainSingleUsedImage_ExpectedListWithUsage() {
        final String subDirectoryName = "subDirectory";
        final String testImageName = "test.jpg";
        final String testImageUrl = subDirectoryName + separator + testImageName;

        final StoragePath testSubDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, subDirectoryName);
        try (DeleteOnCloseStorageFile testSubDirectory = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, subDirectoryName), storageClient)) {

            final StoragePath testImageFilePath = testSubDirectoryPath.resolve(FILE, testImageName);

            assertTrue(testSubDirectory.create());

            assertFalse(storageClient.exists(testImageFilePath));
            storageClient.create(testImageFilePath);
            assertTrue(storageClient.exists(testImageFilePath));

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

    private void clearImageFolderCache(){
        imageFolderCacheManager.invalidate();
    }
}
