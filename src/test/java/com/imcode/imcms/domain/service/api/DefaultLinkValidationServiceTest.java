package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.ValidationLink;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import com.imcode.imcms.persistence.repository.TextRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.imcode.imcms.model.Text.Type.TEXT;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class DefaultLinkValidationServiceTest extends WebAppSpringTestConfig {

    private static final String TEXTS = "test";
    private static final String TEXT_URL = "<a href=\"https://www.google.com\">Test</a>";
    private static final String RELATIVE_TEXT = "<a href=\"imcms.dev.imcode.com/\">Test</a>";
    private static final String NOT_REACHABLE_URL_HTTPS_TEXT = "<a href=\"https://aaa.fff.ddd\">Test</a>";
    private static final String NOT_REACHABLE_URL_HTTP_TEXT = "<a href=\"http://aaa.fff.ddd\">Test</a>";
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
    private DocumentUrlRepository documentUrlRepository;
    @Autowired
    private LoopDataInitializer loopDataInitializer;
    @Mock
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
    }

    @Test
    public void validateDocumentLinks_When_TextNotValidUrl_Expected_EmptyResult() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final Version latestVersion = versionService.create(docId, 1);

        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);

        createText(index, languageJPA, latestVersion, TEXTS);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks, docId, docId);

        assertNotNull(links);
        assertTrue(links.isEmpty());
    }

    @Test
    public void validateDocumentLinks_When_LinkHostNotFound_Expected_CorrectLinks() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();

        final Version version = versionService.create(doc1Id, 1);
        final Version latestVersion = versionService.create(doc2Id, 1);

        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);

        createText(index, languageJPA, version, NOT_REACHABLE_URL_HTTP_TEXT);
        createText(index, languageJPA, latestVersion, NOT_REACHABLE_URL_HTTPS_TEXT);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                doc1Id,
                doc2Id);

        assertNotNull(links);
        assertEquals(2, links.size());


        ValidationLink link = links.get(0);

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());
        assertEquals(getLinkFromText(NOT_REACHABLE_URL_HTTP_TEXT), link.getUrl());

        link = links.get(1);

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isPageFound());

        assertEquals(getLinkFromText(NOT_REACHABLE_URL_HTTPS_TEXT), link.getUrl());

    }

    @Test
    public void validateDocumentLinks_When_ShowOnlyBrokenLinks_Expected_CorrectLinks() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();


        final Version version = versionService.create(doc1Id, 1);
        final Version latestVersion = versionService.create(doc2Id, 1);

        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);

        createText(index, languageJPA, version, TEXT_URL);
        createText(index, languageJPA, latestVersion, NOT_REACHABLE_URL_HTTPS_TEXT);

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

        assertEquals(getLinkFromText(NOT_REACHABLE_URL_HTTPS_TEXT), link.getUrl());

    }

    @Test // todo: need mock
    public void validateDocumentLinks_When_PageNotFound_Expected_CorrectLinks() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final boolean displayOnlyBrokenLinks = false;
        final Version latestVersion = versionService.create(docId, 1);

        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);

        createText(index, languageJPA, latestVersion, RELATIVE_TEXT);

        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertFalse(link.isPageFound());
    }

    @Test // todo: need mock
    public void validateDocumentLinks_When_TextUrlRelative_Expected_CorrectLinks() {
        final int index = 1;
        int docId = documentDataInitializer.createData().getId();
        final boolean displayOnlyBrokenLinks = true;
        final Version latestVersion = versionService.create(docId, 1);

        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);

        createText(index, languageJPA, latestVersion, RELATIVE_TEXT);

        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(displayOnlyBrokenLinks,
                docId,
                docId);

        assertNotNull(links);

        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertFalse(link.isPageFound());
    }

    @Test
    public void validateDocumentLinks_When_ValidTextUrl_Expected_CorrectLinks() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();


        final Version version = versionService.create(doc1Id, 1);

        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);

        createText(index, languageJPA, version, TEXT_URL);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                doc1Id,
                doc1Id);

        assertNotNull(links);

        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
        assertEquals(getLinkFromText(TEXT_URL), link.getUrl());

    }

    @Test
    public void validateDocumentLinks_When_ImageValidUrl_Expected_CorrectLinks() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();

        final Version version = versionService.create(doc1Id, 1);

        final Image image1 = imageDataInitializer.createData(index, version);

        image1.setUrl(getLinkFromText(TEXT_URL));

        final ImageDTO imageDTO1 = imageToImageDTO.apply(image1);

        imageService.saveImage(imageDTO1);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                doc1Id,
                doc1Id);

        assertNotNull(links);

        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
        assertEquals(getLinkFromText(TEXT_URL), link.getUrl());

    }

    @Test
    public void validateDocumentLinks_When_ImageNotValidUrl_Expected_EmptyResult() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();

        final Version version = versionService.create(doc1Id, 1);

        final Image image1 = imageDataInitializer.createData(index, version);

        image1.setUrl("www");

        final ImageDTO imageDTO1 = imageToImageDTO.apply(image1);

        imageService.saveImage(imageDTO1);

        int firstDoc = imageDTO1.getDocId();

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                firstDoc,
                firstDoc);

        assertNotNull(links);

        assertTrue(links.isEmpty());
    }

    @Test // todo: need mock
    public void validateDocumentLinks_When_ImageRelativeUrl_Expected_CorrectList() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();

        final Version version = versionService.create(doc1Id, 1);
        final Version latestVersion = versionService.create(doc2Id, 1);

        final Image image1 = imageDataInitializer.createData(index, version);

        final Image image2 = imageDataInitializer.createData(index, latestVersion);

        image1.setUrl(getLinkFromText(RELATIVE_TEXT));
        image2.setUrl(getLinkFromText(RELATIVE_TEXT));

        final ImageDTO imageDTO1 = imageToImageDTO.apply(image1);
        final ImageDTO imageDTO2 = imageToImageDTO.apply(image2);

        assertNotNull(imageDTO1);
        assertNotNull(imageDTO2);

        imageService.saveImage(imageDTO1);
        imageService.saveImage(imageDTO2);

        int firstDoc = imageDTO1.getDocId();
        int secondDoc = imageDTO2.getDocId();

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                firstDoc,
                secondDoc);

        assertNotNull(links);
    }

    @Test
    public void validateDocumentLinks_When_ImageShowOnlyBrokenLinks_Expected_CorrectList() {
        final int index = 1;
        int doc1Id = documentDataInitializer.createData().getId();
        int doc2Id = documentDataInitializer.createData().getId();

        final Version version = versionService.create(doc1Id, 1);
        final Version latestVersion = versionService.create(doc2Id, 1);

        final Image image1 = imageDataInitializer.createData(index, version);

        final Image image2 = imageDataInitializer.createData(index, latestVersion);

        image1.setUrl(getLinkFromText(TEXT_URL));
        image2.setUrl(getLinkFromText(NOT_REACHABLE_URL_HTTP_TEXT));

        final ImageDTO imageDTO1 = imageToImageDTO.apply(image1);
        final ImageDTO imageDTO2 = imageToImageDTO.apply(image2);

        assertNotNull(imageDTO1);
        assertNotNull(imageDTO2);

        imageService.saveImage(imageDTO1);
        imageService.saveImage(imageDTO2);

        int firstDoc = imageDTO1.getDocId();
        int secondDoc = imageDTO2.getDocId();

        final boolean displayOnlyBrokenLinks = true;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                firstDoc,
                secondDoc);

        assertNotNull(links);
        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertFalse(link.isHostFound());
        assertFalse(link.isHostReachable());
        assertFalse(link.isHostFound());

    }

    @Test
    public void validateDocumentLinks_When_UrlDocNotValidUrl_Expected_CorrectEntities() {
        final int index = 1;

        int doc1Id = documentDataInitializer.createData().getId();

        DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl(TEXTS);
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");

        final Version version = versionDataInitializer.createData(index, doc1Id);
        documentUrlJPA.setVersion(version);

        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument();

        final DocumentUrlDTO documentUrlDTO =
                new DocumentUrlDTO(documentUrlRepository.saveAndFlush(documentUrlJPA));

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        urlDocumentService.save(urlDocumentDTO);

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
    public void validateDocumentLinks_When_UrlDocHasValidUrl_Expected_CorrectLinks() {
        final int index = 1;

        int doc1Id = urlDocumentDataInitializer.createUrlDocument().getId();

        DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl(getLinkFromText(TEXT_URL));
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");

        final Version version = versionDataInitializer.createData(index, doc1Id);
        documentUrlJPA.setVersion(version);

        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument();

        final DocumentUrlDTO documentUrlDTO =
                new DocumentUrlDTO(documentUrlRepository.saveAndFlush(documentUrlJPA));

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        urlDocumentService.save(urlDocumentDTO);

        final boolean displayOnlyBrokenLinks = false;
        List<ValidationLink> links = defaultLinkValidationService.validateDocumentsLinks(
                displayOnlyBrokenLinks,
                doc1Id,
                doc1Id);

        assertNotNull(links);

        assertEquals(1, links.size());

        ValidationLink link = links.get(0);

        assertTrue(link.isHostFound());
        assertTrue(link.isHostReachable());
        assertTrue(link.isPageFound());
    }

    @Test //todo: need mock
    public void validateDocumentLinks_When_UrlDocHasRelativeUrl_Expected_CorrectLinks() {

    }

    @Test
    public void validateDocumentLinks_When_UrlDocValidUrlNotReachable_Expected_CorrectLinks() {
        final int index = 1;

        int doc1Id = documentDataInitializer.createData().getId();

        DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl(getLinkFromText(NOT_REACHABLE_URL_HTTP_TEXT));
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");

        final Version version = versionDataInitializer.createData(index, doc1Id);
        documentUrlJPA.setVersion(version);

        final UrlDocumentDTO urlDocumentDTO = urlDocumentDataInitializer.createUrlDocument();

        final DocumentUrlDTO documentUrlDTO =
                new DocumentUrlDTO(documentUrlRepository.saveAndFlush(documentUrlJPA));

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        urlDocumentService.save(urlDocumentDTO);

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

