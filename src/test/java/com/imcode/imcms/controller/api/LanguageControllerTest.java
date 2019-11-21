package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class LanguageControllerTest extends AbstractControllerTest {

    @Value("#{'${AvailableLanguages}'.split(';')}")
    private List<String> availableLanguages;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Override
    protected String controllerPath() {
        return "/languages";
    }

    @Test
    public void getAll_Expected_CorrectResult() throws Exception {
        final String languagesJSON = asJson(languageDataInitializer.createData());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, languagesJSON);
    }

    @Test
    public void getAvailable_Expected_CorrectResult() throws Exception {
        final String languagesJSON = asJson(languageDataInitializer.createData(availableLanguages));
        final String availableLink = "/available";
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + availableLink);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, languagesJSON);
    }

}
