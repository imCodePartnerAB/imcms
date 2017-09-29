package com.imcode.imcms.controller;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.util.datainitializer.LoopDataInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
@WebAppConfiguration
public class LoopControllerTest extends AbstractControllerTest {
    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_ID = 1;

    private static final LoopDTO TEST_LOOP_DTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_ID, Collections.emptyList());

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Before
    public void createData() {
        loopDataInitializer.createData(TEST_DOC_ID, TEST_LOOP_ID);
    }

    @After
    public void cleanRepos() {
        loopDataInitializer.cleanRepositories();
    }

    @Override
    protected String controllerPath() {
        return "/loop";
    }

    @Test
    public void getLoopExpectedOkAndResponseEqualTestData() throws Exception {
        final String expectedJsonData = asJson(TEST_LOOP_DTO);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("loopId", String.valueOf(TEST_LOOP_ID))
                .param("docId", String.valueOf(TEST_DOC_ID));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJsonData);
    }
}
