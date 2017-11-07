package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageData;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.Format;
import imcode.util.io.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
@WebAppConfiguration
@Transactional
public class ImageServiceTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_IMAGE_INDEX = 1;
    private static final ImageDTO TEST_IMAGE_DTO = new ImageDTO(TEST_IMAGE_INDEX, TEST_DOC_ID);
    private static final int VERSION_INDEX = 0;

    @Autowired
    private ImageService imageService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private Function<Image, ImageDTO> imageToImageDTO;

    @Autowired
    private ImageDataInitializer imageDataInitializer;

    @Before
    public void setUp() throws Exception {
        versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);

        final UserDomainObject user = new UserDomainObject(1);
        Imcms.setUser(user);
    }

    @After
    public void tearDown() throws Exception {
        Imcms.removeUser();
    }

    @Test(expected = DocumentNotExistException.class)
    public void getImage_When_DocumentNotExist_Expect_Exception() {
        final int nonExistingDocId = 0;
        final ImageDTO imageDTO = new ImageDTO(TEST_IMAGE_INDEX, nonExistingDocId);
        imageService.getImage(imageDTO);// should throw exception
    }

    @Test
    public void getImage_When_NotExist_Expect_EmptyDTO() {
        final ImageDTO image = imageService.getImage(TEST_IMAGE_DTO);

        assertEquals(image, TEST_IMAGE_DTO);
    }

    @Test
    public void getImage_When_LoopEntryRefIsNull_Expect_EqualResult() {
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);
        final ImageDTO resultImage = imageService.getImage(TEST_IMAGE_DTO);

        assertEquals(imageDTO, resultImage);
    }

    @Test
    public void getImage_When_LoopEntryRefIsNotNull_Expect_EqualResult() {
        final LoopEntryRef loopEntryRef = new LoopEntryRef(1, 1);
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);
        final ImageDTO resultImage = imageService.getImage(imageDTO);

        assertEquals(imageDTO, resultImage);
    }

    @Test
    public void saveImage_When_LoopEntryRefIsNull_Expect_EqualResult() {
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final ImageDTO result = imageService.getImage(TEST_IMAGE_DTO);

        assertEquals(result, imageDTO);
    }

    @Test
    public void saveImage_When_LoopEntryRefIsNotNull_Expect_EqualResult() {
        final LoopEntryRef loopEntryRef = new LoopEntryRef(1, 1);
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final ImageDTO result = imageService.getImage(imageDTO);

        assertEquals(result, imageDTO);
    }

    @Test
    public void saveImage_When_ImageFileExists_Expect_GeneratedFileExists() throws IOException {
        final ImageDTO imageDTO = Value.with(new ImageDTO(), img -> {
            img.setIndex(TEST_IMAGE_INDEX);
            img.setDocId(TEST_DOC_ID);
            img.setPath("img1.jpg");
            img.setFormat(Format.JPEG);
            img.setLangCode("en");
            img.setName("img1");
            img.setWidth(100);
            img.setHeight(100);
        });

        imageService.saveImage(imageDTO);

        final ImageDTO result = imageService.getImage(imageDTO);

        assertNotNull(result);

        final File croppedImage = new File(ImcmsImageUtils.imagesPath, "generated/" + result.getGeneratedFilename());

        assertTrue(croppedImage.exists());
        assertTrue(FileUtility.forceDelete(croppedImage));
    }

    @Test
    public void saveImage_When_CroppingIsNotDefault_Expect_EqualCropping() throws IOException {
        final ImageData.CropRegion cropRegion = new ImageData.CropRegion(10, 10, 20, 20);
        final ImageDTO imageDTO = Value.with(new ImageDTO(), img -> {
            img.setIndex(TEST_IMAGE_INDEX);
            img.setDocId(TEST_DOC_ID);
            img.setPath("img1.jpg");
            img.setFormat(Format.JPEG);
            img.setLangCode("en");
            img.setName("img1");
            img.setWidth(100);
            img.setHeight(100);
            img.setCropRegion(cropRegion);
        });

        imageService.saveImage(imageDTO);

        final ImageDTO result = imageService.getImage(imageDTO);

        assertNotNull(result);

        final File croppedImage = new File(ImcmsImageUtils.imagesPath, "generated/" + result.getGeneratedFilename());
        assertEquals(result.getCropRegion(), cropRegion);
        assertTrue(FileUtility.forceDelete(croppedImage));
    }
}
