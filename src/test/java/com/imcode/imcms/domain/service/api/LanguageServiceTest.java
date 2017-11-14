package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class LanguageServiceTest {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Test
    public void getAll_When_DefaultLanguagesInDatabase() throws Exception {
        assertEquals(languageDataInitializer.createData(), languageService.getAll());
    }

}