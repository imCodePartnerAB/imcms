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
import com.imcode.imcms.persistence.entity.*;
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

import java.util.*;
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
    public void getText_When_PassedVersion_Expected_TextOfSpecifiedVersion(){
        final int index = 1;
        final LanguageJPA enLang = languages.get(0);

        final Version version1 = latestVersion;
        final Version version2 = versionDataInitializer.createData(2, DOC_ID);

        final String testTextWorkingVersion = "someText";
        final String testTextVersion1 = "someTextVersion1";
        final String testTextVersion2 = "someTextVersion2";

        textDataInitializer.createText(index, enLang, workingVersion, testTextWorkingVersion, null);
        final TextJPA textVersion1 = textDataInitializer.createText(index, enLang, version1, testTextVersion1, null);
        textDataInitializer.createText(index, enLang, version2, testTextVersion2, null);

        final Text expectedTextTextVersion1 = new TextDTO(textVersion1);
        final Text receivedTextVersion1 = textService.getText(DOC_ID, index, version1.getNo(), enLang.getCode(), null);
        assertEquals(expectedTextTextVersion1, receivedTextVersion1);
    }

    @Test
    public void getText_When_noTextOfSpecificVersion_Expected_EmptyText(){
        final int index = 1;
        final LanguageJPA enLang = languages.get(0);

        final Version version1WithoutText = latestVersion;
        final Version version2 = versionDataInitializer.createData(2, DOC_ID);

        final String testTextWorkingVersion = "someText";
        final String testTextVersion2 = "someTextVersion2";

        textDataInitializer.createText(index, enLang, workingVersion, testTextWorkingVersion, null);
        textDataInitializer.createText(index, enLang, version2, testTextVersion2, null);

        final Text expectedEmptyText = new TextDTO(index, DOC_ID, enLang.getCode(), null, false);
        final Text receivedTextVersion1 = textService.getText(DOC_ID, index, version1WithoutText.getNo(), enLang.getCode(), null);
        assertEquals(expectedEmptyText, receivedTextVersion1);
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
	public void getLoopTexts_When_LoopIndexPresent_Expected_EqualResult() {
		final int loopIndex = 1;

		final LanguageJPA languageJPA = languageRepository.findByCode("en");

		final LoopEntryRefJPA loopEntryRef1 = new LoopEntryRefJPA(loopIndex, 1);
		final LoopEntryRefJPA loopEntryRef2 = new LoopEntryRefJPA(loopIndex, 2);

		final TextJPA text1 = textDataInitializer.createText(1, languageJPA, workingVersion, "test", loopEntryRef1);
		final TextJPA text2 = textDataInitializer.createText(2, languageJPA, workingVersion, "test", loopEntryRef2);


		final TextDTO textDTO1 = Optional.of(text1).map(TextDTO::new).get();
		final TextDTO textDTO2 = Optional.of(text2).map(TextDTO::new).get();

		final List<Text> resultTexts = textService.getLoopTexts(DOC_ID, "en", loopIndex);

		assertFalse(resultTexts.isEmpty());
		assertEquals(2, resultTexts.size());
		assertEquals(textDTO1, resultTexts.get(0));
		assertEquals(textDTO2, resultTexts.get(1));
	}

	@Test
	public void getLoopTexts_When_ImagesNotInLoop_EmptyResult() {
		final int loopIndex = 1;

		final LanguageJPA languageJPA = languageRepository.findByCode("en");

		textDataInitializer.createText(1, languageJPA, workingVersion, "test",null);
		textDataInitializer.createText(2, languageJPA, workingVersion, "test", null);

		final List<Text> resultTexts = textService.getLoopTexts(DOC_ID, "en", loopIndex);

		assertTrue(resultTexts.isEmpty());
	}

	@Test
	public void getLoopTexts_When_LoopIndexPresent_And_IncorrectLangCode_Expected_CorrectResult() {
		final int loopIndex = 1;

		final LanguageJPA languageJPA1 = languageRepository.findByCode("en");
		final LanguageJPA languageJPA2 = languageRepository.findByCode("sv");

		final Version version = versionDataInitializer.createData(WORKING_VERSION_NO, DOC_ID);

		final LoopEntryRefJPA loopEntryRef1 = new LoopEntryRefJPA(loopIndex, 1);
		final LoopEntryRefJPA loopEntryRef2 = new LoopEntryRefJPA(loopIndex, 2);

		final TextJPA text1FromLoop1 = textDataInitializer.createText(1, languageJPA1, version,"test", loopEntryRef1);
		final TextJPA text2FromLoop1 = textDataInitializer.createText(2, languageJPA1, version, "test", loopEntryRef2);

		final TextDTO textDTO1FromLoop1 = Optional.of(text1FromLoop1).map(TextDTO::new).get();
		final TextDTO textDTO2FromLoop1 = Optional.of(text2FromLoop1).map(TextDTO::new).get();

		final TextJPA text1FromLoop2 = textDataInitializer.createText(1, languageJPA2, version,"test", loopEntryRef1);
		final TextJPA text2FromLoop2 = textDataInitializer.createText(2, languageJPA2, version, "test", loopEntryRef2);

		final TextDTO textDTO1FromLoop2 = Optional.of(text1FromLoop2).map(TextDTO::new).get();
		final TextDTO textDTO2FromLoop2 = Optional.of(text2FromLoop2).map(TextDTO::new).get();

		final List<Text> resultTexts = textService.getLoopTexts(DOC_ID, "sv", loopIndex);

		assertFalse(resultTexts.isEmpty());

		assertNotEquals(textDTO1FromLoop1, resultTexts.get(0));
		assertNotEquals(textDTO2FromLoop1, resultTexts.get(1));

		assertEquals(textDTO1FromLoop2, resultTexts.get(0));
		assertEquals(textDTO2FromLoop2, resultTexts.get(1));
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

	    final String actualText = textHistoryRepository.getOne(maxTextHistoryId).getText();

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

	    return textRepository.saveAll(texts).stream()
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

    @Test
    public void setAsWorkingVersion_Expect_CopyTextsFromSpecificVersionToWorkingVersion_And_AddEntryToHistory(){
        final Version version1 = latestVersion;
        final Version version2 = versionDataInitializer.createData(2, DOC_ID);

        String textWorkingVersion = "someText";
        String textVersion1 = "someTextVersion1";
        String textVersion2 = "someTextVersion2";

        for (LanguageJPA language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final TextJPA workingText = new TextJPA();
                workingText.setIndex(index);
                workingText.setLanguage(language);
                workingText.setText(textWorkingVersion);
                workingText.setType(TEXT);
                workingText.setVersion(workingVersion);

                final TextJPA versionText1 = new TextJPA(workingText, version1);
                versionText1.setText(textVersion1);

                final TextJPA versionText2 = new TextJPA(workingText, version2);
                versionText2.setText(textVersion2);

                textRepository.saveAll(List.of(workingText));
            }
        }

        final List<TextDTO> textsByWorkingVersion = textRepository.findByVersion(workingVersion).stream().map(TextDTO::new).collect(Collectors.toList());
        final List<TextDTO> textsByVersion1 = textRepository.findByVersion(version1).stream().map(TextDTO::new).collect(Collectors.toList());
        final List<TextDTO> textsByVersion2 = textRepository.findByVersion(version2).stream().map(TextDTO::new).collect(Collectors.toList());

        assertTrue(textHistoryRepository.findAll().isEmpty());

        textService.setAsWorkingVersion(version1);

        final List<TextDTO> textsByWorkingVersionAfterReset  = textRepository.findByVersion(workingVersion).stream().map(TextDTO::new).collect(Collectors.toList());
        final List<TextDTO> textsByVersion1AfterReset = textRepository.findByVersion(version1).stream().map(TextDTO::new).collect(Collectors.toList());
        final List<TextDTO> textsByVersion2AfterReset  = textRepository.findByVersion(version2).stream().map(TextDTO::new).collect(Collectors.toList());

        assertNotEquals(textsByWorkingVersion, textsByWorkingVersionAfterReset);
        assertEquals(textsByWorkingVersionAfterReset, textsByVersion1);
        assertEquals(textsByVersion1, textsByVersion1AfterReset);
        assertEquals(textsByVersion2, textsByVersion2AfterReset);

        assertEquals(textsByWorkingVersionAfterReset.size(), textHistoryRepository.findAll().size());
    }

    @Test
    public void setAsWorkingVersion_When_NoTextsOfSpecificVersion_Expect_WorkingVersionHasNoTexts(){
        final Version version1 = latestVersion;
        final Version version2 = versionDataInitializer.createData(2, DOC_ID);

        final LanguageJPA language = languages.get(0);

        String textWorkingVersion = "someText";
        String textVersion2 = "someTextVersion2";

        final TextJPA workingText = new TextJPA();
        workingText.setIndex(MIN_TEXT_INDEX);
        workingText.setLanguage(language);
        workingText.setText(textWorkingVersion);
        workingText.setType(TEXT);
        workingText.setVersion(workingVersion);

        final TextJPA versionText2 = new TextJPA(workingText, version2);
        versionText2.setText(textVersion2);

        textRepository.saveAll(List.of(workingText, versionText2));

        final List<TextJPA> textsByWorkingVersion = textRepository.findByVersion(workingVersion);
        assertFalse(textsByWorkingVersion.isEmpty());

        textService.setAsWorkingVersion(version1);

        final List<TextJPA> textsByWorkingVersionAfterReset  = textRepository.findByVersion(workingVersion);
        assertTrue(textsByWorkingVersionAfterReset.isEmpty());
    }
}
