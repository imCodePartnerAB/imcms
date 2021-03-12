package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.exception.ImpossibleRemoveLanguageException;
import com.imcode.imcms.domain.exception.LanguageNotAvailableException;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static imcode.server.ImcmsConstants.LANGUAGES;
import static imcode.server.ImcmsConstants.SWE_CODE;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class LanguageServiceTest extends WebAppSpringTestConfig {

    private final String testCodeLanguage = "te";
    private final String testNameLanguage = "testName";
    private final String testNativeLanguage = "testNative";
    private final int defaultAmountLang = 39;

    @Value("#{'${AvailableLanguages}'.split(';')}")
    private List<String> availableLanguages;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private LanguageRepository languageRepository; //need to for one of test =)

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @BeforeEach
    public void setUp() {
        documentDataInitializer.cleanRepositories();
        languageDataInitializer.cleanRepositories();
    }

    // 0 - en
    // 1 - sv
    //default we have 39 languages in db

    @Test
    public void getAvailableLngs_When_TwoLanguagesAvailable_Expected_CorrectResult() {
        assertEquals(languageDataInitializer.createData(availableLanguages), languageService.getAvailableLanguages());
    }

    @Test
    public void getAll_Expected_CorrectResultAndSize() {
        assertEquals(languageDataInitializer.createData(LANGUAGES), languageService.getAll());
        assertEquals(languageDataInitializer.createData(LANGUAGES).size(), languageService.getAll().size());
    }

    @Test
    public void findByCode_When_LanguageAvailable_Expected_CorrectResult() {
        final LanguageDTO expectedLanguage = languageDataInitializer.createData().get(0);
        assertEquals(expectedLanguage, languageService.findByCode(expectedLanguage.getCode()));
    }

    @Test
    public void findByCode_When_LanguageNotAvailable_Expected_CorrectException() {
        assertThrows(LanguageNotAvailableException.class, () -> languageService.findByCode("unknown"));
    }

    @Test
    public void getDefaultLang_When_LanguageSetEng_Expected_CorrectLang() {
        final LanguageDTO expected = languageDataInitializer.createData().get(0);
        final LanguageDTO result = new LanguageDTO(languageService.getDefaultLanguage());

        assertEquals(expected.getCode(), result.getCode());
        assertEquals(expected, result);
    }

    @Test
    public void getEnabledLanguagesByDocId_WhenDocumentHasAllEnabledLanguages_Expected_CorrectResult() {
        languageDataInitializer.createData();
        final DocumentDTO documentDTO = documentDataInitializer.createData();

        final List<Language> langsDocument = documentDTO
                .getCommonContents()
                .stream()
                .map(CommonContent::getLanguage)
                .collect(Collectors.toList());

        assertEquals(langsDocument, languageService.getEnabledContentLanguagesByDocId(documentDTO.getId()));
    }

    @Test
    public void getEnabledLanguagesByDocId_WhenDocumentHasSwedishDisabledLanguage_Expected_CorrectResult() {
        languageDataInitializer.createData();
        final DocumentDTO documentDTO = documentDataInitializer.createData(Meta.PublicationStatus.APPROVED,true, false);

        final List<Language> enabledLanguagesByDocId = languageService.getEnabledContentLanguagesByDocId(documentDTO.getId());

        assertFalse(enabledLanguagesByDocId.isEmpty());
        assertEquals(1, enabledLanguagesByDocId.size());


        assertEquals(getEnabledCommonContentLang(documentDTO), enabledLanguagesByDocId);
    }

    @Test
    public void getEnabledLanguagesByDocId_WhenDocumentHasEnglishDisabledLanguage_Expected_CorrectResult() {
        languageDataInitializer.createData();
        final DocumentDTO documentDTO = documentDataInitializer.createData(Meta.PublicationStatus.APPROVED,false, true);

        final List<Language> enabledLanguagesByDocId = languageService.getEnabledContentLanguagesByDocId(documentDTO.getId());

        assertFalse(enabledLanguagesByDocId.isEmpty());
        assertEquals(1, enabledLanguagesByDocId.size());


        assertEquals(getEnabledCommonContentLang(documentDTO), enabledLanguagesByDocId);
    }

    @Test
    public void getEnabledLanguagesByDocId_WhenDocumentDoesNotHasEnabledLanguages_Expected_EmptyResult() {
        languageDataInitializer.createData();
        final DocumentDTO documentDTO = documentDataInitializer.createData(Meta.PublicationStatus.APPROVED,false, false);

        assertTrue(languageService.getEnabledContentLanguagesByDocId(documentDTO.getId()).isEmpty());
    }

    @Test
    public void saveLanguage_When_LanguageDoNotHasName_Expected_CorrectException() {
        Language language = new LanguageDTO(testCodeLanguage, "", testNativeLanguage);

        assertThrows(DataIsNotValidException.class, () -> languageService.save(language));
    }

    @Test
    public void saveLanguage_When_LanguageNotHasNativeName_Expected_CorrectException() {
        Language language = new LanguageDTO(testCodeLanguage, testNameLanguage, "");

        assertThrows(DataIsNotValidException.class, () -> languageService.save(language));
    }

    @Test
    public void saveLanguage_When_LanguageNotHasCode_Expected_CorrectException() {
        Language language = new LanguageDTO("", testNameLanguage, testNativeLanguage);

        assertThrows(DataIsNotValidException.class, () -> languageService.save(language));
    }

    @Test
    public void saveLanguage_When_LanguageExists_Expected_CorrectObjectResult() {
        final List<LanguageDTO> languages = languageDataInitializer.createData();
        final Language engLang = languages.get(0);

        assertEquals(engLang.getCode(), ENG_CODE);
        assertEquals(engLang, languageService.findByCode(ENG_CODE));

        final Language testLanguage = new LanguageDTO(ENG_CODE, testNameLanguage, testNativeLanguage);
        languageService.save(testLanguage);
        final Language savedLanguage = languageService.findByCode(ENG_CODE);

        final String savedNameLang = savedLanguage.getName();
        final String savedNativeNameLang = savedLanguage.getNativeName();

        assertNotEquals(engLang, savedLanguage);
        assertNotEquals(engLang.getName(), savedNameLang);
        assertNotEquals(engLang.getNativeName(), savedNativeNameLang);

        assertEquals(testNameLanguage, savedNameLang);
        assertEquals(testNativeLanguage, savedNativeNameLang);
    }

    @Test
    public void saveLanguage_When_LanguageNotExist_Expected_CreatedNewLanguage() {
        assertEquals(defaultAmountLang, languageService.getAll().size());

        final Language testLanguage = new LanguageDTO(testCodeLanguage, testNameLanguage, testNativeLanguage);
        languageService.save(testLanguage);

        final List<Language> allLanguages = languageService.getAll();

        final int expectedAmountLangs = defaultAmountLang + 1;

        assertFalse(allLanguages.isEmpty());
        assertEquals(expectedAmountLangs, allLanguages.size());

        final Language savedLanguage = languageRepository.findByCode(testCodeLanguage);

        assertEquals(testCodeLanguage, savedLanguage.getCode());
        assertEquals(testNameLanguage, savedLanguage.getName());
        assertEquals(testNativeLanguage, savedLanguage.getNativeName());
    }

    @Test
    public void deleteByCode_When_LanguageExists_Expected_CorrectDelete() {
        assertEquals(defaultAmountLang, languageService.getAll().size());
        assertNotNull(languageService.findByCode(SWE_CODE));

        languageService.deleteByCode(SWE_CODE);

        assertEquals(defaultAmountLang - 1, languageService.getAll().size());
    }

    @Test
    public void deleteByCode_When_NewLanguage_Expected_CorrectRemove() {
        assertEquals(defaultAmountLang, languageService.getAll().size());
        assertNotNull(languageService.findByCode(SWE_CODE));

        final DocumentDTO doc1 = documentDataInitializer.createData();
        final DocumentDTO doc2 = documentDataInitializer.createData();

        assertEquals(2, doc1.getCommonContents().size());
        assertEquals(2, doc2.getCommonContents().size());

        final Language testLanguage = new LanguageDTO(testCodeLanguage, testNameLanguage, testNativeLanguage);
        languageService.save(testLanguage);

        assertEquals(defaultAmountLang + 1, languageService.getAll().size());

        languageService.deleteByCode(testCodeLanguage);

        assertEquals(defaultAmountLang, languageService.getAll().size());
    }

    @Test
    public void deleteByCode_When_RemoveDefaultLanguage_Expected_CorrectException() {
        assertEquals(defaultAmountLang, languageService.getAll().size());
        assertNotNull(languageService.findByCode(ENG_CODE));

        assertThrows(ImpossibleRemoveLanguageException.class, () -> languageService.deleteByCode(ENG_CODE));
    }

    @Test
    public void deleteByCode_When_DocumentUsingLanguage_Expected_CorrectException() {
        assertEquals(defaultAmountLang, languageService.getAll().size());
        assertNotNull(languageService.findByCode(SWE_CODE));

        documentDataInitializer.createData();

        assertThrows(ImpossibleRemoveLanguageException.class, () -> languageService.deleteByCode(SWE_CODE));
    }

    private List<Language> getEnabledCommonContentLang(DocumentDTO documentDTO) {
         return documentDTO
                .getCommonContents()
                .stream()
                 .filter(CommonContent::isEnabled)
                .map(CommonContent::getLanguage)
                .collect(Collectors.toList());
    }
}
