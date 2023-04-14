package com.imcode.imcms.domain.service.api;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.ImageHistoryService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.util.DeleteOnCloseStorageFile;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class ImageServiceTest extends WebAppSpringTestConfig {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_IMAGE_INDEX = 1;
    private static final ImageDTO TEST_IMAGE_DTO = new ImageDTO(TEST_IMAGE_INDEX, TEST_DOC_ID);
    private static final int VERSION_INDEX = 0;

    @org.springframework.beans.factory.annotation.Value("${ImagePath}")
    private String imagesPath;

    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageHistoryService imageHistoryService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private Function<ImageJPA, ImageDTO> imageJPAToImageDTO;

    @Autowired
    private ImageDataInitializer imageDataInitializer;

    private Version workingVersion;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    @Qualifier("imageStorageClient")
    private StorageClient storageClient;

    @org.springframework.beans.factory.annotation.Value("classpath:img1.jpg")
    private File testImageFile;

    @BeforeEach
    public void setUp() {
        imageRepository.deleteAll();
        workingVersion = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng"); // user lang should exist in common content
        Imcms.setUser(user);
    }

    @AfterEach
    public void tearDown() {
        Imcms.removeUser();
    }

    @Test
    public void getImage_When_DocumentNotExist_Expect_Exception() {
        final int nonExistingDocId = 0;
        final ImageDTO imageDTO = new ImageDTO(TEST_IMAGE_INDEX, nonExistingDocId);

        assertThrows(DocumentNotExistException.class,
                () -> imageService.getImage(imageDTO));
    }

    @Test
    public void getImage_When_NotExist_Expect_EmptyDTO() {
        final ImageDTO image = imageService.getImage(TEST_IMAGE_DTO);

        assertEquals(image, TEST_IMAGE_DTO);
    }

    @Test
    public void getImage_When_LoopEntryRefIsNull_Expect_EqualResult() {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
        final ImageDTO resultImage = imageService.getImage(TEST_IMAGE_DTO);

        assertEquals(imageDTO, resultImage);
    }

    @Test
    public void getImage_When_LoopEntryRefIsNotNull_Expect_EqualResult() {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
        final ImageDTO resultImage = imageService.getImage(imageDTO);

        assertEquals(imageDTO, resultImage);
    }

    @Test
    public void getImage_When_PassedVersion_Expected_ImageOfSpecificVersion_And_GenerateImage() throws IOException {
        final Version version1 = versionDataInitializer.createData(1, TEST_DOC_ID);

        final LanguageJPA enLanguage = languageRepository.findByCode("en");

        final String filePathWorkingVersion = "test-image.jpg";
        final String filePathVersion1 = "test-image1.png";

        final DeleteOnCloseStorageFile imageFileWorkingVersion = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePathWorkingVersion), storageClient);
        final DeleteOnCloseStorageFile imageFileVersion1 = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePathVersion1), storageClient);

        try(imageFileWorkingVersion; imageFileVersion1) {
            imageFileWorkingVersion.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));
            imageFileVersion1.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));

            final ImageDTO imageDTOVersion1 = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePathVersion1);
                img.setFormat(Format.PNG);
                img.setLangCode(enLanguage.getCode());
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
            });
            imageService.saveImage(imageDTOVersion1);

            imageService.createVersionedContent(workingVersion, version1);

            final ImageDTO imageDTOWorkingVersion = new ImageDTO(imageDTOVersion1);
            imageDTOWorkingVersion.setPath(filePathWorkingVersion);
            imageDTOWorkingVersion.setFormat(Format.JPEG);
            imageDTOWorkingVersion.setName("img");
            imageService.saveImage(imageDTOWorkingVersion);

            final ImageDTO expectedImageVersion1 =
                    imageJPAToImageDTO.apply(imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version1, enLanguage, TEST_IMAGE_INDEX));
            final ImageDTO receivedImageVersion1 =
                    imageService.getImage(TEST_DOC_ID, TEST_IMAGE_INDEX, version1.getNo(), enLanguage.getCode(), null);

            assertEquals(expectedImageVersion1, receivedImageVersion1);

            // delete a generated image to check generation
            final StoragePath imageVersion1Path = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, receivedImageVersion1.getGeneratedFilename());
            assertTrue(storageClient.exists(imageVersion1Path));
            storageClient.delete(imageVersion1Path, true);

            imageService.getImage(TEST_DOC_ID, TEST_IMAGE_INDEX, version1.getNo(), enLanguage.getCode(), null);
            assertTrue(storageClient.exists(imageVersion1Path));

            // check generation while obtaining the image of a specific version
            final StoragePath imageWorkingVersionPath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, imageDTOWorkingVersion.getGeneratedFilename());
            assertTrue(storageClient.exists(imageWorkingVersionPath));
            storageClient.delete(imageWorkingVersionPath, true);

            storageClient.delete(imageVersion1Path, true);
        }
    }

    @Test
    public void getImage_When_NoImageOfSpecificVersion_Expected_EmptyImage() throws IOException {
        final Version version1 = versionDataInitializer.createData(1, TEST_DOC_ID);

        final LanguageJPA enLanguage = languageRepository.findByCode("en");
        final String filePathWorkingVersion = "test-image.jpg";
        final File testImageFileWorkingVersion = testImageFile;

        final DeleteOnCloseStorageFile imageFileWorkingVersion = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePathWorkingVersion), storageClient);
        try(imageFileWorkingVersion) {
            imageFileWorkingVersion.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFileWorkingVersion)));

            final ImageDTO imageDTOWorkingVersion = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePathWorkingVersion);
                img.setFormat(Format.PNG);
                img.setLangCode(enLanguage.getCode());
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
            });
            imageService.saveImage(imageDTOWorkingVersion);

            imageService.setAsWorkingVersion(version1);

            final ImageDTO expectedEmptyImage = new ImageDTO(TEST_IMAGE_INDEX, TEST_DOC_ID, null, enLanguage.getCode());
            final ImageDTO receivedImage = imageService.getImage(TEST_DOC_ID, TEST_IMAGE_INDEX, version1.getNo(), enLanguage.getCode(), null);

            assertEquals(expectedEmptyImage, receivedImage);

            final StoragePath imageWorkingVersionPath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, imageDTOWorkingVersion.getGeneratedFilename());
            assertTrue(storageClient.exists(imageWorkingVersionPath));
            storageClient.delete(imageWorkingVersionPath, true);
        }
    }

    @Test
    public void getImages_When_ImageNotInLoopEntryRef_Expected_EqualResult() {
        final Integer createdDocId = documentDataInitializer.createData().getId();

        loopDataInitializer.createData(LoopDTO.empty(createdDocId, 1));

        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, createdDocId, VERSION_INDEX, null);

        final List<ImageJPA> resultImages = imageService.getByDocId(createdDocId);

        assertFalse(resultImages.isEmpty());
        assertEquals(1, resultImages.size());

        assertEquals(image, resultImages.get(0));
    }

    @Test
    public void getImages_When_DocumentHasNotImages_Expected_EmptyResult() {
        final Integer createdDocId = documentDataInitializer.createData().getId();

        loopDataInitializer.createData(LoopDTO.empty(createdDocId, 1));

        imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, null);

        final List<ImageJPA> resultImages = imageService.getByDocId(createdDocId);

        assertTrue(resultImages.isEmpty());
    }


    @Test
    public void getImages_When_ImageInLoopEntryRef_Expected_EqualResult() {

        final Integer createdDocId = documentDataInitializer.createData().getId();

        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, createdDocId, VERSION_INDEX, loopEntryRef);

        final List<ImageJPA> resultImages = imageService.getByDocId(createdDocId);

        assertFalse(resultImages.isEmpty());
        assertEquals(1, resultImages.size());

        assertEquals(image, resultImages.get(0));

    }

    @Test
    public void getImages_When_LoopEntryRefNotExist_Expected_EqualResult() {
        final Integer createdDocId = documentDataInitializer.createData().getId();

        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, createdDocId, VERSION_INDEX, null);

        final List<ImageJPA> resultImages = imageService.getByDocId(createdDocId);

        assertFalse(resultImages.isEmpty());
        assertEquals(1, resultImages.size());

        assertEquals(image, resultImages.get(0));
    }

	@Test
	public void getLoopImages_When_LoopIndexPresent_Expected_EqualResult() {
		final Integer createdDocId = documentDataInitializer.createData().getId();
		final int loopIndex = 1;

		final LoopEntryRefJPA loopEntryRef1 = new LoopEntryRefJPA(loopIndex, 1);
		final LoopEntryRefJPA loopEntryRef2 = new LoopEntryRefJPA(loopIndex, 2);

		final ImageJPA image1 = imageDataInitializer.createData(1, createdDocId, VERSION_INDEX, loopEntryRef1);
		final ImageJPA image2 = imageDataInitializer.createData(2, createdDocId, VERSION_INDEX, loopEntryRef2);

		final ImageDTO imageDTO1= Optional.of(image1).map(imageJPAToImageDTO).get();
		final ImageDTO imageDTO2= Optional.of(image2).map(imageJPAToImageDTO).get();

		final List<ImageDTO> resultImages = imageService.getLoopImages(createdDocId, "en", loopIndex);

		assertFalse(resultImages.isEmpty());
		assertEquals(2, resultImages.size());
		assertEquals(imageDTO1, resultImages.get(0));
		assertEquals(imageDTO2, resultImages.get(1));
	}

	@Test
	public void getLoopImages_When_ImagesNotInLoop_EmptyResult() {
		final Integer createdDocId = documentDataInitializer.createData().getId();
		final int loopIndex = 1;

		imageDataInitializer.createData(1, createdDocId, VERSION_INDEX, null);
		imageDataInitializer.createData(2, createdDocId, VERSION_INDEX, null);

		final List<ImageDTO> resultImages = imageService.getLoopImages(createdDocId, "en", loopIndex);

		assertTrue(resultImages.isEmpty());
	}

	@Test
	public void getLoopImages_When_LoopIndexPresent_And_IncorrectLangCode_Expected_CorrectResult() {
		final Integer createdDocId = documentDataInitializer.createData().getId();
		final int loopIndex = 1;

		final LanguageJPA languageJPA1 = languageRepository.findByCode("en");
		final LanguageJPA languageJPA2 = languageRepository.findByCode("sv");

		final Version version = versionDataInitializer.createData(VERSION_INDEX, createdDocId);

		final LoopEntryRefJPA loopEntryRef1 = new LoopEntryRefJPA(loopIndex, 1);
		final LoopEntryRefJPA loopEntryRef2 = new LoopEntryRefJPA(loopIndex, 2);

		final ImageJPA image1FromLoop1 = imageDataInitializer.generateImage(1, languageJPA1, version, loopEntryRef1);
		final ImageJPA image2FromLoop1 = imageDataInitializer.generateImage(2, languageJPA1, version, loopEntryRef2);

		final ImageDTO imageDTO1FromLoop1 = Optional.of(image1FromLoop1).map(imageJPAToImageDTO).get();
		final ImageDTO imageDTO2FromLoop1 = Optional.of(image2FromLoop1).map(imageJPAToImageDTO).get();

		final ImageJPA image1FromLoop2 = imageDataInitializer.generateImage(1, languageJPA2, version, loopEntryRef1);
		final ImageJPA image2FromLoop2 = imageDataInitializer.generateImage(2, languageJPA2, version, loopEntryRef2);

		final ImageDTO imageDTO1FromLoop2 = Optional.of(image1FromLoop2).map(imageJPAToImageDTO).get();
		final ImageDTO imageDTO2FromLoop2 = Optional.of(image2FromLoop2).map(imageJPAToImageDTO).get();

		final List<ImageDTO> resultImages = imageService.getLoopImages(createdDocId, "sv", loopIndex);

		assertFalse(resultImages.isEmpty());

		assertNotEquals(imageDTO1FromLoop1, resultImages.get(0));
		assertNotEquals(imageDTO2FromLoop1, resultImages.get(1));

		assertEquals(imageDTO1FromLoop2, resultImages.get(0));
		assertEquals(imageDTO2FromLoop2, resultImages.get(1));
	}

    @Test
    public void saveImage_When_LoopEntryRefIsNull_Expect_EqualResult() {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final ImageDTO result = imageService.getImage(TEST_IMAGE_DTO);

        assertEquals(result, imageDTO);
    }

    @Test
    public void saveImage_When_LoopEntryRefIsNull_Expect_SavedHistory() {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        imageService.saveImage(imageDTO);
        imageService.saveImage(imageDTO);

        final ImageDTO result = imageService.getImage(TEST_IMAGE_DTO);
        final List<ImageHistoryDTO> history = imageHistoryService.getAll(imageDTO);

        assertEquals(result, imageDTO);
        assertTrue(!history.isEmpty());
        assertEquals(2, history.size());

        history.forEach(imageHistory -> {
            assertEquals(imageDTO.getIndex(), imageHistory.getIndex());
            assertEquals(imageDTO.getLangCode(), imageHistory.getLangCode());
            assertNull(imageHistory.getLoopEntryRef());
        });
    }

    @Test
    public void saveImage_When_InLoopAndAllLanguagesFlagIsSet_Expect_ImageSavedForAllLanguages() {
        saveImageWhenAllAvailableLanguagesFlagIsSet(true);
    }

    @Test
    public void saveImage_When_NotInLoopAndAllLanguagesFlagIsSet_Expect_ImageSavedForAllLanguages() {
        saveImageWhenAllAvailableLanguagesFlagIsSet(false);
    }

    @Test
    public void saveImage_When_InLoopAndFlagAllLanguagesIsFalse_Expect_ImagesWithDiffLangCodeHaveFalseFlag() {
        saveImageWhenAllAvailableLanguagesFlagIsNotSet(true);
    }

    @Test
    public void saveImage_When_NotInLoopAndFlagAllLanguagesIsFalse_Expect_ImagesWithDiffLangCodeHaveFalseFlag() {
        saveImageWhenAllAvailableLanguagesFlagIsNotSet(false);
    }

    @Test
    public void saveImage_When_LoopEntryRefIsNotNull_Expect_EqualResult() {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final ImageDTO result = imageService.getImage(imageDTO);

        assertEquals(result, imageDTO);
    }

    @Test
    public void saveImage_When_LoopEntryRefIsNotNull_Expect_SavedHistory() {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        imageService.saveImage(imageDTO);
        imageService.saveImage(imageDTO);

        final List<ImageHistoryDTO> history = imageHistoryService.getAll(imageDTO);

        assertTrue(!history.isEmpty());
        assertEquals(2, history.size());

        history.forEach(imageHistory -> {
            assertEquals(imageDTO.getIndex(), imageHistory.getIndex());
            assertEquals(imageDTO.getLangCode(), imageHistory.getLangCode());
            final LoopEntryRefDTO actualLoopEntryRef = imageHistory.getLoopEntryRef();
            assertEquals(loopEntryRef.getLoopIndex(), actualLoopEntryRef.getLoopIndex());
            assertEquals(loopEntryRef.getLoopEntryIndex(), actualLoopEntryRef.getLoopEntryIndex());
        });
    }

    @Test
    public void saveImage_When_ImageFileExists_Expect_GeneratedFileExists() throws IOException {
        final String filePath = "test-image.jpg";

        try(final DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePath), storageClient)){
            imageFile.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));

            final ImageDTO imageDTO = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePath);
                img.setFormat(Format.JPEG);
                img.setLangCode("en");
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
            });

            imageService.saveImage(imageDTO);

            final ImageDTO result = imageService.getImage(imageDTO);

            assertNotNull(result);

            final StoragePath croppedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(croppedImagePath));
            storageClient.delete(croppedImagePath, true);
        }
    }

    @Test
    public void saveImage_When_CroppingIsNotDefault_Expect_EqualCroppingAndGeneratedImageExist() throws IOException {
        final String filePath = "test-image.jpg";

        try(final DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePath), storageClient)) {
            imageFile.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));

            final ImageCropRegionDTO cropRegion = new ImageCropRegionDTO(10, 10, 20, 20);
            final ImageDTO imageDTO = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePath);
                img.setFormat(Format.JPEG);
                img.setLangCode("en");
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setCropRegion(cropRegion);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
            });

            imageService.saveImage(imageDTO);

            final ImageDTO result = imageService.getImage(imageDTO);

            assertNotNull(result);
            assertEquals(result.getCropRegion(), cropRegion);

            final StoragePath generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            storageClient.delete(generatedImagePath, true);
        }
    }

    @Test
    public void saveImage_When_CompressionIsTrueAndDifferentFormat_Expect_GeneratedImagesExistWithSmallerSizeOnlyForJPEG() throws IOException {
        final String filePath = "test-image.jpg";

        try(final DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePath), storageClient)) {
            imageFile.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));

            final ImageDTO imageDTO = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePath);
                img.setFormat(Format.JPEG);
                img.setLangCode("en");
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
                img.setCompress(false);
            });

            //JPEG
            imageService.saveImage(imageDTO);
            ImageDTO result = imageService.getImage(imageDTO);
            assertNotNull(result);

            StoragePath generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            long sizeWithoutCompress;
            try(StorageFile generatedFile = storageClient.getFile(generatedImagePath)){
                sizeWithoutCompress = generatedFile.size();
            }

            storageClient.delete(generatedImagePath, true);

            imageDTO.setCompress(true);
            imageService.saveImage(imageDTO);
            result = imageService.getImage(imageDTO);
            assertNotNull(result);

            generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            long sizeWithCompress;
            try(StorageFile generatedFile = storageClient.getFile(generatedImagePath)){
                sizeWithCompress = generatedFile.size();
            }
            storageClient.delete(generatedImagePath, true);

            assertTrue(sizeWithoutCompress > sizeWithCompress);

            //PNG
            imageDTO.setFormat(Format.PNG);
            imageService.saveImage(imageDTO);
            result = imageService.getImage(imageDTO);
            assertNotNull(result);

            generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            try(StorageFile generatedFile = storageClient.getFile(generatedImagePath)){
                sizeWithoutCompress = generatedFile.size();
            }

            storageClient.delete(generatedImagePath, true);

            imageDTO.setCompress(true);
            imageService.saveImage(imageDTO);
            result = imageService.getImage(imageDTO);
            assertNotNull(result);

            generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            try(StorageFile generatedFile = storageClient.getFile(generatedImagePath)){
                sizeWithCompress = generatedFile.size();
            }
            storageClient.delete(generatedImagePath, true);

            assertFalse(sizeWithoutCompress > sizeWithCompress);

            //GIF
            imageDTO.setFormat(Format.GIF);
            imageService.saveImage(imageDTO);
            result = imageService.getImage(imageDTO);
            assertNotNull(result);

            generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            try(StorageFile generatedFile = storageClient.getFile(generatedImagePath)){
                sizeWithoutCompress = generatedFile.size();
            }

            storageClient.delete(generatedImagePath, true);

            imageDTO.setCompress(true);
            imageService.saveImage(imageDTO);
            result = imageService.getImage(imageDTO);
            assertNotNull(result);

            generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            try(StorageFile generatedFile = storageClient.getFile(generatedImagePath)){
                sizeWithCompress = generatedFile.size();
            }
            storageClient.delete(generatedImagePath, true);

            assertFalse(sizeWithoutCompress > sizeWithCompress);
        }
    }


    @Test
    public void saveImage_When_ImagesHaveDifferentFormats_Expect_GeneratedImagesExist() throws IOException {
        final String filePath = "test-image.jpg";

        try (final DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePath), storageClient)) {
            imageFile.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));

            final ImageDTO imageDTO = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePath);
                img.setFormat(Format.JPEG);
                img.setLangCode("en");
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
                img.setCompress(true);
            });
            //JPEG
            imageService.saveImage(imageDTO);
            ImageDTO result = imageService.getImage(imageDTO);
            assertNotNull(result);

            StoragePath generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            storageClient.delete(generatedImagePath, true);

            //PNG
            imageDTO.setFormat(Format.PNG);
            imageService.saveImage(imageDTO);

            result = imageService.getImage(imageDTO);
            assertNotNull(result);

            generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            storageClient.delete(generatedImagePath, true);

            //GIF
            imageDTO.setFormat(Format.GIF);
            imageService.saveImage(imageDTO);

            result = imageService.getImage(imageDTO);
            assertNotNull(result);

            generatedImagePath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, result.getGeneratedFilename());
            assertTrue(storageClient.exists(generatedImagePath));
            storageClient.delete(generatedImagePath, true);

        }
    }

    @Test
    public void setAsWorkingVersion_Expect_CopyTextsFromSpecificVersionToWorkingVersion_And_GenerateImage_And_AddEntryToHistory() throws IOException {
        final Version newVersion1 = versionDataInitializer.createData(1, TEST_DOC_ID);

        final LanguageJPA enLanguage = languageRepository.findByCode("en");

        final String filePathWorkingVersion = "test-image.jpg";
        final String filePathVersion1 = "test-image1.png";

        final DeleteOnCloseStorageFile imageFileWorkingVersion = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePathWorkingVersion), storageClient);
        final DeleteOnCloseStorageFile imageFileVersion1 = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePathVersion1), storageClient);

        try(imageFileWorkingVersion; imageFileVersion1) {
            imageFileWorkingVersion.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));
            imageFileVersion1.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));

            final ImageDTO imageDTOVersion1 = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePathVersion1);
                img.setFormat(Format.PNG);
                img.setLangCode(enLanguage.getCode());
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
            });
            imageService.saveImage(imageDTOVersion1);

            imageService.createVersionedContent(workingVersion, newVersion1);

            final ImageDTO imageDTOWorkingVersion = new ImageDTO(imageDTOVersion1);
            imageDTOWorkingVersion.setPath(filePathWorkingVersion);
            imageDTOWorkingVersion.setFormat(Format.JPEG);
            imageDTOWorkingVersion.setName("img");
            imageService.saveImage(imageDTOWorkingVersion);

            final ImageDTO imageWorkingVersionBeforeReset =
                    imageJPAToImageDTO.apply(imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(workingVersion, enLanguage, TEST_IMAGE_INDEX));
            final ImageDTO imageVersion1BeforeReset =
                    imageJPAToImageDTO.apply(imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(newVersion1, enLanguage, TEST_IMAGE_INDEX));

            assertEquals(2, imageHistoryService.getAll(imageWorkingVersionBeforeReset).size());

            imageService.setAsWorkingVersion(newVersion1);

            final ImageDTO imageWorkingVersionAfterReset =
                    imageJPAToImageDTO.apply(imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(workingVersion, enLanguage, TEST_IMAGE_INDEX));
            final ImageDTO imageVersion1AfterReset =
                    imageJPAToImageDTO.apply(imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(newVersion1, enLanguage, TEST_IMAGE_INDEX));

            assertNotEquals(imageWorkingVersionBeforeReset, imageWorkingVersionAfterReset);
            assertEquals(imageVersion1AfterReset, imageWorkingVersionAfterReset);
            assertEquals(imageVersion1BeforeReset, imageVersion1AfterReset);

            assertEquals(3, imageHistoryService.getAll(imageWorkingVersionAfterReset).size());

            final StoragePath imageWorkingVersionPath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, imageDTOWorkingVersion.getGeneratedFilename());
            assertTrue(storageClient.exists(imageWorkingVersionPath));
            storageClient.delete(imageWorkingVersionPath, true);

            final StoragePath imageVersion1Path = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, imageDTOVersion1.getGeneratedFilename());
            assertTrue(storageClient.exists(imageVersion1Path));
            storageClient.delete(imageVersion1Path, true);
        }
    }

    @Test
    public void setAsWorkingVersion_When_NoImageOfSpecificVersion_Expect_WorkingVersionHasNoImage() throws IOException {
        final Version version1 = versionDataInitializer.createData(1, TEST_DOC_ID);

        final LanguageJPA enLanguage = languageRepository.findByCode("en");
        final String filePathWorkingVersion = "test-image.jpg";
        final File testImageFileWorkingVersion = testImageFile;

        final DeleteOnCloseStorageFile imageFileWorkingVersion = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePathWorkingVersion), storageClient);
        try(imageFileWorkingVersion) {
            imageFileWorkingVersion.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFileWorkingVersion)));

            final ImageDTO imageDTOWorkingVersion = Value.with(new ImageDTO(), img -> {
                img.setIndex(TEST_IMAGE_INDEX);
                img.setDocId(TEST_DOC_ID);
                img.setPath(filePathWorkingVersion);
                img.setFormat(Format.PNG);
                img.setLangCode(enLanguage.getCode());
                img.setName("img1");
                img.setWidth(100);
                img.setHeight(100);
                img.setAlternateText("");
                img.setLinkUrl("");
                img.setBorder(0);
                img.setAlign("");
                img.setLowResolutionUrl("");
                img.setTarget("");
                img.setType(0);
                img.setRotateAngle(0);
            });
            imageService.saveImage(imageDTOWorkingVersion);

            final ImageJPA imageWorkingVersionBeforeReset = imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(workingVersion, enLanguage, TEST_IMAGE_INDEX);
            assertNotNull(imageWorkingVersionBeforeReset);

            imageService.setAsWorkingVersion(version1);

            final ImageJPA imageWorkingVersionAfterReset = imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(workingVersion, enLanguage, TEST_IMAGE_INDEX);
            assertNull(imageWorkingVersionAfterReset);

            final StoragePath imageWorkingVersionPath = StoragePath.get(FILE, imagesPath, ImcmsConstants.IMAGE_GENERATED_FOLDER, imageDTOWorkingVersion.getGeneratedFilename());
            assertTrue(storageClient.exists(imageWorkingVersionPath));
            storageClient.delete(imageWorkingVersionPath, true);
        }
    }

    @Test
    public void createVersionedContent() {
        int testImageIndex = TEST_IMAGE_INDEX;
        final ImageJPA workingVersionImage = imageDataInitializer.createData(++testImageIndex, TEST_DOC_ID, VERSION_INDEX);

        final Version latestVersion = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);

        imageService.createVersionedContent(workingVersion, latestVersion);

        final List<ImageJPA> latestVersionImages = imageRepository.findByVersion(latestVersion);

        assertEquals(1, latestVersionImages.size());

        final ImageJPA image = latestVersionImages.get(0);

        assertEquals(workingVersionImage.getGeneratedFilename(), image.getGeneratedFilename());
        assertEquals(workingVersionImage.getName(), image.getName());
        assertEquals(workingVersionImage.getUrl(), image.getUrl());
        assertEquals(workingVersionImage.getAlign(), image.getAlign());
        assertEquals(workingVersionImage.getAlternateText(), image.getAlternateText());
        assertEquals(workingVersionImage.getLinkUrl(), image.getLinkUrl());
        assertEquals(workingVersionImage.getIndex(), image.getIndex());
        assertEquals(workingVersionImage.getHeight(), image.getHeight());
        assertEquals(workingVersionImage.getWidth(), image.getWidth());
        assertEquals(workingVersionImage.getLoopEntryRef(), image.getLoopEntryRef());
        assertEquals(workingVersionImage.getCropRegion(), image.getCropRegion());
        assertEquals(workingVersionImage.getLanguage(), image.getLanguage());
        assertEquals(workingVersionImage.getVersion().getDocId(), image.getVersion().getDocId());
    }

    @Test
    public void deleteByDocId_Expect_Deleted() {
        imageDataInitializer.cleanRepositories();
        assertTrue(imageRepository.findAll().isEmpty());

        final Version version = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);
        final Version newVersion = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);

        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, 1, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        loopDataInitializer.createData(loopDTO);

        final List<LanguageJPA> languages = languageRepository.findAll();
        final Version[] versions = {version, newVersion};
        final LoopEntryRefJPA[] loops = {loopEntryRef, null};

        for (int i = 0; i < 20; i++) {
            for (LanguageJPA language : languages) {
                for (Version vers : versions) {
                    for (LoopEntryRefJPA loopEntryRefJPA : loops) {
                        imageDataInitializer.generateImage(i, language, vers, loopEntryRefJPA);
                    }
                }
            }
        }

        assertFalse(imageRepository.findAll().isEmpty());

        imageRepository.deleteByDocId(TEST_DOC_ID);

        assertTrue(imageRepository.findAll().isEmpty());
    }

    @Test
    public void getPublicImageLinks_When_FewVersionExist_Expect_Found() {
        assertTrue(imageRepository.findAll().isEmpty());

        final Version workingVersion = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);
        final Version newVersion = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);
        final int loopIndex = 1;
        final int loopEntryIndex = 1;
        final LoopDTO loopDTO = new LoopDTO(
                TEST_DOC_ID, loopIndex, Collections.singletonList(LoopEntryDTO.createEnabled(loopEntryIndex))
        );
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopIndex, loopEntryIndex);
        loopDataInitializer.createData(loopDTO);

        final List<LanguageJPA> languages = languageRepository.findAll();
        final Version[] versions = {workingVersion, newVersion};
        final int imagesPerVersionPerLanguage = 20;
        final String testLinkUrl = "link_url";

        for (Version version : versions) {
            for (LanguageJPA language : languages) {
                IntStream.range(TEST_IMAGE_INDEX, TEST_IMAGE_INDEX + imagesPerVersionPerLanguage)
                        .forEach(index -> {
                            final ImageJPA image = new ImageJPA();
                            image.setIndex(index);
                            image.setLanguage(language);
                            image.setVersion(version);
                            image.setLoopEntryRef((index % 2 == 0) ? loopEntryRef : null);
                            image.setFormat(Format.JPEG);
                            image.setLinkUrl(testLinkUrl + index);
                            imageRepository.save(image);
                        });
                IntStream.range(TEST_IMAGE_INDEX + imagesPerVersionPerLanguage, TEST_IMAGE_INDEX + (2 * imagesPerVersionPerLanguage))
                        .forEach(index -> {
                            final ImageJPA image = new ImageJPA();
                            image.setIndex(index);
                            image.setLanguage(language);
                            image.setVersion(version);
                            image.setLoopEntryRef((index % 2 == 0) ? loopEntryRef : null);
                            image.setFormat(Format.JPEG);
                            image.setLinkUrl("");
                            imageRepository.save(image);
                        });
            }
        }

        assertFalse(imageRepository.findAll().isEmpty());

        for (LanguageJPA language : languages) {
            final Set<String> links = imageService.getPublicImageLinks(TEST_DOC_ID, language);

            links.forEach(s -> assertTrue(s.startsWith(testLinkUrl)));
        }
    }

    @Test
    public void getFreeIndexForImageInTextEditor_When_SomePositiveIndexesExist_Expect_MinusOne() {
        final int minIndex = TEST_IMAGE_INDEX;
        final LanguageJPA lang = languageRepository.findAll().get(0);

        IntStream.range(minIndex, minIndex + 10)
                .forEach(index -> imageDataInitializer.generateImage(index, lang, workingVersion, null));

        final ImageDTO imageDTO = new ImageDTO(null, TEST_DOC_ID, null, lang.getCode());
        imageDTO.setInText(true);

        final ImageDTO receivedImage = imageService.getImage(imageDTO);

        assertNotNull(receivedImage);
        assertEquals(-1, receivedImage.getIndex().intValue());
    }

    @Test
    public void getFreeIndexForImageInTextEditor_When_SomeNegativeIndexExist_Expect_MinReturned() {
        final int minIndex = TEST_IMAGE_INDEX - 10;
        final LanguageJPA lang = languageRepository.findAll().get(0);

        IntStream.range(minIndex + 1, TEST_IMAGE_INDEX + 10)
                .forEach(index -> imageDataInitializer.generateImage(index, lang, workingVersion, null));

        final ImageDTO imageDTO = new ImageDTO(null, TEST_DOC_ID, null, lang.getCode());
        imageDTO.setInText(true);

        final ImageDTO receivedImage = imageService.getImage(imageDTO);

        assertNotNull(receivedImage);
        assertEquals(minIndex, receivedImage.getIndex().intValue());
    }

    @Test
    public void deleteImage_When_InLoopAndAllLanguagesFlagIsSet_Expect_AllImagesWithDiffLangCodeAreDeleted() {
        deleteImageWhenAllAvailableLanguagesFlagIsSet(true);
    }

    @Test
    public void deleteImage_When_NotInLoopAndAllLanguagesFlagIsSet_Expect_AllImagesWithDiffLangCodeAreDeleted() {
        deleteImageWhenAllAvailableLanguagesFlagIsSet(false);
    }

    @Test
    public void deleteImage_When_InLoopAndAllLanguagesIsNotSet_Expect_AllImagesWithDiffLangCodeHaveFalseFlag() {
        deleteImageWhenAllAvailableLanguagesFlagIsNotSet(true);
    }

    @Test
    public void deleteImage_When_NotInLoopAndAllLanguagesIsNotSet_Expect_AllImagesWithDiffLangCodeHaveFalseFlag() {
        deleteImageWhenAllAvailableLanguagesFlagIsNotSet(false);
    }

    private void saveImageWhenAllAvailableLanguagesFlagIsSet(boolean inLoop) {
        final LoopEntryRefJPA loopEntryRef = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        image.setAllLanguages(true);

        final ImageDTO expected = imageJPAToImageDTO.apply(image);

        imageService.saveImage(expected);

        final List<Language> languages = languageService.getAvailableLanguages();

        assertEquals(2, languages.size());

        languages.forEach(language -> {
            final ImageDTO actual = imageService
                    .getImage(TEST_DOC_ID, TEST_IMAGE_INDEX, language.getCode(), loopEntryRef);

            expected.setLangCode(language.getCode());

            assertEquals(expected, actual);
        });
    }

    private void saveImageWhenAllAvailableLanguagesFlagIsNotSet(boolean inLoop) {
        final LoopEntryRefJPA loopEntryRef = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        final Version version = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);

        languageService.getAvailableLanguages().forEach(language -> {
            final ImageJPA image = imageDataInitializer
                    .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRef);

            image.setAllLanguages(true);
            imageRepository.save(image);
        });

        assertEquals(languageService.getAvailableLanguages().size(), imageRepository.findAll().size());
        imageRepository.findAll().forEach(image -> assertTrue(image.isAllLanguages()));

        final ImageJPA newImage = imageRepository.findAll().get(0);
        newImage.setAllLanguages(false);

        imageService.saveImage(imageJPAToImageDTO.apply(newImage));

        imageRepository.findAll().forEach(image -> assertFalse(image.isAllLanguages()));
    }

    private void deleteImageWhenAllAvailableLanguagesFlagIsSet(boolean inLoop) {
        final Version version = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);

        final LoopEntryRefJPA loopEntryRefJPA = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        languageService.getAvailableLanguages().forEach(language -> {
            final ImageJPA image = imageDataInitializer
                    .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRefJPA);

            image.setAllLanguages(true);
            imageRepository.save(image);
        });

        final List<ImageJPA> images = imageRepository.findAll();

        assertEquals(languageService.getAvailableLanguages().size(), images.size());

        imageService.deleteImage(imageJPAToImageDTO.apply(images.get(0)));

        assertEquals(0, imageRepository.findAll().size());
    }

    private void deleteImageWhenAllAvailableLanguagesFlagIsNotSet(boolean inLoop) {
        final Version version = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);

        final LoopEntryRefJPA loopEntryRefJPA = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        languageService.getAvailableLanguages().forEach(language -> {
            final ImageJPA image = imageDataInitializer
                    .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRefJPA);

            image.setAllLanguages(true);
            imageRepository.save(image);
        });

        final List<ImageJPA> images = imageRepository.findAll();

        assertEquals(languageService.getAvailableLanguages().size(), images.size());

        final ImageJPA image = images.get(0);
        image.setAllLanguages(false);

        imageRepository.save(image);
        imageService.deleteImage(imageJPAToImageDTO.apply(image));

        imageRepository.findAll()
                .forEach(imageJPA -> assertFalse(imageJPA.isAllLanguages()));
    }

    @Test
    public void exifData() throws ImageProcessingException, IOException {
        final String filePath = "test-image.jpg";

        try(final DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, filePath), storageClient)) {
            imageFile.put(new ByteArrayInputStream(FileUtils.readFileToByteArray(testImageFile)));

            final ImageSource imageSource = ImcmsImageUtils.getImageSource(filePath);

            if (!(imageSource instanceof NullImageSource)) {

                try (final InputStream inputStream = imageSource.getInputStreamSource().getInputStream()) {
                    Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

                    for (Directory directory : metadata.getDirectories()) {
                        for (Tag tag : directory.getTags()) {
                            System.out.println(tag);
                        }
                        for (String error : directory.getErrors()) {
                            System.err.println("ERROR: " + error);
                        }
                    }

                }

            }
        }
    }
}
