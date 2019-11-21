package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.exception.LanguageNotAvailableException;
import com.imcode.imcms.domain.service.LanguageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
public class LanguageServiceTest extends WebAppSpringTestConfig {

    @Value("#{'${AvailableLanguages}'.split(';')}")
    private List<String> availableLanguages;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Test
    public void getAvailableLngs_When_TwoLanguagesAvailable_Expected_CorrectResult() {
        assertEquals(languageDataInitializer.createData(availableLanguages), languageService.getAvailableLanguages());
    }

    @Test
    public void getAll_Expected_CorrectResultAndSize() {
        assertEquals(languageDataInitializer.createData(), languageService.getAll());
        assertEquals(languageDataInitializer.createData().size(), languageService.getAll().size());
    }

    @Test
    public void findByCode_When_LanguageAvailable_Expected_CorrectResult() {
        final LanguageDTO expectedLanguage = languageDataInitializer.createData().get(0);
        assertEquals(expectedLanguage, languageService.findByCode(expectedLanguage.getCode()));
    }

    @Test
    public void findByCode_When_LanguageNotAvailable_Expected_CorrectException() {
        assertThrows(LanguageNotAvailableException.class, () -> languageService.findByCode("unknown"));
    }

}
