package com.imcode.imcms.controller.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
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

import static java.io.File.separator;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
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
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(folder.exists());
        assertTrue(folder.isDirectory());
        assertTrue(folder.canRead());
        assertTrue(FileUtility.forceDelete(folder));
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() throws Exception {
        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(folder.exists());

        performRequestBuilderExpectException(FolderAlreadyExistException.class, requestBuilder);

        assertTrue(FileUtility.forceDelete(folder));
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() throws Exception {
        final String testFolderName0 = "test_folder_name";
        final String testFolderName1 = testFolderName0 + separator + "nested1";
        final String testFolderName2 = testFolderName1 + separator + "nested2";
        final String testFolderName3 = testFolderName1 + separator + "nested3";

        final File folder0 = new File(imagesPath, testFolderName0);
        final File folder1 = new File(imagesPath, testFolderName1);
        final File folder2 = new File(imagesPath, testFolderName2);
        final File folder3 = new File(imagesPath, testFolderName3);

        final MockHttpServletRequestBuilder requestBuilder0 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName0));
        final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName1));
        final MockHttpServletRequestBuilder requestBuilder2 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName2));
        final MockHttpServletRequestBuilder requestBuilder3 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName3));

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

        assertTrue(FileUtility.forceDelete(folder3));
        assertTrue(FileUtility.forceDelete(folder2));
        assertTrue(FileUtility.forceDelete(folder1));
        assertTrue(FileUtility.forceDelete(folder0));
    }

    @Test
    public void createNewImageFolder_When_UserIsNotAdmin_Expected_CorrectExceptionAndFolderNotCreated() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        user.addRoleId(RoleId.USERS);
        Imcms.setUser(user); // means current user is not admin now

        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());
        performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilder);
        assertFalse(folder.exists());
    }

    @Test
    public void renameImageFolder_When_FolderExistAndUserIsAdmin_Expect_True() throws Exception {

        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String testFolderNewName = "test_folder_new_name";
        imageFolderDTO.setName(testFolderNewName);
        final File newFolder = new File(imagesPath, testFolderNewName);

        assertFalse(newFolder.exists());

        try {
            final String jsonPostResponse = getJsonResponse(requestBuilderPost);

            assertTrue(Boolean.parseBoolean(jsonPostResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());

            final MockHttpServletRequestBuilder requestBuilderPatch = MockMvcRequestBuilders.patch(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String jsonPatchResponse = getJsonResponse(requestBuilderPatch);

            assertTrue(Boolean.parseBoolean(jsonPatchResponse));
            assertFalse(folder.exists());
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());
            assertTrue(FileUtility.forceDelete(newFolder));

        } finally {
            if (folder.exists()) assertTrue(FileUtility.forceDelete(folder));
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
        }

    }

    @Test
    public void renameImageFolder_When_FolderIsNestedAndExistAndUserIsAdmin_Expect_True() throws Exception {

        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String testNestedFolderName = "nested_folder_name";
        final File nestedFolder = new File(folder, testNestedFolderName);
        final String path = separator + testFolderName + separator + testNestedFolderName;
        final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(testNestedFolderName, path);
        final MockHttpServletRequestBuilder requestBuilderPostNested = getPostRequestBuilderWithContent(imageNestedFolderDTO);

        assertFalse(nestedFolder.exists());

        final String testNestedFolderNewName = "test_folder_new_name";
        imageNestedFolderDTO.setName(testNestedFolderNewName);
        final File newNestedFolder = new File(folder, testNestedFolderNewName);

        assertFalse(newNestedFolder.exists());

        try {
            final String jsonPostResponse = getJsonResponse(requestBuilderPost);

            assertTrue(Boolean.parseBoolean(jsonPostResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());

            final String jsonPostNestedResponse = getJsonResponse(requestBuilderPostNested);

            assertTrue(Boolean.parseBoolean(jsonPostNestedResponse));
            assertTrue(nestedFolder.exists());
            assertTrue(nestedFolder.isDirectory());
            assertTrue(nestedFolder.canRead());

            final MockHttpServletRequestBuilder requestBuilderPatch = MockMvcRequestBuilders.patch(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageNestedFolderDTO));

            final String jsonPatchResponse = getJsonResponse(requestBuilderPatch);

            assertTrue(Boolean.parseBoolean(jsonPatchResponse));
            assertFalse(nestedFolder.exists());
            assertTrue(newNestedFolder.exists());
            assertTrue(newNestedFolder.isDirectory());
            assertTrue(newNestedFolder.canRead());
            assertTrue(FileUtility.forceDelete(newNestedFolder));
            assertTrue(folder.delete());

        } finally {
            if (folder.exists()) assertTrue(FileUtility.forceDelete(folder));
            if (nestedFolder.exists()) assertTrue(FileUtility.forceDelete(nestedFolder));
            if (newNestedFolder.exists()) assertTrue(FileUtility.forceDelete(newNestedFolder));
        }

    }

    @Test
    public void renameImageFolder_When_FolderNotExistAndUserIsAdmin_Expect_CorrectException() throws Exception {

        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);

        assertFalse(folder.exists());

        final String testFolderNewName = "test_folder_new_name";
        imageFolderDTO.setName(testFolderNewName);
        final File newFolder = new File(imagesPath, testFolderNewName);

        assertFalse(newFolder.exists());

        final MockHttpServletRequestBuilder requestBuilderPatch = MockMvcRequestBuilders.patch(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(imageFolderDTO));

        performRequestBuilderExpectException(FolderNotExistException.class, requestBuilderPatch);
        assertFalse(newFolder.exists());

    }

    @Test
    public void renameImageFolder_When_FolderExistWithSuchNameAndUserIsAdmin_Expect_CorrectException() throws Exception {

        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String testFolderName1 = "test_folder_name1";
        final File folder1 = new File(imagesPath, testFolderName1);
        final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(testFolderName1);
        final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(imageFolderDTO1);

        assertFalse(folder1.exists());

        final String testRenamedFolderNewName = testFolderName;
        imageFolderDTO1.setName(testRenamedFolderNewName);
        final File renamedFolder = new File(folder, testRenamedFolderNewName);

        assertFalse(renamedFolder.exists());


        try {

            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(folder.exists());

            final String jsonResponse1 = getJsonResponse(requestBuilder1);

            assertTrue(Boolean.parseBoolean(jsonResponse1));
            assertTrue(folder1.exists());

            final MockHttpServletRequestBuilder requestBuilderPatch = MockMvcRequestBuilders.patch(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO1));

            performRequestBuilderExpectException(FolderAlreadyExistException.class, requestBuilderPatch);
            assertFalse(renamedFolder.exists());

        } finally {
            if (folder.exists()) assertTrue(FileUtility.forceDelete(folder));
            if (folder1.exists()) assertTrue(FileUtility.forceDelete(folder1));
            if (renamedFolder.exists()) assertTrue(FileUtility.forceDelete(renamedFolder));
        }

    }

    @Test
    public void renameImageFolder_When_FolderExistAndUserIsNotAdmin_Expect_CorrectException() throws Exception {

        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String testFolderNewName = "test_folder_new_name";
        imageFolderDTO.setName(testFolderNewName);
        final File newFolder = new File(imagesPath, testFolderNewName);

        assertFalse(newFolder.exists());


        try {
            final String jsonPostResponse = getJsonResponse(requestBuilderPost);

            assertTrue(Boolean.parseBoolean(jsonPostResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());


            final UserDomainObject user = new UserDomainObject(2);
            user.addRoleId(RoleId.USERS);
            Imcms.setUser(user); // means current user is not admin now


            final MockHttpServletRequestBuilder requestBuilderPatch = MockMvcRequestBuilders.patch(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilderPatch);
            assertFalse(newFolder.exists());


        } finally {
            if (folder.exists()) assertTrue(FileUtility.forceDelete(folder));
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    @Test
    public void deleteFolder_When_FolderExist_Expect_TrueAndFolderDeleted() throws Exception {
        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(folder.exists());
        assertTrue(folder.isDirectory());
        assertTrue(folder.canRead());

        try {
            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String response = getJsonResponse(requestBuilderDelete);

            assertEquals(response, "true");
            assertFalse(folder.exists());

        } finally {
            if (folder.exists()) assertTrue(FileUtility.forceDelete(folder));
        }
    }

    @Test
    public void deleteFolder_When_FolderNotExist_Expect_CorrectException() throws Exception {
        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);

        assertFalse(folder.exists());

        try {
            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(FolderNotExistException.class, requestBuilderDelete);
            assertFalse(folder.exists());

        } finally {
            if (folder.exists()) assertTrue(FileUtility.forceDelete(folder));
        }
    }

    @Test
    public void deleteFolder_When_FolderExistAndUserIsNotSuperAdmin_Expect_CorrectExceptionAndFolderNotDeleted() throws Exception {
        final String testFolderName = "test_folder_name";
        final File folder = new File(imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(folder.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(folder.exists());
        assertTrue(folder.isDirectory());
        assertTrue(folder.canRead());

        try {

            final UserDomainObject user = new UserDomainObject(2);
            user.addRoleId(RoleId.USERS);
            Imcms.setUser(user); // means current user is not admin now


            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilderDelete);
            assertTrue(folder.exists());

        } finally {
            if (folder.exists()) assertTrue(FileUtility.forceDelete(folder));
        }
    }
}
