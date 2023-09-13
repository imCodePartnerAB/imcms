package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class TextHistoryJPARepositoryTest extends WebAppSpringTestConfig {

    private static final int TEST_DOC_ID = 1001;

    @Autowired
    private TextHistoryRepository textHistoryRepository;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private UserService userService;

    private TextHistoryJPA textHistoryJPA;

    @BeforeEach
    public void setUp() {
        textHistoryRepository.deleteAll();
        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);
        final User user = userService.getUser(1);
        final int index = 1;

        final TextHistoryJPA textHistoryJPA = new TextHistoryJPA();
        textHistoryJPA.setDocId(TEST_DOC_ID);
        textHistoryJPA.setModifiedBy(user);
        textHistoryJPA.setModifiedDt(new Date());
        textHistoryJPA.setLanguage(languageJPA);
        textHistoryJPA.setType(Text.Type.TEXT);
        textHistoryJPA.setIndex(index);
        textHistoryJPA.setText("Long text");

        this.textHistoryJPA = textHistoryJPA;
    }

    @Test
    public void saveTextHistory_Expected_Saved() {
        final int expectedSize = textHistoryRepository.findAll().size() + 1;

        textHistoryRepository.save(this.textHistoryJPA);

        final int actualSize = textHistoryRepository.findAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void getTextHistoryById_When_TextHistoryExists_Expect_Returned() {
        final TextHistoryJPA expected = textHistoryRepository.save(this.textHistoryJPA);
        final TextHistoryJPA actual = textHistoryRepository.getOne(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByLanguageAndNo_When_OneSpecifiedExists_Expect_OneTextHistoryReturned() {
        final TextHistoryJPA savedTextHistoryJPA = textHistoryRepository.save(this.textHistoryJPA);

        final List<TextHistoryJPA> expected = Collections.singletonList(savedTextHistoryJPA);

        final List<TextHistoryJPA> actual = textHistoryRepository.findTextHistoryNotInLoop(
                TEST_DOC_ID, savedTextHistoryJPA.getLanguage(), 1
        );

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByLanguageAndLoopEntryRefAndNo_When_OneSpecifiedExists_Expect_OneTextHistoryReturned() {
        final LoopEntryRefJPA loopEntryRefJPA = new LoopEntryRefJPA();
        loopEntryRefJPA.setLoopIndex(1);
        loopEntryRefJPA.setLoopEntryIndex(1);

        this.textHistoryJPA.setLoopEntryRef(loopEntryRefJPA);

        final TextHistoryJPA savedTextHistoryJPA = textHistoryRepository.save(textHistoryJPA);

        final List<TextHistoryJPA> expected = Collections.singletonList(savedTextHistoryJPA);

        final List<TextHistoryJPA> actual = textHistoryRepository.findTextHistoryInLoop(
                TEST_DOC_ID, savedTextHistoryJPA.getLanguage(), loopEntryRefJPA, 1
        );

        assertEquals(expected, actual);
    }

    @Test
    public void findTextHistoryNotInLoop_When_MultipleDocsAndHistoriesExist_Expect_FoundForSpecifiedDoc() {
        final Integer newDocId_1 = documentDataInitializer.createData().getId();
        final Integer newDocId_2 = documentDataInitializer.createData().getId();

        final TextHistoryJPA savedTextHistoryJPA = textHistoryRepository.save(textHistoryJPA);
        final LanguageJPA language = savedTextHistoryJPA.getLanguage();

        final TextHistoryJPA newTextHistory_1 = new TextHistoryJPA(textHistoryJPA);
        newTextHistory_1.setId(null);
        newTextHistory_1.setDocId(newDocId_1);
        newTextHistory_1.setLanguage(language);
        newTextHistory_1.setModifiedDt(new Date());
        newTextHistory_1.setModifiedBy(savedTextHistoryJPA.getModifiedBy());

        final TextHistoryJPA newTextHistory_2 = new TextHistoryJPA(newTextHistory_1);
        newTextHistory_2.setDocId(newDocId_2);
        newTextHistory_2.setLanguage(language);
        newTextHistory_2.setModifiedDt(new Date());
        newTextHistory_2.setModifiedBy(newTextHistory_1.getModifiedBy());

        final List<TextHistoryJPA> expected = Collections.singletonList(savedTextHistoryJPA);

        final List<TextHistoryJPA> savedTextHistory_1 = Collections.singletonList(textHistoryRepository.save(
                newTextHistory_1
        ));
        final List<TextHistoryJPA> savedTextHistory_2 = Collections.singletonList(textHistoryRepository.save(
                newTextHistory_2
        ));

        final List<TextHistoryJPA> defaultTextHistory = textHistoryRepository.findTextHistoryNotInLoop(
                textHistoryJPA.getDocId(), language, textHistoryJPA.getIndex()
        );

        final List<TextHistoryJPA> textHistory_1 = textHistoryRepository.findTextHistoryNotInLoop(
                newTextHistory_1.getDocId(), language, newTextHistory_1.getIndex()
        );

        final List<TextHistoryJPA> textHistory_2 = textHistoryRepository.findTextHistoryNotInLoop(
                newTextHistory_2.getDocId(), language, newTextHistory_2.getIndex()
        );

        assertEquals(expected, defaultTextHistory);
        assertEquals(savedTextHistory_1, textHistory_1);
        assertEquals(savedTextHistory_2, textHistory_2);
    }

    @Test
    public void clearHistoryIfLimitExceeded_When_LoopIsNull_Expect_OutdatedHistoryDeleted() {
        assertTrue(textHistoryRepository.findAll().isEmpty());

        final int historySize = 5;
        final int limit = 2;

        final List<TextHistoryJPA> textHistoryList = new ArrayList<>();
        for (int i = 0; i < historySize; i++) {
            final TextHistoryJPA newTextHistory = new TextHistoryJPA(textHistoryJPA);
            newTextHistory.setId(null);
            newTextHistory.setDocId(textHistoryJPA.getDocId());
            newTextHistory.setLanguage(textHistoryJPA.getLanguage());
            newTextHistory.setModifiedDt(new Date());
            newTextHistory.setModifiedBy(textHistoryJPA.getModifiedBy());

            textHistoryList.add(textHistoryRepository.save(newTextHistory));
        }

        assertEquals(historySize, textHistoryRepository.findAll().size());

        textHistoryRepository.clearHistoryIfLimitExceeded(textHistoryJPA.getDocId(), textHistoryJPA.getIndex(), textHistoryJPA.getLanguage().getId(),
                null, null, limit);

        final List<TextHistoryJPA> textHistoryListAfterClear = textHistoryRepository.findAll();

        assertEquals(limit, textHistoryListAfterClear.size());
        assertTrue(textHistoryList.stream().limit(historySize - limit).noneMatch(textHistoryListAfterClear::contains));
        assertTrue(textHistoryList.stream().skip(Math. abs(limit - historySize)).allMatch(textHistoryListAfterClear::contains));
    }

    @Test
    public void clearHistoryIfLimitExceeded_When_LoopIsNotNull_Expect_OutdatedHistoryDeleted() {
        assertTrue(textHistoryRepository.findAll().isEmpty());

        final int historySize = 5;
        final int limit = 2;

        final LoopEntryRefJPA loopEntryRefJPA = new LoopEntryRefJPA();
        loopEntryRefJPA.setLoopIndex(1);
        loopEntryRefJPA.setLoopEntryIndex(1);

        final List<TextHistoryJPA> textHistoryList = new ArrayList<>();
        for (int i = 0; i < historySize; i++) {
            final TextHistoryJPA newTextHistory = new TextHistoryJPA(textHistoryJPA);
            newTextHistory.setId(null);
            newTextHistory.setDocId(textHistoryJPA.getDocId());
            newTextHistory.setLanguage(textHistoryJPA.getLanguage());
            newTextHistory.setModifiedDt(new Date());
            newTextHistory.setModifiedBy(textHistoryJPA.getModifiedBy());
            newTextHistory.setLoopEntryRef(loopEntryRefJPA);

            textHistoryList.add(textHistoryRepository.save(newTextHistory));
        }

        assertEquals(historySize, textHistoryRepository.findAll().size());

        textHistoryRepository.clearHistoryIfLimitExceeded(textHistoryJPA.getDocId(), textHistoryJPA.getIndex(), textHistoryJPA.getLanguage().getId(),
                loopEntryRefJPA.getLoopIndex(), loopEntryRefJPA.getLoopEntryIndex(), limit);

        final List<TextHistoryJPA> textHistoryListAfterClear = textHistoryRepository.findAll();

        assertEquals(limit, textHistoryListAfterClear.size());
        assertTrue(textHistoryList.stream().limit(historySize - limit).noneMatch(textHistoryListAfterClear::contains));
        assertTrue(textHistoryList.stream().skip(Math. abs(limit - historySize)).allMatch(textHistoryListAfterClear::contains));
    }
}