package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.TextDocumentTemplate;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class TextDocumentServiceTest {

    private static final int userId = 1;

    private static File testSolrFolder;

    private TextDocumentDTO createdDoc;

    @Autowired
    private Config config;

    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private TextDocumentDataInitializer documentDataInitializer;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private DocumentService<TextDocumentDTO> documentService;

    @Autowired
    private DocumentDtoFactory documentDtoFactory;

    @Autowired
    private UserService userService;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @AfterClass
    public static void shutDownSolr() {
        try {
            FileUtility.forceDelete(testSolrFolder);
        } catch (Exception e) {
            // windows user may receive it
        }
    }

    @Before
    public void setUp() throws Exception {
        commonContentDataInitializer.cleanRepositories();
        createdDoc = documentDataInitializer.createTextDocument();

        testSolrFolder = new File(config.getSolrHome());

        if (testSolrFolder.mkdirs()) {
            FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);
        }

        final UserDomainObject user = new UserDomainObject(userId);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user); // means current user is admin now
    }

    @Test
    public void createFromParent_When_ParentExist_Expect_Created() {
        final TextDocumentDTO childDoc = documentService.createFromParent(createdDoc.getId());

        assertNotNull(childDoc);

        // equal things
        assertEquals(childDoc.getType(), createdDoc.getType());
        assertEquals(childDoc.getKeywords(), createdDoc.getKeywords());
        assertEquals(childDoc.getCategories(), createdDoc.getCategories());
        assertEquals(childDoc.getRestrictedPermissions(), createdDoc.getRestrictedPermissions());
        assertEquals(childDoc.getRoleIdToPermission(), createdDoc.getRoleIdToPermission());
        assertEquals(childDoc.getProperties(), createdDoc.getProperties());
        assertEquals(childDoc.getTarget(), createdDoc.getTarget());
        assertEquals(childDoc.getDisabledLanguageShowMode(), createdDoc.getDisabledLanguageShowMode());
        assertEquals(childDoc.getDisabledLanguageShowMode(), createdDoc.getDisabledLanguageShowMode());
        assertEquals(childDoc.getCommonContents().size(), createdDoc.getCommonContents().size());

        // special things
        assertNull(childDoc.getId());
        assertEquals(childDoc.getAlias(), "");
        assertEquals(childDoc.getPublicationStatus(), Meta.PublicationStatus.NEW);
        assertEquals(childDoc.getPublicationEnd(), new AuditDTO());
        assertEquals(childDoc.getPublished(), new AuditDTO());
        assertEquals(childDoc.getArchived(), new AuditDTO());
        assertEquals(childDoc.getCreated(), new AuditDTO());
        assertEquals(childDoc.getModified(), new AuditDTO());
        assertEquals(childDoc.getCurrentVersion().getId(), Integer.valueOf(Version.WORKING_VERSION_INDEX));
        assertEquals(childDoc.getTemplate().getTemplateName(), createdDoc.getTemplate().getChildrenTemplateName());

        final List<CommonContent> childCommonContents = childDoc.getCommonContents();
        final List<CommonContent> commonContents = createdDoc.getCommonContents();

        for (int i = 0; i < childCommonContents.size(); i++) {
            final CommonContent childCommonContent = childCommonContents.get(i);
            final CommonContent commonContent = commonContents.get(i);

            assertEquals(childCommonContent.getLanguage(), commonContent.getLanguage());
            assertEquals(childCommonContent.getHeadline(), commonContent.getHeadline());
            assertEquals(childCommonContent.getMenuText(), commonContent.getMenuText());
            assertEquals(childCommonContent.getMenuImageURL(), commonContent.getMenuImageURL());

            assertEquals(childCommonContent.getId(), null);
            assertEquals(childCommonContent.getDocId(), null);
            assertEquals(childCommonContent.getVersionNo(), Integer.valueOf(Version.WORKING_VERSION_INDEX));
        }
    }


    @Test
    public void save_When_CustomTemplateSet_Expect_Saved() throws IOException {
        final String templateName = "test_" + System.currentTimeMillis();
        final int docId = createdDoc.getId();

        final File templateDirectory = templateService.getTemplateDirectory();
        final File templateFile = new File(templateDirectory, templateName + ".jsp");

        try {
            assertTrue(templateFile.createNewFile());

            final TemplateDTO template = new TemplateDTO(templateName, false);
            templateService.save(template);

            final TextDocumentTemplateDTO templateDTO = new TextDocumentTemplateDTO(docId, templateName, templateName);
            final TextDocumentTemplate savedTemplate = textDocumentTemplateService.save(templateDTO);
            assertNotNull(savedTemplate);

            final TextDocumentDTO documentDTO = documentService.get(docId);
            documentDTO.setTemplate(templateDTO);

            documentService.save(documentDTO);

            final TextDocumentDTO savedDoc = documentService.get(documentDTO.getId());
            final TextDocumentTemplate savedDocTemplate = savedDoc.getTemplate();

            assertEquals(savedDocTemplate, savedTemplate);

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void copyTextDocument_Expect_Copied() {
        commonContentDataInitializer.createData(1001, 0);

        final List<CategoryJPA> categories = categoryDataInitializer.createData(3);
        final TextDocumentTemplateJPA textTemplate =
                templateDataInitializer.createData(1001, "test", "test");

        final TextDocumentDTO documentDTO = documentService.get(1001);
        documentDTO.setCategories(new HashSet<>(categories));
        documentDTO.setTemplate(new TextDocumentTemplateDTO(textTemplate));
        documentDTO.setKeywords(new HashSet<>(Arrays.asList("1", "2", "3")));

        final TextDocumentDTO originalTextDocument = documentService.save(documentDTO);

        final TextDocumentDTO copiedTextDocument = documentService.copy(1001);

        assertThat(metaRepository.findAll(), hasSize(3));

        assertThat(copiedTextDocument.getId(), is(not(originalTextDocument.getId())));
        assertThat(copiedTextDocument.getTemplate().getDocId(), is(not(originalTextDocument.getTemplate().getDocId())));

        final List<CommonContent> originalCommonContents = originalTextDocument.getCommonContents();
        final List<CommonContent> copiedCommonContents = copiedTextDocument.getCommonContents();

        IntStream.range(0, originalCommonContents.size())
                .forEach(i -> {
                    final CommonContent originalCommonContent = originalCommonContents.get(i);
                    final CommonContent copiedCommonContent = copiedCommonContents.get(i);

                    assertThat(copiedCommonContent.getId(), is(not(originalCommonContent.getId())));
                    assertThat(copiedCommonContent.getDocId(), is(not(originalCommonContent.getDocId())));
                    assertThat(copiedCommonContent.getHeadline(), is(not(originalCommonContent.getHeadline())));
                    assertThat(copiedCommonContent.getVersionNo(), is(Version.WORKING_VERSION_INDEX));
                });

        assertThat(copiedTextDocument.getKeywords(), is(originalTextDocument.getKeywords()));
    }


    @Test
    public void save_When_NewEmptyDoc_Expect_NoError() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now

        final TextDocumentDTO emptyDocumentDTO = documentDtoFactory.createEmptyTextDocument();
        documentService.save(emptyDocumentDTO);
    }

    @Test
    public void save_When_NewEmptyDoc_Expect_Saved() {
        final int sizeBeforeSave = metaRepository.findAll().size();

        save_When_NewEmptyDoc_Expect_NoError();

        final int sizeAfterSave = metaRepository.findAll().size();

        assertEquals(sizeBeforeSave + 1, sizeAfterSave);
    }

    @Test
    public void save_When_NewEmptyDocWithTarget_Expect_Saved() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now

        final String testTarget = "_test";
        final TextDocumentDTO emptyDocumentDTO = documentDtoFactory.createEmptyTextDocument();
        emptyDocumentDTO.setTarget(testTarget);

        final TextDocumentDTO documentDTO = documentService.save(emptyDocumentDTO);

        assertEquals(documentDTO.getTarget(), testTarget);
    }

    @Test
    public void copyDocument_Expect_Copied() {
        commonContentDataInitializer.createData(1001, 0);

        final List<CategoryJPA> categories = categoryDataInitializer.createData(3);

        final TextDocumentDTO documentDTO = documentService.get(1001);
        documentDTO.setCategories(new HashSet<>(categories));
        documentDTO.setKeywords(new HashSet<>(Arrays.asList("1", "2", "3")));

        final DocumentDTO originalDocument = documentService.save(documentDTO);

        final DocumentDTO copiedDocument = documentService.copy(1001);

        assertThat(metaRepository.findAll(), hasSize(3));

        assertThat(copiedDocument.getId(), is(not(originalDocument.getId())));

        final List<CommonContent> originalCommonContents = originalDocument.getCommonContents();
        final List<CommonContent> copiedCommonContents = copiedDocument.getCommonContents();

        IntStream.range(0, originalCommonContents.size())
                .forEach(i -> {
                    final CommonContent originalCommonContent = originalCommonContents.get(i);
                    final CommonContent copiedCommonContent = copiedCommonContents.get(i);

                    assertThat(copiedCommonContent.getId(), is(not(originalCommonContent.getId())));
                    assertThat(copiedCommonContent.getDocId(), is(not(originalCommonContent.getDocId())));
                    assertThat(copiedCommonContent.getHeadline(), is(not(originalCommonContent.getHeadline())));
                    assertThat(copiedCommonContent.getVersionNo(), is(Version.WORKING_VERSION_INDEX));
                });

        checkExistingAuditDTO(copiedDocument.getCreated());
        checkExistingAuditDTO(copiedDocument.getModified());

        checkNotExistingAuditDTO(copiedDocument.getPublished());
        checkNotExistingAuditDTO(copiedDocument.getPublicationEnd());
        checkNotExistingAuditDTO(copiedDocument.getArchived());

        final Set<Category> originalCategories = originalDocument.getCategories();
        final Set<Category> copiedCategories = copiedDocument.getCategories();

        assertThat(copiedCategories.size(), is(originalCategories.size()));
        assertTrue(originalCategories.containsAll(copiedCategories));

        assertThat(copiedDocument.getKeywords(), is(originalDocument.getKeywords()));
    }

    private void checkExistingAuditDTO(AuditDTO auditDTO) {
        assertThat(auditDTO.getId(), is(userId));
        assertThat(auditDTO.getBy(), is(userService.getUser(userId).getLogin()));
        assertNotNull(auditDTO.getDate());
        assertNotNull(auditDTO.getTime());
    }

    private void checkNotExistingAuditDTO(AuditDTO auditDTO) {
        assertNull(auditDTO.getId());
        assertNull(auditDTO.getBy());
        assertNull(auditDTO.getDate());
        assertNull(auditDTO.getTime());
    }
}