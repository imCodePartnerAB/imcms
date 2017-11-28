package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class LanguageControllerTest extends AbstractControllerTest {

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Override
    protected String controllerPath() {
        return "/languages";
    }

    @Test
    public void getLanguages() throws Exception {
        final String languagesJSON = asJson(languageDataInitializer.createData());
        getAllExpectedOkAndJsonContentEquals(languagesJSON);
    }

}
