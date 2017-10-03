package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.util.datainitializer.VersionDataInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
public class TextRepositoryTest {

    private static final int DOC_ID = 1001;
    private static final int MIN_TEXT_NO = 1;
    private static final int MAX_TEXT_NO = 10;
    private static final int TEXTS_COUNT__PER_VERSION__PER_LANGUAGE = 10;

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private VersionDataInitializer versionDataInitializer;

    private List<Language> languages;
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
        for (int no = MIN_TEXT_NO; no <= MAX_TEXT_NO; no++) {
            for (Language language : languages) {
                for (Version version : versions) {
                    Text text = new Text();

                    text.setNo(no);
                    text.setType(TextType.PLAIN_TEXT);
                    text.setLanguage(language);
                    text.setVersion(version);
                    text.setText("test");

                    if ((no & 1) == 1) {
                        text.setLoopEntryRef(new LoopEntryRef(no, no));
                    }

                    textRepository.save(text);
                }
            }
        }
        textRepository.flush();
    }

    @Test
    public void testFindByDocVersionAndNoAndLoopEntryIsNull() throws Exception {
        for (int no = MIN_TEXT_NO; no <= MAX_TEXT_NO; no++) {
            for (Language language : languages) {
                for (Version version : versions) {
                    Text text = textRepository.findByVersionAndLanguageAndNoWhereLoopEntryRefIsNull(
                            version, language, no
                    );

                    if ((no & 1) == 1) {
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
    public void testDeleteByDocVersionAndLanguage() throws Exception {
        for (Language language : languages) {
            for (Version version : versions) {
                int deletedCount = textRepository.deleteByVersionAndLanguage(version, language);

                assertThat(deletedCount, equalTo(TEXTS_COUNT__PER_VERSION__PER_LANGUAGE));
            }
        }
    }
}
