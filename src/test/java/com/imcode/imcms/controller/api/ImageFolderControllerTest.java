package com.imcode.imcms.controller.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class ImageFolderControllerTest extends AbstractControllerTest {

    @Value("${ImagePath}")
    private File imagesPath;

    @Override
    protected String controllerPath() {
        return "/images/folders";
    }

    @Before
    public void setAdminAsCurrentUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now
    }

    @Test
    public void getImageFolder_Expect_Ok() throws Exception {
        performRequestBuilderExpectedOk(MockMvcRequestBuilders.get(controllerPath()));
    }

    @Test
    public void createNewImageFolder_When_FolderNotExistBefore_Expect_FolderCreatedAndIsDirectoryAndReadableAndThenRemoved() throws Exception {
        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testFolderName);

        assertFalse(folder.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(folder.exists());
        assertTrue(folder.isDirectory());
        assertTrue(folder.canRead());
        assertTrue(folder.delete());
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() throws Exception {
        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testFolderName);

        assertFalse(folder.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(folder.exists());

        performRequestBuilderExpectException(FolderAlreadyExistException.class, requestBuilder);

        assertTrue(folder.delete());
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() throws Exception {
        final String testFolderName0 = "test_folder_name";
        final String testFolderName1 = testFolderName0 + "/nested1";
        final String testFolderName2 = testFolderName1 + "/nested2";
        final String testFolderName3 = testFolderName1 + "/nested3";

        final File folder0 = new File(imagesPath, testFolderName0);
        final File folder1 = new File(imagesPath, testFolderName1);
        final File folder2 = new File(imagesPath, testFolderName2);
        final File folder3 = new File(imagesPath, testFolderName3);

        final MockHttpServletRequestBuilder requestBuilder0 = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testFolderName0);

        final MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testFolderName1);

        final MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testFolderName2);

        final MockHttpServletRequestBuilder requestBuilder3 = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testFolderName3);

        assertFalse(folder0.exists());
        assertFalse(folder1.exists());
        assertFalse(folder2.exists());
        assertFalse(folder3.exists());

        final String jsonResponse0 = getJsonResponse(requestBuilder0);
        final String jsonResponse1 = getJsonResponse(requestBuilder1);
        final String jsonResponse2 = getJsonResponse(requestBuilder2);
        final String jsonResponse3 = getJsonResponse(requestBuilder3);

        assertTrue(Boolean.parseBoolean(jsonResponse0));
        assertTrue(Boolean.parseBoolean(jsonResponse1));
        assertTrue(Boolean.parseBoolean(jsonResponse2));
        assertTrue(Boolean.parseBoolean(jsonResponse3));

        assertTrue(folder0.exists());
        assertTrue(folder1.exists());
        assertTrue(folder2.exists());
        assertTrue(folder3.exists());

        assertTrue(folder0.isDirectory());
        assertTrue(folder1.isDirectory());
        assertTrue(folder2.isDirectory());
        assertTrue(folder3.isDirectory());

        assertTrue(folder0.canRead());
        assertTrue(folder1.canRead());
        assertTrue(folder2.canRead());
        assertTrue(folder3.canRead());

        assertTrue(folder3.delete());
        assertTrue(folder2.delete());
        assertTrue(folder1.delete());
        assertTrue(folder0.delete());
    }

    @Test
    public void createNewImageFolder_When_UserIsNotAdmin_Expected_CorrectExceptionAndFolderNotCreated() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        user.addRoleId(RoleId.USERS);
        Imcms.setUser(user); // means current user is not admin now

        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testFolderName);

        assertFalse(folder.exists());
        performRequestBuilderExpectException(IllegalAccessException.class, requestBuilder);
        assertFalse(folder.exists());
    }
}
