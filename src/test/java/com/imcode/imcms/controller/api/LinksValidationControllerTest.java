package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.ValidationLink;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.domain.service.api.DefaultLinkValidationService;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.TextRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.imcode.imcms.model.Text.Type.TEXT;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Transactional
public class LinksValidationControllerTest extends AbstractControllerTest {

    private static final String TEXTS = "test";
    private static final String TEXT_URL = "<a href=\"https://www.google.com\">Test</a>";
    private static final String NOT_FOUND_URL_HTTPS_TEXT = "<a href=\"https://aaa.fff.ddd\">Test</a>";
    private static final Pattern LINK_VALIDATION_PATTERN = Pattern.compile("href\\s*=\\s*\"(.*)\"");

    @Autowired
    private DocumentDataInitializer documentDataInitializer;
    @Autowired
    private LanguageDataInitializer languageDataInitializer;
    @Autowired
    private VersionService versionService;
    @Autowired
    private TextRepository textRepository;
    @Autowired
    private ImageDataInitializer imageDataInitializer;
    @Autowired
    private Function<Image, ImageDTO> imageToImageDTO;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UrlDocumentDataInitializer urlDocumentDataInitializer;
    @Autowired
    private DocumentService<UrlDocumentDTO> urlDocumentService;
    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private LoopDataInitializer loopDataInitializer;
    @Autowired
    private DefaultLinkValidationService defaultLinkValidationService;


    @Override
    protected String controllerPath() {
        return "/links";
    }

    private String getLinkFromText(String text) {
        Matcher m = LINK_VALIDATION_PATTERN.matcher(text);
        m.find();
        String extractedLink = m.group(1);
        return extractedLink;
    }

    @AfterEach
    public void clearTestData() {
        documentDataInitializer.cleanRepositories();
        languageDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
        loopDataInitializer.cleanRepositories();
        urlDocumentDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }

    @Test
    public void validateDocumentsLinks_When_TextNotValidUrl_Expected_EmptyResult() throws Exception {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version latestVersionDoc = versionService.create(docId, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, latestVersionDoc, TEXTS);
        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> validationLinks = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);
        assertNotNull(validationLinks);
        assertTrue(validationLinks.isEmpty());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(validationLinks));
    }

    @Test
    public void validateDocumentsLinks_When_TextValidUrl_Expected_OkAndCorrectLinks() throws Exception {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version latestVersionDoc = versionService.create(docId, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, latestVersionDoc, TEXT_URL);
        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> validationLinks = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);
        assertNotNull(validationLinks);
        assertEquals(1, validationLinks.size());
        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(validationLinks));
    }

    @Test
    public void validateDocumentsLinks_When_ImageTextValidUrl_Expected_OkAndCorrectLinks() throws Exception {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setLinkUrl(getLinkFromText(TEXT_URL));

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> validationLinks = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);
        assertNotNull(validationLinks);

        assertEquals(1, validationLinks.size());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(validationLinks));
    }

    @Test
    public void validateDocumentsLinks_When_ImageTextNotValidUrl_Expected_OkAndCorrectLinks() throws Exception {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setLinkUrl(TEXTS);

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> validationLinks = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);
        assertNotNull(validationLinks);
        assertEquals(2, validationLinks.size());
        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(validationLinks));
    }

    @Test
    public void validateDocumentLinks_When_ShowOnlyBrokenLinks_Expected_OkAndCorrectLinks() throws Exception {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();
        final Version versionDoc1 = versionService.create(doc1Id, 1);
        final Version versionDoc2 = versionService.create(doc2Id, 1);

        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, versionDoc1, TEXT_URL);
        createText(index, languageJPA, versionDoc2, NOT_FOUND_URL_HTTPS_TEXT);

        final boolean displayOnlyBrokenLinks = true;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(doc1Id, doc2Id, displayOnlyBrokenLinks);

        assertNotNull(links);
        assertEquals(1, links.size());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + doc1Id)
                .param("endDocumentId", "" + doc2Id);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(links));
    }

    @Test
    public void validateDocumentLinks_When_UrlDocValidUrlOnAllLanguages_Expected_OkAndCorrectEntities() throws Exception {
        final int index = 1;
        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument(TEXT_URL);
        final int docId = urlDocumentDTO.getId();
        final Version version = versionDataInitializer.createData(index, docId);
        final UserDomainObject user = new UserDomainObject(1);
        Imcms.setUser(user);
        urlDocumentDTO.setLatestVersion(AuditDTO.fromVersion(version));
        urlDocumentService.save(urlDocumentDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);

        assertNotNull(links);
        assertEquals(2, links.size());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(links));

    }

    @Test
    public void validateDocumentLinks_When_UrlDocNotValidUrlOnAllLanguages_Expected_OkAndCorrectEntities() throws Exception {
        final int index = 1;
        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument(TEXTS);
        final int docId = urlDocumentDTO.getId();
        final Version version = versionDataInitializer.createData(index, docId);
        final UserDomainObject user = new UserDomainObject(1);
        Imcms.setUser(user);
        urlDocumentDTO.setLatestVersion(AuditDTO.fromVersion(version));
        urlDocumentService.save(urlDocumentDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);

        assertNotNull(links);
        assertEquals(2, links.size());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(links));
    }

    @Test
    public void validateDocumentLinks_When_LoopHasNotValidUrlInTextAndImage_Expected_OkAndCorrectLinks() throws Exception {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(index, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setLinkUrl(TEXTS);

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        createText(index, languageJPA, versionDoc, TEXTS, loopEntryRef);
        imageDataInitializer.generateImage(imageDTO.getIndex(), languageJPA, versionDoc, loopEntryRef);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);

        assertNotNull(links);
        assertEquals(2, links.size());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(links));
    }

    @Test
    public void validateDocumentLinks_When_LoopHasValidUrlInTextAndImage_Expected_OkAndCorrectLinks() throws Exception {

        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(index, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setLinkUrl(getLinkFromText(TEXT_URL));

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        createText(index, languageJPA, versionDoc, TEXT_URL, loopEntryRef);
        imageDataInitializer.generateImage(imageDTO.getIndex(), languageJPA, versionDoc, loopEntryRef);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(docId, docId, displayOnlyBrokenLinks);

        assertNotNull(links);
        assertEquals(2, links.size());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + docId)
                .param("endDocumentId", "" + docId);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(links));
    }

    @Test
    public void validateDocumentLinks_When_DocumentIdsInRange_Expected_OkAndCorrectLinks() throws Exception {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();
        int doc3Id = documentDataInitializer.createData().getId();

        final Version versionDoc1 = versionService.create(doc1Id, 1);
        final Version versionDoc2 = versionService.create(doc2Id, 1);
        final Version versionDoc3 = versionService.create(doc3Id, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, versionDoc1, TEXT_URL);
        createText(index, languageJPA, versionDoc2, TEXT_URL);
        createText(index, languageJPA, versionDoc3, TEXT_URL);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(doc1Id, doc3Id, displayOnlyBrokenLinks);

        assertNotNull(links);
        assertEquals(3, links.size());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + doc1Id)
                .param("endDocumentId", "" + doc3Id);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(links));
    }

    @Test
    public void validateDocumentLinks_When_StartIdMoreThanEndId_Expected_EmptyResult() throws Exception {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();
        final Version versionDoc1 = versionService.create(doc1Id, 1);
        final Version versionDoc2 = versionService.create(doc2Id, 1);

        assertTrue(doc2Id > doc1Id);

        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, versionDoc1, TEXT_URL);
        createText(index, languageJPA, versionDoc2, NOT_FOUND_URL_HTTPS_TEXT);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(doc2Id, doc1Id, displayOnlyBrokenLinks);

        assertNotNull(links);
        assertTrue(links.isEmpty());

        final MockHttpServletRequestBuilder requestBuilder = get(controllerPath() + "/validate")
                .param("filterBrokenLinks", "" + displayOnlyBrokenLinks)
                .param("startDocumentId", "" + doc2Id)
                .param("endDocumentId", "" + doc1Id);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(links));
    }


    private void createText(int index, LanguageJPA language, Version version, String testText) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText(testText);
        text.setType(TEXT);
        text.setVersion(version);

        textRepository.saveAndFlush(text);
    }

    private void createText(int index, LanguageJPA language, Version version, String testText, LoopEntryRef loopEntryRef) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText(testText);
        text.setType(TEXT);
        text.setVersion(version);
        text.setLoopEntryRef(loopEntryRef);

        textRepository.saveAndFlush(text);
    }


}
