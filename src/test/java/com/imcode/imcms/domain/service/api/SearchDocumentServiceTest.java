package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.CategoryDTO;
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
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
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
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by dmizem from Ubrainians for imCode on 20.10.17.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class SearchDocumentServiceTest {

    private static final int DOC_ID = 1001;

    private static Imcms imcmsStatic;

    private static VersionDataInitializer versionDataInitializerStatic;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @Autowired
    private MetaRepository metaRepository;

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
    private DocumentService<? super TextDocumentDTO> documentService;

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

        if (!testSolrFolder.mkdirs()) {
            assertTrue(FileUtility.forceDelete(testSolrFolder));
        }

        FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);

        versionDataInitializer.cleanRepositories();

        versionDataInitializer.createData(0, DOC_ID);
        versionDataInitializer.createData(1, DOC_ID);
        versionDataInitializer.createData(2, DOC_ID);

        Imcms.invokeStart();
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
    @Ignore // categories not working
//    @Transactional
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
            final UserDomainObject user = new UserDomainObject(1);
            user.addRoleId(RoleId.SUPERADMIN);
            user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
            Imcms.setUser(user); // means current user is admin now

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
}
