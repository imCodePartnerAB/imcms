package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.TemplateDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TemplateControllerTest extends AbstractControllerTest {

    @Autowired
    private TemplateDataInitializer dataInitializer;
    private List<TemplateDTO> templatesExpected;

    @Override
    protected String controllerPath() {
        return "/templates";
    }

    @Before
    public void setUp() throws Exception {
        dataInitializer.cleanRepositories();
        templatesExpected = dataInitializer.createData(5);
    }

    @After
    public void tearDown() throws Exception {
        dataInitializer.cleanRepositories();
    }

    @Test
    public void getTemplatesTest() throws Exception {
        final String templatesJson = asJson(templatesExpected);
        getAllExpectedOkAndJsonContentEquals(templatesJson);
    }

}
