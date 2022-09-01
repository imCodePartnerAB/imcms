package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.util.DeleteOnCloseStorageFile;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.UserDomainObject;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ImageFileServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private Function<ImageJPA, ImageDTO> imageJPAToImageDTO;
    @Autowired
    private ImageFileService imageFileService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private VersionService versionService;

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;
    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Autowired
    @Qualifier("imageStorageClient")
    private StorageClient storageClient;

    @Value("classpath:img1.jpg")
    private File testImageFile;

    @Value("${ImagePath}")
    private String imagesPath;

    @BeforeEach
    public void prepareData() {
        imageDataInitializer.cleanRepositories();
        commonContentDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng"); // user lang should exist in common content
        Imcms.setUser(user);
    }

    @AfterEach
    public void clearTestData() {
        imageDataInitializer.cleanRepositories();
        commonContentDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @Test
    public void saveNewImageFiles_When_FolderIsNotSet_Expect_CorrectResultSize() throws IOException {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);

        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        final List<MultipartFile> files = Collections.singletonList(file);
        final List<ImageFileDTO> imageFileDTOS = imageFileService.saveNewImageFiles(null, files);

        assertNotNull(imageFileDTOS);
        assertEquals(files.size(), imageFileDTOS.size());

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void saveNewImageFiles_When_TwoFilesSentAndFolderIsNotSet_Expect_CorrectResultSize() throws IOException {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);

        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        final List<MultipartFile> files = Arrays.asList(file, file);
        final List<ImageFileDTO> imageFileDTOS = imageFileService.saveNewImageFiles(null, files);

        assertNotNull(imageFileDTOS);
        assertEquals(files.size(), imageFileDTOS.size());

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void saveNewImageFiles_When_TwoFilesSentAndFolderIsSet_Expect_CorrectResultSize() throws IOException {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);

        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        final List<MultipartFile> files = Arrays.asList(file, file);
        final String folder = separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;
        final List<ImageFileDTO> imageFileDTOS = imageFileService.saveNewImageFiles(folder, files);

        assertNotNull(imageFileDTOS);
        assertEquals(files.size(), imageFileDTOS.size());

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void saveNewImageFiles_When_TwoFilesSentAndFolderNotExistButIsSet_Expect_CorrectException() throws IOException {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);

        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        final List<MultipartFile> files = Arrays.asList(file, file);
        final String nonExistingFolder = separator + "generateddddd";

        assertThrows(StorageFileNotFoundException.class, () -> imageFileService.saveNewImageFiles(nonExistingFolder, files));
    }

	@Test
	public void moveImageFile_When_ImageFileNotUsed_Expect_SuccessfulResults() throws IOException {
		final String destinationFolderName = "test-folder";
		final String testImageName = "test-image.jpg";
		final String testImageNewPath = destinationFolderName + separator + testImageName;
        final StoragePath destinationFolderPath = StoragePath.get(DIRECTORY, imagesPath, destinationFolderName);

        storageClient.create(StoragePath.get(FILE, imagesPath, testImageName));
		storageClient.create(destinationFolderPath);

		ImageFileDTO result = imageFileService.moveImageFile(testImageNewPath, testImageName);

		assertEquals(result.getName(), testImageName);
		assertEquals(result.getPath(), testImageNewPath);

		storageClient.delete(destinationFolderPath, true);
	}

	@Test
	public void moveImageFile_When_ImageInUse_Expect_SuccessfulResults() throws IOException {
		final String destinationFolderName = "test-folder";
		final String testImageName = "test-image.jpg";
		final String testImageNewPath = destinationFolderName + separator + testImageName;
		final StoragePath destinationFolderPath = StoragePath.get(DIRECTORY, imagesPath, destinationFolderName);

        storageClient.create(StoragePath.get(FILE, imagesPath, testImageName));

		final int workingDocId = documentDataInitializer.createData().getId();
		final Version workingVersion = versionService.getDocumentWorkingVersion(workingDocId);

		final ImageJPA image = imageDataInitializer.createData(1, testImageName, testImageName, workingVersion);

        storageClient.create(destinationFolderPath);

		ImageFileDTO result = imageFileService.moveImageFile(testImageNewPath, testImageName);

		final ImageDTO updatedImageDTO = imageService.getImage(image.getVersion().getDocId(), image.getIndex(), image.getLanguage().getCode(), image.getLoopEntryRef());

		assertEquals(result.getName(), testImageName);
		assertEquals(result.getPath(), testImageNewPath);
		assertEquals(updatedImageDTO.getPath(), result.getPath());

        storageClient.delete(destinationFolderPath, true);
	}

    @Test
    public void deleteImage_When_ImageExist_Expect_EmptyList() throws IOException {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);

        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        final List<MultipartFile> files = Collections.singletonList(file);
        final String folder = separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;

        final List<ImageFileDTO> imageFileDTOS = imageFileService.saveNewImageFiles(folder, files);

        assertNotNull(imageFileDTOS);
        assertEquals(files.size(), 1);

        final ImageFileDTO imageFileDTO = imageFileDTOS.get(0);

        final StoragePath createdImageFilePath = StoragePath.get(FILE, imagesPath, imageFileDTO.getPath());
        try (final DeleteOnCloseStorageFile createdImageFile = new DeleteOnCloseStorageFile(createdImageFilePath, storageClient)) {
            assertTrue(createdImageFile.exists());
            assertTrue(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertFalse(createdImageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAtWorkingSingleDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        final StoragePath testImageFilePath = StoragePath.get(FILE, imagesPath, testImageFileName);
        try (final DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {
            assertFalse(testImageFile.exists());
            assertTrue(testImageFile.create());
            assertTrue(testImageFile.exists());

            final int workingDocId = documentDataInitializer.createData().getId();

            final Version workingVersion = versionService.getDocumentWorkingVersion(workingDocId);

            final ImageJPA image = imageDataInitializer.createData(1, workingVersion);

            image.setName(testImageFileName);
            image.setUrl(testImageFileName);

            final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

            imageService.saveImage(imageDTO);

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAtLatestSingleDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        final StoragePath testImageFilePath = StoragePath.get(FILE, imagesPath, testImageFileName);
        try (final DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {
            assertFalse(testImageFile.exists());
            assertTrue(testImageFile.create());
            assertTrue(testImageFile.exists());

            final int latestDocId = documentDataInitializer.createData().getId();

            final Version latestVersion = versionService.getLatestVersion(latestDocId);

            final ImageJPA image = imageDataInitializer.createData(1, latestVersion);

            image.setName(testImageFileName);
            image.setUrl(testImageFileName);

            final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

            imageService.saveImage(imageDTO);

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_ImageAtSubdirectoryUsedAtLatestSingleDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final String testSubDirectoryName = "subdirectory";

        final StoragePath testSubdirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testSubDirectoryName);
        final StoragePath testImageFilePath = StoragePath.get(FILE, testSubdirectoryPath.toString(), testImageFileName);
        try (final DeleteOnCloseStorageFile testSubdirectory = new DeleteOnCloseStorageFile(testSubdirectoryPath, storageClient);
             final DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {

            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(File.separator + testSubDirectoryName + File.separator + testImageFileName);

            assertFalse(testImageFile.exists());
            assertTrue(testSubdirectory.create());
            assertTrue(testImageFile.create());
            assertTrue(testImageFile.exists());

            final int latestDocId = documentDataInitializer.createData().getId();
            versionService.create(latestDocId, 1);
            final Version latestVersion = versionService.getLatestVersion(latestDocId);
            final ImageJPA image = imageDataInitializer.createData(1, latestVersion);

            image.setName(testImageFileName);
            image.setUrl(testSubDirectoryName + File.separator + testImageFileName);

            final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

            imageService.saveImage(imageDTO);
            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_ImageAtSubdirectoryUsedAtLatestSingleDocument_Expect_TrueAndFileDeleted() throws IOException {
        final String testImageFileName = "test.jpg";
        final String testSubDirectoryName = "subdirectory";

        final StoragePath testSubdirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testSubDirectoryName);
        final StoragePath testImageFilePath = StoragePath.get(FILE, testSubdirectoryPath.toString(), testImageFileName);
        try (final DeleteOnCloseStorageFile testSubdirectory = new DeleteOnCloseStorageFile(testSubdirectoryPath, storageClient);
             final DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {
            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(testSubDirectoryName + File.separator + testImageFileName);

            assertFalse(testImageFile.exists());
            assertTrue(testSubdirectory.create());
            assertTrue(testImageFile.create());
            assertTrue(testImageFile.exists());
            imageFileService.deleteImage(imageFileDTO);
            assertFalse(testImageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAtWorkingDocumentAndPublishedDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        final StoragePath testImageFilePath = StoragePath.get(FILE, imagesPath, testImageFileName);
        try (final DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {
            assertFalse(testImageFile.exists());
            assertTrue(testImageFile.create());
            assertTrue(testImageFile.exists());

            final int latestDocId = documentDataInitializer.createData().getId();
            final int workingDocId = documentDataInitializer.createData().getId();

            final Version workingVersion = versionService.getDocumentWorkingVersion(workingDocId);

            versionService.create(latestDocId, 1);
            final Version latestVersion = versionService.create(latestDocId, 1);

            final ImageJPA imageLatest = imageDataInitializer.createData(1, latestVersion);
            final ImageJPA imageWorking = imageDataInitializer.createData(1, workingVersion);

            imageLatest.setName(testImageFileName);
            imageLatest.setUrl(testImageFileName);

            imageWorking.setName(testImageFileName);
            imageWorking.setUrl(testImageFileName);

            final ImageDTO imageDTOLatest = imageJPAToImageDTO.apply(imageLatest);
            final ImageDTO imageDTOWorking = imageJPAToImageDTO.apply(imageWorking);

            imageService.saveImage(imageDTOLatest);
            imageService.saveImage(imageDTOWorking);

            List<ImageFileUsageDTO> usages = imageFileService.deleteImage(imageFileDTO);

            assertFalse(usages.isEmpty());
            assertEquals(3, usages.size());
            assertTrue(testImageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAtIntermediateDocumentVersion_Expect_TrueAndImageDeleted() throws IOException {
        final String testImageFileName = "test.jpg";
        final String test2ImageFileName = "test2.jpg";

        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        final ImageFileDTO imageFile2DTO = new ImageFileDTO();
        imageFile2DTO.setPath(test2ImageFileName);

        final StoragePath testImageFilePath = StoragePath.get(FILE, imagesPath, testImageFileName);
        try (final DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {
            assertFalse(testImageFile.exists());
            assertTrue(testImageFile.create());
            assertTrue(testImageFile.exists());

            final int tempDocId = documentDataInitializer.createData().getId();
            final Version intermediateVersion = versionService.create(tempDocId, 1);

            // fixme: check usage
            final ImageJPA imageIntermediate = imageDataInitializer.createData(1, testImageFileName, testImageFileName, intermediateVersion);

            final Version latestVersion = versionService.create(tempDocId, 1);

            // fixme: check usage
            final ImageJPA imageLatest = imageDataInitializer.createData(1, test2ImageFileName, test2ImageFileName, latestVersion);

            List<ImageFileUsageDTO> usages = imageFileService.deleteImage(imageFileDTO);

            assertTrue(usages.isEmpty());
            assertFalse(testImageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_ImageNotUsedAtAnyLatestAndWorkingDocument_Expect_TrueAndFileDeleted() throws IOException {
        final String testImageFileName = "test.jpg";
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        final StoragePath testImageFilePath = StoragePath.get(FILE, imagesPath, testImageFileName);
        try (final DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {
            assertFalse(testImageFile.exists());
            assertTrue(testImageFile.create());
            assertTrue(testImageFile.exists());

            List<ImageFileUsageDTO> usages = imageFileService.deleteImage(imageFileDTO);

            assertTrue(usages.isEmpty());
            assertFalse(testImageFile.exists());
        }
    }

    private void deleteFile(ImageFileDTO imageFileDTO) {
        final StoragePath deleteMe = StoragePath.get(FILE, imagesPath, imageFileDTO.getPath());
        storageClient.delete(deleteMe, true);
    }
}
