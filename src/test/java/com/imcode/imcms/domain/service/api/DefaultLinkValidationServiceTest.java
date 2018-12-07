package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.ValidationLink;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.TextRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.imcode.imcms.model.Text.Type.TEXT;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class DefaultLinkValidationServiceTest extends WebAppSpringTestConfig {

    private static final String TEXTS = "test";
    private static final String TEXT_URL = "<a href=\"https://www.google.com\">Test</a>";
    private static final String NOT_FOUND_URL_HTTPS_TEXT = "<a href=\"https://aaa.fff.ddd\">Test</a>";
    private static final String NOT_FOUND_URL_HTTP_TEXT = "<a href=\"http://aaa.fff.ddd\">Test</a>";
    private static final String NOT_REACHABLE_URL_IP = "<a href=\"http://a:0:a0a::\"> Test</a>";
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
    private CommonContentService commonContentService;
    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;
    @Autowired
    private DefaultLinkValidationService defaultLinkValidationService;

    private String getLinkFromText(String text) {
        Matcher m = LINK_VALIDATION_PATTERN.matcher(text);
        String extractedLink = m.group(1);
        return extractedLink;
    }

    @AfterEach
    public void clearTestData() {
        documentDataInitializer.cleanRepositories();
        languageDataInitializer.cleanRepositories();
        commonContentDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
        loopDataInitializer.cleanRepositories();
        urlDocumentDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }

    @Test
    public void validateDocumentLinks_When_TextNotValidUrl_Expected_EmptyResult() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version latestVersionDoc = versionService.create(docId, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, latestVersionDoc, TEXTS);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks, docId, docId);

        assertNotNull(links);
        assertTrue(links.isEmpty());
    }

    @Test
    public void validateDocumentLinks_When_UrlValidButNotFoundUrl_Expected_CorrectLinks() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version latestVersionDoc = versionService.create(docId, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));
        final List<CommonContent> commonContentDTOS = commonContentDataInitializer.createData(latestVersionDoc);

        commonContentDTOS.get(0).setHeadline("Test");

        commonContentService.save(docId, commonContentDTOS);

        createText(index, languageJPA, latestVersionDoc, NOT_FOUND_URL_HTTP_TEXT);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertEquals(commonContentDTOS.get(0).getHeadline(), link.getDocumentData().getTitle());

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());
        assertEquals(getLinkFromText(NOT_FOUND_URL_HTTP_TEXT), link.getUrl());
    }

    @Test
    public void validateDocumentLinks_When_ShowOnlyBrokenLinks_Expected_CorrectLinks() {

        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();
        final Version versionDoc1 = versionService.create(doc1Id, 1);
        final Version versionDoc2 = versionService.create(doc2Id, 1);

        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, versionDoc1, TEXT_URL);
        createText(index, languageJPA, versionDoc2, NOT_FOUND_URL_HTTPS_TEXT);

        final boolean displayOnlyBrokenLinks = true;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                doc1Id,
                doc2Id);

        assertNotNull(links);
        assertEquals(1, links.size());


        ValidationLink link = links.get(0);

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());

        assertEquals(getLinkFromText(NOT_FOUND_URL_HTTPS_TEXT), link.getUrl());
    }

    @Test
    public void validateDocumentLinks_When_ValidTextUrl_Expected_CorrectLinks() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version version = versionService.create(docId, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));
        final List<CommonContent> commonContentDTOS = commonContentDataInitializer.createData(version);
        commonContentDTOS.get(0).setHeadline("Test");

        commonContentService.save(docId, commonContentDTOS);

        createText(index, languageJPA, version, TEXT_URL);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertEquals(commonContentDTOS.get(0).getHeadline(), link.getDocumentData().getTitle());
        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
        assertEquals(getLinkFromText(TEXT_URL), link.getUrl());
    }

    @Test
    public void validateDocumentLinks_When_ImageValidUrl_Expected_CorrectLinks() {

        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setUrl(getLinkFromText(TEXT_URL));

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
        assertEquals(getLinkFromText(TEXT_URL), link.getUrl());
    }

    @Test
    public void validateDocumentLinks_When_ImageNotReachableUrl_Expected_CorrectLinks() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();

        final Version versionDoc = versionService.create(doc1Id, 1);

        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setUrl(getLinkFromText(NOT_REACHABLE_URL_IP));

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                doc1Id,
                doc1Id);

        assertNotNull(links);

        assertEquals(1, links.size());
        ValidationLink link = links.get(0);

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_UrlDocNotValidUrl_Expected_CorrectEntities() {
        final int index = 1;
        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument(TEXTS);
        final int docId = urlDocumentDTO.getId();
        final Version version = versionDataInitializer.createData(index, docId);

        urlDocumentDTO.setLatestVersion(AuditDTO.fromVersion(version));
        urlDocumentService.save(urlDocumentDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_UrlDocHasValidUrl_Expected_CorrectLinks() {

        final int index = 1;
        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument(TEXT_URL);
        final int docId = urlDocumentDTO.getId();
        final Version version = versionDataInitializer.createData(index, docId);

        urlDocumentDTO.setLatestVersion(AuditDTO.fromVersion(version));
        urlDocumentService.save(urlDocumentDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);
        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_UrlDocValidUrlNotReachable_Expected_CorrectLinks() {
        final int index = 1;
        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument(NOT_REACHABLE_URL_IP);
        final int docId = urlDocumentDTO.getId();
        final Version version = versionDataInitializer.createData(index, docId);

        urlDocumentDTO.setLatestVersion(AuditDTO.fromVersion(version));
        urlDocumentService.save(urlDocumentDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_LoopHasNotValidUrlInTextAndImage_Expected_CorrectLinks() {

        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(index, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setUrl(getLinkFromText(TEXTS));

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        createText(index, languageJPA, versionDoc, TEXTS, loopEntryRef);
        imageDataInitializer.generateImage(imageDTO.getIndex(), languageJPA, versionDoc, loopEntryRef);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_LoopHasValidUrlAndImageText_Expected_CorrectLinks() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);
        final LoopDTO loopDTO = new LoopDTO(docId, index, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);

        loopDataInitializer.createData(loopDTO);

        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setUrl(getLinkFromText(TEXT_URL));

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        createText(index, languageJPA, versionDoc, TEXT_URL, loopEntryRef);
        imageDataInitializer.generateImage(imageDTO.getIndex(), languageJPA, versionDoc, loopEntryRef);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_LoopHasNotReachableUrlText_Expected_CorrectLinks() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        final Version version = versionService.create(doc1Id, 1);
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));
        final Image image = imageDataInitializer.createData(index, version);

        image.setUrl(getLinkFromText(NOT_REACHABLE_URL_IP));

        final ImageDTO imageDTO1 = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO1);

        createText(index, languageJPA, version, NOT_FOUND_URL_HTTP_TEXT, loopEntryRef);
        imageDataInitializer.generateImage(imageDTO1.getIndex(), languageJPA, version, loopEntryRef);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                doc1Id,
                doc1Id);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_DocumentIdsHaveThisRange_Expected_CorrectSizeAndLinks() {
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
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                doc1Id,
                doc3Id);

        assertNotNull(links);
        assertEquals(3, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_DocumentHasTextImageAndLoopWithValidUrl_Expected_CorrectLinks() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version versionDoc = versionService.create(docId, 1);

        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));
        final Image image = imageDataInitializer.createData(index, versionDoc);

        image.setUrl(getLinkFromText(TEXT_URL));

        final ImageDTO imageDTO = imageToImageDTO.apply(image);

        imageService.saveImage(imageDTO);

        createText(index, languageJPA, versionDoc, TEXT_URL, loopEntryRef);
        createText(index, languageJPA, versionDoc, TEXT_URL);
        imageDataInitializer.generateImage(imageDTO.getIndex(), languageJPA, versionDoc, loopEntryRef);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(3, links.size());
    }

    @Test
    public void validateDocumentLinks_When_StartIdMoreThanEndId_Expected_EmptyResult() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();
        final Version versionDoc1 = versionService.create(doc1Id, 1);
        final Version versionDoc2 = versionService.create(doc1Id, 1);

        assertTrue(doc2Id > doc1Id);

        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        createText(index, languageJPA, versionDoc1, TEXT_URL);
        createText(index, languageJPA, versionDoc2, NOT_FOUND_URL_HTTPS_TEXT);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                doc2Id,
                doc1Id);

        assertNotNull(links);
        assertTrue(links.isEmpty());
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

