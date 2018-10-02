package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Meta;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.io.File.separator;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ImageFileServiceTest {

    @Autowired
    Function<Image, ImageDTO> imageToImageDTO;

    @Autowired
    private ImageFileService imageFileService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;
    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Value("classpath:img1.jpg")
    private File testImageFile;

    @Value("${ImagePath}")
    private File imagesPath;

    @Before
    public void prepareData() {
        imageDataInitializer.cleanRepositories();
        commonContentDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @After
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

    @Test(expected = FolderNotExistException.class)
    public void saveNewImageFiles_When_TwoFilesSentAndFolderNotExistButIsSet_Expect_CorrectException() throws IOException {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);

        final MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, imageFileBytes);
        final List<MultipartFile> files = Arrays.asList(file, file);
        final String nonExistingFolder = separator + "generateddddd";

        imageFileService.saveNewImageFiles(nonExistingFolder, files); // exception should be thrown here
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
        final File createdImageFile = new File(imagesPath, imageFileDTO.getPath());

        assertTrue(createdImageFile.exists());
        assertTrue(imageFileService.deleteImage(imageFileDTO).isEmpty());
        assertFalse(createdImageFile.exists());
    }

    @Test(expected = FileNotFoundException.class)
    public void deleteImage_When_ImageNotExist_Expect_CorrectException() throws IOException {
        final String nonExistingFileName = "not_existing_image_i_hope.jpg";
        final File nonExistingImageFile = new File(imagesPath, nonExistingFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(nonExistingFileName);

        assertFalse(nonExistingImageFile.exists());
        imageFileService.deleteImage(imageFileDTO); // exception expected here
    }


    @Test
    public void deleteImage_When_ImageUsedAtWorkingSingleDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final int workingDocId = documentDataInitializer.createData().getId();

            final Version workingVersion = versionService.getDocumentWorkingVersion(workingDocId);

            final Image image = imageDataInitializer.createData(1, workingVersion);

            image.setName(testImageFileName);
            image.setUrl(testImageFileName);

            final ImageDTO imageDTO = imageToImageDTO.apply(image);

            imageService.saveImage(imageDTO);

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        } finally {
            assertTrue(testImageFile.delete());
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAtLatestSingleDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final int latestDocId = documentDataInitializer.createData().getId();

            final Version latestVersion = versionService.getLatestVersion(latestDocId);

            final Image image = imageDataInitializer.createData(1, latestVersion);

            image.setName(testImageFileName);
            image.setUrl(testImageFileName);

            final ImageDTO imageDTO = imageToImageDTO.apply(image);

            imageService.saveImage(imageDTO);

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        } finally {
            assertTrue(testImageFile.delete());
        }
    }

    @Test
    public void deleteImage_When_ImageAtSubdirectoryUsedAtLatestSingleDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final String testSubDirectoryName = "subdiretory";
        final File testSubdirectory = new File(imagesPath, testSubDirectoryName);
        final File testImageFile = new File(testSubdirectory, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(File.separator + testSubDirectoryName + File.separator + testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            assertTrue(testSubdirectory.mkdir());
            assertTrue(testImageFile.createNewFile());
            assertTrue(testImageFile.exists());

            final int latestDocId = documentDataInitializer.createData().getId();
            versionService.create(latestDocId, 1);
            final Version latestVersion = versionService.getLatestVersion(latestDocId);
            final Image image = imageDataInitializer.createData(1, latestVersion);

            image.setName(testImageFileName);
            image.setUrl(testSubDirectoryName + File.separator + testImageFileName);

            final ImageDTO imageDTO = imageToImageDTO.apply(image);

            imageService.saveImage(imageDTO);
            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());

            assertTrue(testImageFile.exists());

        } finally {
            FileUtils.deleteDirectory(testSubdirectory);
        }
    }

    @Test
    public void deleteImage_When_ImageAtSubdirectoryUsedAtLatestSingleDocument_Expect_TrueAndFileDeleted() throws IOException {
        final String testImageFileName = "test.jpg";
        final String testSubDirectoryName = "subdiretory";
        final File testSubdirectory = new File(imagesPath, testSubDirectoryName);
        final File testImageFile = new File(testSubdirectory, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testSubDirectoryName + File.separator + testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            assertTrue(testSubdirectory.mkdir());
            assertTrue(testImageFile.createNewFile());
            assertTrue(testImageFile.exists());
            imageFileService.deleteImage(imageFileDTO);
            assertFalse(testImageFile.exists());
        } finally {
            FileUtils.deleteDirectory(testSubdirectory);
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAtWorkingDocumentAndPublishedDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final int latestDocId = documentDataInitializer.createData().getId();
            final int workingDocId = documentDataInitializer.createData().getId();

            final Version workingVersion = versionService.getDocumentWorkingVersion(workingDocId);

            versionService.create(latestDocId, 1);
            final Version latestVersion = versionService.create(latestDocId, 1);

            final Image imageLatest = imageDataInitializer.createData(1, latestVersion);
            final Image imageWorking = imageDataInitializer.createData(1, workingVersion);

            imageLatest.setName(testImageFileName);
            imageLatest.setUrl(testImageFileName);

            imageWorking.setName(testImageFileName);
            imageWorking.setUrl(testImageFileName);

            final ImageDTO imageDTOLatest = imageToImageDTO.apply(imageLatest);
            final ImageDTO imageDTOWorking = imageToImageDTO.apply(imageWorking);

            imageService.saveImage(imageDTOLatest);
            imageService.saveImage(imageDTOWorking);

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        } finally {
            assertTrue(testImageFile.delete());
        }
    }

    @Test
    public void deleteImage_When_ImageUsedNotAtPublicOrWorkingDocument_Expect_TrueAndImageDeleted() throws IOException {
        final String testImageFileName = "test.jpg";
        final String test2ImageFileName = "test2.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);

        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        final ImageFileDTO imageFile2DTO = new ImageFileDTO();
        imageFile2DTO.setPath(test2ImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final int tempDocId = documentDataInitializer.createData().getId();
            final Version intermediateVersion = versionService.create(tempDocId, 1);

            final Image imageIntermediate = imageDataInitializer.createData(1, testImageFileName, testImageFileName, intermediateVersion);

            final Version latestVersion = versionService.create(tempDocId, 1);
            final Image imageLatest = imageDataInitializer.createData(1, testImageFileName, test2ImageFileName, latestVersion);

            imageFileService.deleteImage(imageFileDTO);

            assertFalse(testImageFile.exists());

        } finally {
            if (testImageFile.exists()) {
                testImageFile.delete();
            }
        }
    }

    @Test
    public void deleteImage_When_ImageNotUsedAtAnyLatestAndWorkingDocument_Expect_TrueAndFileDeleted() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            imageFileService.deleteImage(imageFileDTO);

            assertFalse(testImageFile.exists());
        } finally {
            if (testImageFile.exists()) {
                testImageFile.delete();
            }
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAsMenuImageAtPublishedOrWorkingDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final DocumentDTO tempDocumentDTO = documentDataInitializer.createData(Meta.PublicationStatus.APPROVED);

            tempDocumentDTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(File.separator + imageFileDTO.getPath()));
            commonContentService.save(tempDocumentDTO.getId(), tempDocumentDTO.getCommonContents());

            List<CommonContent> latestCommonContent = commonContentDataInitializer
                    .createData(tempDocumentDTO.getId(), tempDocumentDTO.getLatestVersion().getId() + 1);
            latestCommonContent
                    .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));


            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());

            assertTrue(testImageFile.exists());

        } finally {
            if (testImageFile.exists()) {
                testImageFile.delete();
            }
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAsMenuImageAtWorkingDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final DocumentDTO tempDocumentDTO = documentDataInitializer.createData();

            tempDocumentDTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));
            commonContentService.save(tempDocumentDTO.getId(), tempDocumentDTO.getCommonContents());

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        } finally {
            if (testImageFile.exists()) {
                testImageFile.delete();
            }
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAsMenuImageAtPublishedDocument_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final DocumentDTO tempDocumentDTO = documentDataInitializer.createData(Meta.PublicationStatus.APPROVED);

            List<CommonContent> latestCommonContent = commonContentDataInitializer
                    .createData(tempDocumentDTO.getId(), tempDocumentDTO.getLatestVersion().getId() + 1);
            latestCommonContent
                    .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());

            assertTrue(testImageFile.exists());

        } finally {
            if (testImageFile.exists()) {
                testImageFile.delete();
            }
        }
    }

    @Test
    public void deleteImage_When_ImageUsedAsMenuImageAtSeveralWorkingDocuments_Expect_ListWithUsages() throws IOException {
        final String testImageFileName = "test.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final DocumentDTO tempDocument1DTO = documentDataInitializer.createData();
            tempDocument1DTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));
            commonContentService.save(tempDocument1DTO.getId(), tempDocument1DTO.getCommonContents());

            final DocumentDTO tempDocument2DTO = documentDataInitializer.createData();
            tempDocument2DTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));
            commonContentService.save(tempDocument2DTO.getId(), tempDocument2DTO.getCommonContents());

            final DocumentDTO tempDocument3DTO = documentDataInitializer.createData();
            tempDocument3DTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));
            commonContentService.save(tempDocument3DTO.getId(), tempDocument3DTO.getCommonContents());

            assertFalse(imageFileService.deleteImage(imageFileDTO).isEmpty());
            assertTrue(testImageFile.exists());
        } finally {
            if (testImageFile.exists()) {
                testImageFile.delete();
            }
        }
    }

    @Test
    public void deleteImage_When_ImageNotUsedAsMenuImage_Expect_TrueAndFileDeleted() throws IOException {
        final String testImageFileName = "test.jpg";
        final String stubImageFileName = "testStub.jpg";
        final File testImageFile = new File(imagesPath, testImageFileName);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try {
            assertFalse(testImageFile.exists());
            testImageFile.createNewFile();
            assertTrue(testImageFile.exists());

            final DocumentDTO tempDocumentDTO = documentDataInitializer.createData(Meta.PublicationStatus.APPROVED);

            tempDocumentDTO.getCommonContents()
                    .forEach(commonContent -> commonContent.setMenuImageURL(File.separator + stubImageFileName));
            commonContentService.save(tempDocumentDTO.getId(), tempDocumentDTO.getCommonContents());

            List<CommonContent> latestCommonContent = commonContentDataInitializer
                    .createData(tempDocumentDTO.getId(), tempDocumentDTO.getLatestVersion().getId() + 1);

            latestCommonContent.forEach(commonContent -> commonContent.setMenuImageURL(File.separator + stubImageFileName));

            imageFileService.deleteImage(imageFileDTO);

            assertFalse(testImageFile.exists());

        } finally {
            if (testImageFile.exists()) {
                testImageFile.delete();
            }
        }
    }

    private void deleteFile(ImageFileDTO imageFileDTO) {
        final File deleteMe = new File(imagesPath, imageFileDTO.getPath());

        try {
            assertTrue(FileUtility.forceDelete(deleteMe));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
