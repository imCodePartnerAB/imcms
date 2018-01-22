package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageData.ImageCropRegionDTO;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ImageServiceTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_IMAGE_INDEX = 1;
    private static final ImageDTO TEST_IMAGE_DTO = new ImageDTO(TEST_IMAGE_INDEX, TEST_DOC_ID);
    private static final int VERSION_INDEX = 0;

    @org.springframework.beans.factory.annotation.Value("${ImagePath}")
    private File imagesPath;

    @Autowired
    private ImageService imageService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private Function<Image, ImageDTO> imageToImageDTO;

    @Autowired
    private ImageDataInitializer imageDataInitializer;

    private Version workingVersion;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Autowired
    private LanguageRepository languageRepository;

    @Before
    public void setUp() throws Exception {
        workingVersion = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng"); // user lang should exist in common content
        Imcms.setUser(user);
    }

    @After
    public void tearDown() {
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
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
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
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
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

        final File croppedImage = new File(imagesPath, "generated/" + result.getGeneratedFilename());

        assertTrue(croppedImage.exists());
        assertTrue(FileUtility.forceDelete(croppedImage));
    }

    @Test
    public void saveImage_When_CroppingIsNotDefault_Expect_EqualCropping() throws IOException {
        final ImageCropRegionDTO cropRegion = new ImageCropRegionDTO(10, 10, 20, 20);
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

        ImageDTO result = null;

        try {
            result = imageService.getImage(imageDTO);

            assertNotNull(result);
            assertEquals(result.getCropRegion(), cropRegion);

        } finally {
            if (result != null) {
                final File croppedImage = new File(imagesPath, "generated/" + result.getGeneratedFilename());
                assertTrue(FileUtility.forceDelete(croppedImage));
            }
        }

    }

    @Test
    public void getByVersion() {
        int i = TEST_IMAGE_INDEX;

        final Set<ImageDTO> expected = Stream.of(
                imageDataInitializer.createData(++i, TEST_DOC_ID, VERSION_INDEX),
                imageDataInitializer.createData(++i, TEST_DOC_ID, VERSION_INDEX),
                imageDataInitializer.createData(++i, TEST_DOC_ID, VERSION_INDEX),
                imageDataInitializer.createData(++i, TEST_DOC_ID, VERSION_INDEX))
                .map(imageToImageDTO)
                .collect(Collectors.toSet());

        final Set<ImageDTO> actual = imageService.getByVersion(workingVersion);

        assertEquals(actual.size(), expected.size());
        assertEquals(actual, expected);
    }

    @Test
    public void createVersionedContent() {
        int testImageIndex = TEST_IMAGE_INDEX;
        final ImageDTO workingVersionImage = imageToImageDTO.apply(
                imageDataInitializer.createData(++testImageIndex, TEST_DOC_ID, VERSION_INDEX));

        final Version latestVersion = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);

        imageService.createVersionedContent(workingVersion, latestVersion);

        final Set<ImageDTO> latestVersionImages = imageService.getByVersion(latestVersion);

        assertEquals(1, latestVersionImages.size());
        assertTrue(latestVersionImages.contains(workingVersionImage));
    }

    @Test
    public void deleteByDocId_Expect_Deleted() {
        imageDataInitializer.cleanRepositories();
        assertTrue(imageRepository.findAll().isEmpty());

        final Version version = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);
        final Version newVersion = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);

        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, 1, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        loopDataInitializer.createData(loopDTO);

        final List<LanguageJPA> languages = languageRepository.findAll();
        final Version[] versions = {version, newVersion};
        final LoopEntryRefJPA[] loops = {loopEntryRef, null};

        for (int i = 0; i < 20; i++) {
            for (LanguageJPA language : languages) {
                for (Version vers : versions) {
                    for (LoopEntryRefJPA loopEntryRefJPA : loops) {
                        imageDataInitializer.generateImage(i, language, vers, loopEntryRefJPA);
                    }
                }
            }
        }

        assertFalse(imageRepository.findAll().isEmpty());

        imageRepository.deleteByDocId(TEST_DOC_ID);

        assertTrue(imageRepository.findAll().isEmpty());
    }

    @Test
    public void getPublicImageLinks_When_FewVersionExist_Expect_Found() {
        assertTrue(imageRepository.findAll().isEmpty());

        final Version workingVersion = versionDataInitializer.createData(VERSION_INDEX, TEST_DOC_ID);
        final Version newVersion = versionDataInitializer.createData(VERSION_INDEX + 1, TEST_DOC_ID);
        final int loopIndex = 1;
        final int loopEntryIndex = 1;
        final LoopDTO loopDTO = new LoopDTO(
                TEST_DOC_ID, loopIndex, Collections.singletonList(LoopEntryDTO.createEnabled(loopEntryIndex))
        );
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopIndex, loopEntryIndex);
        loopDataInitializer.createData(loopDTO);

        final List<LanguageJPA> languages = languageRepository.findAll();
        final Version[] versions = {workingVersion, newVersion};
        final int imagesPerVersionPerLanguage = 20;
        final String testLinkUrl = "link_url";

        for (Version version : versions) {
            for (LanguageJPA language : languages) {
                IntStream.range(TEST_IMAGE_INDEX, TEST_IMAGE_INDEX + imagesPerVersionPerLanguage)
                        .forEach(index -> {
                            final Image image = new Image();
                            image.setIndex(index);
                            image.setLanguage(language);
                            image.setVersion(version);
                            image.setLoopEntryRef((index % 2 == 0) ? loopEntryRef : null);
                            image.setFormat(Format.JPEG);
                            image.setLinkUrl(testLinkUrl + index);
                            imageRepository.save(image);
                        });
                IntStream.range(TEST_IMAGE_INDEX + imagesPerVersionPerLanguage, TEST_IMAGE_INDEX + (2 * imagesPerVersionPerLanguage))
                        .forEach(index -> {
                            final Image image = new Image();
                            image.setIndex(index);
                            image.setLanguage(language);
                            image.setVersion(version);
                            image.setLoopEntryRef((index % 2 == 0) ? loopEntryRef : null);
                            image.setFormat(Format.JPEG);
                            image.setLinkUrl("");
                            imageRepository.save(image);
                        });
            }
        }

        assertFalse(imageRepository.findAll().isEmpty());

        for (LanguageJPA language : languages) {
            final Set<String> links = imageService.getPublicImageLinks(TEST_DOC_ID, language);

            links.forEach(s -> assertTrue(s.startsWith(testLinkUrl)));
        }
    }
}
