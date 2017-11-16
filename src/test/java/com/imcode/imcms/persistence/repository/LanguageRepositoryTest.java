package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.persistence.entity.Language;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static imcode.server.ImcmsConstants.SWE_CODE;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class LanguageRepositoryTest {

    @Autowired
    private LanguageRepository languageRepository;

    @Test
    public void testFindAll() throws Exception {
        assertEquals(2, languageRepository.findAll().size());
    }

    @Test
    public void testFindByCode() throws Exception {
        assertNotNull(languageRepository.findByCode(ENG_CODE));
        assertNotNull(languageRepository.findByCode(SWE_CODE));
    }

    @Test
    public void testSave() throws Exception {
        Language language = new Language();

        language.setId(1);
        language.setCode(ENG_CODE);
        language.setName("English");

        languageRepository.save(language);
    }

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
