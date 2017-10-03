package com.imcode.imcms.controller;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.util.datainitializer.LoopDataInitializer;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
@WebAppConfiguration
public class LoopControllerTest extends AbstractControllerTest {
    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_INDEX = 1;

    private static final LoopDTO TEST_LOOP_DTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Collections.emptyList());

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Before
    public void createData() {
        cleanRepos();
        loopDataInitializer.createData(TEST_DOC_ID, TEST_LOOP_INDEX);
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
                .param("loopIndex", String.valueOf(TEST_LOOP_INDEX))
                .param("docId", String.valueOf(TEST_DOC_ID));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJsonData);
    }

    @Test
    public void postLoop_When_UserIsNotAdmin_Expect_BadRequest() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        Imcms.setUser(user); // means current user is default user

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(TEST_LOOP_DTO));

        try {
            performRequestBuilderExpectedOk(requestBuilder);
            Assert.fail("Expected exception wasn't thrown!");

        } catch (NestedServletException e) {
            Assert.assertEquals(e.getCause().getMessage(), "User do not have access to change loop structure.");
        }
    }
}
