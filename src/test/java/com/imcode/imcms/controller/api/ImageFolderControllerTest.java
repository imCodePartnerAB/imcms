package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.component.ImageFolderCacheManager;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import com.imcode.imcms.storage.exception.FolderNotEmptyException;
import com.imcode.imcms.storage.exception.StorageFileNotFoundException;
import com.imcode.imcms.storage.exception.SuchStorageFileExistsException;
import com.imcode.imcms.util.DeleteOnCloseStorageFile;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Transactional
public class ImageFolderControllerTest extends AbstractControllerTest {

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;
    @Autowired
    private ImageFolderCacheManager imageFolderCacheManager;

    @Autowired
    @Qualifier("imageStorageClient")
    private StorageClient storageClient;

    @Value("${ImagePath}")
    private String imagesPath;
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
        clearImageFolderCache();
    }

    @Test
    public void getImageFolder_Expect_Ok() throws Exception {
        performRequestBuilderExpectedOk(get(controllerPath()));
    }

    @Test
    public void createNewImageFolder_When_FolderNotExistBefore_Expect_FolderCreatedAndIsDirectoryAndReadableAndThenRemoved() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseStorageFile folder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName), storageClient)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            performRequestBuilderExpectedOk(requestBuilder);

            assertTrue(folder.exists());
        }
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseStorageFile folder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName), storageClient)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            performRequestBuilderExpectedOk(requestBuilder);
            assertTrue(folder.exists());

            performRequestBuilderExpectException(SuchStorageFileExistsException.class, requestBuilder);
        }
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() throws Exception {
        final String testFolderName0 = "test_folder_name";
        final String testFolderName1 = testFolderName0 + separator + "nested1";
        final String testFolderName2 = testFolderName1 + separator + "nested2";
        final String testFolderName3 = testFolderName1 + separator + "nested3";

        try (DeleteOnCloseStorageFile folder3 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName3), storageClient);
             DeleteOnCloseStorageFile folder2 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName2), storageClient);
             DeleteOnCloseStorageFile folder1 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName1), storageClient);
             DeleteOnCloseStorageFile folder0 = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName0), storageClient)) {
            final MockHttpServletRequestBuilder requestBuilder0 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName0));
            final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName1));
            final MockHttpServletRequestBuilder requestBuilder2 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName2));
            final MockHttpServletRequestBuilder requestBuilder3 = getPostRequestBuilderWithContent(new ImageFolderDTO(testFolderName3));

            assertFalse(folder0.exists());
            assertFalse(folder1.exists());
            assertFalse(folder2.exists());
            assertFalse(folder3.exists());

            performRequestBuilderExpectedOk(requestBuilder0);
            performRequestBuilderExpectedOk(requestBuilder1);
            performRequestBuilderExpectedOk(requestBuilder2);
            performRequestBuilderExpectedOk(requestBuilder3);

            assertTrue(folder0.exists());
            assertTrue(folder1.exists());
            assertTrue(folder2.exists());
            assertTrue(folder3.exists());
        }
    }

    @Test
    public void renameImageFolder_When_FolderExistAndUserIsAdmin_Expect_True() throws Exception {

        final String testFolderName = "test_folder_name";
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
        final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

        final String testFolderNewName = "test_folder_new_name";
        imageFolderDTO.setName(testFolderNewName);

        try (DeleteOnCloseStorageFile folder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName), storageClient);
             DeleteOnCloseStorageFile newFolder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderNewName), storageClient)) {
            assertFalse(folder.exists());
            assertFalse(newFolder.exists());

            performRequestBuilderExpectedOk(requestBuilderPost);

            assertTrue(folder.exists());

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectedOk(requestBuilderPut);

            assertFalse(folder.exists());
            assertTrue(newFolder.exists());
        }
    }

    @Test
    public void renameImageFolder_When_FolderIsNestedAndExistAndUserIsAdmin_Expect_True() throws Exception {

        final String testFolderName = "test_folder_name";
        final String testNestedFolderName = "nested_folder_name";
        final String testNestedFolderNewName = "test_folder_new_name";

        final StoragePath folderPath = StoragePath.get(DIRECTORY, imagesPath, testFolderName);
        final StoragePath nestedFolderPath = folderPath.resolve(DIRECTORY, testNestedFolderName);
        final StoragePath newNestedFolderPath = folderPath.resolve(DIRECTORY, testNestedFolderNewName);
        try (DeleteOnCloseStorageFile folder = new DeleteOnCloseStorageFile(folderPath, storageClient);
             DeleteOnCloseStorageFile nestedFolder = new DeleteOnCloseStorageFile(nestedFolderPath, storageClient);
             DeleteOnCloseStorageFile newNestedFolder = new DeleteOnCloseStorageFile(newNestedFolderPath, storageClient)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilderPost = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final String path = separator + testFolderName + separator + testNestedFolderName;
            final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(testNestedFolderName, path);
            final MockHttpServletRequestBuilder requestBuilderPostNested = getPostRequestBuilderWithContent(imageNestedFolderDTO);

            assertFalse(nestedFolder.exists());

            imageNestedFolderDTO.setName(testNestedFolderNewName);

            assertFalse(newNestedFolder.exists());

            performRequestBuilderExpectedOk(requestBuilderPost);

            assertTrue(folder.exists());

            performRequestBuilderExpectedOk(requestBuilderPostNested);

            assertTrue(nestedFolder.exists());

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageNestedFolderDTO));

            performRequestBuilderExpectedOk(requestBuilderPut);

            assertFalse(nestedFolder.exists());
            assertTrue(newNestedFolder.exists());
        }
    }

    @Test
    public void renameImageFolder_When_FolderNotExistAndUserIsAdmin_Expect_CorrectException() throws Exception {
        final String testFolderName = "test_folder_name";
        final StoragePath folderPath = StoragePath.get(DIRECTORY, imagesPath, testFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);

        assertFalse(storageClient.exists(folderPath));

        final String testFolderNewName = "test_folder_new_name";
        imageFolderDTO.setName(testFolderNewName);
        final StoragePath newFolderPath = StoragePath.get(DIRECTORY, imagesPath, testFolderNewName);

        assertFalse(storageClient.exists(newFolderPath));

        final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(imageFolderDTO));

        performRequestBuilderExpectException(StorageFileNotFoundException.class, requestBuilderPut);

        assertFalse(storageClient.exists(newFolderPath));
    }

    @Test
    public void renameImageFolder_When_FolderExistWithSuchNameAndUserIsAdmin_Expect_CorrectException() throws Exception {

        final String testFolderName = "test_folder_name";
        final String testFolderName1 = "test_folder_name1";

        final StoragePath folderPath = StoragePath.get(DIRECTORY, imagesPath, testFolderName);
        final StoragePath folder1Path = StoragePath.get(DIRECTORY, imagesPath, testFolderName1);
        final StoragePath renamedFolderPath = folderPath.resolve(DIRECTORY, imagesPath, testFolderName);
        try (DeleteOnCloseStorageFile folder = new DeleteOnCloseStorageFile(folderPath, storageClient);
             DeleteOnCloseStorageFile folder1 = new DeleteOnCloseStorageFile(folder1Path, storageClient);
             DeleteOnCloseStorageFile renamedFolder = new DeleteOnCloseStorageFile(renamedFolderPath, storageClient)) {
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(testFolderName1);
            final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(imageFolderDTO1);

            assertFalse(folder1.exists());

            imageFolderDTO1.setName(testFolderName);

            assertFalse(renamedFolder.exists());


            performRequestBuilderExpectedOk(requestBuilder);

            assertTrue(folder.exists());

            performRequestBuilderExpectedOk(requestBuilder1);

            assertTrue(folder1.exists());

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFolderDTO1));

            performRequestBuilderExpectException(SuchStorageFileExistsException.class, requestBuilderPut);
            assertFalse(renamedFolder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderExist_Expect_TrueAndFolderDeleted() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseStorageFile folder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName), storageClient)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);
            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(folder.exists());

            performRequestBuilderExpectedOk(requestBuilder);

            assertTrue(folder.exists());

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectedOk(requestBuilderDelete);

            assertFalse(folder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderNotExist_Expect_CorrectException() throws Exception {
        final String testFolderName = "test_folder_name";

        try (DeleteOnCloseStorageFile folder = new DeleteOnCloseStorageFile(StoragePath.get(DIRECTORY, imagesPath, testFolderName), storageClient)) {

            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testFolderName);

            assertFalse(folder.exists());

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(StorageFileNotFoundException.class, requestBuilderDelete);
            assertFalse(folder.exists());
        }
    }

    @Test
    public void deleteFolder_When_FolderNotEmptyAndFolderExist_Expect_FolderNotEmptyExceptionAndFolderNotDeleted() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.jpg";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testImagePath = testDirectoryPath.resolve(FILE, testImageName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);

            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(testDirectory.exists());

            performRequestBuilderExpectedOk(requestBuilder);

            storageClient.create(testImagePath);
            assertTrue(storageClient.exists(testImagePath));

            assertTrue(testDirectory.exists());

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(FolderNotEmptyException.class, requestBuilderDelete);

            assertTrue(testDirectory.exists());
        }
    }

    @Test
    public void deleteFolder_When_SubFolderIsEmptyAndFolderExist_Expect_TrueDAndFolderDeleted() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubdirectory";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testSubdirectoryPath = testDirectoryPath.resolve(DIRECTORY, testSubdirectoryName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);
            final ImageFolderDTO imageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + separator + testSubdirectoryPath.getName());

            final MockHttpServletRequestBuilder requestBuilder0 = getPostRequestBuilderWithContent(imageFolderDTO);
            final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(imageSubFolderDTO);

            assertFalse(storageClient.exists(testSubdirectoryPath));

            performRequestBuilderExpectedOk(requestBuilder0);
            performRequestBuilderExpectedOk(requestBuilder1);

            assertTrue(testDirectory.exists());

            assertTrue(storageClient.exists(testSubdirectoryPath));

            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectedOk(requestBuilderDelete);

            assertFalse(testDirectory.exists());
        }
    }

    @Test
    public void canBeDeleted_When_FolderNotEmptyAndFolderExist_Expect_FolderNotEmptyException() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.jpg";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testImagePath = testDirectoryPath.resolve(FILE, testImageName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);

            final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

            assertFalse(testDirectory.exists());

            performRequestBuilderExpectedOk(requestBuilder);

            storageClient.create(testImagePath);
            assertTrue(storageClient.exists(testImagePath));

            assertTrue(testDirectory.exists());

            final MockHttpServletRequestBuilder requestBuilderGet = post(controllerPath() + "/can-delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(FolderNotEmptyException.class, requestBuilderGet);
        }
    }

    @Test
    public void canBeDeleted_When_SubFolderIsEmptyFolderExist_Expect_True() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubdirectory";

        final StoragePath testDirectoryPath = StoragePath.get(DIRECTORY, imagesPath, testDirectoryName);
        try (DeleteOnCloseStorageFile testDirectory = new DeleteOnCloseStorageFile(testDirectoryPath, storageClient)) {

            final StoragePath testSubdirectoryPath = testDirectoryPath.resolve(DIRECTORY, testSubdirectoryName);
            final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);
            final ImageFolderDTO imageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + separator + testSubdirectoryPath.getName());

            final MockHttpServletRequestBuilder requestBuilder0 = getPostRequestBuilderWithContent(imageFolderDTO);
            final MockHttpServletRequestBuilder requestBuilder1 = getPostRequestBuilderWithContent(imageSubFolderDTO);

            assertFalse(storageClient.exists(testSubdirectoryPath));

            performRequestBuilderExpectedOk(requestBuilder0);
            performRequestBuilderExpectedOk(requestBuilder1);

            assertTrue(testDirectory.exists());

            assertTrue(storageClient.exists(testSubdirectoryPath));

            final MockHttpServletRequestBuilder requestBuilderDelete = post(controllerPath() + "/can-delete")
                    .contentType(MediaType.APPLICATION_JSON)
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

        final StoragePath testImageFilePath = StoragePath.get(FILE, imagesPath, testImageFileName);
        try (DeleteOnCloseStorageFile testImageFile = new DeleteOnCloseStorageFile(testImageFilePath, storageClient)) {

            testImageFile.put(new FileInputStream(testFile));

            clearImageFolderCache();

            imageDataInitializer.createAllAvailableImageContent(true, testImageFileName, testImageFileName);

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

        final StoragePath testDirectoryFilePath = StoragePath.get(DIRECTORY, imagesPath, testDirectory);
        try (DeleteOnCloseStorageFile testDirectoryFile = new DeleteOnCloseStorageFile(testDirectoryFilePath, storageClient)) {

            final StoragePath testImageFilePath = testDirectoryFilePath.resolve(FILE, testStubImageName);

            assertTrue(testFile.exists());
            assertTrue(testDirectoryFile.create());

            assertFalse(storageClient.exists(testImageFilePath));
            storageClient.put(testImageFilePath, new FileInputStream(testFile));
            assertTrue(storageClient.exists(testImageFilePath));

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

        final StoragePath testDirectoryFilePath = StoragePath.get(DIRECTORY, imagesPath, testDirectory);
        try (DeleteOnCloseStorageFile testDirectoryFile = new DeleteOnCloseStorageFile(testDirectoryFilePath, storageClient)) {

            final String testImageName = "test.jpg";
            final String testImageUrl = testDirectory + separator + testImageName;

            StoragePath testImageFilePath = testDirectoryFilePath.resolve(FILE, testImageName);

            assertTrue(testFile.exists());
            assertTrue(testDirectoryFile.create());

            assertFalse(storageClient.exists(testImageFilePath));
            storageClient.put(testImageFilePath, new FileInputStream(testFile));
            assertTrue(storageClient.exists(testImageFilePath));

            clearImageFolderCache();

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

        final StoragePath testFolderPath = StoragePath.get(DIRECTORY, imagesPath, folderPathToCheck);
        try (DeleteOnCloseStorageFile testFolder = new DeleteOnCloseStorageFile(testFolderPath, storageClient)) {

            final String testImage1Name = "test1.jpg";
            final String testImage2Name = "test2.jpg";
            final String testImage1Url = folderPathToCheck + separator + testImage1Name;
            final String testImage2Url = folderPathToCheck + separator + testImage2Name;

            final StoragePath testImage1FilePath = testFolderPath.resolve(FILE, testImage1Name);
            final StoragePath testImage2FilePath = testFolderPath.resolve(FILE, testImage2Name);

            assertTrue(testFile.exists());
            assertTrue(testFolder.create());

            assertFalse(storageClient.exists(testImage1FilePath));
            assertFalse(storageClient.exists(testImage2FilePath));
            storageClient.put(testImage1FilePath, new FileInputStream(testFile));
            storageClient.put(testImage2FilePath, new FileInputStream(testFile));
            assertTrue(storageClient.exists(testImage1FilePath));
            assertTrue(storageClient.exists(testImage2FilePath));

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

    private void clearImageFolderCache(){
        imageFolderCacheManager.invalidate();
    }

}
