package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.persistence.entity.Language;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class LanguageRepositoryTest {

    @Autowired
    private LanguageRepository languageRepository;

    @Test
    public void findByCode() throws Exception {
        final List<Language> languages = languageRepository.findAll();
        // langs should be already in DB, if this will be changed in future rewrite test with data creation
        assertTrue(!languages.isEmpty());

        final Language language = languages.get(0);
        final Language testLang = languageRepository.findByCode(language.getCode());

        assertEquals(testLang, language);
    }

}
