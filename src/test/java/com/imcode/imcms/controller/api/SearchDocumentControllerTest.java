package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class SearchDocumentControllerTest extends AbstractControllerTest {

    private static final int ADMIN_ID = 1;

    @Autowired
    private DocumentIndex documentIndex;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private Function<TextDocumentDTO, DocumentStoredFieldsDTO> textDocumentDTOtoDocumentStoredFieldsDTO;

    @Autowired
    private DocumentService<TextDocumentDTO> documentService;

    @Override
    protected String controllerPath() {
        return "/documents/search";
    }

    @Before
    public void setUp() throws Exception {
        final UserDomainObject user = new UserDomainObject(ADMIN_ID);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user);
    }

    @After
    public void cleanRepos() {
        Imcms.removeUser();
    }

    @Test
    public void getTextDocument_When_DefaultSearchQuery_Expect_DocumentStoredFieldsDtoJson() throws Exception {

        final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();

        documentService.save(textDocument);

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS =
                Collections.singletonList(textDocumentDTOtoDocumentStoredFieldsDTO.apply(textDocument));

        final String expectedJson = asJson(documentStoredFieldsDTOS);

        final Integer documentId = textDocument.getId();

        try {
            documentIndex.indexDocument(documentId);

            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            documentDataInitializer.cleanRepositories(documentId);
            documentIndex.indexDocument(documentId);
        }

    }
}