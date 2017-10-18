package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Loop;
import com.imcode.imcms.persistence.repository.LoopRepository;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
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

import java.util.Collections;

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

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now
    }

    @Override
    protected String controllerPath() {
        return "/loops";
    }

    @Test
    public void getLoop_Expect_OkAndResponseEqualTestData() throws Exception {
        final String expectedJsonData = asJson(TEST_LOOP_DTO);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("index", String.valueOf(TEST_LOOP_INDEX))
                .param("docId", String.valueOf(TEST_DOC_ID));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJsonData);
    }

    @Test
    public void postLoop_When_UserIsNotAdmin_Expect_IllegalAccessException() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        Imcms.setUser(user); // means current user is default user

        performPostWithContentExpectException(TEST_LOOP_DTO, IllegalAccessException.class);
    }

    @Test
    public void postLoop_When_UserIsAdmin_Expect_Ok() throws Exception {
        performPostWithContentExpectOk(TEST_LOOP_DTO);
    }

    @Test
    public void postLoop_When_UserIsAdminAndDocNotExist_Expect_DocumentNotExistException() throws Exception {
        final int nonExistingDocId = -13;
        final LoopDTO loopDTO = new LoopDTO(nonExistingDocId, TEST_LOOP_INDEX, Collections.emptyList());

        performPostWithContentExpectException(loopDTO, DocumentNotExistException.class);
    }

    @Test
    public void postLoop_When_UserIsAdminAndLoopNotExist_Expect_Ok() throws Exception {
        final int nonExistingLoopIndex = 666;
        final Version workingVersion = versionRepository.findWorking(TEST_DOC_ID);
        final Loop shouldNotExist = loopRepository.findByVersionAndIndex(workingVersion, nonExistingLoopIndex);

        if (shouldNotExist != null) { // this should never happen, but...
            loopRepository.delete(shouldNotExist);
        }

        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, nonExistingLoopIndex, Collections.emptyList());
        performPostWithContentExpectOk(loopDTO);
    }
}
