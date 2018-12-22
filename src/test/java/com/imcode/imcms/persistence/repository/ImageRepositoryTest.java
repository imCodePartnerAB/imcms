package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import imcode.util.image.Format;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ImageRepositoryTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    private static final int VERSION_INDEX = 0;
    private static final int IMAGE_INDEX = 1;

    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private LoopDataInitializer loopDataInitializer;
    @Autowired
    private ImageDataInitializer imageDataInitializer;

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private LanguageRepository languageRepository;

    private Version version;
    private LanguageJPA english;
    private LanguageJPA swedish;

    @BeforeEach
    public void setUp() {
        imageDataInitializer.cleanRepositories();
        assertTrue(imageRepository.findAll().isEmpty()); // for clean results

        version = versionDataInitializer.createData(VERSION_INDEX, DOC_ID);
        english = languageRepository.findByCode("en");
        swedish = languageRepository.findByCode("sv");
    }

    @Test
    public void findByVersionAndLanguageWhereLoopEntryRefIsNull() {
        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, null);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, null);

        List<Image> images = imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNull(version, english);

        assertTrue(images.size() > 0);
        assertEquals(imageEng, images.get(0));

        images = imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNull(version, swedish);

        assertTrue(images.size() > 0);
        assertEquals(imageSwe, images.get(0));
    }

    @Test
    public void findByVersionAndLanguageWhereLoopEntryRefIsNotNull() {
        final LoopDTO loopDTO = new LoopDTO(DOC_ID, 1, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);

        loopDataInitializer.createData(loopDTO);

        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, loopEntryRef);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, loopEntryRef);

        List<Image> images = imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, english);

        assertTrue(images.size() > 0);
        assertEquals(imageEng, images.get(0));

        images = imageRepository.findByVersionAndLanguageWhereLoopEntryRefIsNotNull(version, swedish);

        assertTrue(images.size() > 0);
        assertEquals(imageSwe, images.get(0));
    }

    @Test
    public void findByVersionAndIndexWhereLoopEntryRefIsNull() {
        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, null);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, null);
        final List<Image> images = imageRepository.findByVersionAndIndexWhereLoopEntryRefIsNull(version, 1);

        assertTrue(images.contains(imageSwe) && images.contains(imageEng));
    }

    @Test
    public void findByVersionAndIndexAndLoopEntryRef() {
        final LoopDTO loopDTO = new LoopDTO(DOC_ID, 1, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        loopDataInitializer.createData(loopDTO);

        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, loopEntryRef);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, loopEntryRef);
        final List<Image> images = imageRepository.findByVersionAndIndexAndLoopEntryRef(version, 1, loopEntryRef);

        assertTrue(images.contains(imageSwe) && images.contains(imageEng));
    }

    @Test
    public void findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull() {
        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, null);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, null);

        final Image imageEngResult = imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, english, 1);
        final Image imageSweResult = imageRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, swedish, 1);

        assertEquals(imageSwe, imageSweResult);
        assertEquals(imageEng, imageEngResult);
    }

    @Test
    public void findByVersionAndLanguageAndIndexAndLoopEntryRef() {
        final LoopDTO loopDTO = new LoopDTO(DOC_ID, 1, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        loopDataInitializer.createData(loopDTO);

        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, loopEntryRef);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, loopEntryRef);

        final Image imageEngResult = imageRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, english, 1, loopEntryRef);
        final Image imageSweResult = imageRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, swedish, 1, loopEntryRef);

        assertEquals(imageSwe, imageSweResult);
        assertEquals(imageEng, imageEngResult);
    }

    @Test
    public void findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull() {
        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, null);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, null);

        final Integer imageEngId = imageRepository.findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, english, 1);
        final Integer imageSweId = imageRepository.findIdByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, swedish, 1);

        assertEquals(imageSwe.getId(), imageSweId);
        assertEquals(imageEng.getId(), imageEngId);
    }

    @Test
    public void findIdByVersionAndLanguageAndIndexAndLoopEntryRef() {
        final LoopDTO loopDTO = new LoopDTO(DOC_ID, 1, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        loopDataInitializer.createData(loopDTO);

        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, loopEntryRef);
        final Image imageSwe = imageDataInitializer.generateImage(IMAGE_INDEX, swedish, version, loopEntryRef);

        final Integer imageEngId = imageRepository.findIdByVersionAndLanguageAndIndexAndLoopEntryRef(version, english, 1, loopEntryRef);
        final Integer imageSweId = imageRepository.findIdByVersionAndLanguageAndIndexAndLoopEntryRef(version, swedish, 1, loopEntryRef);

        assertEquals(imageSwe.getId(), imageSweId);
        assertEquals(imageEng.getId(), imageEngId);
    }

    @Test
    public void findAllRegenerationCandidates_When_AllPossibleCasesExist_Expect_OnlyOneFound() {
        final Image image = new Image(); // not empty url, genFile = null - should be found
        image.setIndex(1);
        image.setLanguage(swedish);
        image.setVersion(version);
        image.setLoopEntryRef(null);
        image.setFormat(Format.JPEG);
        image.setUrl("dummy");
        imageRepository.save(image);

        final Image image1 = new Image(); // empty url, genFile = null - should not be found
        image1.setIndex(2);
        image1.setLanguage(swedish);
        image1.setVersion(version);
        image1.setLoopEntryRef(null);
        image1.setFormat(Format.JPEG);
        image1.setUrl("");
        imageRepository.save(image1);

        final Image image2 = new Image(); // null url, genFile = null - should not be found
        image2.setIndex(3);
        image2.setLanguage(swedish);
        image2.setVersion(version);
        image2.setLoopEntryRef(null);
        image2.setFormat(Format.JPEG);
        image2.setUrl("");
        imageRepository.save(image2);

        final Image image3 = new Image(); // null url, empty genFile - should not be found
        image3.setIndex(4);
        image3.setLanguage(swedish);
        image3.setVersion(version);
        image3.setLoopEntryRef(null);
        image3.setFormat(Format.JPEG);
        image3.setGeneratedFilename("");
        imageRepository.save(image3);

        final Image image4 = new Image(); // null url, not empty genFile - should be found
        image4.setIndex(5);
        image4.setLanguage(swedish);
        image4.setVersion(version);
        image4.setLoopEntryRef(null);
        image4.setFormat(Format.JPEG);
        image4.setGeneratedFilename("not_empty");
        imageRepository.save(image4);

        final Image image5 = new Image(); // empty url, empty genFile - should not be found
        image5.setIndex(6);
        image5.setLanguage(swedish);
        image5.setVersion(version);
        image5.setLoopEntryRef(null);
        image5.setFormat(Format.JPEG);
        image5.setGeneratedFilename("");
        image5.setUrl("");
        imageRepository.save(image5);

        final Image image6 = new Image(); // not empty url, not empty genFile - should be found
        image6.setIndex(7);
        image6.setLanguage(swedish);
        image6.setVersion(version);
        image6.setLoopEntryRef(null);
        image6.setFormat(Format.JPEG);
        image6.setGeneratedFilename("not_empty");
        image6.setUrl("not_empty");
        imageRepository.save(image6);

        final Image image7 = new Image(); // not empty url, empty genFile - should be found
        image7.setIndex(8);
        image7.setLanguage(swedish);
        image7.setVersion(version);
        image7.setLoopEntryRef(null);
        image7.setFormat(Format.JPEG);
        image7.setUrl("not_empty");
        image7.setGeneratedFilename("");
        imageRepository.save(image7);

        final Image image8 = new Image(); // empty url, not empty genFile - should be found
        image8.setIndex(9);
        image8.setLanguage(swedish);
        image8.setVersion(version);
        image8.setLoopEntryRef(null);
        image8.setFormat(Format.JPEG);
        image8.setUrl("");
        image8.setGeneratedFilename("not_empty");
        imageRepository.save(image8);

        final List<Image> images = new ArrayList<>(imageRepository.findAllRegenerationCandidates());

        assertEquals(5, images.size());
        assertTrue(images.containsAll(Arrays.asList(image, image4, image6, image7, image8)));
    }

    @Test
    public void deleteByVersionAndLanguage() {
        final Image imageEng = imageDataInitializer.generateImage(IMAGE_INDEX, english, version, null);
        List<Image> images = imageRepository.findAll();

        assertEquals(1, images.size());
        assertEquals(images.get(0), imageEng);

        imageRepository.deleteByVersionAndLanguage(version, english);
        images = imageRepository.findAll();

        assertTrue(images.isEmpty());
    }

    @Test
    public void deleteByDocId() {
        assertTrue(imageRepository.findAll().isEmpty());

        final Version newVersion = versionDataInitializer.createData(VERSION_INDEX + 1, DOC_ID);

        final LoopDTO loopDTO = new LoopDTO(DOC_ID, 1, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        loopDataInitializer.createData(loopDTO);

        final LanguageJPA[] languages = {english, swedish};
        final Version[] versions = {version, newVersion};
        final LoopEntryRefJPA[] loops = {loopEntryRef, null};

        for (int i = IMAGE_INDEX; i < IMAGE_INDEX + 20; i++) {
            for (LanguageJPA language : languages) {
                for (Version version : versions) {
                    for (LoopEntryRefJPA loopEntryRefJPA : loops) {
                        imageDataInitializer.generateImage(i, language, version, loopEntryRefJPA);
                    }
                }
            }
        }

        assertFalse(imageRepository.findAll().isEmpty());

        imageRepository.deleteByDocId(DOC_ID);

        assertTrue(imageRepository.findAll().isEmpty());
    }

    @Test
    public void findImageLinkUrlByVersionAndLanguage() {
        assertTrue(imageRepository.findAll().isEmpty());

        final Version newVersion = versionDataInitializer.createData(VERSION_INDEX + 1, DOC_ID);
        final int loopIndex = 1;
        final int loopEntryIndex = 1;
        final LoopDTO loopDTO = new LoopDTO(
                DOC_ID, loopIndex, Collections.singletonList(LoopEntryDTO.createEnabled(loopEntryIndex))
        );
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(loopIndex, loopEntryIndex);
        loopDataInitializer.createData(loopDTO);

        final LanguageJPA[] languages = {english, swedish};
        final Version[] versions = {version, newVersion};
        final int imagesPerVersionPerLanguage = 20;
        final String testLinkUrl = "link_url";

        for (Version version : versions) {
            for (LanguageJPA language : languages) {
                IntStream.range(IMAGE_INDEX, IMAGE_INDEX + imagesPerVersionPerLanguage)
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
                IntStream.range(IMAGE_INDEX + imagesPerVersionPerLanguage, IMAGE_INDEX + (2 * imagesPerVersionPerLanguage))
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

        for (Version version : versions) {
            for (LanguageJPA language : languages) {
                final Set<String> links = imageRepository.findNonEmptyImageLinkUrlByVersionAndLanguage(version, language);

                links.forEach(s -> assertTrue(s.startsWith(testLinkUrl)));
            }
        }

    }

    @Test
    public void findByVersionAndLanguageImage() {
        assertTrue(imageRepository.findAll().isEmpty());

        final Version newVersion = versionDataInitializer.createData(VERSION_INDEX + 1, DOC_ID);
        final LanguageJPA[] languages = {english, swedish};
        final Version[] versions = {version, newVersion};
        final int imagesPerVersionPerLanguage = 20;
        final String testLinkUrl = "link_url";

        for (Version version : versions) {
            for (LanguageJPA language : languages) {
                IntStream.range(IMAGE_INDEX, IMAGE_INDEX + imagesPerVersionPerLanguage)
                        .forEach(index -> {
                            final Image image = new Image();
                            image.setIndex(index);
                            image.setLanguage(language);
                            image.setVersion(version);
                            image.setFormat(Format.JPEG);
                            image.setLinkUrl(testLinkUrl + index);
                            imageRepository.save(image);
                        });
            }

        }

        assertFalse(imageRepository.findAll().isEmpty());

        for (Version version : versions) {
            for (LanguageJPA language : languages) {
                Set<Image> images = imageRepository.findByVersionAndLanguage(version, language);

                assertFalse(images.isEmpty());
                images.forEach(image -> assertTrue(image.getLinkUrl().startsWith(testLinkUrl)));
            }
        }
    }

    @Test
    public void findMinIndexByVersion_When_SomeRegularImagesExist_Expect_MinReturned() {
        final int minIndex = IMAGE_INDEX;

        IntStream.range(minIndex, minIndex + 10)
                .forEach(index -> imageDataInitializer.generateImage(index, english, version, null));

        final Integer minIndexByVersion = imageRepository.findMinIndexByVersion(DOC_ID);

        assertNotNull(minIndexByVersion);
        assertEquals(minIndex, minIndexByVersion.intValue());
    }

    @Test
    public void findMinIndexByVersion_When_SomeNegativeIndexExist_Expect_MinReturned() {
        final int minIndex = IMAGE_INDEX - 10;

        IntStream.range(minIndex, IMAGE_INDEX + 10)
                .forEach(index -> imageDataInitializer.generateImage(index, english, version, null));

        final Integer minIndexByVersion = imageRepository.findMinIndexByVersion(DOC_ID);

        assertNotNull(minIndexByVersion);
        assertEquals(minIndex, minIndexByVersion.intValue());
    }
}
