package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static imcode.server.ImcmsConstants.ENG_CODE;
import static imcode.server.ImcmsConstants.SWE_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class LanguageRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private LanguageRepository languageRepository;

    @Test
    public void testFindAll() {
//        assertEquals(39, languageRepository.findAll().size()); update CI database !
    }

    @Test
    public void testFindByCode() {
        assertNotNull(languageRepository.findByCode(ENG_CODE));
        assertNotNull(languageRepository.findByCode(SWE_CODE));
    }

    @Test
    public void testSave() {
        LanguageJPA language = new LanguageJPA();

        language.setId(1);
        language.setCode(ENG_CODE);
        language.setName("English");

        languageRepository.save(language);
    }

    @Test
    public void findByCode() {
        final List<LanguageJPA> languages = languageRepository.findAll();
        // langs should be already in DB, if this will be changed in future rewrite test with data creation
        assertTrue(!languages.isEmpty());

        final LanguageJPA language = languages.get(0);
        final LanguageJPA testLang = languageRepository.findByCode(language.getCode());

        assertEquals(testLang, language);
    }

}
