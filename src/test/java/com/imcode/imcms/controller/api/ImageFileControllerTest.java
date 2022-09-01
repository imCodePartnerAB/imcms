package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Roles;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@Transactional
public class ImageFileControllerTest extends AbstractControllerTest {

    @Autowired
    Function<ImageJPA, ImageDTO> imageJPAToImageDTO;

    @Autowired
    private ImageService imageService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;
    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    @Qualifier("imageStorageClient")
    private StorageClient storageClient;
    
    @Value("classpath:img1.jpg")
    private File testImageFile;

    @Value("${ImagePath}")
    private String imagesPath;

    @Override
    protected String controllerPath() {
        return "/images/files";
    }

    @BeforeEach
    public void setAdminAsCurrentUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now

        documentDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
        commonContentDataInitializer.cleanRepositories();
    }

    @AfterEach
    public void clearTestData() {
        documentDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
        commonContentDataInitializer.cleanRepositories();
    }

    @Test
    public void uploadImageFile_When_FolderIsNotSet_Expect_OkAndCorrectResponse() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockMultipartHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath()).file(file);

        final String jsonResponse = getJsonResponse(fileUploadRequestBuilder);
        final List<ImageFileDTO> imageFileDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFileDTO>>() {
        });

        assertNotNull(imageFileDTOS);
        assertEquals(1, imageFileDTOS.size());

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void uploadImageFile_When_FolderIsNotSetAndTwoFilesSent_Expect_OkAndCorrectResponse() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockMultipartHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath())
                .file(file)
                .file(file);

        final String jsonResponse = getJsonResponse(fileUploadRequestBuilder);
        final List<ImageFileDTO> imageFileDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFileDTO>>() {
        });

        assertNotNull(imageFileDTOS);
        assertEquals(2, imageFileDTOS.size());

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void uploadImageFile_When_FolderIsSet_Expect_OkAndCorrectResponse() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath())
                .file(file)
                .param("folder", separator + ImcmsConstants.IMAGE_GENERATED_FOLDER);

        final String jsonResponse = getJsonResponse(fileUploadRequestBuilder);
        final List<ImageFileDTO> imageFileDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFileDTO>>() {
        });

        assertNotNull(imageFileDTOS);
        assertEquals(1, imageFileDTOS.size());

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void uploadImageFile_When_FolderIsSetButNotExist_Expect_CorrectException() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath())
                .file(file)
                .param("folder", separator + "generatedddddd"); // non-existing folder

        performRequestBuilderExpectException(StorageFileNotFoundException.class, fileUploadRequestBuilder);
    }

    @Test
    public void deleteImage_When_UserIsAdminAndFileExist_Expect_EmptyListAndImageDeleted() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final String originalFilename = "img1-test.jpg";
        final MockMultipartFile file = new MockMultipartFile("files", originalFilename, null, imageFileBytes);
        final String folderName = separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath())
                .file(file)
                .param("folder", folderName);

        performRequestBuilderExpectedOk(fileUploadRequestBuilder);

        final String path = folderName + separator + originalFilename;

        try (DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, path), storageClient)) {
            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(path);

            assertTrue(imageFile.exists());

            final MockHttpServletRequestBuilder requestBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFileDTO));

            final String response = getJsonResponse(requestBuilder);

            assertEquals("[]", response);
            assertFalse(imageFile.exists());
        }
    }

    @Test
    public void deleteImage_When_PublishedOrWorkingDocumentImageReferencesImage_Expect_ListWithUsagesAndImageNotDeleted() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final String originalFilename = "img1-test.jpg";
        final MockMultipartFile file = new MockMultipartFile("files", originalFilename, null, imageFileBytes);
        final String folderName = separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;

        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath())
                .file(file)
                .param("folder", folderName);

        final String path = folderName + separator + originalFilename;

        try (DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, path), storageClient)) {
            assertFalse(imageFile.exists());
            performRequestBuilderExpectedOk(fileUploadRequestBuilder);
            assertTrue(imageFile.exists());

            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(path);

            assertTrue(imageFile.exists());

            final int tempDocId = documentDataInitializer.createData().getId();
            final int latestDocId = documentDataInitializer.createData().getId();

            final Version workingVersion = versionService.getDocumentWorkingVersion(tempDocId);

            versionService.create(latestDocId, 1);
            final Version latestVersion = versionService.getDocumentWorkingVersion(latestDocId);

            final ImageJPA imageLatest = imageDataInitializer.createData(1, latestVersion);
            final ImageJPA imageWorking = imageDataInitializer.createData(1, workingVersion);

            imageLatest.setName(originalFilename);
            imageLatest.setLinkUrl(separator + originalFilename);

            imageWorking.setName(originalFilename);
            imageWorking.setLinkUrl(separator + originalFilename);

            final ImageDTO imageDTOLatest = imageJPAToImageDTO.apply(imageLatest);
            final ImageDTO imageDTOWorking = imageJPAToImageDTO.apply(imageWorking);

            imageService.saveImage(imageDTOLatest);
            imageService.saveImage(imageDTOWorking);

            final MockHttpServletRequestBuilder requestLatestBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageDTOLatest));

            final MockHttpServletRequestBuilder requestWorkingBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageDTOWorking));

            final String jsonResponseLatest = getJsonResponseWithExpectedStatus(requestLatestBuilder, METHOD_NOT_ALLOWED.value());
            final List<ImageFileUsageDTO> imageFileUsagesDTOSLatest = fromJson(jsonResponseLatest, new TypeReference<List<ImageFileUsageDTO>>() {
            });

            assertNotNull(imageFileUsagesDTOSLatest);
            assertEquals(2, imageFileUsagesDTOSLatest.size());


            final String jsonResponseWorking = getJsonResponseWithExpectedStatus(requestWorkingBuilder, METHOD_NOT_ALLOWED.value());
            final List<ImageFileUsageDTO> imageFileUsagesDTOSWorking = fromJson(jsonResponseWorking, new TypeReference<List<ImageFileUsageDTO>>() {
            });

            assertNotNull(imageFileUsagesDTOSWorking);
            assertEquals(2, imageFileUsagesDTOSWorking.size());
        }
    }

    @Test
    public void deleteImage_ImageNotReferencedAtWorkingPublishedDocuments_Expect_EmptyListAndImageDeleted() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final String originalFilename = "img1-test.jpg";
        final MockMultipartFile file = new MockMultipartFile("files", originalFilename, null, imageFileBytes);
        final String folderName = separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;

        final MockHttpServletRequestBuilder fileUploadRequestBuilder = multipart(controllerPath())
                .file(file)
                .param("folder", folderName);

        final String path = folderName + separator + originalFilename;

        try (DeleteOnCloseStorageFile imageFile = new DeleteOnCloseStorageFile(StoragePath.get(FILE, imagesPath, path), storageClient)) {
            assertFalse(imageFile.exists());
            performRequestBuilderExpectedOk(fileUploadRequestBuilder);
            assertTrue(imageFile.exists());

            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(path);

            assertTrue(imageFile.exists());

            final MockHttpServletRequestBuilder requestBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFileDTO));

            final String response = getJsonResponse(requestBuilder);

            assertEquals("[]", response);
            assertFalse(imageFile.exists());
        }
    }

    private void deleteFile(ImageFileDTO imageFileDTO) {
        final StoragePath deleteMe = StoragePath.get(FILE, imagesPath, imageFileDTO.getPath());
        storageClient.delete(deleteMe, true);
    }

}
