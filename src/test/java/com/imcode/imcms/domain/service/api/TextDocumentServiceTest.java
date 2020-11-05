package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.ImageDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.MenuDataInitializer;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.components.datainitializer.TextDataInitializer;
import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.TextDocumentTemplate;
import com.imcode.imcms.persistence.entity.CategoryJPA;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TextDocumentServiceTest extends WebAppSpringTestConfig {

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
    private TextService textService;

    @Autowired
    private ImageService imageService;

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
    private ImageDataInitializer imageDataInitializer;

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Autowired
    private MenuDataInitializer menuDataInitializer;

    @Autowired
    private TextDataInitializer textDataInitializer;

    @Autowired
    private UserService userService;

    @Autowired
    private VersionService versionService;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @AfterAll
    public static void shutDownSolr() {
        try {
            FileUtility.forceDelete(testSolrFolder);
        } catch (Exception e) {
            // windows user may receive it
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        commonContentDataInitializer.cleanRepositories();
        templateDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();
        menuDataInitializer.cleanRepositories();
        textDataInitializer.cleanRepositories();
        loopDataInitializer.cleanRepositories();
        imageDataInitializer.cleanRepositories();
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
        assertEquals(childDoc.getLatestVersion().getId(), createdDoc.getCurrentVersion().getId());

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

            assertNull(childCommonContent.getId());
            assertNull(childCommonContent.getDocId());
            assertEquals(childCommonContent.getVersionNo(), Integer.valueOf(Version.WORKING_VERSION_INDEX));
        }
    }

    @Test
    public void createFromParent_When_ParentExist_Expect_CreatedAndSavedSpecifiedHeadlineTexts() {
        TextDocumentDTO childDoc = documentService.createFromParent(createdDoc.getId());

        assertNotNull(childDoc);

        final List<CommonContent> childCommonContents = childDoc.getCommonContents();
        for (int i = 0; i < childCommonContents.size(); i++) {
            final CommonContent childCommonContent = childCommonContents.get(i);
            childCommonContent.setHeadline("test" + i);
        }
        childDoc = documentService.save(childDoc);

        for (int i = 0; i < childCommonContents.size(); i++) {
            final CommonContent childCommonContent = childCommonContents.get(i);
            assertEquals("test" + i, textService.getText(childDoc.getId(), 1, childCommonContent.getLanguage().getCode(), null).getText());
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

            final TemplateDTO template = new TemplateDTO(null, templateName, false, null);
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
    public void copyDocument_Expect_CopiedTextDocumentWithAllContents() {

        final Integer createdDocId = documentDataInitializer.createData().getId();

        assertNotNull(documentService.get(createdDocId));


        final List<CategoryJPA> categories = categoryDataInitializer.createData(3);
        final TextDocumentTemplateJPA textTemplate =
                templateDataInitializer.createData(createdDocId, "test", "test");

        final TextDocumentDTO documentDTO = documentService.get(createdDocId);
        documentDTO.setCategories(new HashSet<>(categories));
        documentDTO.setTemplate(new TextDocumentTemplateDTO(textTemplate));
        documentDTO.setKeywords(new HashSet<>(Arrays.asList("1", "2", "3")));

        final TextDocumentDTO originalTextDocument = documentService.save(documentDTO);
        final Integer originalDocId = originalTextDocument.getId();

        final Version latestVersion = versionService.getLatestVersion(originalDocId);

        final String testImageName = "test.jpg";

        final LanguageJPA languageJPA = new LanguageJPA(languageDataInitializer.createData().get(0));

        imageDataInitializer.createData(1, "test", testImageName, latestVersion);

        textDataInitializer.createText(1, languageJPA, latestVersion, "testText");

        final List<LoopEntryDTO> oneEntry = Collections.singletonList(LoopEntryDTO.createEnabled(1));

        loopDataInitializer.createData(new LoopDTO(originalDocId, 1, oneEntry), latestVersion);

        documentService.publishDocument(originalTextDocument.getId(), 1);

        final TextDocumentDTO copiedTextDocument = documentService.copy(createdDocId);

        assertThat(metaRepository.findAll(), hasSize(4));

        final List<TextJPA> docOriginalTexts = textService.getByDocId(originalTextDocument.getId());
        final List<TextJPA> docCopiedTexts = textService.getByDocId(copiedTextDocument.getId());

        final List<ImageJPA> docOriginalImages = imageService.getByDocId(originalTextDocument.getId());
        final List<ImageJPA> docCopiedImages = imageService.getByDocId(copiedTextDocument.getId());

        assertEquals(docOriginalTexts.size(), docCopiedTexts.size());

        assertNotEquals(docOriginalTexts.stream()
                        .map(TextJPA::getId)
                        .collect(Collectors.toList()),

                docCopiedTexts.stream()
                        .map(TextJPA::getId)
                        .collect(Collectors.toList())
        );


        assertNotEquals(docOriginalTexts.stream()
                        .map(TextJPA::getVersion)
                        .collect(Collectors.toList()),

                docCopiedTexts.stream()
                        .map(TextJPA::getVersion)
                        .collect(Collectors.toList())
        );

        assertEquals(docOriginalImages.size(), docCopiedImages.size());

        assertThat(docOriginalImages.stream()
                        .map(ImageJPA::getVersion)
                        .map(Version::getDocId)
                        .collect(Collectors.toList()),

                is(not(docCopiedImages.stream()
                        .map(ImageJPA::getVersion)
                        .map(Version::getDocId)
                        .collect(Collectors.toList()))));

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