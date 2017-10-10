package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ImageServiceTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_IMAGE_INDEX = 1;
    private static final ImageDTO TEST_IMAGE_DTO = new ImageDTO(TEST_IMAGE_INDEX);

    @Autowired
    private ImageService imageService;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private Function<Image, ImageDTO> imageToImageDTO;

    @Before
    public void setUp() throws Exception {
        commonContentDataInitializer.cleanRepositories();
        commonContentDataInitializer.createData(TEST_DOC_ID, 0);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("en"); // user lang should exist in common content
        Imcms.setUser(user);
    }

    @After
    public void tearDown() throws Exception {
        Imcms.removeUser();
    }

    @Test(expected = DocumentNotExistException.class)
    public void getImage_When_DocumentNotExist_Expect_Exception() {
        int nonExistingDocId = 0;
        imageService.getImage(nonExistingDocId, TEST_IMAGE_INDEX);// should throw exception
    }

    @Test
    public void getImage_When_NotExist_Expect_EmptyDTO() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("en");
        Imcms.setUser(user);

        final ImageDTO image = imageService.getImage(TEST_DOC_ID, TEST_IMAGE_INDEX);

        assertEquals(image, TEST_IMAGE_DTO);
    }

    @Test
    public void getImage_When_ImageExist_Expect_EqualResult() {
        final Image image = Value.with(new Image(), img -> {
            img.setIndex(TEST_IMAGE_INDEX);
            img.setLanguage(languageRepository.findByCode("en"));
            img.setVersion(versionRepository.findWorking(TEST_DOC_ID));
        });
        imageRepository.save(image);

        final ImageDTO imageDTO = imageToImageDTO.apply(image);
        final ImageDTO resultImage = imageService.getImage(TEST_DOC_ID, TEST_IMAGE_INDEX);

        assertEquals(imageDTO, resultImage);
    }
}
