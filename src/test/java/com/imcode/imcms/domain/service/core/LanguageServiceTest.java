package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
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