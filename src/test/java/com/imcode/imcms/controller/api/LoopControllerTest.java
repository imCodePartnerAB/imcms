package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LoopRepository;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Transactional
public class LoopControllerTest extends AbstractControllerTest {
    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_INDEX = 1;

    private static final Loop TEST_LOOP = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Collections.emptyList());

    @Autowired
    private LoopDataInitializer loopDataInitializer;
    @Autowired
    private LoopRepository loopRepository;
    @Autowired
    private VersionRepository versionRepository;

    @BeforeEach
    public void createData() {
        loopDataInitializer.createData(TEST_LOOP);

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now
    }

    @Override
    protected String controllerPath() {
        return "/loops";
    }

    @Test
    public void getLoop_Expect_OkAndResponseEqualTestData() throws Exception {
        final String expectedJsonData = asJson(TEST_LOOP);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("index", String.valueOf(TEST_LOOP_INDEX))
                .param("docId", String.valueOf(TEST_DOC_ID));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJsonData);
    }

    @Test
    public void postLoop_When_UserIsNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        Imcms.setUser(user); // means current user is default user

        performPostWithContentExpectException(TEST_LOOP, NoPermissionToEditDocumentException.class);
    }

    @Test
    public void postLoop_When_UserIsAdmin_Expect_Ok() throws Exception {
        performPostWithContentExpectOk(TEST_LOOP);
    }

    @Test
    public void postLoop_When_UserIsAdminAndDocNotExist_Expect_DocumentNotExistException() throws Exception {
        final int nonExistingDocId = -13;
        final Loop loop = new LoopDTO(nonExistingDocId, TEST_LOOP_INDEX, Collections.emptyList());

        performPostWithContentExpectException(loop, DocumentNotExistException.class);
    }

    @Test
    public void postLoop_When_UserIsAdminAndLoopNotExist_Expect_Ok() throws Exception {
        final int nonExistingLoopIndex = 666;
        final Version workingVersion = versionRepository.findWorking(TEST_DOC_ID);
        final LoopJPA shouldNotExist = loopRepository.findByVersionAndIndex(workingVersion, nonExistingLoopIndex);

        if (shouldNotExist != null) { // this should never happen, but...
            loopRepository.delete(shouldNotExist);
        }

        final Loop loop = new LoopDTO(TEST_DOC_ID, nonExistingLoopIndex, Collections.emptyList());
        performPostWithContentExpectOk(loop);
    }

    @Test
    public void postLoop_When_NotEmptyEntries_Expect_Saved() throws Exception {
        final List<LoopEntryDTO> entries = Arrays.asList(
                new LoopEntryDTO(1, true),
                new LoopEntryDTO(2, false),
                new LoopEntryDTO(3, true)
        );

        final Loop loop = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, entries);

        performPostWithContentExpectOk(loop);

        final String expectedJsonData = asJson(loop);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("index", String.valueOf(TEST_LOOP_INDEX))
                .param("docId", String.valueOf(TEST_DOC_ID));

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJsonData);
    }
}
