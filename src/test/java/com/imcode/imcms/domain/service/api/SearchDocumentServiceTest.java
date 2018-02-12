package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private DocumentService<TextDocumentDTO> documentService;

    @Autowired
    private DocumentIndex documentIndex;

    @Autowired
    private Function<TextDocumentDTO, DocumentStoredFieldsDTO> textDocumentDTOtoDocumentStoredFieldsDTO;

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

//        Thread.sleep(TimeUnit.SECONDS.toMillis(2)); // to let solr init, not sure 2 sec is exact time
    }

    @Test
    public void searchDocuments_When_DocId1001Requested_Expect_Found() {
        final PageRequestDTO pageRequest = new PageRequestDTO(
                0, 10, new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID))
        );
        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

        searchQueryDTO.setTerm(String.valueOf(DOC_ID));
        searchQueryDTO.setPage(pageRequest);

        assertEquals(1, searchDocumentService.searchDocuments(searchQueryDTO).size());
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
    public void search_When_SomeAmountOfDocumentsExist_Expect_AllFound() throws Exception {
        final int caseNumber = 15;
        final List<Integer> docIds = new ArrayList<>();

        try {
            for (int i = 0; i < caseNumber; i++) {
                final TextDocumentDTO documentDTO = documentDataInitializer.createTextDocument();
                docIds.add(documentService.save(documentDTO));
            }

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

        textDocumentDTOS.forEach(textDocumentDTO -> docIds.add(documentService.save(textDocumentDTO)));

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
