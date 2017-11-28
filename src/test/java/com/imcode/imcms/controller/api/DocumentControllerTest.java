package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class DocumentControllerTest extends AbstractControllerTest {

    private DocumentDTO createdDoc;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Before
    public void setUp() throws Exception {
        createdDoc = documentDataInitializer.createData();
    }

    @Test
    public void getDocument() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Override
    protected String controllerPath() {
        return "/documents";
    }
}