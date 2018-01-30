package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
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
public class TextHistoryJPARepositoryTest {

    @Autowired
    private TextHistoryRepository textHistoryRepository;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private UserService userService;

    private TextHistoryJPA textHistoryJPA;

    @Before
    public void setUp() {
        final Language en = languageDataInitializer.createData().get(0);
        final LanguageJPA languageJPA = new LanguageJPA(en);
        final User user = userService.getUser(1);
        final int index = 1;

        final TextHistoryJPA textHistoryJPA = new TextHistoryJPA();
        textHistoryJPA.setModifiedBy(user);
        textHistoryJPA.setModifiedDt(new Date());
        textHistoryJPA.setLanguage(languageJPA);
        textHistoryJPA.setType(Text.Type.PLAIN_TEXT);
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

        final List<TextHistoryJPA> actual = textHistoryRepository
                .findAllByLanguageAndNo(savedTextHistoryJPA.getLanguage(), 1);

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

        final List<TextHistoryJPA> actual = textHistoryRepository
                .findAllByLanguageAndLoopEntryRefAndNo(
                        savedTextHistoryJPA.getLanguage(), loopEntryRefJPA, 1
                );

        assertEquals(expected, actual);
    }
}