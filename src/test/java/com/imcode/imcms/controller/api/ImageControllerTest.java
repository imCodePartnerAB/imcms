package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.ImageRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ImageControllerTest extends AbstractControllerTest {

    private static final int TEST_IMAGE_INDEX = 1;
    private static final int TEST_VERSION_INDEX = 0;
    private static int testDocId;
    private static ImageDTO testImageDto;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private Function<ImageJPA, ImageDTO> imageJPAToImageDTO;

    @Autowired
    private ImageDataInitializer imageDataInitializer;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LanguageService languageService;

    @Override
    protected String controllerPath() {
        return "/images";
    }

    @BeforeEach
    public void setUp() {
        documentDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();

        testDocId = documentDataInitializer.createData().getId();
        versionDataInitializer.createData(TEST_VERSION_INDEX, testDocId);
        testImageDto = new ImageDTO(TEST_IMAGE_INDEX, testDocId);

        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng"); // user lang should exist in common content
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @AfterEach
    public void tearDown() {
        Imcms.removeUser();
        documentDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
    }

    @Test
    public void getImage_Expect_Ok() throws Exception {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(testDocId))
                .param("index", String.valueOf(TEST_IMAGE_INDEX))
                .param("langCode", imageDTO.getLangCode());

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void getImage_When_ImageNotExist_Expect_OkAndEmptyDTO() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(testDocId))
                .param("index", String.valueOf(TEST_IMAGE_INDEX))
                .param("langCode", testImageDto.getLangCode());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(testImageDto));
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
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", String.valueOf(testDocId))
                .param("index", String.valueOf(TEST_IMAGE_INDEX))
                .param("langCode", imageDTO.getLangCode());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(imageDTO));
    }

    @Test
    public void getImage_When_LoopEntryRefIsNotNull_Expect_OkAndEqualContent() throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);
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

        performPostWithContentExpectException(testImageDto, NoPermissionToEditDocumentException.class);
    }

    @Test
    public void postImage_When_DocNotExist_Expect_Exception() throws Exception {
        final int nonExistingDocId = 0;
        final ImageDTO imageDTO = new ImageDTO(TEST_IMAGE_INDEX, nonExistingDocId);

        performPostWithContentExpectException(imageDTO, DocumentNotExistException.class);
    }

    @Test
    public void postImage_When_LoopEntryRefIsNull_Expect_Ok() throws Exception {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX, null);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        performPostWithContentExpectOk(imageDTO);
    }

    @Test
    public void postImage_When_LoopEntryRefIsNotNull_Expect_Ok() throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX, loopEntryRef);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        performPostWithContentExpectOk(imageDTO);
    }

    @Test
    public void postImage_When_DataChanged_Expect_CorrectSave() throws Exception {
        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX, null);
        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

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
        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, testDocId);

        final LoopEntryRefJPA loopEntryRefJPA = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        languageService.getAvailableLanguages()
                .forEach(language -> {
                    final ImageJPA image = imageDataInitializer
                            .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRefJPA);

                    image.setAllLanguages(true);
                    imageRepository.save(image);
                });

        final List<ImageJPA> images = imageRepository.findAll();

        assertEquals(languageService.getAvailableLanguages().size(), images.size());

        final ImageDTO imageDTO = imageJPAToImageDTO.apply(images.get(0));

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(0, imageRepository.findAll().size());
    }

    private void deleteImageWhenAllLanguagesFlagIsNotSet(boolean inLoop) throws Exception {
        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, testDocId);

        final LoopEntryRefJPA loopEntryRefJPA = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        languageService.getAvailableLanguages()
                .forEach(language -> {
                    final ImageJPA image = imageDataInitializer
                            .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRefJPA);

                    image.setAllLanguages(true);
                    imageRepository.save(image);
                });

        final List<ImageJPA> images = imageRepository.findAll();

        assertEquals(languageService.getAvailableLanguages().size(), images.size());

        final ImageJPA image = images.get(0);
        image.setAllLanguages(false);

        imageRepository.save(image);

        final ImageDTO imageDTO = imageJPAToImageDTO.apply(images.get(0));

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        imageRepository.findAll()
                .forEach(imageJPA -> assertFalse(imageJPA.isAllLanguages()));
    }

    private void testDeletingImage_WhenImageExists(boolean inLoop) throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);

        final ImageJPA image = inLoop
                ? imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX, loopEntryRef)
                : imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX);

        image.setGeneratedFilename("testGeneratedFilename");
        imageRepository.save(image);

        assertEquals(1, imageRepository.findAll().size());

        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(0, imageRepository.findAll().size());
    }

    private void testDeletingImage_WhenImageDoesNotExist(boolean inLoop) throws Exception {
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);

        final ImageJPA image = inLoop
                ? imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX, loopEntryRef)
                : imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX);

        final ImageDTO imageDTO = imageJPAToImageDTO.apply(image);

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

        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, testDocId);

        languageService.getAvailableLanguages().forEach(language -> {
            final ImageJPA image = imageDataInitializer
                    .generateImage(TEST_IMAGE_INDEX, new LanguageJPA(language), version, loopEntryRef);

            image.setAllLanguages(true);
            imageRepository.save(image);
        });

        imageRepository.findAll().forEach(image -> assertTrue(image.isAllLanguages()));

        final ImageJPA newImage = imageRepository.findAll().get(0);
        newImage.setAllLanguages(false);

        final ImageDTO imageDTO = imageJPAToImageDTO.apply(newImage);

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(imageDTO);
        performRequestBuilderExpectedOk(requestBuilder);

        imageRepository.findAll().forEach(image -> assertFalse(image.isAllLanguages()));
    }

    private void saveImageWhenAllLanguagesFlagIsSet(boolean inLoop) throws Exception {
        final LoopEntryRefJPA loopEntryRef = inLoop
                ? new LoopEntryRefJPA(1, 1)
                : null;

        final ImageJPA image = imageDataInitializer.createData(TEST_IMAGE_INDEX, testDocId, TEST_VERSION_INDEX, loopEntryRef);
        image.setAllLanguages(true);

        final ImageDTO expected = imageJPAToImageDTO.apply(image);

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(expected);
        performRequestBuilderExpectedOk(requestBuilder);

        languageService.getAvailableLanguages()
                .forEach(language -> {
                    final ImageDTO actual = imageService
                            .getImage(testDocId, TEST_IMAGE_INDEX, language.getCode(), loopEntryRef);

                    expected.setLangCode(language.getCode());

                    assertEquals(expected, actual);
                });
    }
}
