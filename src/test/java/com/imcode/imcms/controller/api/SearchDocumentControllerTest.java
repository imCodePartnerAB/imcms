package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.mapping.jpa.User;
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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

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
        final Integer documentId = documentService.save(textDocument);

        try {
            waitForIndexUpdates();

            final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS =
                    Collections.singletonList(textDocumentDTOtoDocumentStoredFieldsDTO.apply(textDocument));

            final String expectedJson = asJson(documentStoredFieldsDTOS);
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());

            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            documentDataInitializer.cleanRepositories(documentId);
            documentIndex.removeDocument(documentId);
        }
    }

    @Test
    public void getTextDocuments_When_SecondPageIsSet_Expect_DocumentStoredFieldsDtoJson() throws Exception {

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < 110; i++) {
                textDocumentDTOS.add(documentDataInitializer.createTextDocument());
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO);
                docIds.add(id);
            });

            waitForIndexUpdates();

            final int from = 0;
            final int to = 10;

            final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = IntStream.range(from, to)
                    .map(i -> to - i + from - 1)
                    .mapToObj(textDocumentDTOS::get)
                    .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                    .collect(Collectors.toList());

            final String expectedJson = asJson(documentStoredFieldsDTOS);

            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("page.page", String.valueOf(1))
                    .param("page.size", String.valueOf(100));

            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    @Test
    public void checkTextDocumentWithMaxId_When_DefaultSearchQuerySet_Expect_isFirstFound() throws Exception {

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < 50; i++) {
                textDocumentDTOS.add(documentDataInitializer.createTextDocument());
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO);
                docIds.add(id);
            });

            final OptionalInt oMax = docIds.stream()
                    .mapToInt(id -> id)
                    .max();

            assertTrue(oMax.isPresent());

            final int maxId = oMax.getAsInt();

            waitForIndexUpdates();

            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
            final String responseJSON = getJsonResponse(requestBuilder);
            final List list = fromJson(responseJSON, List.class);

            assertEquals(maxId, ((LinkedHashMap) list.get(0)).get("id"));

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    @Test
    public void searchTextDocuments_When_UserIdSet_Expect_Found() throws Exception {
        final int documentNumberFirstUser = 14;
        final int documentNumberSecondUser = 6;

        //create 2 users
        final List<User> users = userDataInitializer.createData(2, RoleId.USERS_ID);
        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            // create docs for 1st user
            for (int i = 0; i < documentNumberFirstUser; i++) {
                final AuditDTO auditDTO = new AuditDTO();
                auditDTO.setId(users.get(0).getId());

                final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();
                textDocument.setCreated(auditDTO);

                textDocumentDTOS.add(textDocument);
            }

            // create docs for 2st user
            for (int i = 0; i < documentNumberSecondUser; i++) {
                final AuditDTO auditDTO = new AuditDTO();
                auditDTO.setId(users.get(1).getId());

                final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();
                textDocument.setCreated(auditDTO);

                textDocumentDTOS.add(textDocument);
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO);
                docIds.add(id);
            });

            waitForIndexUpdates();

            // create query for 1st user
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("userId", users.get(0).getId().toString());

            String responseJSON = getJsonResponse(requestBuilder);
            List documentList = fromJson(responseJSON, List.class);

            // check document number for 1st user
            assertEquals(documentNumberFirstUser, documentList.size());

            requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("userId", users.get(1).getId().toString());
            responseJSON = getJsonResponse(requestBuilder);
            documentList = fromJson(responseJSON, List.class);

            // check document number for 2st user
            assertEquals(documentNumberSecondUser, documentList.size());

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });

            userDataInitializer.cleanRepositories(users);
        }
    }

    @Test
    public void searchTextDocuments_When_SpecifiedCategorySet_Expect_Found() throws Exception {
        final int documentNumberWithSpecifiedCategory = 10;
        final int documentNumberWithoutCategory = 6;
        final int categoryNumber = 1;

        categoryDataInitializer.createData(categoryNumber);

        final List<CategoryDTO> categories = categoryDataInitializer.getCategoriesAsDTO();
        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < documentNumberWithSpecifiedCategory; i++) {
                final TextDocumentDTO textDocumentWithCategory = documentDataInitializer.createTextDocument();
                textDocumentWithCategory.getCategories().add(categories.get(0));
                textDocumentDTOS.add(textDocumentWithCategory);
            }

            for (int i = 0; i < documentNumberWithoutCategory; i++) {
                textDocumentDTOS.add(documentDataInitializer.createTextDocument());
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO);
                docIds.add(id);
            });

            waitForIndexUpdates();

            final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = IntStream.range(0, documentNumberWithSpecifiedCategory)
                    .map(i -> documentNumberWithSpecifiedCategory - i - 1)
                    .mapToObj(textDocumentDTOS::get)
                    .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                    .collect(Collectors.toList());

            final String expectedJson = asJson(documentStoredFieldsDTOS);
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("categoriesId[0]", String.valueOf(categories.get(0).getId()));

            assertEquals(documentNumberWithSpecifiedCategory, fromJson(getJsonResponse(requestBuilder), List.class).size());
            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });

            categoryDataInitializer.cleanRepositories();
        }
    }

    @Test
    public void searchTextDocuments_When_TermIsSetAsLastDigitOfSpecifiedId_Expect_FoundDocuments() throws Exception {
        testForLastDigits(1);
    }

    @Test
    public void searchTextDocuments_When_TermIsSetAsLastTwoDigitsOfSpecifiedId_Expect_FoundDocuments() throws Exception {
        testForLastDigits(2);
    }

    @Test
    public void searchTextDocuments_When_TermIsSetAsLastThreeDigitsOfSpecifiedId_Expect_FoundDocuments() throws Exception {
        testForLastDigits(3);
    }

    @Test
    public void searchTextDocuments_When_TermIsSetAsLastFourDigitsOfSpecifiedId_Expect_FoundDocuments() throws Exception {
        testForLastDigits(4);
    }

    @Test
    public void checkTextDocument_When_TermIsSetAsSpecifiedDocId_Expect_Found() throws Exception {
        final int documentNumber = 10;
        final int docIdChecking = 4; // between 0 and documentNumber-1

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < documentNumber; i++) {
                final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();
                textDocumentDTOS.add(textDocument);
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO);
                docIds.add(id);
            });

            waitForIndexUpdates();

            List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = textDocumentDTOS.stream()
                    .skip(docIdChecking)
                    .limit(1)
                    .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                    .collect(Collectors.toList());

            final String expectedJson = asJson(documentStoredFieldsDTOS);
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("term", String.valueOf(textDocumentDTOS.get(docIdChecking).getId()));

            assertEquals(1, fromJson(getJsonResponse(requestBuilder), List.class).size());
            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    private void testForLastDigits(int lastDigitsNumber) throws Exception {
        final int documentNumber = 35;

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            textDocumentDTOS.add(documentDataInitializer.createTextDocument());
        }

        textDocumentDTOS.forEach(textDocumentDTO -> {
            final int id = documentService.save(textDocumentDTO);
            docIds.add(id);
        });

        waitForIndexUpdates();

        final String firstId = String.valueOf(docIds.get(0));
        final String lastDigitOfFirstID = firstId.substring(firstId.length() - lastDigitsNumber);

        final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = textDocumentDTOS.stream()
                .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                .filter(doc -> String.valueOf(doc.getId()).contains(lastDigitOfFirstID))
                .collect(Collectors.toList());

        final int expectedDocumentListSize = documentStoredFieldsDTOS.size();

        Collections.reverse(documentStoredFieldsDTOS);

        final String expectedJson = asJson(documentStoredFieldsDTOS);

        try {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("term", lastDigitOfFirstID);

            assertEquals(expectedDocumentListSize, fromJson(getJsonResponse(requestBuilder), List.class).size());
            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    private void waitForIndexUpdates() throws InterruptedException {
        while (!documentIndex.isUpdateDone()) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }
}