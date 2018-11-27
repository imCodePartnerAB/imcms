package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.domain.service.LanguageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Transactional
public class LanguageServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Test
    public void getAll_When_DefaultLanguagesInDatabase() {
        assertEquals(languageDataInitializer.createData(), languageService.getAll());
    }

}
