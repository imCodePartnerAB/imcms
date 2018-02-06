package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import com.imcode.imcms.persistence.repository.TextRepository;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.imcode.imcms.model.Text.Type.PLAIN_TEXT;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class TextServiceTest {

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

    @BeforeClass
    public static void setUser() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now
    }

    private Version workingVersion;
    private Version latestVersion;
    private List<LanguageJPA> languages;

    @Before
    public void setUp() throws Exception {
        textRepository.deleteAll();
        textRepository.flush();

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
                text.setType(PLAIN_TEXT);
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
                text.setType(PLAIN_TEXT);

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
                textDTO.setType(PLAIN_TEXT);

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
                textDTO.setType(PLAIN_TEXT);

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
    public void saveText_When_InLoopAndTextValueIsTheSame_Expect_TextAndTextHistoryAreNotUpdated() {
        testSavingTextInLoopWithoutUpdating(true);
    }

    @Test
    public void saveText_When_InLoopAndTextValueIsNew_Expect_TextAndTextHistoryAreUpdated() {
        testSavingTextInLoopWithUpdating(true);
    }

    @Test
    public void saveText_When_NotInLoopAndTextValueIsTheSame_Expect_TextAndTextHistoryAreNotUpdated() {
        testSavingTextInLoopWithoutUpdating(false);
    }

    @Test
    public void saveText_When_NotInLoopAndTextValueIsNew_Expect_TextAndTextHistoryAreUpdated() {
        testSavingTextInLoopWithUpdating(false);
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

    @Test
    public void getByVersion() {
        final Set<Text> expected = createTexts(),
                actual = textService.getByVersion(workingVersion);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void createVersionedContent() {
        final int index = 1;
        final TextJPA text = createText(index, languages.get(0), workingVersion);
        textRepository.saveAndFlush(text);

        final TextDTO workingVersionText = new TextDTO(text);

        textService.createVersionedContent(workingVersion, latestVersion);

        final Set<Text> byVersion = textService.getByVersion(latestVersion);

        assertNotNull(byVersion);
        assertEquals(1, byVersion.size());
        assertTrue(byVersion.contains(workingVersionText));
    }

    @Test
    public void getPublicTexts_When_FewVersionsExist_Expect_AllTextsForLatestVersionFound() {
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
        text.setType(PLAIN_TEXT);
        text.setVersion(version);

        return text;
    }

    private void testSavingTextInLoopWithoutUpdating(boolean inLoop) {
        final int index = 1;
        final String languageCode = languages.get(0).getCode();
        final LoopEntryRefDTO loopEntryRefDTO = inLoop ?
                new LoopEntryRefDTO(1, 1) :
                null;

        final Text textDTO = new TextDTO(index, DOC_ID, languageCode, loopEntryRefDTO);
        textDTO.setText("test");
        textDTO.setType(PLAIN_TEXT);

        textService.save(textDTO);

        assertEquals(1, textRepository.findAll().size());
        assertEquals(1, textHistoryRepository.findAll().size());

        // save with the same text
        textService.save(textDTO);

        assertEquals(1, textRepository.findAll().size());
        assertEquals(1, textHistoryRepository.findAll().size());
    }

    private void testSavingTextInLoopWithUpdating(boolean inLoop) {
        final int index = 1;
        final String languageCode = languages.get(0).getCode();
        final LoopEntryRefDTO loopEntryRefDTO = inLoop ?
                new LoopEntryRefDTO(1, 1) :
                null;

        final Text textDTO = new TextDTO(index, DOC_ID, languageCode, loopEntryRefDTO);
        textDTO.setText("test");
        textDTO.setType(PLAIN_TEXT);

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
}
