package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.TextDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import com.imcode.imcms.persistence.repository.TextRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.imcode.imcms.model.Text.Type.HTML;
import static com.imcode.imcms.model.Text.Type.TEXT;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TextServiceTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    private static final int WORKING_VERSION_NO = 0;
    private static final int LATEST_VERSION_NO = 1;
    private static final String ENG_CODE = "en";
    private static final String SWE_CODE = "sv";
    private static final int MIN_TEXT_INDEX = 1;
    private static final int MAX_TEXT_INDEX = 10;

    @Autowired
    private TextService textService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private TextHistoryRepository textHistoryRepository;
    private Version workingVersion;
    private Version latestVersion;
    private List<LanguageJPA> languages;

    @Autowired
    private TextDataInitializer textDataInitializer;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @BeforeAll
    public static void setUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now
    }

    @BeforeEach
    public void setUp() {
        textDataInitializer.cleanRepositories();
        textHistoryRepository.deleteAll();
        documentDataInitializer.cleanRepositories();

        workingVersion = versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);
        latestVersion = versionDataInitializer.createData(LATEST_VERSION_NO, DOC_ID);

        languages = Arrays.asList(languageRepository.findByCode(ENG_CODE), languageRepository.findByCode(SWE_CODE));
        // both langs should already be created
    }

    @Test
    public void testTextServiceInjected() {
        assertNotNull(textService);
    }

    @Test
    public void getText_When_NotInLoop_Expect_CorrectDTO() {
        final List<Text> textDTOS = new ArrayList<>();

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final TextJPA text = new TextJPA();
                text.setIndex(index);
                text.setLanguage(language);
                text.setText("test");
                text.setType(TEXT);
                text.setVersion(workingVersion);

                textRepository.save(text);
                textDTOS.add(new TextDTO(text));
            }
        }

        for (Text textDTO : textDTOS) {
            final Text savedText = textService.getText(textDTO);
            assertEquals(savedText, textDTO);
        }
    }


    @Test
    public void getTexts_When_TextInLoop_Expected_EqualResult() {
        final Integer idNewDoc = documentDataInitializer.createData().getId();
        final Version workVersion = versionDataInitializer.createData(WORKING_VERSION_NO, idNewDoc);
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        final LanguageJPA enLang = languages.get(0);
        final String testText = "someTextTest";

        textDataInitializer.createText(1, enLang, workVersion, testText, loopEntryRef);

        List<TextJPA> resultTexts = textService.getByDocId(idNewDoc);

        assertFalse(resultTexts.isEmpty());

        assertEquals(1, resultTexts.size());
        assertEquals(textService.getText(1, testText), resultTexts);

    }

    @Test
    public void getByDocId_When_LikePublishTrueButTextHasWorkVersion_Expected_EqualResult() {
        final Integer idNewDoc = documentDataInitializer.createData().getId();
        final Version workVersion = versionDataInitializer.createData(WORKING_VERSION_NO, idNewDoc);

        final LanguageJPA enLang = languages.get(0);

        final String testText = "someTextTest";

        textDataInitializer.createLikePublishedText(1, enLang, workVersion, testText, null);

        List<TextJPA> resultTexts = textService.getByDocId(idNewDoc);

        assertFalse(resultTexts.isEmpty());

        assertEquals(1, resultTexts.size());
        assertEquals(textService.getText(1, testText), resultTexts);

    }

    @Test
    public void getTextsLikePublished_When_LikePublishedFalseAndLoopExistsAndTextHasWorkVersion_Expected_EmptyResult() {
        final int index = 1;
        final String testText = "testText";
        final LanguageJPA engLang = languages.get(0);
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        textDataInitializer.createText(index, engLang, workingVersion, testText, loopEntryRef);

        final Text receivedTestText = textService.getText(DOC_ID, index, engLang.getCode(), loopEntryRef);

        assertNotNull(receivedTestText);
        assertEquals(testText, receivedTestText.getText());

        assertTrue(textService.getLikePublishedTexts(DOC_ID, engLang).isEmpty());
    }

    @Test
    public void getTextLikePublished_When_LikePublishedFalseAndLoopExistsAndTextHasWorkVersion_Expected_NUllResult() {
        final int index = 1;
        final String testText = "testText";
        final LanguageJPA engLang = languages.get(0);
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);
        textDataInitializer.createText(index, engLang, workingVersion, testText, loopEntryRef);

        final Text receivedTestText = textService.getText(DOC_ID, index, engLang.getCode(), loopEntryRef);

        assertNotNull(receivedTestText);
        assertEquals(testText, receivedTestText.getText());

        assertNull(textService.getLikePublishedText(DOC_ID, index, engLang.getCode(), loopEntryRef));
    }

    @Test
    public void getTextsLikePublished_When_LikePublishedFalse_Expected_EmptyResult() {
        final Integer documentId = documentDataInitializer.createData().getId();
        versionDataInitializer.createData(LATEST_VERSION_NO, documentId);
        final LanguageJPA engLang = languages.get(0);

        final Set<Text> createdTexts = createTexts();

        assertFalse(createdTexts.isEmpty());

        assertTrue(textService.getLikePublishedTexts(documentId, engLang).isEmpty());
    }

    @Test
    public void getTextsLikePublished_When_LikePublishedTrueButTextHasWorkVersion_Expected_EqualResult() {
        final int index = 1;
        final String testText = "testText";
        final LanguageJPA engLang = languages.get(0);
        final Integer docId = documentDataInitializer.createData().getId();
        final Version workVersion = versionDataInitializer.createData(WORKING_VERSION_NO, docId);

        textDataInitializer.createText(index, engLang, workVersion, testText);
        textDataInitializer.createText(index + 1, engLang, workVersion, testText);

        assertFalse(textService.getByDocId(docId).isEmpty());

        assertTrue(textService.getLikePublishedTexts(docId, engLang).isEmpty());

        final String publishedTestText = testText + 1;

        textDataInitializer.createLikePublishedText(index + 2, engLang, workVersion, publishedTestText, null);

        assertFalse(textService.getLikePublishedTexts(docId, engLang).isEmpty());

        assertEquals(publishedTestText, textService.getLikePublishedText(docId, index + 2, engLang.getCode(), null).getText());
    }

    @Test
    public void getTextsLikePublished_When_LikePublishedTrueAndTextLatestVersion_Expected_EqualResult() {

        final int index = 1;
        final TextJPA text = createText(index, languages.get(0), workingVersion);
        textRepository.saveAndFlush(text);

        textService.createVersionedContent(workingVersion, latestVersion);

        final LanguageJPA enLang = languages.get(0);

        final String testText = "someTextTest";

        textDataInitializer.createLikePublishedText(2, enLang, workingVersion, testText, null);


        Set<Text> resultTexts = textService.getLikePublishedTexts(DOC_ID, enLang);

        assertFalse(resultTexts.isEmpty());

        assertEquals(1, resultTexts.size());
        assertEquals(textService.getLikePublishedTexts(DOC_ID, enLang), resultTexts);
    }

    @Test
    public void getTextsLikePublished_When_LikePublishedTrueAndDocHasWorkVersion_Expected_EqualResult() {

        final Integer docId = documentDataInitializer.createData().getId();

        final Version workVersion = versionDataInitializer.createData(WORKING_VERSION_NO, docId);
        final LanguageJPA enLang = languages.get(0);
        final String testText = "someTextTest";

        textDataInitializer.createLikePublishedText(1, enLang, workVersion, testText, null);

        assertFalse(textService.getLikePublishedTexts(docId, enLang).isEmpty());

        final Text likePublishedText = textService.getLikePublishedText(docId, 1, enLang.getCode(), null);

        assertNotNull(likePublishedText);

        assertEquals(testText, likePublishedText.getText());

    }

    @Test
    public void getTexts_When_TextNotInLoop_Expected_EqualResult() {

        final Integer idNewDoc = documentDataInitializer.createData().getId();
        final Version workVersion = versionDataInitializer.createData(WORKING_VERSION_NO, idNewDoc);
        final LanguageJPA enLang = languages.get(0);
        final String testText = "someTextTest";

        textDataInitializer.createText(1, enLang, workVersion, testText, null);

        List<TextJPA> resultTexts = textService.getByDocId(idNewDoc);

        assertFalse(resultTexts.isEmpty());

        assertEquals(1, resultTexts.size());
        assertEquals(textService.getText(1, testText), resultTexts);
    }


    @Test
    public void getTexts_When_TextsNotExist_Expected_EmptyResult() {
        final Integer idNewDoc = documentDataInitializer.createData().getId();

        final Version workVersion = versionDataInitializer.createData(WORKING_VERSION_NO, 1001);
        final LanguageJPA enLang = languages.get(0);
        final String testText = "someTextTest";

        textDataInitializer.createText(1, enLang, workVersion, testText, null);

        List<TextJPA> resultTexts = textService.getByDocId(idNewDoc);

        assertTrue(resultTexts.isEmpty());
    }

    @Test
    public void getText_When_InLoop_Expect_CorrectDTO() {
        final List<Text> textDTOS = new ArrayList<>();
        final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(1, 1);

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final TextJPA text = new TextJPA();
                text.setIndex(index);
                text.setVersion(workingVersion);
                text.setLanguage(language);
                text.setLoopEntryRef(loopEntryRef);
                text.setText("test");
                text.setType(TEXT);

                textRepository.save(text);
                textDTOS.add(new TextDTO(text));
            }
        }

        for (Text textDTO : textDTOS) {
            final Text savedText = textService.getText(textDTO);
            assertEquals(savedText, textDTO);
        }
    }

    @Test
    public void saveText_When_NotInLoop_Expect_CorrectDTO() {
        final List<Text> textDTOS = new ArrayList<>();

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final Text textDTO = new TextDTO(index, DOC_ID, language.getCode(), null);
                textDTO.setText("test");
                textDTO.setType(TEXT);

                textDTOS.add(textDTO);

                textService.save(textDTO);
            }
        }

        for (Text textDTO : textDTOS) {
            final Text savedText = textService.getText(textDTO);
            assertEquals(savedText, textDTO);
        }
    }

    @Test
    public void saveText_When_InLoop_Expect_CorrectDTO() {
        final List<Text> textDTOS = new ArrayList<>();
        final LoopEntryRef loopEntryRef = new LoopEntryRefDTO(1, 1);

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final Text textDTO = new TextDTO(index, DOC_ID, language.getCode(), loopEntryRef);
                textDTO.setText("test");
                textDTO.setType(TEXT);

                textDTOS.add(textDTO);

                textService.save(textDTO);
            }
        }

        for (Text textDTO : textDTOS) {
            final Text savedText = textService.getText(textDTO);
            assertEquals(savedText, textDTO);
        }
    }

    @Test
    public void saveText_When_LikePublishedTrue_Expect_CorrectDTO() {
        final List<Text> textDTOS = new ArrayList<>();
        final LoopEntryRef loopEntryRef = new LoopEntryRefDTO(1, 1);

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final Text textDTO = new TextDTO(index, DOC_ID, language.getCode(), loopEntryRef, true);
                textDTO.setText("test");
                textDTO.setType(TEXT);

                textDTOS.add(textDTO);

                textService.save(textDTO);
            }
        }

        for (Text textDTO : textDTOS) {
            final Text savedText = textService.getText(textDTO);
            assertEquals(savedText, textDTO);
        }



        assertFalse(textService.getLikePublishedTexts(DOC_ID, new LanguageJPA(languages.get(0))).isEmpty());

        for (LanguageJPA language : languages) {
           final Set<Text> likePublishedTexts = textService.getLikePublishedTexts(DOC_ID, new LanguageJPA(language));

            assertEquals(textDTOS.size() / 2, likePublishedTexts.size());
        }
    }

    @Test
    public void saveText_When_InLoopAndTextValueIsTheSame_Expect_TextAndTextHistoryAreNotUpdated() {
        testSavingTextInLoopWithoutUpdating(true);
    }

    @Test
    public void saveText_When_NotInLoopAndTextValueIsTheSame_Expect_TextAndTextHistoryAreNotUpdated() {
        testSavingTextInLoopWithoutUpdating(false);
    }

    private void testSavingTextInLoopWithoutUpdating(boolean inLoop) {
        final int index = 1;
        final String languageCode = languages.get(0).getCode();
        final LoopEntryRefDTO loopEntryRefDTO = inLoop ?
                new LoopEntryRefDTO(1, 1) :
                null;

        final Text textDTO = new TextDTO(index, DOC_ID, languageCode, loopEntryRefDTO);
        textDTO.setText("test");
        textDTO.setType(TEXT);

        textService.save(textDTO);

        assertEquals(1, textRepository.findAll().size());
        assertEquals(1, textHistoryRepository.findAll().size());

        // save with the same text
        textService.save(textDTO);

        assertEquals(1, textRepository.findAll().size());
        assertEquals(1, textHistoryRepository.findAll().size());
    }

    @Test
    public void saveText_When_InLoopAndTextValueIsNew_Expect_TextAndTextHistoryAreUpdated() {
        testSavingTextInLoopWithUpdating(true);
    }

    @Test
    public void saveText_When_NotInLoopAndTextValueIsNew_Expect_TextAndTextHistoryAreUpdated() {
        testSavingTextInLoopWithUpdating(false);
    }

    private void testSavingTextInLoopWithUpdating(boolean inLoop) {
        final int index = 1;
        final String languageCode = languages.get(0).getCode();
        final LoopEntryRefDTO loopEntryRefDTO = inLoop ?
                new LoopEntryRefDTO(1, 1) :
                null;

        final Text textDTO = new TextDTO(index, DOC_ID, languageCode, loopEntryRefDTO);
        textDTO.setText("test");
        textDTO.setType(TEXT);

        textService.save(textDTO);

        assertEquals(1, textRepository.findAll().size());
        assertEquals(1, textHistoryRepository.findAll().size());

        // save with new text
        final String newTestValue = "new test value";
        textDTO.setText(newTestValue);

        textService.save(textDTO);

        final List<TextHistoryJPA> all = textHistoryRepository.findAll();

        assertEquals(1, textRepository.findAll().size());
        assertEquals(2, all.size());

        final int maxTextHistoryId = all
                .stream()
                .mapToInt(TextHistoryJPA::getId)
                .max()
                .getAsInt();

        final String actualText = textHistoryRepository.findOne(maxTextHistoryId).getText();

        assertEquals(newTestValue, actualText);
    }

    @Test
    public void deleteByDocId() {
        createTexts();

        final long prevNumberOfTextsForDoc = textRepository.findAll().stream()
                .filter(textJPA -> Objects.equals(DOC_ID, textJPA.getVersion().getDocId()))
                .count();

        assertNotEquals(prevNumberOfTextsForDoc, 0L);

        textService.deleteByDocId(DOC_ID);

        final long newNumberOfTextsForDoc = textRepository.findAll().stream()
                .filter(textJPA -> Objects.equals(DOC_ID, textJPA.getVersion().getDocId()))
                .count();

        assertEquals(newNumberOfTextsForDoc, 0L);
    }

    private Set<Text> createTexts() {
        final ArrayList<TextJPA> texts = new ArrayList<>();
        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                texts.add(createText(index, language, workingVersion));
            }
        }

        return textRepository.save(texts).stream()
                .map(TextDTO::new)
                .collect(Collectors.toSet());
    }

    private TextJPA createText(int index, LanguageJPA language, Version version) {
        final TextJPA text = new TextJPA();
        text.setIndex(index);
        text.setLanguage(language);
        text.setText("test");
        text.setType(TEXT);
        text.setVersion(version);

        return text;
    }

    @Test
    public void createVersionedContent() {
        final int index = 1;
        final TextJPA text = createText(index, languages.get(0), workingVersion);
        textRepository.saveAndFlush(text);

        textService.createVersionedContent(workingVersion, latestVersion);

        final List<TextJPA> byVersion = textRepository.findByVersion(latestVersion);

        assertNotNull(byVersion);
        assertEquals(1, byVersion.size());

        final TextJPA textJPA = byVersion.get(0);

        assertEquals(text.getIndex(), textJPA.getIndex());
        assertEquals(text.getDocId(), textJPA.getDocId());
        assertEquals(text.getLanguage(), textJPA.getLanguage());
        assertEquals(text.getText(), textJPA.getText());
        assertEquals(text.getLoopEntryRef(), textJPA.getLoopEntryRef());
    }

    @Test
    public void getPublicTexts_When_FewVersionsExist_Expect_AllTextsForLatestVersionFound() { //
        final Version middleVersion = latestVersion;
        final Version newLatestVersion = versionDataInitializer.createData(LATEST_VERSION_NO + 1, DOC_ID);

        final List<Text> latestVersionTexts = new ArrayList<>();

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                textRepository.save(createText(index, language, workingVersion));
                textRepository.save(createText(index, language, middleVersion));
                final TextJPA latestVersionText = createText(index, language, newLatestVersion);
                textRepository.save(latestVersionText);
                latestVersionTexts.add(new TextDTO(latestVersionText));
            }
        }

        final List<Text> foundLatestVersionTexts = new ArrayList<>();

        for (LanguageJPA language : languages) {
            foundLatestVersionTexts.addAll(textService.getPublicTexts(DOC_ID, language));
        }

        assertTrue(foundLatestVersionTexts.containsAll(latestVersionTexts));
    }

    @Test
    public void saveText_When_TypeIsHtmlWithLegalContent_Expect_SavedWithoutChanges() {
        final List<Text> textDTOS = new ArrayList<>();
        final String legalText = "this is legal text";

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final Text textDTO = new TextDTO(index, DOC_ID, language.getCode(), null);
                textDTO.setText(legalText);
                textDTO.setType(HTML);

                textDTOS.add(textDTO);

                textService.save(textDTO);
            }
        }

        for (Text textDTO : textDTOS) {
            final Text savedText = textService.getText(textDTO);
            assertEquals(savedText, textDTO);
        }
    }
}
