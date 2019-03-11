package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.exception.DirectoryNotEmptyException;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.util.DeleteOnCloseFile;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Transactional
public class ImageFolderControllerTest extends AbstractControllerTest {

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Value("${ImagePath}")
    private File imagesPath;
    @Value("classpath:img1.jpg")
    private File testFile;

    @Override
    protected String controllerPath() {
        return "/images/folders";
    }

    @BeforeEach
    public void setAdminAsCurrentUserAndPrepareData() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now

        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @AfterEach
    public void clearTestData() {
        imageDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
    }

    @Test
    public void getImageFolder_Expect_Ok() throws Exception {
        performRequestBuilderExpectedOk(get(controllerPath()));
    }

    @Test
    public void createNewImageFolder_When_FolderNotExistBefore_Expect_FolderCreatedAndIsDirectoryAndReadableAndThenRemoved() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());
        }
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(folder.exists());

            performRequestBuilderExpectException(FolderAlreadyExistException.class, requestBuilder);
        }
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() throws Exception {
        final String testFolderName0 = "test_folder_name";
        final String testFolderName1 = testFolderName0 + separator + "nested1";
        final String testFolderName2 = testFolderName1 + separator + "nested2";
        final String testFolderName3 = testFolderName1 + separator + "nested3";

        try (DeleteOnCloseFile folder3 = new DeleteOnCloseFile(imagesPath, testFolderName3);
             DeleteOnCloseFile folder2 = new DeleteOnCloseFile(imagesPath, testFolderName2);
             DeleteOnCloseFile folder1 = new DeleteOnCloseFile(imagesPath, testFolderName1);
             DeleteOnCloseFile folder0 = new DeleteOnCloseFile(imagesPath, testFolderName0)) {
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
        }
    }

    @Test
    public void createNewImageFolder_When_UserIsNotAdmin_Expected_CorrectExceptionAndFolderNotCreated() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        user.addRoleId(Roles.USER.getId());
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
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

        final String testFolderNewName = "test_folder_new_name";
        imageFolderDTO.setName(testFolderNewName);

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName);
             DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, testFolderNewName)) {
            assertFalse(folder.exists());
            assertFalse(newFolder.exists());

            final String jsonPostResponse = getJsonResponse(requestBuilderPost);

            assertTrue(Boolean.parseBoolean(jsonPostResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String jsonPutResponse = getJsonResponse(requestBuilderPut);

            assertTrue(Boolean.parseBoolean(jsonPutResponse));
            assertFalse(folder.exists());
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());
        }
    }

    @Test
    public void renameImageFolder_When_FolderIsNestedAndExistAndUserIsAdmin_Expect_True() throws Exception {

        final String testFolderName = "test_folder_name";
        final String testNestedFolderName = "nested_folder_name";
        final String testNestedFolderNewName = "test_folder_new_name";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName);
             DeleteOnCloseFile nestedFolder = new DeleteOnCloseFile(folder, testNestedFolderName);
             DeleteOnCloseFile newNestedFolder = new DeleteOnCloseFile(folder, testNestedFolderNewName)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final String path = separator + testFolderName + separator + testNestedFolderName;
            final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(testNestedFolderName, path);
            final MockHttpServletRequestBuilder requestBuilderPostNested = getPostRequestBuilderWithContent(imageNestedFolderDTO);

            assertFalse(nestedFolder.exists());

            imageNestedFolderDTO.setName(testNestedFolderNewName);

            assertFalse(newNestedFolder.exists());

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

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageNestedFolderDTO));

            final String jsonPutResponse = getJsonResponse(requestBuilderPut);

            assertTrue(Boolean.parseBoolean(jsonPutResponse));
            assertFalse(nestedFolder.exists());
            assertTrue(newNestedFolder.exists());
            assertTrue(newNestedFolder.isDirectory());
            assertTrue(newNestedFolder.canRead());
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

        final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(imageFolderDTO));

        performRequestBuilderExpectException(FolderNotExistException.class, requestBuilderPut);

        assertFalse(newFolder.exists());
    }

    @Test
    public void renameImageFolder_When_FolderExistWithSuchNameAndUserIsAdmin_Expect_CorrectException() throws Exception {

        final String testFolderName = "test_folder_name";
        final String testFolderName1 = "test_folder_name1";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName);
             DeleteOnCloseFile folder1 = new DeleteOnCloseFile(imagesPath, testFolderName1);
             DeleteOnCloseFile renamedFolder = new DeleteOnCloseFile(folder, testFolderName)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(testFolderName1);
            final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(imageFolderDTO1);

            assertFalse(folder1.exists());

            imageFolderDTO1.setName(testFolderName);

            assertFalse(renamedFolder.exists());


            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(folder.exists());

            final String jsonResponse1 = getJsonResponse(requestBuilder1);

            assertTrue(Boolean.parseBoolean(jsonResponse1));
            assertTrue(folder1.exists());

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO1));

            performRequestBuilderExpectException(FolderAlreadyExistException.class, requestBuilderPut);
            assertFalse(renamedFolder.exists());
        }
    }

    @Test
    public void renameImageFolder_When_FolderExistAndUserIsNotAdmin_Expect_CorrectException() throws Exception {

        final String testFolderName = "test_folder_name";
        final String testFolderNewName = "test_folder_new_name";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName);
             DeleteOnCloseFile newFolder = new DeleteOnCloseFile(imagesPath, testFolderNewName)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            imageFolderDTO.setName(testFolderNewName);

            assertFalse(newFolder.exists());

            final String jsonPostResponse = getJsonResponse(requestBuilderPost);

            assertTrue(Boolean.parseBoolean(jsonPostResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());

            final UserDomainObject user = new UserDomainObject(2);
            user.addRoleId(Roles.USER.getId());
            Imcms.setUser(user); // means current user is not admin now

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilderPut);
            assertFalse(newFolder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderExist_Expect_TrueAndFolderDeleted() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String response = getJsonResponse(requestBuilderDelete);

            assertEquals(response, "true");
            assertFalse(folder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderNotExist_Expect_CorrectException() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);

            assertFalse(folder.exists());

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(FolderNotExistException.class, requestBuilderDelete);
            assertFalse(folder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderExistAndUserIsNotSuperAdmin_Expect_CorrectExceptionAndFolderNotDeleted() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseFile folder = new DeleteOnCloseFile(imagesPath, testFolderName)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(folder.exists());
            assertTrue(folder.isDirectory());
            assertTrue(folder.canRead());

            final UserDomainObject user = new UserDomainObject(2);
            user.addRoleId(Roles.USER.getId());
            Imcms.setUser(user); // means current user is not admin now


            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilderDelete);
            assertTrue(folder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderNotEmptyAndFolderExist_Expect_DirectoryNotEmptyExceptionAndFolderNotDeleted() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.jpg";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testImage = new File(testDirectory, testImageName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);

            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(testDirectory.exists());

            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(testImage.createNewFile());
            assertTrue(testImage.exists());

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(testDirectory.exists());
            assertTrue(testDirectory.isDirectory());
            assertTrue(testDirectory.canRead());

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(DirectoryNotEmptyException.class, requestBuilderDelete);

            assertTrue(testDirectory.exists());
        }
    }

    @Test
    public void deleteFolder_When_SubFolderIsEmptyAndFolderExist_Expect_TrueDAndFolderDeleted() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubdirectory";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);
            final ImageFolderDTO imageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + separator + testSubdirectory.getName());

            final MockHttpServletRequestBuilder requestBuilder0 = getPostRequestBuilderWithContent(imageFolderDTO);
            final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(imageSubFolderDTO);

            assertFalse(testSubdirectory.exists());

            final String jsonResponse0 = getJsonResponse(requestBuilder0);
            final String jsonResponse1 = getJsonResponse(requestBuilder1);

            assertTrue(Boolean.parseBoolean(jsonResponse0));
            assertTrue(Boolean.parseBoolean(jsonResponse1));

            assertTrue(testDirectory.exists());
            assertTrue(testDirectory.isDirectory());
            assertTrue(testDirectory.canRead());

            assertTrue(testSubdirectory.exists());
            assertTrue(testSubdirectory.isDirectory());
            assertTrue(testSubdirectory.canRead());

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String response = getJsonResponse(requestBuilderDelete);

            assertEquals(response, "true");
            assertFalse(testDirectory.exists());
        }
    }

    @Test
    public void canBeDeleted_When_FolderNotEmptyAndFolderExist_Expect_DirectoryNotEmptyException() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.jpg";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testImage = new File(testDirectory, testImageName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);

            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(testDirectory.exists());

            final String jsonResponse = getJsonResponse(requestBuilder);

            assertTrue(testImage.createNewFile());
            assertTrue(testImage.exists());

            assertTrue(Boolean.parseBoolean(jsonResponse));
            assertTrue(testDirectory.exists());
            assertTrue(testDirectory.isDirectory());
            assertTrue(testDirectory.canRead());

            final MockHttpServletRequestBuilder requestBuilderGet = post(controllerPath() + "/can-delete")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(DirectoryNotEmptyException.class, requestBuilderGet);
        }
    }

    @Test
    public void canBeDeleted_When_SubFolderIsEmptyFolderExist_Expect_True() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubdirectory";

        try (DeleteOnCloseFile testDirectory = new DeleteOnCloseFile(imagesPath, testDirectoryName)) {

            final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);
            final ImageFolderDTO imageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + separator + testSubdirectory.getName());

            final MockHttpServletRequestBuilder requestBuilder0 = getPostRequestBuilderWithContent(imageFolderDTO);
            final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(imageSubFolderDTO);

            assertFalse(testSubdirectory.exists());

            final String jsonResponse0 = getJsonResponse(requestBuilder0);
            final String jsonResponse1 = getJsonResponse(requestBuilder1);

            assertTrue(Boolean.parseBoolean(jsonResponse0));
            assertTrue(Boolean.parseBoolean(jsonResponse1));

            assertTrue(testDirectory.exists());
            assertTrue(testDirectory.isDirectory());
            assertTrue(testDirectory.canRead());

            assertTrue(testSubdirectory.exists());
            assertTrue(testSubdirectory.isDirectory());
            assertTrue(testSubdirectory.canRead());

            final MockHttpServletRequestBuilder requestBuilderDelete = post(controllerPath() + "/can-delete")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String response = getJsonResponse(requestBuilderDelete);

            assertEquals(response, "true");
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderContainsUsedImage_Expect_OkAndListWithUsageForImage() throws Exception {
        final String testImageFileName = "test.jpg";
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        try (DeleteOnCloseFile testImageFile = new DeleteOnCloseFile(imagesPath, testImageFileName)) {
            Files.copy(testFile.toPath(), testImageFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);

            imageDataInitializer.createAllAvailableImageContent(
                    true, testImageFileName, testImageFileName
            );

            final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                    .param("path", "");

            final String jsonResponse = getJsonResponse(requestBuilderGet);
            final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
            });

            assertNotNull(imageFileUsagesDTOS);
            assertEquals(1, imageFileUsagesDTOS.size());
            assertEquals(2, imageFileUsagesDTOS.get(0).getUsages().size());
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderNotContainsUsedImage_Expect_OkAndEmptyList() throws Exception {
        final String folderPathToCheck = "";
        final String testStubImageName = "testStub.jpg";

        final String testDirectory = "subDirectory";
        final String testStubImageUrl = testDirectory + separator + testStubImageName;

        try (DeleteOnCloseFile testDirectoryFile = new DeleteOnCloseFile(imagesPath, testDirectory)) {

            final File testImageFile = new File(testDirectoryFile, testStubImageName);

            assertTrue(testFile.exists());
            assertTrue(testDirectoryFile.mkdirs());

            assertFalse(testImageFile.exists());
            Files.copy(testFile.toPath(), testImageFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            assertTrue(testImageFile.exists());

            imageDataInitializer.createAllAvailableImageContent(
                    true, testStubImageUrl, testStubImageUrl
            );

            final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                    .param("path", folderPathToCheck);

            final String jsonResponse = getJsonResponse(requestBuilderGet);
            final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
            });

            assertTrue(imageFileUsagesDTOS.isEmpty());
        }
    }

    @Test
    public void checkSubFolderForImagesUsed_When_SubFolderContainsUsedImage_Expect_OkAndListListWithUsageForImage() throws Exception {
        final String testDirectory = "subDirectory";

        try (DeleteOnCloseFile testDirectoryFile = new DeleteOnCloseFile(imagesPath, testDirectory)) {

            final String testImageName = "test.jpg";
            final String testImageUrl = testDirectory + separator + testImageName;

            File testImageFile = new File(testDirectoryFile, testImageName);

            assertTrue(testFile.exists());
            assertTrue(testDirectoryFile.mkdirs());

            assertFalse(testImageFile.exists());
            Files.copy(testFile.toPath(), testImageFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            assertTrue(testImageFile.exists());

            imageDataInitializer.createAllAvailableImageContent(
                    true, testImageUrl, testImageUrl
            );

            final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                    .param("path", separator + testDirectory);

            final String jsonResponse = getJsonResponse(requestBuilderGet);
            final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
            });

            assertNotNull(imageFileUsagesDTOS);
            assertEquals(1, imageFileUsagesDTOS.size());
            assertEquals(2, imageFileUsagesDTOS.get(0).getUsages().size());
        }
    }

    @Test
    public void checkSubFolderForImagesUsed_When_FolderContainsSeveralUsedImages_Expect_OkAndListListWithUsageForImages() throws Exception {
        final String folderPathToCheck = "subDirectory";

        try (DeleteOnCloseFile testFolder = new DeleteOnCloseFile(imagesPath, folderPathToCheck)) {

            final String testImage1Name = "test1.jpg";
            final String testImage2Name = "test2.jpg";
            final String testImage1Url = folderPathToCheck + separator + testImage1Name;
            final String testImage2Url = folderPathToCheck + separator + testImage2Name;

            final File testImage1File = new File(testFolder, testImage1Name);
            final File testImage2File = new File(testFolder, testImage2Name);

            assertTrue(testFile.exists());
            assertTrue(testFolder.mkdirs());

            assertFalse(testImage1File.exists());
            assertFalse(testImage2File.exists());
            Files.copy(testFile.toPath(), testImage1File.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            Files.copy(testFile.toPath(), testImage2File.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            assertTrue(testImage1File.exists());
            assertTrue(testImage2File.exists());

            imageDataInitializer.createAllAvailableImageContent(
                    false, testImage1Url, testImage2Url
            );

            final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                    .param("path", separator + folderPathToCheck);
            final String jsonResponse = getJsonResponse(requestBuilderGet);
            final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
            });

            assertFalse(imageFileUsagesDTOS.isEmpty());
            assertEquals(2, imageFileUsagesDTOS.size());
            // TODO: 26.09.18 Make better check of returned data
            assertEquals(1, imageFileUsagesDTOS.get(0).getUsages().size());
            assertEquals(1, imageFileUsagesDTOS.get(1).getUsages().size());
        }
    }

}
