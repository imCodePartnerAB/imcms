package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.persistence.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TextRepositoryTest {

    private static final int DOC_ID = 1001;
    private static final int MIN_TEXT_INDEX = 1;
    private static final int MAX_TEXT_INDEX = 10;
    private static final int TEXTS_COUNT__PER_VERSION__PER_LANGUAGE = MAX_TEXT_INDEX;

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private VersionDataInitializer versionDataInitializer;

    private List<LanguageJPA> languages;
    private List<Version> versions;

    @Before
    public void setUp() {
        versionDataInitializer.cleanRepositories();
        languages = Arrays.asList(languageRepository.findByCode("en"), languageRepository.findByCode("sv"));
        // both langs should already be created

        versions = Arrays.asList(
                versionDataInitializer.createData(0, DOC_ID),
                versionDataInitializer.createData(1, DOC_ID),
                versionDataInitializer.createData(2, DOC_ID)
        );

        // texts with odd no have loop entry with the same loop no and entry no
        for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
            for (LanguageJPA language : languages) {
                for (Version version : versions) {
                    TextJPA text = new TextJPA();

                    text.setIndex(index);
                    text.setType(Text.Type.PLAIN_TEXT);
                    text.setLanguage(language);
                    text.setVersion(version);
                    text.setText("test");

                    if ((index & 1) == 1) {
                        text.setLoopEntryRef(new LoopEntryRefJPA(index, index));
                    }

                    textRepository.save(text);
                }
            }
        }
        textRepository.flush();
    }

    @Test
    public void testFindByDocVersionAndIndexAndLoopEntryIsNull() throws Exception {
        for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
            for (LanguageJPA language : languages) {
                for (Version version : versions) {
                    TextJPA text = textRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(
                            version, language, index
                    );

                    if ((index & 1) == 1) {
                        assertNull(text);
                    } else {
                        assertNotNull(text);
                        assertNull(text.getLoopEntryRef());
                    }
                }
            }
        }
    }

    @Test
    public void testFindByDocVersionAndIndexAndLoopEntryRef() throws Exception {
        for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
            final LoopEntryRefJPA loopEntryRef = new LoopEntryRefJPA(index, index);
            for (LanguageJPA language : languages) {
                for (Version version : versions) {
                    TextJPA text = textRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(
                            version, language, index, loopEntryRef
                    );

                    if ((index & 1) == 0) {
                        assertNull(text);
                    } else {
                        assertNotNull(text);
                        assertEquals(loopEntryRef, text.getLoopEntryRef());
                    }
                }
            }
        }
    }

    @Test
    public void testDeleteByDocVersionAndLanguage() throws Exception {
        for (LanguageJPA language : languages) {
            for (Version version : versions) {
                int deletedCount = textRepository.deleteByVersionAndLanguage(version, language);

                assertThat(deletedCount, equalTo(TEXTS_COUNT__PER_VERSION__PER_LANGUAGE));
            }
        }
    }
}
