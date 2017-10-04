package com.imcode.imcms.controller;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.service.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import com.imcode.imcms.util.datainitializer.LoopDataInitializer;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
@WebAppConfiguration
@Transactional
public class LoopControllerTest extends AbstractControllerTest {
    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_INDEX = 1;

    private static final LoopDTO TEST_LOOP_DTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Collections.emptyList());

    @Autowired
    private LoopDataInitializer loopDataInitializer;
    @Autowired
    private LoopRepository loopRepository;
    @Autowired
    private VersionRepository versionRepository;

    @Before
    public void createData() {
        loopDataInitializer.createData(TEST_LOOP_DTO);
    }

    @Override
    protected String controllerPath() {
        return "/loop";
    }

    @Test
    public void getLoop_Expect_OkAndResponseEqualTestData() throws Exception {
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
            performRequestBuilderExpectedOk(requestBuilder); // here exception should be thrown!!1
            fail("Expected exception wasn't thrown!");

        } catch (NestedServletException e) {
            assertTrue(
                    "Should be DocumentNotExistException!!",
                    (e.getCause() instanceof IllegalAccessException)
            );
            return;
        }

        fail("Expected exception wasn't thrown!");
    }

    @Test
    public void postLoop_When_UserIsAdmin_Expect_Ok() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final MockHttpServletRequestBuilder getLoopReqBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("loopIndex", String.valueOf(TEST_LOOP_INDEX))
                .param("docId", String.valueOf(TEST_DOC_ID));

        final String loopJson = performRequestBuilderExpectedOkAndContentJsonUtf8(getLoopReqBuilder)
                .andReturn()
                .getResponse()
                .getContentAsString();

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(loopJson);

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void postLoop_When_UserIsAdminAndDocNotExist_Expect_Exception() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final int nonExistingDocId = -13;
        final LoopDTO loopDTO = new LoopDTO(nonExistingDocId, TEST_LOOP_INDEX, Collections.emptyList());
        final String jsonData = asJson(loopDTO);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonData);

        try {
            performRequestBuilderExpectedOk(requestBuilder); // here exception should be thrown!!1
            fail("Expected exception wasn't thrown!");

        } catch (NestedServletException e) {
            assertTrue(
                    "Should be DocumentNotExistException!!",
                    (e.getCause() instanceof DocumentNotExistException)
            );
            return;
        }

        fail("Expected exception wasn't thrown!");
    }

    @Test
    public void postLoop_When_UserIsAdminAndLoopNotExist_Expect_Ok() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final int nonExistingLoopIndex = 666;
        final Version workingVersion = versionRepository.findWorking(TEST_DOC_ID);
        final Loop shouldNotExist = loopRepository.findByVersionAndNo(workingVersion, nonExistingLoopIndex);

        if (shouldNotExist != null) { // this should newer happen, but...
            loopRepository.delete(shouldNotExist);
        }

        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, nonExistingLoopIndex, Collections.emptyList());
        final String jsonData = asJson(loopDTO);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonData);

        performRequestBuilderExpectedOk(requestBuilder);
    }
}
