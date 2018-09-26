package com.imcode.imcms.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImageFolderItemUsageDTO;
import com.imcode.imcms.domain.exception.DirectoryNotEmptyException;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

import static java.io.File.separator;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Transactional
public class ImageFolderControllerTest extends AbstractControllerTest {

    @Autowired
    private CommonContentService commonContentService;
    @Autowired
    private VersionService versionService;

    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Value("${ImagePath}")
    private File imagesPath;

    @Override
    protected String controllerPath() {
        return "/images/folders";
    }

    @Before
    public void setAdminAsCurrentUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now
    }

    @Test
    public void getImageFolder_Expect_Ok() throws Exception {
        performRequestBuilderExpectedOk(get(controllerPath()));
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

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String jsonPutResponse = getJsonResponse(requestBuilderPut);

            assertTrue(Boolean.parseBoolean(jsonPutResponse));
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

            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageNestedFolderDTO));

            final String jsonPutResponse = getJsonResponse(requestBuilderPut);

            assertTrue(Boolean.parseBoolean(jsonPutResponse));
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

        final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(imageFolderDTO));

        performRequestBuilderExpectException(FolderNotExistException.class, requestBuilderPut);
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

        imageFolderDTO1.setName(testFolderName);
        final File renamedFolder = new File(folder, testFolderName);

        assertFalse(renamedFolder.exists());


        try {

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
            user.addRoleId(Roles.USER.getId());
            Imcms.setUser(user); // means current user is not admin now


            final MockHttpServletRequestBuilder requestBuilderPut = MockMvcRequestBuilders.put(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilderPut);
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
            user.addRoleId(Roles.USER.getId());
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

    @Test
    public void deleteFolder_When_FolderNotEmptyAndFolderExist_Expect_DirectoryNotEmptyExceptionAndFolderNotDeleted() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.png";
        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testImage = new File(testDirectory, testImageName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(testDirectory.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        testImage.createNewFile();

        assertTrue(testImage.exists());

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(testDirectory.exists());
        assertTrue(testDirectory.isDirectory());
        assertTrue(testDirectory.canRead());

        try {
            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(DirectoryNotEmptyException.class, requestBuilderDelete);

            assertTrue(testDirectory.exists());

        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test
    public void deleteFolder_When_SubFolderIsEmptyAndFolderExist_Expect_TrueDAndFolderDeleted() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubdirectory";
        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);
        final ImageFolderDTO imageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());

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

        try {
            final MockHttpServletRequestBuilder requestBuilderDelete = delete(controllerPath())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String response = getJsonResponse(requestBuilderDelete);

            assertEquals(response, "true");
            assertFalse(testDirectory.exists());

        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test
    public void canBeDeleted_When_FolderNotEmptyAndFolderExist_Expect_DirectoryNotEmptyException() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testImageName = "test.png";
        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testImage = new File(testDirectory, testImageName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageFolderDTO);

        assertFalse(testDirectory.exists());

        final String jsonResponse = getJsonResponse(requestBuilder);

        testImage.createNewFile();

        assertTrue(testImage.exists());

        assertTrue(Boolean.parseBoolean(jsonResponse));
        assertTrue(testDirectory.exists());
        assertTrue(testDirectory.isDirectory());
        assertTrue(testDirectory.canRead());

        try {
            final MockHttpServletRequestBuilder requestBuilderGet = post(controllerPath() + "/can-delete")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            performRequestBuilderExpectException(DirectoryNotEmptyException.class, requestBuilderGet);

        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test
    public void canBeDeleted_When_SubFolderIsEmptyFolderExist_Expect_True() throws Exception {
        final String testDirectoryName = "testDirectory";
        final String testSubdirectoryName = "testSubdirectory";
        final File testDirectory = new File(imagesPath, testDirectoryName);
        final File testSubdirectory = new File(testDirectory, testSubdirectoryName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(testDirectoryName);
        final ImageFolderDTO imageSubFolderDTO = new ImageFolderDTO(testDirectory.getName() + File.separator + testSubdirectory.getName());

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

        try {
            final MockHttpServletRequestBuilder requestBuilderDelete = post(controllerPath() + "/can-delete")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJson(imageFolderDTO));

            final String response = getJsonResponse(requestBuilderDelete);

            assertEquals(response, "true");

        } finally {
            FileUtils.deleteDirectory(testDirectory);
        }
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderContainsUsedImage_Expect_OkAndListWithUsageForImage() throws Exception {
        final String testImageFileName = "test.png";
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(testImageFileName);

        final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

        commonDocumentDTO.getCommonContents()
                .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));
        commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

        final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
        Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
        final Image imageLatest = imageDataInitializer.createData(1, latestVersion);
        imageLatest.setName(testImageFileName);
        imageLatest.setLinkUrl(File.separator + testImageFileName);

        final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
        Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
        final Image imageWorking = imageDataInitializer.createData(1, workingVersion);
        imageWorking.setName(testImageFileName);
        imageWorking.setLinkUrl(File.separator + testImageFileName);

        final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                .content(imageFileDTO.getPath());

        final String jsonResponse = getJsonResponse(requestBuilderGet);
        final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
        });

        assertNotNull(imageFileUsagesDTOS);
        assertEquals(1, imageFileUsagesDTOS.size());
        assertEquals(4, imageFileUsagesDTOS.get(0).getUsages());
    }

    @Test
    public void checkFolderForImagesUsed_When_FolderNotContainsUsedImage_Expect_OkAndEmptyList() throws Exception {
        final String folderPathToCheck = "";
        final String testStubImageFileName = "testStub.png";

        final ImageFileDTO imageFileDTOStub = new ImageFileDTO();
        imageFileDTOStub.setPath(testStubImageFileName);

        final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

        commonDocumentDTO.getCommonContents()
                .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTOStub.getPath()));
        commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

        final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
        Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
        final Image imageLatest = imageDataInitializer.createData(1, latestVersion);
        imageLatest.setName(testStubImageFileName);
        imageLatest.setLinkUrl(File.separator + testStubImageFileName);

        final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
        Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
        final Image imageWorking = imageDataInitializer.createData(1, workingVersion);
        imageWorking.setName(testStubImageFileName);
        imageWorking.setLinkUrl(File.separator + testStubImageFileName);

        final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                .content(folderPathToCheck);

        final String jsonResponse = getJsonResponse(requestBuilderGet);
        final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
        });

        assertTrue(imageFileUsagesDTOS.isEmpty());
    }

    @Test
    public void checkSubFolderForImagesUsed_When_SubFolderContainsUsedImage_Expect_OkAndListListWithUsageForImage() throws Exception {
        final String folderPathToCheck = "subDirectory";

        final String testImageFileName = "test.png";
        final String subDirectoryName = folderPathToCheck;
        final ImageFileDTO imageFileDTO = new ImageFileDTO();
        imageFileDTO.setPath(File.separator + subDirectoryName + File.separator + testImageFileName);

        final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

        commonDocumentDTO.getCommonContents()
                .forEach(commonContent -> commonContent.setMenuImageURL(imageFileDTO.getPath()));
        commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

        final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
        Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
        final Image imageLatest = imageDataInitializer.createData(1, latestVersion);
        imageLatest.setName(testImageFileName);
        imageLatest.setLinkUrl(File.separator + testImageFileName);

        final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
        Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
        final Image imageWorking = imageDataInitializer.createData(1, workingVersion);
        imageWorking.setName(testImageFileName);
        imageWorking.setLinkUrl(File.separator + testImageFileName);

        final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                .content(folderPathToCheck);

        final String jsonResponse = getJsonResponse(requestBuilderGet);
        final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
        });

        assertNotNull(imageFileUsagesDTOS);
        assertEquals(1, imageFileUsagesDTOS.size());
        assertEquals(4, imageFileUsagesDTOS.get(0).getUsages());
    }

    @Test
    public void checkSubFolderForImagesUsed_When_FolderContainsSeveralUsedImages_Expect_OkAndListListWithUsageForImages() throws Exception {
        final String folderPathToCheck = "subDirectory";

        final String testImage1FileName = "test1.png";
        final String testImage2FileName = "test2.png";

        final ImageFileDTO imageFile1DTO = new ImageFileDTO();
        imageFile1DTO.setPath(testImage1FileName);

        final ImageFileDTO imageFile2DTO = new ImageFileDTO();
        imageFile2DTO.setPath(testImage2FileName);

        final DocumentDTO commonDocumentDTO = documentDataInitializer.createData();

        commonDocumentDTO.getCommonContents()
                .forEach(commonContent -> commonContent.setMenuImageURL(imageFile2DTO.getPath()));
        commonContentService.save(commonDocumentDTO.getId(), commonDocumentDTO.getCommonContents());

        final DocumentDTO latestDocumentDTO = documentDataInitializer.createData();
        Version latestVersion = versionService.create(latestDocumentDTO.getId(), 1);
        final Image imageLatest = imageDataInitializer.createData(1, latestVersion);
        imageLatest.setName(testImage2FileName);
        imageLatest.setLinkUrl(File.separator + testImage2FileName);

        final DocumentDTO workingDocumentDTO = documentDataInitializer.createData();
        Version workingVersion = versionService.getDocumentWorkingVersion(workingDocumentDTO.getId());
        final Image imageWorking = imageDataInitializer.createData(1, workingVersion);
        imageWorking.setName(testImage1FileName);
        imageWorking.setLinkUrl(File.separator + testImage1FileName);

        final MockHttpServletRequestBuilder requestBuilderGet = get(controllerPath() + "/check")
                .content(folderPathToCheck);

        final String jsonResponse = getJsonResponse(requestBuilderGet);
        final List<ImageFolderItemUsageDTO> imageFileUsagesDTOS = fromJson(jsonResponse, new TypeReference<List<ImageFolderItemUsageDTO>>() {
        });

        assertTrue(imageFileUsagesDTOS.isEmpty());
        assertEquals(2, imageFileUsagesDTOS.size());
        // TODO: 26.09.18 Make better check of returned data
        assertEquals(4, imageFileUsagesDTOS.get(0).getUsages());
        assertEquals(2, imageFileUsagesDTOS.get(1).getUsages());

    }

}
