package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

public class ImageControllerTest extends AbstractControllerTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_IMAGE_INDEX = 1;
    private static final int TEST_VERSION_INDEX = 0;
    private static final ImageDTO TEST_IMAGE_DTO = new ImageDTO(TEST_IMAGE_INDEX, TEST_DOC_ID);

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private Function<Image, ImageDTO> imageToImageDTO;

    @Autowired
    private ImageDataInitializer imageDataInitializer;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    protected String controllerPath() {
        return "/images";
    }

    @Before
    public void setUp() throws Exception {
        versionDataInitializer.createData(TEST_VERSION_INDEX, TEST_DOC_ID);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng"); // user lang should exist in common content
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @After
    public void tearDown() {
        Imcms.removeUser();
    }

    @Test
    public void getImage_Expect_Ok() throws Exception {
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(TEST_DOC_ID))
                .param("index", String.valueOf(TEST_IMAGE_INDEX))
                .param("langCode", imageDTO.getLangCode());

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getImage_When_ImageNotExist_Expect_OkAndEmptyDTO() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(TEST_DOC_ID))
                .param("index", String.valueOf(TEST_IMAGE_INDEX))
                .param("langCode", TEST_IMAGE_DTO.getLangCode());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(TEST_IMAGE_DTO));
    }

    @Test
    public void getImage_When_DocumentNotExist_Expect_Exception() throws Exception {
        final int nonExistingDocId = 0;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(nonExistingDocId))
                .param("index", String.valueOf(TEST_IMAGE_INDEX));

        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
    }

    @Test
    public void getImage_When_ImageExist_Expect_OkAndEqualContent() throws Exception {
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(TEST_DOC_ID))
                .param("index", String.valueOf(TEST_IMAGE_INDEX))
                .param("langCode", imageDTO.getLangCode());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(imageDTO));
    }

    @Test
    public void getImage_When_LoopEntryRefIsNotNull_Expect_OkAndEqualContent() throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(imageDTO.getDocId()))
                .param("index", String.valueOf(imageDTO.getIndex()))
                .param("loopEntryRef.loopIndex", String.valueOf(loopEntryRef.getLoopIndex()))
                .param("loopEntryRef.loopEntryIndex", String.valueOf(loopEntryRef.getLoopEntryIndex()))
                .param("langCode", imageDTO.getLangCode());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(imageDTO));
    }

    @Test
    public void postImage_When_UserIsNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        user.setLanguageIso639_2("eng"); // user lang should exist in common content
        Imcms.setUser(user); // means current user is default user

        performPostWithContentExpectException(TEST_IMAGE_DTO, NoPermissionToEditDocumentException.class);
    }

    @Test
    public void postImage_When_DocNotExist_Expect_Exception() throws Exception {
        final int nonExistingDocId = 0;
        final ImageDTO imageDTO = new ImageDTO(TEST_IMAGE_INDEX, nonExistingDocId);

        performPostWithContentExpectException(imageDTO, DocumentNotExistException.class);
    }

    @Test
    public void postImage_When_LoopEntryRefIsNull_Expect_Ok() throws Exception {
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX, null);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        performPostWithContentExpectOk(imageDTO);
    }

    @Test
    public void postImage_When_LoopEntryRefIsNotNull_Expect_Ok() throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        performPostWithContentExpectOk(imageDTO);
    }

    @Test
    public void postImage_When_DataChanged_Expect_CorrectSave() throws Exception {
        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX, null);
        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        final MockHttpServletRequestBuilder getImageReqBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(imageDTO.getDocId()))
                .param("index", String.valueOf(imageDTO.getIndex()))
                .param("langCode", imageDTO.getLangCode());

        final String imageJson = getJsonResponse(getImageReqBuilder);
        final ImageDTO imageDtoResult = fromJson(imageJson, ImageDTO.class);

        assertEquals(imageDTO, imageDtoResult);

        imageDtoResult.setWidth(100);

        assertNotEquals(imageDTO, imageDtoResult);
        performPostWithContentExpectOk(imageDtoResult);

        final String imageChangedJson = getJsonResponse(getImageReqBuilder);
        final ImageDTO imageDtoChangedResult = fromJson(imageChangedJson, ImageDTO.class);

        assertEquals(imageDtoResult, imageDtoChangedResult);
        assertNotEquals(imageDTO, imageDtoChangedResult);

        imageDTO.setWidth(100);

        assertEquals(imageDTO, imageDtoChangedResult);
    }

    @Test
    public void saveImage_When_InLoopAndAllLanguagesFlagIsSet_Expect_ImageSavedForAllLanguages() throws Exception {
        saveImageWhenAllLanguagesFlagIsSet(true);
    }

    @Test
    public void saveImage_When_NotInLoopAndAllLanguagesFlagIsSet_Expect_ImageSavedForAllLanguages() throws Exception {
        saveImageWhenAllLanguagesFlagIsSet(false);
    }

    @Test
    public void saveImage_When_InLoopAndFlagAllLanguagesIsFalse_Expect_ImagesWithDiffLangCodeHaveFalseFlag()
            throws Exception {

        saveImageWhenAllLanguagesFlagIsNotSet(true);
    }

    @Test
    public void saveImage_When_NotInLoopAndFlagAllLanguagesIsFalse_Expect_ImagesWithDiffLangCodeHaveFalseFlag()
            throws Exception {

        saveImageWhenAllLanguagesFlagIsNotSet(false);
    }

    @Test
    public void deleteImage_When_InLoopAndImageExists_Expect_ImageIsDeleted() throws Exception {
        testDeletingImage_WhenImageExists(true);
    }

    @Test
    public void deleteImage_When_NotInLoopAndImageExists_Expect_ImageIsDeleted() throws Exception {
        testDeletingImage_WhenImageExists(false);
    }

    @Test
    public void deleteImageContent_When_InLoopAndImageDoesNotExist_Expect_ImageIsNotSavedInDatabase() throws Exception {
        testDeletingImage_WhenImageDoesNotExist(true);
    }

    @Test
    public void deleteImageContent_When_NotInLoopAndImageDoesNotExist_Expect_ImageIsNotSavedInDatabase()
            throws Exception {

        testDeletingImage_WhenImageDoesNotExist(false);
    }

    @Test
    public void deleteImage_When_InLoopAndAllLanguagesFlagIsSet_Expect_AllImagesWithDiffLangCodeAreDeleted()
            throws Exception {

        deleteImageWhenAllLanguagesFlagIsSet(true);
    }

    @Test
    public void deleteImage_When_NotInLoopAndAllLanguagesFlagIsSet_Expect_AllImagesWithDiffLangCodeAreDeleted()
            throws Exception {

        deleteImageWhenAllLanguagesFlagIsSet(false);
    }

    @Test
    public void deleteImage_When_InLoopAndAllLanguagesIsNotSet_Expect_AllImagesWithDiffLangCodeHaveFalseFlag()
            throws Exception {

        deleteImageWhenAllLanguagesFlagIsNotSet(true);
    }

    @Test
    public void deleteImage_When_NotInLoopAndAllLanguagesIsNotSet_Expect_AllImagesWithDiffLangCodeHaveFalseFlag()
            throws Exception {

        deleteImageWhenAllLanguagesFlagIsNotSet(false);
    }

    private void deleteImageWhenAllLanguagesFlagIsSet(boolean inLoop) throws Exception {
        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, TEST_DOC_ID);

        final LoopEntryRefJPA loopEntryRefJPA = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        languageRepository.findAll()
                .forEach(language -> {
                    final Image image = imageDataInitializer
                            .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRefJPA);

                    image.setAllLanguages(true);
                    imageRepository.save(image);
                });

        final List<Image> images = imageRepository.findAll();

        assertEquals(languageRepository.findAll().size(), images.size());

        final ImageDTO imageDTO = imageToImageDTO.apply(images.get(0));

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(0, imageRepository.findAll().size());
    }

    private void deleteImageWhenAllLanguagesFlagIsNotSet(boolean inLoop) throws Exception {
        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, TEST_DOC_ID);

        final LoopEntryRefJPA loopEntryRefJPA = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        languageRepository.findAll()
                .forEach(language -> {
                    final Image image = imageDataInitializer
                            .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRefJPA);

                    image.setAllLanguages(true);
                    imageRepository.save(image);
                });

        final List<Image> images = imageRepository.findAll();

        assertEquals(languageRepository.findAll().size(), images.size());

        final Image image = images.get(0);
        image.setAllLanguages(false);

        imageRepository.save(image);

        final ImageDTO imageDTO = imageToImageDTO.apply(images.get(0));

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        imageRepository.findAll()
                .forEach(imageJPA -> assertFalse(imageJPA.isAllLanguages()));
    }

    private void testDeletingImage_WhenImageExists(boolean inLoop) throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);

        final Image image = inLoop
                ? imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX, loopEntryRef)
                : imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX);

        image.setGeneratedFilename("testGeneratedFilename");
        imageRepository.save(image);

        assertEquals(1, imageRepository.findAll().size());

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(0, imageRepository.findAll().size());
    }

    private void testDeletingImage_WhenImageDoesNotExist(boolean inLoop) throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);

        final Image image = inLoop
                ? imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX, loopEntryRef)
                : imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX);

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageRepository.delete(image);

        assertEquals(0, imageRepository.findAll().size());

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(0, imageRepository.findAll().size());
    }

    private void saveImageWhenAllLanguagesFlagIsNotSet(boolean inLoop) throws Exception {
        final LoopEntryRefJPA loopEntryRef = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, TEST_DOC_ID);

        languageRepository.findAll().forEach(language -> {
            final Image image = imageDataInitializer
                    .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRef);

            image.setAllLanguages(true);
            imageRepository.save(image);
        });

        imageRepository.findAll().forEach(image -> assertTrue(image.isAllLanguages()));

        final Image newImage = imageRepository.findAll().get(0);
        newImage.setAllLanguages(false);

        final ImageDTO imageDTO = imageToImageDTO.apply(newImage);

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        imageRepository.findAll().forEach(image -> assertFalse(image.isAllLanguages()));
    }

    private void saveImageWhenAllLanguagesFlagIsSet(boolean inLoop) throws Exception {
        final LoopEntryRefJPA loopEntryRef = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        final Image image = imageDataInitializer.createData(TEST_IMAGE_INDEX, TEST_DOC_ID, TEST_VERSION_INDEX, loopEntryRef);
        image.setAllLanguages(true);

        final ImageDTO expected = imageToImageDTO.apply(image);

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(expected);
        performRequestBuilderExpectedOk(requestBuilder);

        languageRepository.findAll()
                .forEach(language -> {
                    final ImageDTO actual = imageService
                            .getImage(TEST_DOC_ID, TEST_IMAGE_INDEX, language.getCode(), loopEntryRef);

                    expected.setLangCode(language.getCode());

                    assertEquals(expected, actual);
                });
    }
}
