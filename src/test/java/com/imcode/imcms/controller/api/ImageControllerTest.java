package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class ImageControllerTest extends AbstractControllerTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_IMAGE_INDEX = 1;
    private static final int TEST_VERSION_INDEX = 0;
    private static final ImageDTO TEST_IMAGE_DTO = new ImageDTO(TEST_IMAGE_INDEX);

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private Function<Image, ImageDTO> imageToImageDTO;

    @Override
    protected String controllerPath() {
        return "/image";
    }

    @Before
    public void setUp() throws Exception {
        commonContentDataInitializer.cleanRepositories();
        commonContentDataInitializer.createData(TEST_DOC_ID, TEST_VERSION_INDEX);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("en"); // user lang should exist in common content
        Imcms.setUser(user);
    }

    @After
    public void tearDown() throws Exception {
        Imcms.removeUser();
    }

    @Test
    public void controllerGetRequest_Expect_Ok() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(TEST_DOC_ID))
                .param("index", String.valueOf(TEST_IMAGE_INDEX));

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void controllerGetRequest_When_ImageNotExist_Expect_OkAndEmptyDTO() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(TEST_DOC_ID))
                .param("index", String.valueOf(TEST_IMAGE_INDEX));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(TEST_IMAGE_DTO));
    }

    @Test
    public void controllerGetRequest_When_DocumentNotExist_Expect_Exception() throws Exception {
        final int nonExistingDocId = 0;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(nonExistingDocId))
                .param("index", String.valueOf(TEST_IMAGE_INDEX));

        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
    }

    @Test
    public void controllerGetRequest_When_ImageExist_Expect_OkAndEqualContent() throws Exception {
        final Image image = Value.with(new Image(), img -> {
            img.setIndex(TEST_IMAGE_INDEX);
            img.setLanguage(languageRepository.findByCode("en"));
            img.setVersion(versionRepository.findWorking(TEST_DOC_ID));
        });
        imageRepository.save(image);

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(TEST_DOC_ID))
                .param("index", String.valueOf(TEST_IMAGE_INDEX));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(imageDTO));
    }

    @Test
    public void postImage_When_UserIsNotAdmin_Expect_IllegalAccessException() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        Imcms.setUser(user); // means current user is default user

        performPostWithContentExpectException(TEST_IMAGE_DTO, IllegalAccessException.class);
    }
}
