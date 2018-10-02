package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
