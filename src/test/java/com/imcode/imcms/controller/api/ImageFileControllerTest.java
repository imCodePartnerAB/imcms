package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.exception.ImageReferenceException;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Transactional
public class ImageFileControllerTest extends AbstractControllerTest {

    @Autowired
    Function<Image, ImageDTO> imageToImageDTO;
    @Autowired
    private ImageService imageService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Value("classpath:img1.jpg")
    private File testImageFile;

    @Value("${ImagePath}")
    private File imagesPath;

    @Override
    protected String controllerPath() {
        return "/images/files";
    }

    @Before
    public void setAdminAsCurrentUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now

        documentDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
    }

    @After
    public void clearTestData() {
        documentDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
    }

    @Test
    public void uploadImageFile_When_FolderIsNotSet_Expect_OkAndCorrectResponse() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockMultipartHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath()).file(file);

        final String jsonResponse = getJsonResponse(fileUploadRequestBuilder);
        final List<ImageFileDTO> imageFileDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFileDTO>>() {
        });

        assertNotNull(imageFileDTOS);
        assertEquals(imageFileDTOS.size(), 1);

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void uploadImageFile_When_FolderIsNotSetAndTwoFilesSent_Expect_OkAndCorrectResponse() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockMultipartHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .file(file);

        final String jsonResponse = getJsonResponse(fileUploadRequestBuilder);
        final List<ImageFileDTO> imageFileDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFileDTO>>() {
        });

        assertNotNull(imageFileDTOS);
        assertEquals(imageFileDTOS.size(), 2);

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void uploadImageFile_When_UserIsNotAdmin_Expect_CorrectException() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        user.addRoleId(Roles.USER.getId());
        Imcms.setUser(user); // means current user is not admin now

        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockMultipartHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath()).file(file);

        performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, fileUploadRequestBuilder);
    }

    @Test
    public void uploadImageFile_When_FolderIsSet_Expect_OkAndCorrectResponse() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .param("folder", File.separator + ImcmsConstants.IMAGE_GENERATED_FOLDER);

        final String jsonResponse = getJsonResponse(fileUploadRequestBuilder);
        final List<ImageFileDTO> imageFileDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFileDTO>>() {
        });

        assertNotNull(imageFileDTOS);
        assertEquals(imageFileDTOS.size(), 1);

        imageFileDTOS.forEach(this::deleteFile);
    }

    @Test
    public void uploadImageFile_When_FolderIsSetButNotExist_Expect_CorrectException() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .param("folder", File.separator + "generatedddddd"); // non-existing folder

        performRequestBuilderExpectException(FolderNotExistException.class, fileUploadRequestBuilder);
    }

    @Test
    public void deleteImage_When_UserIsAdminAndFileExist_Expect_True() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final String originalFilename = "img1-test.jpg";
        final MockMultipartFile file = new MockMultipartFile("files", originalFilename, null, imageFileBytes);
        final String folderName = File.separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .param("folder", folderName);

        performRequestBuilderExpectedOk(fileUploadRequestBuilder);

        final File imageFile = new File(imagesPath, folderName + File.separator + originalFilename);

        try {
            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(folderName + File.separator + originalFilename);

            assertTrue(imageFile.exists());

            final MockHttpServletRequestBuilder requestBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFileDTO));

            final String response = getJsonResponse(requestBuilder);

            assertEquals(response, "true");
            assertFalse(imageFile.exists());

        } finally {
            if (imageFile.exists()) {
                assertTrue(FileUtility.forceDelete(imageFile));
            }
        }

    }

    @Test
    public void deleteImage_When_UserIsAdminAndFileNotExist_Expect_CorrectException() throws Exception {
        final String originalFilename = "img1-test.jpg";
        final String folderName = File.separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;
        final File imageFile = new File(imagesPath, folderName + File.separator + originalFilename);
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(folderName + File.separator + originalFilename);

        assertFalse(imageFile.exists());

        final MockHttpServletRequestBuilder requestBuilder = delete(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(imageFileDTO));

        try {
            getJsonResponse(requestBuilder); // exception here
            fail("Expected exception wasn't thrown!");

        } catch (FileNotFoundException ignore) {
            // expected exception
        }

        assertFalse(imageFile.exists());
    }

    @Test
    public void deleteImage_When_UserIsNotAdmin_Expect_CorrectExceptionAndFileNotDeleted() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final String originalFilename = "img1-test.jpg";
        final MockMultipartFile file = new MockMultipartFile("files", originalFilename, null, imageFileBytes);
        final String folderName = File.separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;
        final File imageFile = new File(imagesPath, folderName + File.separator + originalFilename);

        final MockHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .param("folder", folderName);

        try {
            assertFalse(imageFile.exists());
            performRequestBuilderExpectedOk(fileUploadRequestBuilder);
            assertTrue(imageFile.exists());

            final UserDomainObject user = new UserDomainObject(2);
            user.addRoleId(Roles.USER.getId());
            Imcms.setUser(user); // means current user is not admin now

            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(folderName + File.separator + originalFilename);

            final MockHttpServletRequestBuilder requestBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFileDTO));

            assertTrue(imageFile.exists());
            performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilder);

        } finally {
            if (imageFile.exists()) {
                assertTrue(FileUtility.forceDelete(imageFile));
            }
        }
    }

    @Test
    public void deleteImage_When_PublishedOrWorkingDocumentImageReferencesImage_Expect_CorrectExceptionAndImageNotDeleted() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final String originalFilename = "img1-test.jpg";
        final MockMultipartFile file = new MockMultipartFile("files", originalFilename, null, imageFileBytes);
        final String folderName = File.separator + ImcmsConstants.IMAGE_GENERATED_FOLDER;
        final File imageFile = new File(imagesPath, folderName + File.separator + originalFilename);

        final MockHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .param("folder", folderName);

        try {
            assertFalse(imageFile.exists());
            performRequestBuilderExpectedOk(fileUploadRequestBuilder);
            assertTrue(imageFile.exists());

            final ImageFileDTO imageFileDTO = new ImageFileDTO();
            imageFileDTO.setPath(folderName + File.separator + originalFilename);

            assertTrue(imageFile.exists());

            final int tempDocId = documentDataInitializer.createData().getId();
            final int latestDocId = documentDataInitializer.createData().getId();

            final Version workingVersion = versionService.getDocumentWorkingVersion(tempDocId);

            versionService.create(latestDocId, 1);
            final Version latestVersion = versionService.getDocumentWorkingVersion(latestDocId);

            final Image imageLatest = imageDataInitializer.createData(1, latestVersion);
            final Image imageWorking = imageDataInitializer.createData(1, workingVersion);

            imageLatest.setName(originalFilename);
            imageLatest.setLinkUrl(File.separator + originalFilename);

            imageWorking.setName(originalFilename);
            imageWorking.setLinkUrl(File.separator + originalFilename);

            final ImageDTO imageDTOLatest = imageToImageDTO.apply(imageLatest);
            final ImageDTO imageDTOWorking = imageToImageDTO.apply(imageWorking);

            imageService.saveImage(imageDTOLatest);
            imageService.saveImage(imageDTOWorking);

            final MockHttpServletRequestBuilder requestLatestBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageDTOLatest));

            final MockHttpServletRequestBuilder requestWorkingBuilder = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageDTOWorking));

            performRequestBuilderExpectException(ImageReferenceException.class, requestLatestBuilder);
            performRequestBuilderExpectException(ImageReferenceException.class, requestWorkingBuilder);
        } finally {
            if (imageFile.exists()) {
                assertTrue(FileUtility.forceDelete(imageFile));
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
