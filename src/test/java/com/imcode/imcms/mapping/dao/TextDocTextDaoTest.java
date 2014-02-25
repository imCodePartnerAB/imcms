package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.*;
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
public class TextDocTextDaoTest {

    static final int DOC_ID = 1001;

    static final int MIN_TEXT_NO = 1;
    static final int MAX_TEXT_NO = 10;
    static final int LANGUAGES_COUNT__PER_VERSION = 2;
    static final int TEXTS_COUNT__PER_VERSION__PER_LANGUAGE = 10;
    static final int TEXTS_COUNT__PER_VERSION = LANGUAGES_COUNT__PER_VERSION * TEXTS_COUNT__PER_VERSION__PER_LANGUAGE;


    @Inject
    DocVersionDao docVersionDao;

    @Inject
    DocLanguageDao docLanguageDao;

    @Inject
    TextDocTextDao textDocTextDao;

    @Inject
    UserDao userDao;

    List<DocLanguage> docLanguages;

    List<DocVersion> docVersions;

    @Before
    public void setUp() {
        User user = userDao.saveAndFlush(new User("admin", "admin", "admin@imcode.com"));
        docLanguages = Arrays.asList(
                docLanguageDao.saveAndFlush(new DocLanguage("en", "English", "English", true)),
                docLanguageDao.saveAndFlush(new DocLanguage("se", "Swedish", "Svenska", true))
        );

        docVersions = Arrays.asList(
                docVersionDao.saveAndFlush(new DocVersion(DOC_ID, 0, user, new Date(), user, new Date())),
                docVersionDao.saveAndFlush(new DocVersion(DOC_ID, 1, user, new Date(), user, new Date())),
                docVersionDao.saveAndFlush(new DocVersion(DOC_ID, 2, user, new Date(), user, new Date()))
        );

        // texts with odd no have loop entry with the same loop no and entry no
        for (int no = MIN_TEXT_NO; no <= MAX_TEXT_NO; no++) {
            for (DocLanguage docLanguage : docLanguages) {
                for (DocVersion docVersion: docVersions) {
                    TextDocText text = new TextDocText();

                    text.setNo(no);
                    text.setType(TextDocType.PLAIN_TEXT);
                    text.setDocLanguage(docLanguage);
                    text.setDocVersion(docVersion);

                    if ((no & 1) == 1) {
                        int loopNo = no;
                        int entryNo = no;
                        text.setLoopEntryRef(new TextDocLoopEntryRef(loopNo, entryNo));
                    }

                    textDocTextDao.saveAndFlush(text);
                }
            }
        }
    }

    @Test
    public void testFindByDocVersion() throws Exception {
        for (DocVersion docVersion: docVersions) {
            List<TextDocText> texts = textDocTextDao.findByDocVersion(docVersion);
            assertThat(texts.size(), is(TEXTS_COUNT__PER_VERSION));
        }
    }

    @Test
    public void testFindByDocVersionAndDocLanguage() throws Exception {
        for (DocVersion docVersion: docVersions) {
            for (DocLanguage docLanguage : docLanguages) {
                List<TextDocText> texts = textDocTextDao.findByDocVersionAndDocLanguage(docVersion, docLanguage);
                assertThat(texts.size(), is(TEXTS_COUNT__PER_VERSION__PER_LANGUAGE));
            }
        }
    }

    @Test
    public void testFindByDocVersionAndNoAndLoopEntryIsNull() throws Exception {
        for (int no = MIN_TEXT_NO; no <= MAX_TEXT_NO; no++) {
            for (DocLanguage docLanguage : docLanguages) {
                for (DocVersion docVersion: docVersions) {
                    TextDocText text = textDocTextDao.findByDocVersionAndDocLanguageAndNoAndLoopEntryIsNull(
                            docVersion, docLanguage, no
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
            for (DocLanguage docLanguage : docLanguages) {
                for (DocVersion docVersion: docVersions) {
                    TextDocLoopEntryRef entryRef = new TextDocLoopEntryRef(no, no);
                    TextDocText text = textDocTextDao.findByDocVersionAndDocLanguageAndNoAndLoopEntryRef(
                            docVersion, docLanguage, no, entryRef
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
        for (DocLanguage docLanguage : docLanguages) {
            for (DocVersion docVersion: docVersions) {
                int deletedCount = textDocTextDao.deleteByDocVersionAndDocLanguage(docVersion, docLanguage);

                assertThat(deletedCount, equalTo(TEXTS_COUNT__PER_VERSION__PER_LANGUAGE));
            }
        }
    }
}
