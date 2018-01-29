package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistory;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class TextHistoryRepositoryTest {

    @Autowired
    private TextHistoryRepository textHistoryRepository;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private UserService userService;

    private TextHistory textHistory;

    @Before
    public void setUp() {
        final Version version = versionDataInitializer.createData(Version.WORKING_VERSION_INDEX, 1001);
        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);
        final User user = userService.getUser(1);
        final int index = 1;

        final TextHistory textHistory = new TextHistory();
        textHistory.setVersion(version);
        textHistory.setModifiedBy(user);
        textHistory.setModifiedDt(new Date());
        textHistory.setLanguage(languageJPA);
        textHistory.setType(Text.Type.PLAIN_TEXT);
        textHistory.setIndex(index);
        textHistory.setText("Long text");

        this.textHistory = textHistory;
    }

    @Test
    public void saveTextHistory_Expected_Saved() {
        final int expectedSize = textHistoryRepository.findAll().size() + 1;

        textHistoryRepository.save(this.textHistory);

        final int actualSize = textHistoryRepository.findAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void getTextHistoryById_When_TextHistoryExists_Expect_Returned() {
        final TextHistory expected = textHistoryRepository.save(this.textHistory);
        final TextHistory actual = textHistoryRepository.getOne(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByVersionAndLanguageAndNo_When_OneSpecifiedExists_Expect_OneTextHistoryReturned() {
        final TextHistory savedTextHistory = textHistoryRepository.save(this.textHistory);

        final List<TextHistory> expected = Collections.singletonList(savedTextHistory);

        final List<TextHistory> actual = textHistoryRepository
                .findAllByVersionAndLanguageAndNo(
                        savedTextHistory.getVersion(), savedTextHistory.getLanguage(), 1
                );

        assertEquals(expected, actual);
    }

    @Test
    public void findAllByVersionAndLanguageAndLoopEntryRefAndNo_When_OneSpecifiedExists_Expect_OneTextHistoryReturned() {
        final LoopEntryRefJPA loopEntryRefJPA = new LoopEntryRefJPA();
        loopEntryRefJPA.setLoopIndex(1);
        loopEntryRefJPA.setLoopEntryIndex(1);

        this.textHistory.setLoopEntryRef(loopEntryRefJPA);

        final TextHistory savedTextHistory = textHistoryRepository.save(textHistory);

        final List<TextHistory> expected = Collections.singletonList(savedTextHistory);

        final List<TextHistory> actual = textHistoryRepository
                .findAllByVersionAndLanguageAndLoopEntryRefAndNo(
                        savedTextHistory.getVersion(), savedTextHistory.getLanguage(), loopEntryRefJPA, 1);

        assertEquals(expected, actual);
    }
}