package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class ImageFileControllerTest extends AbstractControllerTest {

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
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now
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

        final File imagesPathFolder = imagesPath.getParentFile();

        imageFileDTOS.forEach(imageFileDTO -> assertTrue(new File(imagesPathFolder, imageFileDTO.getPath()).delete()));
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

        final File imagesPathFolder = imagesPath.getParentFile();

        imageFileDTOS.forEach(imageFileDTO -> assertTrue(new File(imagesPathFolder, imageFileDTO.getPath()).delete()));
    }

    @Test
    public void uploadImageFile_When_UserIsNotAdmin_Expect_CorrectException() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        user.addRoleId(RoleId.USERS);
        Imcms.setUser(user); // means current user is not admin now

        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockMultipartHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath()).file(file);

        performRequestBuilderExpectException(IllegalAccessException.class, fileUploadRequestBuilder);
    }

    @Test
    public void uploadImageFile_When_FolderIsSet_Expect_OkAndCorrectResponse() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .param("folder", "/generated");

        final String jsonResponse = getJsonResponse(fileUploadRequestBuilder);
        final List<ImageFileDTO> imageFileDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFileDTO>>() {
        });

        assertNotNull(imageFileDTOS);
        assertEquals(imageFileDTOS.size(), 1);

        final File imagesPathFolder = imagesPath.getParentFile();

        imageFileDTOS.forEach(imageFileDTO -> assertTrue(new File(imagesPathFolder, imageFileDTO.getPath()).delete()));
    }

    @Test
    public void uploadImageFile_When_FolderIsSetButNotExist_Expect_CorrectException() throws Exception {
        final byte[] imageFileBytes = FileUtils.readFileToByteArray(testImageFile);
        final MockMultipartFile file = new MockMultipartFile("files", "img1-test.jpg", null, imageFileBytes);
        final MockHttpServletRequestBuilder fileUploadRequestBuilder = fileUpload(controllerPath())
                .file(file)
                .param("folder", "/generatedddddd"); // non-existing folder

        try {
            performRequestBuilderExpectedOk(fileUploadRequestBuilder); // exception should be thrown here
            fail("Expected exception wasn't thrown");

        } catch (IOException e) {
            assertEquals(e.getMessage(), "Folder not exist! Folder creation is another service job.");
        }
    }

}
