package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.*;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentDataDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.imcode.imcms.model.Text.Type.TEXT;
import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class DocumentDataServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private DocumentDataService documentDataService;
    @Autowired
    private DocumentService<DocumentDTO> documentService;
    @Autowired
    private TextService textService;
    @Autowired
    private VersionService versionService;
    @Autowired
    private DocumentContentDataInitializer documentContentDataInitializer;
    @Autowired
    private DocumentDataInitializer documentDataInitializer;
    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private LanguageDataInitializer languageDataInitializer;
    @Autowired
    private CategoryDataInitializer categoryDataInitializer;
    @Autowired
    private LanguageService languageService;

    @BeforeEach
    public void setUp() {
        documentContentDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();

        final UserDomainObject userSuperAdmin = new UserDomainObject(1);
        userSuperAdmin.setLanguageIso639_2("eng");
        userSuperAdmin.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(userSuperAdmin);

        final Language currentLanguage = languageDataInitializer.createData().get(0);
        Imcms.setLanguage(currentLanguage);
    }

    @Test
    public void getDataByDocIdAndAvailableLangs_When_DocumentExistAndVersionWorking_Expected_CorrectResult() {
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        final int docId = documentDTO.getId();

        assertEquals(documentDTO.getLatestVersion().getId(), (Integer) WORKING_VERSION_INDEX);

        Version version = versionDataInitializer.createData(WORKING_VERSION_INDEX, docId);
        final DocumentDataDTO documentDataDTO = documentContentDataInitializer.createData(version);
        assertNotNull(documentDataDTO);

        final DocumentDataDTO expectedDocumentDataDTO = documentDataService.getDataByDocIdAndAvailableLangs(docId);
        assertNotNull(expectedDocumentDataDTO);
        assertFalse(expectedDocumentDataDTO.getTextsDTO().isEmpty()
                && expectedDocumentDataDTO.getImagesDTO().isEmpty()
                && expectedDocumentDataDTO.getMenusDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getLoopsDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getTextsDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getImagesDTO().isEmpty()
                && expectedDocumentDataDTO.getCategoriesDTO().isEmpty());

        assertEquals(expectedDocumentDataDTO, documentDataDTO);
    }

    @Test
    public void getDataByDocIdAndAvailableLangs_When_DocumentHasLatestVersionAndContentHasWorkVersion_Expected_EmptyResult(){
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        final int docId = documentDTO.getId();

        assertEquals(documentDTO.getLatestVersion().getId(), (Integer) WORKING_VERSION_INDEX);

        //Publish document with empty content
        documentService.publishDocument(docId, Imcms.getUser().getId());
        final DocumentDTO publishedDocument = documentService.get(docId);
        assertNotEquals(publishedDocument.getLatestVersion().getId(), WORKING_VERSION_INDEX);

        //Modify data without publish
        Version version = versionDataInitializer.createData(WORKING_VERSION_INDEX, docId);
        final DocumentDataDTO documentDataDTO = documentContentDataInitializer.createData(version);

        assertFalse(documentDataDTO.getTextsDTO().isEmpty()
                && documentDataDTO.getImagesDTO().isEmpty()
                && documentDataDTO.getMenusDTO().isEmpty()
                && documentDataDTO.getLoopDataDTO().getLoopsDTO().isEmpty()
                && documentDataDTO.getLoopDataDTO().getTextsDTO().isEmpty()
                && documentDataDTO.getLoopDataDTO().getImagesDTO().isEmpty()
                && documentDataDTO.getCategoriesDTO().isEmpty());

        final DocumentDataDTO expectedDocumentDataDTO = documentDataService.getDataByDocIdAndAvailableLangs(docId);

        assertTrue(expectedDocumentDataDTO.getTextsDTO().isEmpty()
                && expectedDocumentDataDTO.getImagesDTO().isEmpty()
                && expectedDocumentDataDTO.getMenusDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getLoopsDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getTextsDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getImagesDTO().isEmpty());
    }

    @Test
    public void getDataByDocIdAndAvailableLangs_When_DocumentAndContentHaveDifferenceVersion_Expected_CorrectResult() {
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        final int docId = documentDTO.getId();

        assertEquals(documentDTO.getLatestVersion().getId(), (Integer) WORKING_VERSION_INDEX);

        //Fill the document with data and publish
        Version version = versionDataInitializer.createData(WORKING_VERSION_INDEX, docId);
        DocumentDataDTO documentDataDTO = documentContentDataInitializer.createData(version);

        boolean isPublished = documentService.publishDocument(docId, Imcms.getUser().getId());
        assertTrue(isPublished);

        final DocumentDTO publishedDocument = documentService.get(docId);
        assertNotEquals(publishedDocument.getLatestVersion().getId(), WORKING_VERSION_INDEX);

        //Get all document data and compare
        DocumentDataDTO expectedDocumentDataDTO = documentDataService.getDataByDocIdAndAvailableLangs(docId);
        assertEquals(expectedDocumentDataDTO, documentDataDTO);

        String langCode = languageDataInitializer.createData().get(0).getCode();

        //Modify data without publish and compare
        TextDTO textDTO = new TextDTO(1, docId, langCode, null);
        textDTO.setType(TEXT);
        textDTO.setText("edited text");

        final TextDTO savedText = new TextDTO(textService.save(textDTO));
        assertTrue(versionService.hasNewerVersion(docId));

        TextDTO publishedText = new TextDTO(textService.getPublicText(docId, 1, langCode, null));
        assertNotEquals(savedText, publishedText);

        //Publish and compare
        documentService.publishDocument(docId, Imcms.getUser().getId());
        assertEquals(savedText, documentDataService.getDataByDocIdAndAvailableLangs(docId).getTextsDTO().get(0));
    }

    @Test
    public void getDataByDocIdAndAvailableLangs_When_DocumentExistAndHasNotContent_Expected_EmptyResult() {
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        final DocumentDataDTO expectedDocumentDataDTO = documentDataService.getDataByDocIdAndAvailableLangs(documentDTO.getId());

        assertTrue(expectedDocumentDataDTO.getTextsDTO().isEmpty()
                && expectedDocumentDataDTO.getImagesDTO().isEmpty()
                && expectedDocumentDataDTO.getMenusDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getLoopsDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getTextsDTO().isEmpty()
                && expectedDocumentDataDTO.getLoopDataDTO().getImagesDTO().isEmpty()
                && expectedDocumentDataDTO.getCategoriesDTO().isEmpty());
    }

    @Test
    public void getDataByDocIdAndAvailableLangs_When_DocumentExistAndHasCategories_Expected_CorrectResult() {
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        final int docId = documentDTO.getId();

        DocumentDataDTO documentDataDTO = documentContentDataInitializer.createData();

        Set<CategoryDTO> categoriesDTO = categoryDataInitializer.createData(2).stream()
                .map(CategoryDTO::new).collect(Collectors.toSet());
        documentDTO.setCategories(new HashSet<>(categoriesDTO));
        documentService.save(documentDTO);
        documentDataDTO.setCategoriesDTO(categoriesDTO);

        DocumentDataDTO expectedDocumentDataDTO = documentDataService.getDataByDocIdAndAvailableLangs(docId);
        assertEquals(expectedDocumentDataDTO, documentDataDTO);
    }

    @Test
    public void getAllByDocId_When_DocumentNotExist_Expected_CorrectException(){
        int fakeId = 0;
        assertThrows(DocumentNotExistException.class, () -> documentDataService.getDataByDocIdAndAvailableLangs(fakeId));
    }

    @Test
    public void getDataByDocIdAndAvailableLangs_When_DocumentHasContentInAvailableLanguages_Expected_CorrectResult() {
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        final int docId = documentDTO.getId();

        final Version version = versionDataInitializer.createData(WORKING_VERSION_INDEX, docId);

        final DocumentDataDTO expectedDocumentDataDTO = documentContentDataInitializer.createData(version, languageService.getAvailableLanguages());

        assertEquals(expectedDocumentDataDTO, documentDataService.getDataByDocIdAndAvailableLangs(docId));
    }

    @Test
    public void getDataByDocIdAndAvailableLangs_When_DocumentHasContentInAvailableAndUnavailableLanguage_Expected_ContentOnlyInAvailableLanguage(){
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        final int docId = documentDTO.getId();

        final Version version = versionDataInitializer.createData(WORKING_VERSION_INDEX, docId);

        final List<Language> availableLanguages = languageService.getAvailableLanguages();
        final Language unavailableLanguage = languageDataInitializer.createData(List.of("kk")).get(0);

        assertFalse(availableLanguages.contains(unavailableLanguage));

        final DocumentDataDTO expectedDocumentDataDTO = documentContentDataInitializer.createData(version, List.of(availableLanguages.get(0), unavailableLanguage));

        expectedDocumentDataDTO.setTextsDTO(expectedDocumentDataDTO.getTextsDTO().stream()
                .filter(text -> !text.getLangCode().equals(unavailableLanguage.getCode()))
                .collect(Collectors.toList()));
        expectedDocumentDataDTO.getLoopDataDTO().setTextsDTO(expectedDocumentDataDTO.getLoopDataDTO().getTextsDTO().stream()
                .filter(text -> !text.getLangCode().equals(unavailableLanguage.getCode()))
                .collect(Collectors.toList()));

        expectedDocumentDataDTO.setImagesDTO(expectedDocumentDataDTO.getImagesDTO().stream()
                .filter(image -> !image.getLangCode().equals(unavailableLanguage.getCode()))
                .collect(Collectors.toList()));
        expectedDocumentDataDTO.getLoopDataDTO().setImagesDTO(expectedDocumentDataDTO.getLoopDataDTO().getImagesDTO().stream()
                .filter(image -> !image.getLangCode().equals(unavailableLanguage.getCode()))
                .collect(Collectors.toList()));

        assertEquals(expectedDocumentDataDTO, documentDataService.getDataByDocIdAndAvailableLangs(docId));
    }
}
