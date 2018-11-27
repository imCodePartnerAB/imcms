package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TextRepositoryTest extends WebAppSpringTestConfig {

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

    @BeforeEach
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
                    text.setType(Text.Type.TEXT);
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
    public void testFindByDocVersionAndIndexAndLoopEntryIsNull() {
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
    public void testFindByDocVersionAndIndexAndLoopEntryRef() {
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
    public void testDeleteByDocVersionAndLanguage() {
        for (LanguageJPA language : languages) {
            for (Version version : versions) {
                int deletedCount = textRepository.deleteByVersionAndLanguage(version, language);

                assertEquals(deletedCount, TEXTS_COUNT__PER_VERSION__PER_LANGUAGE);
            }
        }
    }

    @Test
    public void testFindByDocVersionAndLanguage() {
        for (LanguageJPA language : languages) {
            for (Version version : versions) {
                final Set<TextJPA> texts = textRepository.findByVersionAndLanguage(version, language);

                assertEquals(texts.size(), TEXTS_COUNT__PER_VERSION__PER_LANGUAGE);
            }
        }
    }

    @Test
    public void deleteByDocId() {
        final long prevNumberOfTextsForDoc = textRepository.findAll().stream()
                .filter(textJPA -> Objects.equals(DOC_ID, textJPA.getVersion().getDocId()))
                .count();

        assertNotEquals(prevNumberOfTextsForDoc, 0L);

        textRepository.deleteByDocId(DOC_ID);

        final long newNumberOfTextsForDoc = textRepository.findAll().stream()
                .filter(textJPA -> Objects.equals(DOC_ID, textJPA.getVersion().getDocId()))
                .count();

        assertEquals(newNumberOfTextsForDoc, 0L);
    }
}
