package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.exception.FolderAlreadyExistException;
import com.imcode.imcms.domain.exception.FolderNotExistException;
import com.imcode.imcms.domain.service.ImageFolderService;
import imcode.util.io.FileUtility;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ImageFolderServiceTest {

    private static final int SUB_FOLDERS_NUM = 2;
    private static final int FILES_NUM = 2;

    @Autowired
    private ImageFolderService imageFolderService;

    @Value("${ImagePath}")
    private File imagesPath;

    @Test
    public void getImageFolder_WhenPathIsEmpty_Expected_RootFolderIsReturned() {
        final ImageFolderDTO imageFolder = imageFolderService.getImageFolder(new ImageFolderDTO(null, ""));

        assertNotNull(imageFolder);
        assertThat(imageFolder.getName(), is("images"));
        assertThat(imageFolder.getPath(), is(""));
    }

    @Test
    public void getImageFolder_When_PathHasOneFolder_Expect_SpecifiedFolderIsReturned() throws IOException {

        final String name = String.valueOf(System.currentTimeMillis());
        final String path = "/" + name;

        final File rootSubFolder = createTestFolder(imagesPath, path);

        testGetImageFolder(name, path, rootSubFolder);

        assertTrue(FileUtility.forceDelete(rootSubFolder));
    }

    @Test
    public void getImageFolder_When_PathHasTwoFolders_Expect_SpecifiedFolderIsReturned() throws IOException {

        final String name = String.valueOf(System.currentTimeMillis());
        final String tempPath = "/" + name;

        final File rootSubFolder = createTestFolder(imagesPath, tempPath);
        final File rootSubSubFolder = createTestFolder(rootSubFolder, tempPath); // sub sub =)

        final String path = tempPath + tempPath;

        testGetImageFolder(name, path, rootSubSubFolder);

        assertTrue(FileUtility.forceDelete(rootSubFolder));
    }

    @Test
    public void createNewImageFolder_When_FolderNotExistBefore_Expect_FolderCreatedAndIsDirectoryAndReadableAndThenRemoved() throws IOException {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());
        assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
        assertTrue(newFolder.exists());
        assertTrue(newFolder.isDirectory());
        assertTrue(newFolder.canRead());
        assertTrue(FileUtility.forceDelete(newFolder));
    }

    @Test
    public void createNewImageFolder_When_FolderAlreadyExist_Expect_FolderCreationAndThenExceptionAndFolderRemove() throws IOException {
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
            assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    @Test
    public void createNewImageFolder_When_NestedFoldersToSave_Expect_FoldersCreatedAndAreDirectoriesAndReadableAndThenRemoved() throws IOException {
        final String newFolderName0 = "new_test_folder";
        final String newFolderPath0 = separator + newFolderName0;

        final String newFolderName1 = "nested1";
        final String newFolderPath1 = newFolderPath0 + separator + newFolderName1;

        final String newFolderName2 = "nested2";
        final String newFolderPath2 = newFolderPath1 + separator + newFolderName2;

        final String newFolderName3 = "nested3";
        final String newFolderPath3 = newFolderPath1 + separator + newFolderName3;

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

            assertTrue(FileUtility.forceDelete(newFolder3));
            assertTrue(FileUtility.forceDelete(newFolder2));
            assertTrue(FileUtility.forceDelete(newFolder1));
            assertTrue(FileUtility.forceDelete(newFolder0));

        } finally {
            if (newFolder3.exists()) assertTrue(FileUtility.forceDelete(newFolder3));
            if (newFolder2.exists()) assertTrue(FileUtility.forceDelete(newFolder2));
            if (newFolder1.exists()) assertTrue(FileUtility.forceDelete(newFolder1));
            if (newFolder0.exists()) assertTrue(FileUtility.forceDelete(newFolder0));
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
            assertTrue(FileUtility.forceDelete(renamedFolder));

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
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
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
            if (renamedFolder.exists()) assertTrue(FileUtility.forceDelete(renamedFolder));
        }
    }

    @Test
    public void renameFolder_When_FolderExistsNestedInRootImagesDirectory_Expect_True() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        assertFalse(newFolder.exists());

        final String newNestedFolderName = "nested_folder";
        final String path = newFolderName + separator + newNestedFolderName;
        final File newNestedFolder = new File(imagesPath, path);
        final ImageFolderDTO imageNestedFolderDTO = new ImageFolderDTO(newNestedFolderName, separator + path);

        assertFalse(newNestedFolder.exists());

        final String nestedFolderNewName = "new_name";
        final File renamedFolder = new File(imagesPath, newFolderName + separator + nestedFolderNewName);

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
            assertTrue(FileUtility.forceDelete(renamedFolder));

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
            if (newNestedFolder.exists()) assertTrue(FileUtility.forceDelete(newNestedFolder));
            if (renamedFolder.exists()) assertTrue(FileUtility.forceDelete(renamedFolder));
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

            final String folderNewName = newFolderName;
            final File renamedFolder = new File(imagesPath, folderNewName);
            imageFolderDTO1.setName(folderNewName);

            assertTrue(renamedFolder.exists());
            imageFolderService.renameFolder(imageFolderDTO1); // exception expected here!

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
            if (newFolder1.exists()) assertTrue(FileUtility.forceDelete(newFolder1));
        }
    }

    @Test
    public void deleteFolder_When_FolderExist_Expect_TrueAndFolderDeleted() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try {
            assertFalse(newFolder.exists());
            assertTrue(imageFolderService.createImageFolder(imageFolderDTO));
            assertTrue(newFolder.exists());
            assertTrue(newFolder.isDirectory());

            assertTrue(imageFolderService.deleteFolder(imageFolderDTO));
            assertFalse(newFolder.exists());

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    @Test(expected = FolderNotExistException.class)
    public void deleteFolder_When_FolderNotExist_Expect_CorrectException() throws Exception {
        final String newFolderName = "new_test_folder";
        final File newFolder = new File(imagesPath, newFolderName);
        final ImageFolderDTO imageFolderDTO = new ImageFolderDTO(newFolderName);

        try {
            assertFalse(newFolder.exists());
            imageFolderService.deleteFolder(imageFolderDTO); // exception expected here!

        } finally {
            if (newFolder.exists()) assertTrue(FileUtility.forceDelete(newFolder));
        }
    }

    private void testGetImageFolder(String name, String path, File rootSubFolder) throws IOException {
        final List<Pair<String, String>> namePathPairFolders = new ArrayList<>();

        for (int i = 0; i < SUB_FOLDERS_NUM; i++) {
            final String folderName = String.valueOf(i);

            createTestFolder(rootSubFolder, folderName);

            namePathPairFolders.add(Pair.of(folderName, path + "/" + folderName));
        }

        final List<Pair<String, String>> namePathPairFiles = new ArrayList<>();

        for (int i = 0; i < FILES_NUM; i++) {
            final File testPngImage = createTestPngImage(path);
            final String imgName = testPngImage.getName();

            namePathPairFiles.add(Pair.of(imgName, path + "/" + imgName));
        }

        final ImageFolderDTO imageFolder = imageFolderService.getImageFolder(new ImageFolderDTO(null, path));

        assertNotNull(imageFolder);

        assertThat(imageFolder.getName(), is(name));
        assertThat(imageFolder.getPath(), is(path));

        final List<ImageFolderDTO> subFolders = imageFolder.getFolders();
        final List<ImageFileDTO> files = imageFolder.getFiles();

        assertThat(subFolders, hasSize(SUB_FOLDERS_NUM));
        assertThat(files, hasSize(FILES_NUM));

        subFolders.stream()
                .map(imageFolderDTO -> Pair.of(imageFolderDTO.getName(), imageFolderDTO.getPath()))
                .forEach(pair -> assertTrue(namePathPairFolders.contains(pair)));

        files.stream()
                .map(imageFileDTO -> Pair.of(imageFileDTO.getName(), imageFileDTO.getPath()))
                .forEach(pair -> assertTrue(namePathPairFiles.contains(pair)));
    }

    private File createTestPngImage(String path) throws IOException {
        final BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        final File file = new File(imagesPath, path + "/" + System.currentTimeMillis() + ".png");

        ImageIO.write(img, "png", file);

        return file;
    }

    private File createTestFolder(File parent, String path) {
        final File folder = new File(parent, path);
        final boolean mkdir = folder.mkdir();

        if (mkdir) {
            return folder;
        } else {
            throw new RuntimeException("Error during folder creation.");
        }
    }
}
