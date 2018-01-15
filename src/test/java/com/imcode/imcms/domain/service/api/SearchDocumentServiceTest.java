package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by dmizem from Ubrainians for imCode on 20.10.17.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class SearchDocumentServiceTest {

    private static final int DOC_ID = 1001;

    private static boolean flag = true;

    private static Imcms imcmsStatic;

    private static VersionDataInitializer versionDataInitializerStatic;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private SearchDocumentService searchDocumentService;

    @Autowired
    private Imcms imcms;

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

    @AfterClass
    public static void shutDownSolr() {
        imcmsStatic.stop();
        Imcms.removeUser();
        versionDataInitializerStatic.cleanRepositories();
    }

    @PostConstruct
    public void initSolr() throws Exception {

        imcmsStatic = imcms;
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

        Thread.sleep(TimeUnit.SECONDS.toMillis(2)); // to let solr init, not sure 2 sec is exact time
    }

    @Test
    public void searchDocuments_When_DocId1001Requested_Expect_Found() {
        PageRequest pageRequest = new PageRequest(0, 10, new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID)));

        SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

        searchQueryDTO.setTerm(String.valueOf(DOC_ID));
        searchQueryDTO.setPage(pageRequest);

        assertEquals(1, searchDocumentService.searchDocuments(searchQueryDTO).size());
    }

    @Test
    public void search_When_CategorySpecified_Expect_Found() {
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
            final PageRequest pageRequest = new PageRequest(0, 10, sort);
            final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(4));
            } catch (InterruptedException e) {
                // don't really care
            }

            searchQueryDTO.setPage(pageRequest);
            searchQueryDTO.setCategoriesId(Collections.singletonList(savedId));

            assertEquals(1, searchDocumentService.searchDocuments(searchQueryDTO).size());

        } finally {
            categoryService.delete(savedId);
            categoryTypeService.delete(savedType.getId());
            documentDataInitializer.cleanRepositories(documentDTO.getId());

            assertFalse(categoryService.getById(savedId).isPresent());
            assertFalse(categoryTypeService.get(savedType.getId()).isPresent());
        }
    }

    @Test
    public void search_When_OneHundredDocumentsExist_Expect_Found() {
        int caseNumber = 100;
        List<Integer> ids = new ArrayList<>();

        try {
            for (int i = 0; i < caseNumber; i++) {
                TextDocumentDTO documentDTO = documentDataInitializer.createTextDocument();
                ids.add(documentService.save(documentDTO));
            }

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(8));
            } catch (InterruptedException e) {
                // ignore
            }

            SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            PageRequest pageRequest = new PageRequest(0, 10, new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID)));
            searchQueryDTO.setPage(pageRequest);

            int documentCount = searchDocumentService.searchDocuments(searchQueryDTO).size();

            assertEquals(101, documentCount);
        } finally {
            ids.forEach(id -> {
                documentDataInitializer.cleanRepositories(id);
                documentIndex.removeDocument(id);
            });
        }
    }

    @Test
    public void search_When_SpecifiedCategorySet_Expect_OneDocument() {
        // create category type
        String categoryName = "test_type_name" + System.currentTimeMillis();
        CategoryType categoryType = new CategoryTypeJPA(null, categoryName, 0,
                false, false);

        CategoryTypeJPA savedCategoryType = new CategoryTypeJPA(categoryTypeService.save(categoryType));

        // create 2 categories
        String firstTestCategoryName = "first_category_name" + System.currentTimeMillis();
        Category firstCategory = new CategoryJPA(firstTestCategoryName, "dummy", "", savedCategoryType);
        Category savedFirstCategory = categoryService.save(firstCategory);

        String secondTestCategoryName = "second_category_name" + System.currentTimeMillis();
        Category secondCategory = new CategoryJPA(secondTestCategoryName, "dummy", "", savedCategoryType);
        Category savedSecondCategory = categoryService.save(secondCategory);

        // create 3 documents
        TextDocumentDTO firstDocument = documentDataInitializer.createTextDocument();
        TextDocumentDTO secondDocument = documentDataInitializer.createTextDocument();
        TextDocumentDTO thirdDocument = documentDataInitializer.createTextDocument();

        try {
            // add categories to documents (firstCategory add only to secondDocument)
            firstDocument.getCategories().add(new CategoryDTO(savedSecondCategory));
            secondDocument.getCategories().add(new CategoryDTO(savedFirstCategory));
            thirdDocument.getCategories().add(new CategoryDTO(savedSecondCategory));

            // save documents
            documentService.save(firstDocument);
            documentService.save(secondDocument);
            documentService.save(thirdDocument);

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(4));
            } catch (InterruptedException e) {
                // don't really care
            }

            Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            PageRequest pageRequest = new PageRequest(0, 10, sort);
            SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setPage(pageRequest);
            searchQueryDTO.setCategoriesId(Collections.singletonList(savedFirstCategory.getId()));

            List<DocumentStoredFieldsDTO> documents = searchDocumentService.searchDocuments(searchQueryDTO);
            assertEquals(1, documents.size());

            DocumentStoredFieldsDTO dto = documents.get(0);
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
    public void search_When_SpecifiedCategorySet_Expect_MultipleDocuments() {
        // create category type
        String categoryName = "test_type_name" + System.currentTimeMillis();
        CategoryType categoryType = new CategoryTypeJPA(null, categoryName, 0,
                false, false);

        CategoryTypeJPA savedCategoryType = new CategoryTypeJPA(categoryTypeService.save(categoryType));

        // create category
        String firstTestCategoryName = "category_name" + System.currentTimeMillis();
        Category firstCategory = new CategoryJPA(firstTestCategoryName, "dummy", "", savedCategoryType);
        Category savedCategory = categoryService.save(firstCategory);

        // create 3 documents
        TextDocumentDTO firstDocument = documentDataInitializer.createTextDocument();
        TextDocumentDTO secondDocument = documentDataInitializer.createTextDocument();
        TextDocumentDTO thirdDocument = documentDataInitializer.createTextDocument();

        try {
            // add categories to documents
            firstDocument.getCategories().add(new CategoryDTO(savedCategory));
            secondDocument.getCategories().add(new CategoryDTO(savedCategory));
            thirdDocument.getCategories().add(new CategoryDTO(savedCategory));

            // save documents
            documentService.save(firstDocument);
            documentService.save(secondDocument);
            documentService.save(thirdDocument);

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(4));
            } catch (InterruptedException e) {
                // don't really care
            }

            Sort sort = new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID));
            PageRequest pageRequest = new PageRequest(0, 10, sort);
            SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
            searchQueryDTO.setPage(pageRequest);
            searchQueryDTO.setCategoriesId(Collections.singletonList(savedCategory.getId()));

            List<DocumentStoredFieldsDTO> documents = searchDocumentService.searchDocuments(searchQueryDTO);
            assertEquals(3, documents.size());

            List<Integer> ids = documents
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
}
