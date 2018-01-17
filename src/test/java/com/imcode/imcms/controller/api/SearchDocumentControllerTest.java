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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            documentIndex.removeDocument(documentId);
        }
    }

    @Test
    public void getTextDocuments_When_SecondPageIsSet_Expect_DocumentStoredFieldsDtoJson() throws Exception {
        List<Integer> ids = new ArrayList<>();

        List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();
        for (int i = 0; i < 110; i++) {
            textDocumentDTOS.add(documentDataInitializer.createTextDocument());
        }

        textDocumentDTOS.forEach(textDocumentDTO -> {
            final int id = documentService.save(textDocumentDTO);
            ids.add(id);
            documentIndex.indexDocument(id);
        });

        Thread.sleep(TimeUnit.SECONDS.toMillis(20));

        final int from = 0;
        final int to = 10;

        final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = IntStream.range(from, to)
                .map(i -> to - i + from - 1)
                .mapToObj(textDocumentDTOS::get)
                .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                .collect(Collectors.toList());

        String expectedJson = asJson(documentStoredFieldsDTOS);

        try {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("page.page", String.valueOf(1))
                    .param("page.size", String.valueOf(100));

            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            ids.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    @Test
    public void checkTextDocumentWithMaxId_When_DefaultSearchQuerySet_Expect_isFirstFound() throws Exception {
        List<Integer> ids = new ArrayList<>();

        List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            textDocumentDTOS.add(documentDataInitializer.createTextDocument());
        }

        textDocumentDTOS.forEach(textDocumentDTO -> {
            final int id = documentService.save(textDocumentDTO);
            ids.add(id);
            documentIndex.indexDocument(id);
        });

        Thread.sleep(TimeUnit.SECONDS.toMillis(12));

        int maxId = ids.stream()
                .mapToInt(id -> id)
                .max()
                .getAsInt();

        try {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
            final String responseJSON = getJsonResponse(requestBuilder);
            final List list = fromJson(responseJSON, List.class);

            Assert.assertEquals(maxId, ((LinkedHashMap) list.get(0)).get("id"));

        } finally {
            ids.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }
}