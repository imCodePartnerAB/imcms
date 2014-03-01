package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.JpaConfiguration;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.DocVersionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@Transactional
public class TextRepositoryTest {

    static final int DOC_ID = 1001;

    static final int MIN_TEXT_NO = 1;
    static final int MAX_TEXT_NO = 10;
    static final int LANGUAGES_COUNT__PER_VERSION = 2;
    static final int TEXTS_COUNT__PER_VERSION__PER_LANGUAGE = 10;
    static final int TEXTS_COUNT__PER_VERSION = LANGUAGES_COUNT__PER_VERSION * TEXTS_COUNT__PER_VERSION__PER_LANGUAGE;


    @Inject
    DocVersionRepository docVersionRepository;

    @Inject
    LanguageRepository languageRepository;

    @Inject
    TextRepository textRepository;

    @Inject
    UserRepository userRepository;

    List<Language> languages;

    List<DocVersion> docVersions;

    @Before
    public void setUp() {
        User user = userRepository.saveAndFlush(new User("admin", "admin", "admin@imcode.com"));
        languages = Arrays.asList(
                languageRepository.saveAndFlush(new Language("en", "English", "English")),
                languageRepository.saveAndFlush(new Language("se", "Swedish", "Svenska"))
        );

        docVersions = Arrays.asList(
                docVersionRepository.saveAndFlush(new DocVersion(DOC_ID, 0, user, new Date(), user, new Date())),
                docVersionRepository.saveAndFlush(new DocVersion(DOC_ID, 1, user, new Date(), user, new Date())),
                docVersionRepository.saveAndFlush(new DocVersion(DOC_ID, 2, user, new Date(), user, new Date()))
        );

        // texts with odd no have loop entry with the same loop no and entry no
        for (int no = MIN_TEXT_NO; no <= MAX_TEXT_NO; no++) {
            for (Language language : languages) {
                for (DocVersion docVersion : docVersions) {
                    Text text = new Text();

                    text.setNo(no);
                    text.setType(TextType.PLAIN_TEXT);
                    text.setLanguage(language);
                    text.setDocVersion(docVersion);

                    if ((no & 1) == 1) {
                        int loopNo = no;
                        int entryNo = no;
                        text.setLoopEntryRef(new LoopEntryRef(loopNo, entryNo));
                    }

                    textRepository.saveAndFlush(text);
                }
            }
        }
    }


    @Test
    public void testFindByDocVersionAndDocLanguage() throws Exception {
        fail();
//        for (DocVersion docVersion : docVersions) {
//            for (Language language : languages) {
//                List<Text> texts = textRepository.findByDocVersionAndDocLanguage(docVersion, language);
//                assertThat(texts.size(), is(TEXTS_COUNT__PER_VERSION__PER_LANGUAGE));
//            }
//        }
    }

    @Test
    public void testFindByDocVersionAndNoAndLoopEntryIsNull() throws Exception {
        for (int no = MIN_TEXT_NO; no <= MAX_TEXT_NO; no++) {
            for (Language language : languages) {
                for (DocVersion docVersion : docVersions) {
                    Text text = textRepository.findByDocVersionAndLanguageAndNoAndLoopEntryIsNull(
                            docVersion, language, no
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
    public void testFindByDocVersionAndDocLanguageAndNoAndLoopEntryRef() throws Exception {
        for (int no = MIN_TEXT_NO; no <= MAX_TEXT_NO; no++) {
            for (Language language : languages) {
                for (DocVersion docVersion : docVersions) {
                    LoopEntryRef entryRef = new LoopEntryRef(no, no);
                    Text text = textRepository.findByDocVersionAndLanguageAndNoAndLoopEntryRef(
                            docVersion, language, no, entryRef
                    );

                    if ((no & 1) == 1) {
                        assertNotNull(text);
                        assertEquals(entryRef, text.getLoopEntryRef());
                    } else {
                        assertNull(text);
                    }
                }
            }
        }
    }

    @Test
    public void testFindByDocVersionAndNoAndLoopEntryRef() throws Exception {
        fail();
    }

    @Test
    public void testFindByDocVersionAndDocLanguageAndNoAndLoopEntryRefIsNull() throws Exception {
        fail();
    }


    @Test
    public void testDeleteByDocVersionAndDocLanguage() throws Exception {
        for (Language language : languages) {
            for (DocVersion docVersion : docVersions) {
                int deletedCount = textRepository.deleteByDocVersionAndLanguage(docVersion, language);

                assertThat(deletedCount, equalTo(TEXTS_COUNT__PER_VERSION__PER_LANGUAGE));
            }
        }
    }
}
