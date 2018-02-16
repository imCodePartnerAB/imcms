package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.CategoryTypeService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.SearchDocumentService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.CategoryTypeJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserRoles;
import com.imcode.imcms.persistence.repository.UserRepository;
import com.imcode.imcms.persistence.repository.UserRolesRepository;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by dmizem from Ubrainians for imCode on 20.10.17.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class SearchDocumentServiceTest {

    private static final int DOC_ID = 1001;

    private static boolean flag = true;

    private static VersionDataInitializer versionDataInitializerStatic;

    private static final List<String> mockData = new ArrayList<>();

    private static final String titleField = DocumentIndex.FIELD__META_HEADLINE;
    private static final String aliasField = DocumentIndex.FIELD__ALIAS;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private SearchDocumentService searchDocumentService;

    @Autowired
    private Config config;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TextDocumentDataInitializer documentDataInitializer;

    @Autowired
    private DocumentService<TextDocumentDTO> documentService;

    @Autowired
    private DocumentIndex documentIndex;

    @Autowired
    private Function<TextDocumentDTO, DocumentStoredFieldsDTO> textDocumentDTOtoDocumentStoredFieldsDTO;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeClass
    public static void setMockData() {
        mockData.add("asdf a");
        mockData.add("555");
        mockData.add("1");
        mockData.add("1187 ENG+ (1)");
        mockData.add("DSTE 12");
    }

    @AfterClass
    public static void shutDown() {
        Imcms.removeUser();
        versionDataInitializerStatic.cleanRepositories();
    }

    @PostConstruct
    public void initSolr() throws Exception {

        versionDataInitializerStatic = versionDataInitializer;

        final File testSolrFolder = new File(config.getSolrHome());

        if (flag) {
            if (!testSolrFolder.mkdirs()) {
                try {
                    FileUtility.forceDelete(testSolrFolder);
                } catch (IOException e) {
                    // windows user may receive it
                }
            }

            FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);
            flag = false;
        }

        versionDataInitializer.cleanRepositories();

        versionDataInitializer.createData(0, DOC_ID);
        versionDataInitializer.createData(1, DOC_ID);
        versionDataInitializer.createData(2, DOC_ID);

        Imcms.invokeStart();

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(user); // means current user is admin now

        Thread.sleep(TimeUnit.SECONDS.toMillis(1)); // to let solr init
    }

    @Test
    public void checkTextDocument_When_TermIsSetAsKeyword_Expect_Found() throws Exception {
        testKeywordOrAliasOrHeadlineForOneDocument("keyword");
    }

    @Test
    public void checkTextDocument_When_TermIsSetAsAlias_Expect_Found() throws Exception {
        testKeywordOrAliasOrHeadlineForOneDocument("alias");
    }

    @Test
    public void searchTextDocument_When_TermIsSetAsHeadline_Expect_Found() throws Exception {
        testKeywordOrAliasOrHeadlineForOneDocument("headline");
    }

    @Test
    public void checkTextDocuments_When_TermIsSetAsKeyword_Expect_DocumentsIsFound() throws Exception {
        testKeywordOrAliasOrHeadlineForMultipleDocuments("keyword");
    }

    @Test
    public void checkTextDocuments_When_TermIsSetAsAlias_Expect_DocumentsIsFound() throws Exception {
        testKeywordOrAliasOrHeadlineForMultipleDocuments("alias");
    }

    @Test
    public void checkTextDocuments_When_TermIsSetAsTitle_Expect_DocumentsIsFound() throws Exception {
        testKeywordOrAliasOrHeadlineForMultipleDocuments("headline");
    }

    private void testKeywordOrAliasOrHeadlineForMultipleDocuments(String field) throws Exception {
        final int documentNumberWithSpecifiedKeyword = 6;
        final int documentNumberWithoutKeyword = 4;

        final String termText = "some_text" + new Random().nextInt(100);

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < documentNumberWithSpecifiedKeyword; i++) {
                final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();

                setTextDocumentField(field, textDocument, termText);
                textDocumentDTOS.add(textDocument);
            }

            for (int i = 0; i < documentNumberWithoutKeyword; i++) {
                textDocumentDTOS.add(documentDataInitializer.createTextDocument());
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO).getId();
                docIds.add(id);
            });

            waitForIndexUpdates();

            List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = textDocumentDTOS.stream()
                    .limit(documentNumberWithSpecifiedKeyword)
                    .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                    .collect(Collectors.toList());

            Collections.reverse(documentStoredFieldsDTOS);

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setTerm(termText);

            final List<DocumentStoredFieldsDTO> actual = searchDocumentService.searchDocuments(searchQueryDTO);

            assertEquals(documentNumberWithSpecifiedKeyword, actual.size());
            assertEquals(documentStoredFieldsDTOS, actual);

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    private void testKeywordOrAliasOrHeadlineForOneDocument(String field) throws Exception {
        final int documentNumber = 5;
        final int docIdCheckingIndex = new Random().nextInt(documentNumber);

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < documentNumber; i++) {
                final TextDocumentDTO textDocument = documentDataInitializer.createTextDocument();
                final String termText = textDocument.getId() + field;

                setTextDocumentField(field, textDocument, termText);
                textDocumentDTOS.add(textDocument);
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO).getId();
                docIds.add(id);
            });

            waitForIndexUpdates();

            List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = textDocumentDTOS.stream()
                    .skip(docIdCheckingIndex)
                    .limit(1)
                    .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                    .collect(Collectors.toList());

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setTerm(String.valueOf(
                    textDocumentDTOS.get(docIdCheckingIndex).getId()) + field);

            final List<DocumentStoredFieldsDTO> actual = searchDocumentService.searchDocuments(searchQueryDTO);

            assertEquals(1, actual.size());
            assertEquals(documentStoredFieldsDTOS, actual);
        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    private void setTextDocumentField(String field, TextDocumentDTO textDocument, String termText) {
        switch (field) {
            case "headline":
                textDocument.getCommonContents().get(0).setHeadline(termText);
                break;
            case "alias":
                textDocument.setAlias(termText);
                break;
            case "keyword":
                textDocument.getKeywords().add(termText);
                break;
        }
    }

    @Test
    public void searchDocuments_When_DocId1001Requested_Expect_Found() throws InterruptedException {
        documentIndex.removeDocument(DOC_ID);
        documentIndex.indexDocument(DOC_ID);
        waitForIndexUpdates();

        final PageRequestDTO pageRequest = new PageRequestDTO(
                0, 10, new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID))
        );
        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

        searchQueryDTO.setTerm(String.valueOf(DOC_ID));
        searchQueryDTO.setPage(pageRequest);

        assertEquals(1, searchDocumentService.searchDocuments(searchQueryDTO).size());
    }

    @Test
    public void search_When_SomeAmountOfDocumentsExist_Expect_AllFound() throws Exception {
        final int caseNumber = 15;
        final List<Integer> docIds = new ArrayList<>();

        try {
            for (int i = 0; i < caseNumber; i++) {
                final TextDocumentDTO documentDTO = documentDataInitializer.createTextDocument();
                docIds.add(documentService.save(documentDTO).getId());
            }

            waitForIndexUpdates();
            waitForIndexUpdates();

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            final int documentCount = searchDocumentService.searchDocuments(searchQueryDTO).size();

            assertEquals(caseNumber + 1, documentCount);

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
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
        final int documentNumber = 12;

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        documentIndex.removeDocument(1001); // this doc is already indexed in some cases

        for (int i = 0; i < documentNumber; i++) {
            textDocumentDTOS.add(documentDataInitializer.createTextDocument());
        }

        textDocumentDTOS.forEach(textDocumentDTO -> {
            final int id = documentService.save(textDocumentDTO).getId();
            docIds.add(id);
        });

        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        waitForIndexUpdates();

        final String firstId = String.valueOf(docIds.get(0));
        final String lastDigitOfFirstID = firstId.substring(firstId.length() - lastDigitsNumber);

        final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = textDocumentDTOS.stream()
                .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                .filter(doc -> String.valueOf(doc.getId()).contains(lastDigitOfFirstID))
                .collect(Collectors.toList());

        Collections.reverse(documentStoredFieldsDTOS);

        try {

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setTerm(lastDigitOfFirstID);

            assertEquals(documentStoredFieldsDTOS, searchDocumentService.searchDocuments(searchQueryDTO));
        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    @Test
    public void searchTextDocuments_When_UserIdSet_Expect_Found() throws Exception {
        final int documentNumberFirstUser = 6;
        final int documentNumberSecondUser = 4;

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
                final int id = documentService.save(textDocumentDTO).getId();
                docIds.add(id);
            });

            waitForIndexUpdates();

            // create query for 1st user
            SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setUserId(users.get(0).getId());

            // check document number for 1st user
            assertEquals(documentNumberFirstUser, searchDocumentService.searchDocuments(searchQueryDTO).size());

            searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setUserId(users.get(1).getId());

            // check document number for 2st user
            assertEquals(documentNumberSecondUser, searchDocumentService.searchDocuments(searchQueryDTO).size());

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });

            users.forEach(user -> {
                final List<UserRoles> userRolesByUserId = userRolesRepository.getUserRolesByUserId(user.getId());
                userRolesRepository.delete(userRolesByUserId);

                userRepository.delete(user);
            });

        }
    }

    @Test
    public void checkTextDocumentWithMaxId_When_DefaultSearchQuerySet_Expect_isFirstFound() throws Exception {

        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < 15; i++) {
                textDocumentDTOS.add(documentDataInitializer.createTextDocument());
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO).getId();
                docIds.add(id);
            });

            final OptionalInt oMax = docIds.stream()
                    .mapToInt(id -> id)
                    .max();

            assertTrue(oMax.isPresent());

            final int maxId = oMax.getAsInt();

            waitForIndexUpdates();

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            assertThat(searchDocumentService.searchDocuments(searchQueryDTO).get(0).getId(), is(maxId));

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    @Test
    public void getTextDocuments_When_SecondPageIsSet_Expect_DocumentStoredFieldsDtoJson() throws Exception {

        documentIndex.removeDocument(1001); // this doc is already indexed in some cases
        final List<Integer> docIds = new ArrayList<>();
        final List<TextDocumentDTO> textDocumentDTOS = new ArrayList<>();

        try {
            for (int i = 0; i < 15; i++) {
                textDocumentDTOS.add(documentDataInitializer.createTextDocument());
            }

            textDocumentDTOS.forEach(textDocumentDTO -> {
                final int id = documentService.save(textDocumentDTO).getId();
                docIds.add(id);
            });

            waitForIndexUpdates();

            final int from = 0;
            final int to = 5;

            final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = IntStream.range(from, to)
                    .map(i -> to - i + from - 1)
                    .mapToObj(textDocumentDTOS::get)
                    .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                    .collect(Collectors.toList());

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            searchQueryDTO.setPage(new PageRequestDTO(1, 10));


            assertEquals(documentStoredFieldsDTOS, searchDocumentService.searchDocuments(searchQueryDTO));

        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    @Test
    public void search_When_CategorySpecified_Expect_Found() throws InterruptedException {
        final String testTypeName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(
                null, testTypeName, 0, false, false
        );
        final CategoryTypeJPA savedType = new CategoryTypeJPA(categoryTypeService.save(categoryType));

        final String testCategoryName = "test_category_name" + System.currentTimeMillis();
        final Category category = new CategoryJPA(testCategoryName, "dummy", "", savedType);
        final Category saved = categoryService.save(category);
        final Integer savedId = saved.getId();
        final TextDocumentDTO documentDTO = documentDataInitializer.createTextDocument();

        try {
            documentDTO.getCategories().add(new CategoryDTO(saved));

            documentService.save(documentDTO);

            final Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            final PageRequestDTO pageRequest = new PageRequestDTO(0, 10, sort);
            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            waitForIndexUpdates();

            searchQueryDTO.setPage(pageRequest);
            searchQueryDTO.setCategoriesId(Collections.singletonList(savedId));

            assertEquals(1, searchDocumentService.searchDocuments(searchQueryDTO).size());

        } finally {
            categoryService.delete(savedId);
            categoryTypeService.delete(savedType.getId());
            documentDataInitializer.cleanRepositories(documentDTO.getId());
            documentIndex.removeDocument(documentDTO.getId());

            assertFalse(categoryService.getById(savedId).isPresent());
            assertFalse(categoryTypeService.get(savedType.getId()).isPresent());
        }
    }

    @Test
    public void search_When_SpecifiedCategorySet_Expect_OneDocument() throws InterruptedException {
        // create category type
        final String categoryName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(null, categoryName, 0,
                false, false);

        final CategoryTypeJPA savedCategoryType = new CategoryTypeJPA(categoryTypeService.save(categoryType));

        // create 2 categories
        final String firstTestCategoryName = "first_category_name" + System.currentTimeMillis();
        final Category firstCategory = new CategoryJPA(
                firstTestCategoryName, "dummy", "", savedCategoryType
        );
        final Category savedFirstCategory = categoryService.save(firstCategory);

        final String secondTestCategoryName = "second_category_name" + System.currentTimeMillis();
        final Category secondCategory = new CategoryJPA(
                secondTestCategoryName, "dummy", "", savedCategoryType
        );
        final Category savedSecondCategory = categoryService.save(secondCategory);

        // create 3 documents
        final TextDocumentDTO firstDocument = documentDataInitializer.createTextDocument();
        final TextDocumentDTO secondDocument = documentDataInitializer.createTextDocument();
        final TextDocumentDTO thirdDocument = documentDataInitializer.createTextDocument();

        try {
            // add categories to documents (firstCategory add only to secondDocument)
            firstDocument.getCategories().add(new CategoryDTO(savedSecondCategory));
            secondDocument.getCategories().add(new CategoryDTO(savedFirstCategory));
            thirdDocument.getCategories().add(new CategoryDTO(savedSecondCategory));

            // save documents
            documentService.save(firstDocument);
            documentService.save(secondDocument);
            documentService.save(thirdDocument);

            waitForIndexUpdates();

            final Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            final PageRequestDTO pageRequest = new PageRequestDTO(0, 10, sort);
            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setPage(pageRequest);
            searchQueryDTO.setCategoriesId(Collections.singletonList(savedFirstCategory.getId()));

            final List<DocumentStoredFieldsDTO> documents = searchDocumentService.searchDocuments(searchQueryDTO);
            assertEquals(1, documents.size());

            final DocumentStoredFieldsDTO dto = documents.get(0);
            assertEquals(secondDocument.getId(), dto.getId());

        } finally {
            categoryService.delete(savedFirstCategory.getId());
            categoryService.delete(savedSecondCategory.getId());

            categoryTypeService.delete(savedCategoryType.getId());

            documentDataInitializer.cleanRepositories(firstDocument.getId());
            documentDataInitializer.cleanRepositories(secondDocument.getId());
            documentDataInitializer.cleanRepositories(thirdDocument.getId());

            documentIndex.removeDocument(firstDocument.getId());
            documentIndex.removeDocument(secondDocument.getId());
            documentIndex.removeDocument(thirdDocument.getId());
        }
    }

    @Test
    public void search_When_SpecifiedCategorySet_Expect_MultipleDocuments() throws InterruptedException {
        // create category type
        final String categoryName = "test_type_name" + System.currentTimeMillis();
        final CategoryType categoryType = new CategoryTypeJPA(null, categoryName, 0,
                false, false);

        final CategoryTypeJPA savedCategoryType = new CategoryTypeJPA(categoryTypeService.save(categoryType));

        // create category
        final String firstTestCategoryName = "category_name" + System.currentTimeMillis();
        final Category firstCategory = new CategoryJPA(
                firstTestCategoryName, "dummy", "", savedCategoryType
        );
        final Category savedCategory = categoryService.save(firstCategory);

        // create 3 documents
        final TextDocumentDTO firstDocument = documentDataInitializer.createTextDocument();
        final TextDocumentDTO secondDocument = documentDataInitializer.createTextDocument();
        final TextDocumentDTO thirdDocument = documentDataInitializer.createTextDocument();

        try {
            // add categories to documents
            firstDocument.getCategories().add(new CategoryDTO(savedCategory));
            secondDocument.getCategories().add(new CategoryDTO(savedCategory));
            thirdDocument.getCategories().add(new CategoryDTO(savedCategory));

            // save documents
            documentService.save(firstDocument);
            documentService.save(secondDocument);
            documentService.save(thirdDocument);

            waitForIndexUpdates();

            final Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            final PageRequestDTO pageRequest = new PageRequestDTO(0, 10, sort);
            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            searchQueryDTO.setPage(pageRequest);
            searchQueryDTO.setCategoriesId(Collections.singletonList(savedCategory.getId()));

            final List<DocumentStoredFieldsDTO> documents = searchDocumentService.searchDocuments(searchQueryDTO);
            assertEquals(3, documents.size());

            final List<Integer> ids = documents
                    .stream()
                    .map(DocumentStoredFieldsDTO::getId)
                    .collect(Collectors.toList());

            assertTrue(ids.contains(firstDocument.getId()));
            assertTrue(ids.contains(secondDocument.getId()));
            assertTrue(ids.contains(thirdDocument.getId()));

        } finally {
            categoryService.delete(savedCategory.getId());
            categoryTypeService.delete(savedCategoryType.getId());

            documentDataInitializer.cleanRepositories(firstDocument.getId());
            documentDataInitializer.cleanRepositories(secondDocument.getId());
            documentDataInitializer.cleanRepositories(thirdDocument.getId());

            documentIndex.removeDocument(firstDocument.getId());
            documentIndex.removeDocument(secondDocument.getId());
            documentIndex.removeDocument(thirdDocument.getId());
        }
    }

    @Test
    public void search_When_SpecifiedKeywordSet_Expect_OneDocument() throws InterruptedException {
        final String firstKeyword = "firstKeyword";
        final String secondKeyword = "secondKeyword";

        // create 3 documents
        final TextDocumentDTO firstDocument = documentDataInitializer.createTextDocument();
        final TextDocumentDTO secondDocument = documentDataInitializer.createTextDocument();
        final TextDocumentDTO thirdDocument = documentDataInitializer.createTextDocument();

        try {
            firstDocument.getKeywords().add(firstKeyword);
            secondDocument.getKeywords().add(firstKeyword);
            thirdDocument.getKeywords().add(secondKeyword);

            // save documents
            documentService.save(firstDocument);
            documentService.save(secondDocument);
            documentService.save(thirdDocument);

            waitForIndexUpdates();

            final Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            final PageRequestDTO pageRequest = new PageRequestDTO(0, 10, sort);
            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            searchQueryDTO.setTerm(secondKeyword);
            searchQueryDTO.setPage(pageRequest);

            final List<DocumentStoredFieldsDTO> documents = searchDocumentService.searchDocuments(searchQueryDTO);
            assertEquals(1, documents.size());

            final DocumentStoredFieldsDTO dto = documents.get(0);
            assertEquals(thirdDocument.getId(), dto.getId());

        } finally {
            documentDataInitializer.cleanRepositories(firstDocument.getId());
            documentDataInitializer.cleanRepositories(secondDocument.getId());
            documentDataInitializer.cleanRepositories(thirdDocument.getId());

            documentIndex.removeDocument(firstDocument.getId());
            documentIndex.removeDocument(secondDocument.getId());
            documentIndex.removeDocument(thirdDocument.getId());
        }
    }

    @Test
    public void search_When_SpecifiedKeywordSet_Expect_MultipleDocuments() throws InterruptedException {
        final String keyword = "keyword";

        // create 2 documents
        final TextDocumentDTO firstDocument = documentDataInitializer.createTextDocument();
        final TextDocumentDTO secondDocument = documentDataInitializer.createTextDocument();

        try {
            firstDocument.getKeywords().add(keyword);
            secondDocument.getKeywords().add(keyword);

            // save documents
            documentService.save(firstDocument);
            documentService.save(secondDocument);

            waitForIndexUpdates();

            final Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            final PageRequestDTO pageRequest = new PageRequestDTO(0, 10, sort);
            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            searchQueryDTO.setTerm(keyword);
            searchQueryDTO.setPage(pageRequest);

            final List<DocumentStoredFieldsDTO> documents = searchDocumentService.searchDocuments(searchQueryDTO);
            assertEquals(2, documents.size());

            final List<Integer> ids = documents.stream()
                    .map(DocumentStoredFieldsDTO::getId)
                    .collect(Collectors.toList());

            assertTrue(ids.contains(firstDocument.getId()));
            assertTrue(ids.contains(secondDocument.getId()));

        } finally {
            documentDataInitializer.cleanRepositories(firstDocument.getId());
            documentDataInitializer.cleanRepositories(secondDocument.getId());

            documentIndex.removeDocument(firstDocument.getId());
            documentIndex.removeDocument(secondDocument.getId());
        }
    }

    @Test
    public void search_When_SpecifiedPageRequest_Expect_Found() throws InterruptedException {
        // create documents
        final List<TextDocumentDTO> docs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            docs.add(documentDataInitializer.createTextDocument());
        }

        try {
            // save documents
            docs.forEach(documentService::save);

            waitForIndexUpdates();

            // create page request
            final Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            final PageRequestDTO pageRequest = new PageRequestDTO(0, 6, sort);
            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setPage(pageRequest);

            final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = searchDocumentService.searchDocuments(
                    searchQueryDTO
            );

            assertEquals(6, documentStoredFieldsDTOS.size());

        } finally {
            docs.forEach(document -> {
                documentDataInitializer.cleanRepositories(document.getId());
                documentIndex.removeDocument(document.getId());
            });
        }
    }

    @Test
    public void search_When_UseDefaultPageRequest_Expect_Found() throws InterruptedException {
        // create documents
        final List<TextDocumentDTO> docs = new ArrayList<>();
        final int numberOfDocs = 6;
        for (int i = 0; i < numberOfDocs; i++) {
            docs.add(documentDataInitializer.createTextDocument());
        }

        try {
            // save documents
            docs.forEach(documentService::save);

            waitForIndexUpdates();
            waitForIndexUpdates();

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOS = searchDocumentService.searchDocuments(
                    searchQueryDTO
            );

            assertEquals(numberOfDocs + 1, documentStoredFieldsDTOS.size());

        } finally {
            docs.forEach(document -> {
                documentDataInitializer.cleanRepositories(document.getId());
                documentIndex.removeDocument(document.getId());
            });
        }
    }

    @Test
    public void getDocuments_When_SortingByTitleASC_Expect_CorrectData() throws InterruptedException {
        checkSorting(Comparator.comparing(DocumentStoredFieldsDTO::getTitle), titleField, Sort.Direction.ASC);
    }

    @Test
    public void getDocuments_When_SortingByTitleDESC_Expect_CorrectData() throws InterruptedException {
        checkSorting(Comparator.comparing(DocumentStoredFieldsDTO::getTitle).reversed(), titleField, Sort.Direction.DESC);
    }

    @Test
    public void getDocuments_When_SortingByAliasASC_Expect_CorrectData() throws InterruptedException {
        checkSorting(Comparator.comparing(DocumentStoredFieldsDTO::getAlias), aliasField, Sort.Direction.ASC);
    }

    @Test
    public void getDocuments_When_SortingByAliasDESC_Expect_CorrectData() throws InterruptedException {
        checkSorting(Comparator.comparing(DocumentStoredFieldsDTO::getAlias).reversed(), aliasField, Sort.Direction.DESC);
    }

    private void checkSorting(Comparator<DocumentStoredFieldsDTO> comparator, String property, Sort.Direction direction) throws InterruptedException {
        final int documentListSize = mockData.size();

        List<TextDocumentDTO> textDocumentDTOS = IntStream.range(0, documentListSize)
                .mapToObj(i -> documentDataInitializer.createTextDocument())
                .collect(Collectors.toList());

        IntStream.range(0, documentListSize)
                .forEach(i -> {
                    textDocumentDTOS.get(i).getCommonContents().get(0).setHeadline(mockData.get(i));
                    textDocumentDTOS.get(i).setAlias(mockData.get(i));
                });

        List<DocumentStoredFieldsDTO> expected = textDocumentDTOS.stream()
                .map(textDocumentDTOtoDocumentStoredFieldsDTO)
                .sorted(comparator)
                .collect(Collectors.toList());

        final List<Integer> docIds = new ArrayList<>();

        textDocumentDTOS.forEach(textDocumentDTO -> docIds.add(documentService.save(textDocumentDTO).getId()));

        try {
            waitForIndexUpdates();

            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            final PageRequestDTO pageRequestDTO = new PageRequestDTO();
            pageRequestDTO.setProperty(property);
            pageRequestDTO.setDirection(direction);

            searchQueryDTO.setPage(pageRequestDTO);

            final List<DocumentStoredFieldsDTO> actual = searchDocumentService.searchDocuments(searchQueryDTO)
                    .stream()
                    .filter(doc -> docIds.contains(doc.getId()))
                    .collect(Collectors.toList());

            assertEquals(expected, actual);
        } finally {
            docIds.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    private void waitForIndexUpdates() throws InterruptedException {
        while (!documentIndex.isUpdateDone()) {
            Thread.sleep(10);
        }
    }
}
