package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
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
        assertTrue(imageFolderService.createNewFolder(imageFolderDTO));
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
        assertTrue(imageFolderService.createNewFolder(imageFolderDTO));
        assertTrue(newFolder.exists());

        try {
            imageFolderService.createNewFolder(imageFolderDTO); // exception expected here
            fail("Expected exception wasn't thrown!");

        } catch (FolderAlreadyExistException e) {
            assertTrue(newFolder.delete());
        }
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() {
        final String newFolderName0 = "new_test_folder";
        final String newFolderName1 = newFolderName0 + "/nested1";
        final String newFolderName2 = newFolderName1 + "/nested2";
        final String newFolderName3 = newFolderName1 + "/nested3";

        final File newFolder0 = new File(imagesPath, newFolderName0);
        final File newFolder1 = new File(imagesPath, newFolderName1);
        final File newFolder2 = new File(imagesPath, newFolderName2);
        final File newFolder3 = new File(imagesPath, newFolderName3);

        final ImageFolderDTO imageFolderDTO0 = new ImageFolderDTO(newFolderName0);
        final ImageFolderDTO imageFolderDTO1 = new ImageFolderDTO(newFolderName1);
        final ImageFolderDTO imageFolderDTO2 = new ImageFolderDTO(newFolderName2);
        final ImageFolderDTO imageFolderDTO3 = new ImageFolderDTO(newFolderName3);

        assertFalse(newFolder0.exists());
        assertFalse(newFolder1.exists());
        assertFalse(newFolder2.exists());
        assertFalse(newFolder3.exists());

        assertTrue(imageFolderService.createNewFolder(imageFolderDTO0));
        assertTrue(imageFolderService.createNewFolder(imageFolderDTO1));
        assertTrue(imageFolderService.createNewFolder(imageFolderDTO2));
        assertTrue(imageFolderService.createNewFolder(imageFolderDTO3));

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
    }
}
