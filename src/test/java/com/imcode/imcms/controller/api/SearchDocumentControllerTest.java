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

    @Test
    public void searchTextDocuments_When_UserIdSet_Expect_Found() throws Exception {
        final int documentNumberFirstUser = 14;
        final int documentNumberSecondUser = 6;

        //create 2 users
        final List<User> users = userDataInitializer.createData(2, RoleId.USERS_ID);

        List<Integer> ids = new ArrayList<>();

        List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        // create docs for 1st user
        for (int i = 0; i < documentNumberFirstUser; i++) {
            AuditDTO auditDTO = new AuditDTO();
            auditDTO.setId(users.get(0).getId());

            final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();
            textDocument.setCreated(auditDTO);

            textDocumentDTOS.add(textDocument);
        }

        // create docs for 2st user
        for (int i = 0; i < documentNumberSecondUser; i++) {
            AuditDTO auditDTO = new AuditDTO();
            auditDTO.setId(users.get(1).getId());

            final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();
            textDocument.setCreated(auditDTO);

            textDocumentDTOS.add(textDocument);
        }

        textDocumentDTOS.forEach(textDocumentDTO -> {
            final int id = documentService.save(textDocumentDTO);
            ids.add(id);
            documentIndex.indexDocument(id);
        });

        Thread.sleep(TimeUnit.SECONDS.toMillis(20));

        try {
            // create query for 1st user
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("userId", users.get(0).getId().toString());
            String responseJSON = getJsonResponse(requestBuilder);
            List documentList = fromJson(responseJSON, List.class);

            // check document number for 1st user
            Assert.assertEquals(documentNumberFirstUser, documentList.size());

            requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("userId", users.get(1).getId().toString());
            responseJSON = getJsonResponse(requestBuilder);
            documentList = fromJson(responseJSON, List.class);

            // check document number for 2st user
            Assert.assertEquals(documentNumberSecondUser, documentList.size());

        } finally {
            ids.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });

            userDataInitializer.cleanRepositories(users);
        }
    }

    @Test
    public void searchTextDocuments_When_SpecifiedCategorySet_Expect_Found() throws Exception {
        final int documentNumberWithSpecifiedCategory = 14;
        final int documentNumberWithoutCategory = 6;
        final int categoryNumber = 1;

        categoryDataInitializer.createData(categoryNumber);
        final List<CategoryDTO> categories = categoryDataInitializer.getCategoriesAsDTO();

        List<Integer> ids = new ArrayList<>();

        List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();
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
            ids.add(id);
            documentIndex.indexDocument(id);
        });

        Thread.sleep(TimeUnit.SECONDS.toMillis(12));

        final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = IntStream.range(0, documentNumberWithSpecifiedCategory)
                .map(i -> documentNumberWithSpecifiedCategory - i - 1)
                .mapToObj(textDocumentDTOS::get)
                .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                .collect(Collectors.toList());

        String expectedJson = asJson(documentStoredFieldsDTOS);

        try {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("categoriesId[0]", String.valueOf(categories.get(0).getId()));

            Assert.assertEquals(documentNumberWithSpecifiedCategory, fromJson(getJsonResponse(requestBuilder), List.class).size());
            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            ids.forEach(id -> {
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

    private void testForLastDigits(int lastDigitsNumber) throws Exception {
        final int documentNumber = 35;

        final List<Integer> ids = new ArrayList<>();

        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();
        for (int i = 0; i < documentNumber; i++) {
            textDocumentDTOS.add(documentDataInitializer.createTextDocument());
        }

        textDocumentDTOS.forEach(textDocumentDTO -> {
            final int id = documentService.save(textDocumentDTO);
            ids.add(id);
            documentIndex.indexDocument(id);
        });

        Thread.sleep(TimeUnit.SECONDS.toMillis(10));

        final String firstId = String.valueOf(ids.get(0));
        final String lastDigitsOfFirstID = firstId.substring(firstId.length() - lastDigitsNumber);

        final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = textDocumentDTOS.stream()
                .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                .filter(doc -> String.valueOf(doc.getId()).contains(lastDigitsOfFirstID))
                .collect(Collectors.toList());

        final int expectedSize = documentStoredFieldsDTOS.size();

        Collections.reverse(documentStoredFieldsDTOS);

        final String expectedJson = asJson(documentStoredFieldsDTOS);

        try {
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("term", lastDigitsOfFirstID);

            Assert.assertEquals(expectedSize, fromJson(getJsonResponse(requestBuilder), List.class).size());
            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, expectedJson);

        } finally {
            ids.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }
}