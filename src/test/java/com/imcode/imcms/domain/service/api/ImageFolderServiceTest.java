package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
@WebAppConfiguration
@Transactional
public class ImageFolderServiceTest {

    @Autowired
    private ImageFolderService imageFolderService;

    @Value("${ImagePath}")
    private File imagesPath;

    @Test
    public void getImageFolder() {
        final ImageFolderDTO imageFolder = imageFolderService.getImageFolder();

        assertNotNull(imageFolder);
    }

    @Test
    public void createNewImageFolder_When_FolderNotExistBefore_Expect_FolderCreatedAndIsDirectoryAndReadableAndThenRemoved() {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());
        assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
        assertTrue(newFolder.exists());
        assertTrue(newFolder.isDirectory());
        assertTrue(newFolder.canRead());
        assertTrue(newFolder.delete());
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());
        assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
        assertTrue(newFolder.exists());

        try {
            imageFolderService.createImageFolder(imageFolderDTO); // exception expected here
            fail("Expected exception wasn't thrown!");

        } catch (FolderAlreadyExistException e) {
            assertTrue(newFolder.delete());
        }
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() {
        final String newFolderName0 = "new_test_folder";
        final String newFolderPath0 = "/" + newFolderName0;

        final String newFolderName1 = "nested1";
        final String newFolderPath1 = newFolderPath0 + "/" + newFolderName1;

        final String newFolderName2 = "nested2";
        final String newFolderPath2 = newFolderPath1 + "/" + newFolderName2;

        final String newFolderName3 = "nested3";
        final String newFolderPath3 = newFolderPath1 + "/" + newFolderName3;

        final File newFolder0 = new File(imagesPath, newFolderPath0);
        final File newFolder1 = new File(imagesPath, newFolderPath1);
        final File newFolder2 = new File(imagesPath, newFolderPath2);
        final File newFolder3 = new File(imagesPath, newFolderPath3);

        final ImageFolderDTO imageFolderDTO0 = new ImageFolderDTO(newFolderName0, newFolderPath0);
        final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1, newFolderPath1);
        final ImageFolderDTO imageFolderDTO2 = new ImageFolderDTO(newFolderName2, newFolderPath2);
        final ImageFolderDTO imageFolderDTO3 = new ImageFolderDTO(newFolderName3, newFolderPath3);

        assertFalse(newFolder0.exists());
        assertFalse(newFolder1.exists());
        assertFalse(newFolder2.exists());
        assertFalse(newFolder3.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO0));
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO1));
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO2));
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO3));

            assertTrue(newFolder0.exists());
            assertTrue(newFolder1.exists());
            assertTrue(newFolder2.exists());
            assertTrue(newFolder3.exists());

            assertTrue(newFolder0.isDirectory());
            assertTrue(newFolder1.isDirectory());
            assertTrue(newFolder2.isDirectory());
            assertTrue(newFolder3.isDirectory());

            assertTrue(newFolder0.canRead());
            assertTrue(newFolder1.canRead());
            assertTrue(newFolder2.canRead());
            assertTrue(newFolder3.canRead());

            assertTrue(newFolder3.delete());
            assertTrue(newFolder2.delete());
            assertTrue(newFolder1.delete());
            assertTrue(newFolder0.delete());

        } finally {
            if (newFolder3.exists()) assertTrue(newFolder3.delete());
            if (newFolder2.exists()) assertTrue(newFolder2.delete());
            if (newFolder1.exists()) assertTrue(newFolder1.delete());
            if (newFolder0.exists()) assertTrue(newFolder0.delete());
        }
    }

    @Test
    public void renameFolder_When_FolderExistsInRootImagesDirectory_Expect_True() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());

            final String folderNewName = "new_name";
            final File renamedFolder = new File(imagesPath, folderNewName);
            imageFolderDTO.setName(folderNewName);

            assertFalse(renamedFolder.exists());
            assertTrue(imageFolderService.renameFolder(imageFolderDTO));
            assertTrue(renamedFolder.exists());
            assertFalse(newFolder.exists());
            assertTrue(renamedFolder.delete());

        } finally {
            if (newFolder.exists()) assertTrue(newFolder.delete());
        }
    }

    @Test(expected = FolderNotExistException.class)
    public void renameFolder_When_FolderNotExist_Expect_CorrectException() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        final String folderNewName = "new_name";
        final File renamedFolder = new File(imagesPath, folderNewName);

        assertFalse(renamedFolder.exists());

        try {
            imageFolderDTO.setName(folderNewName);

            assertFalse(renamedFolder.exists());
            imageFolderService.renameFolder(imageFolderDTO); // exception expected here!

        } finally {
            if (newFolder.exists()) assertTrue(newFolder.delete());
            if (renamedFolder.exists()) assertTrue(renamedFolder.delete());
        }
    }

    @Test
    public void renameFolder_When_FolderExistsNestedInRootImagesDirectory_Expect_True() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        final String newNestedFolderName = "nested_folder";
        final String path = newFolderName + "/" + newNestedFolderName;
        final File newNestedFolder = new File(imagesPath, path);
        final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(newNestedFolderName, "/" + path);

        assertFalse(newNestedFolder.exists());

        final String nestedFolderNewName = "new_name";
        final File renamedFolder = new File(imagesPath, newFolderName + "/" + nestedFolderNewName);

        assertFalse(renamedFolder.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());

            assertTrue(imageFolderService.createImageFolder(imageNestedFolderDTO));
            assertTrue(newNestedFolder.exists());

            imageNestedFolderDTO.setName(nestedFolderNewName);

            assertFalse(renamedFolder.exists());
            assertTrue(imageFolderService.renameFolder(imageNestedFolderDTO));
            assertTrue(renamedFolder.exists());
            assertFalse(newNestedFolder.exists());
            assertTrue(renamedFolder.delete());

        } finally {
            if (newFolder.exists()) assertTrue(newFolder.delete());
            if (newNestedFolder.exists()) assertTrue(newNestedFolder.delete());
            if (renamedFolder.exists()) assertTrue(renamedFolder.delete());
        }
    }

    @Test(expected = FolderAlreadyExistException.class)
    public void renameFolder_When_FolderWithSuchNameExist_Expect_CorrectException() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        final String newFolderName1 = "new_test_folder1";
        final File newFolder1 = new File(imagesPath, newFolderName1);
        final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1);

        assertFalse(newFolder1.exists());

        try {
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());
            assertTrue(newFolder.canRead());

            assertTrue(imageFolderService.createImageFolder(imageFolderDTO1));
            assertTrue(newFolder1.exists());
            assertTrue(newFolder1.isDirectory());
            assertTrue(newFolder1.canRead());

            final String folderNewName = newFolderName1;
            final File renamedFolder = new File(imagesPath, folderNewName);
            imageFolderDTO1.setName(folderNewName);

            assertTrue(renamedFolder.exists());
            imageFolderService.renameFolder(imageFolderDTO1); // exception expected here!

        } finally {
            if (newFolder.exists()) assertTrue(newFolder.delete());
            if (newFolder1.exists()) assertTrue(newFolder1.delete());
        }
    }

}
