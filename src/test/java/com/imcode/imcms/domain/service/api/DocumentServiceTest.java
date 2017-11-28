package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.core.TextDocumentTemplateService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.DO_NOT_SHOW;
import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class DocumentServiceTest {

    private DocumentDTO createdDoc;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TextDocumentTemplateService templateService;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Before
    public void setUp() throws Exception {
        createdDoc = documentDataInitializer.createData();
    }

    @Test
    public void get() throws Exception {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        assertEquals(documentDTO, createdDoc);
    }

    @Test
    public void getDocumentTitle() throws Exception {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final String testHeadline = "test_headline";

        for (CommonContentDTO commonContentDTO : documentDTO.getCommonContents()) {
            commonContentDTO.setHeadline(testHeadline);
        }

        documentService.save(documentDTO);

        assertEquals(documentService.getDocumentTitle(createdDoc.getId()), testHeadline);
    }

    @Test
    public void getDocumentTarget() throws Exception {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final String testTarget = "_target";
        documentDTO.setTarget(testTarget);
        documentService.save(documentDTO);

        assertEquals(documentService.getDocumentTarget(createdDoc.getId()), testTarget);
    }

    @Test
    public void getDocumentLink_When_NoAlias_Expect_DocIdInLink() throws Exception {
        final int docId = createdDoc.getId();

        assertEquals(documentService.getDocumentLink(docId), "/" + createdDoc.getId());
    }

    @Test
    public void getDocumentLink_When_AliasIsSet_Expect_AliasInLink() throws Exception {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final String testAlias = "test_alias";
        documentDTO.setAlias(testAlias);
        documentService.save(documentDTO);

        assertEquals(documentService.getDocumentLink(createdDoc.getId()), "/" + testAlias);
    }

    @Test
    public void save_With_Target_Expected_Saved() throws Exception {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setTarget("test_target");

        documentService.save(documentDTO);

        final DocumentDTO documentDTO1 = documentService.get(documentDTO.getId());

        assertEquals(documentDTO1, documentDTO);
    }

    @Test
    public void save_When_CustomCommonContentsSet_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        final List<CommonContentDTO> commonContents = documentDTO.getCommonContents();

        for (int i = 0; i < commonContents.size(); i++) {
            CommonContentDTO commonContentDTO = commonContents.get(i);
            commonContentDTO.setHeadline("Test headline " + i);
            commonContentDTO.setMenuText("Test menu text " + i);
            commonContentDTO.setMenuImageURL("Test menu image url " + i);
            commonContentDTO.setEnabled((i % 2) == 0);
        }

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(savedDocumentDTO.getCommonContents(), commonContents);
    }

    @Test
    public void save_When_TargetAndAliasChanged_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final String newTarget = "_blank";
        final String newAlias = "test-alias";

        documentDTO.setTarget(newTarget);
        documentDTO.setAlias(newAlias);
        documentService.save(documentDTO);

        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(savedDocumentDTO.getTarget(), newTarget);
        assertEquals(savedDocumentDTO.getAlias(), newAlias);
    }

    @Test
    public void save_When_DifferentPublicationStatusSet_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final Meta.PublicationStatus statusApproved = Meta.PublicationStatus.APPROVED;
        final Meta.PublicationStatus statusDisapproved = Meta.PublicationStatus.DISAPPROVED;
        final Meta.PublicationStatus statusNew = Meta.PublicationStatus.NEW;

        // approved
        documentDTO.setPublicationStatus(statusApproved);
        documentService.save(documentDTO);

        DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO.getPublicationStatus(), statusApproved);

        // disapproved
        documentDTO.setPublicationStatus(statusDisapproved);
        documentService.save(documentDTO);

        savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO.getPublicationStatus(), statusDisapproved);

        // new
        documentDTO.setPublicationStatus(statusNew);
        documentService.save(documentDTO);

        savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO.getPublicationStatus(), statusNew);
    }

    @Test
    public void save_When_CreatedAndModifiedAndArchivedAndPublishedAndDepublishedAttributesSet_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final User user = userDataInitializer.createData("testUser");

        final Supplier<AuditDTO> auditCreator = () -> {
            final AuditDTO auditDTO = new AuditDTO();
            auditDTO.setDateTime(new Date());
            auditDTO.setId(user.getId());
            auditDTO.setBy(user.getLogin());
            return auditDTO;
        };

        final AuditDTO createdAudit = auditCreator.get();
        final AuditDTO modifiedAudit = auditCreator.get();
        final AuditDTO archivedAudit = auditCreator.get();
        final AuditDTO publishedAudit = auditCreator.get();
        final AuditDTO depublishedAudit = auditCreator.get();

        documentDTO.setCreated(createdAudit);
        documentDTO.setModified(modifiedAudit);
        documentDTO.setArchived(archivedAudit);
        documentDTO.setPublished(publishedAudit);
        documentDTO.setPublicationEnd(depublishedAudit);

        documentService.save(documentDTO);
        DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(createdAudit, savedDocumentDTO.getCreated());
        assertEquals(modifiedAudit, savedDocumentDTO.getModified());
        assertEquals(archivedAudit, savedDocumentDTO.getArchived());
        assertEquals(publishedAudit, savedDocumentDTO.getPublished());
        assertEquals(depublishedAudit, savedDocumentDTO.getPublicationEnd());

        // only for nullable things
        final AuditDTO emptyArchivedAudit = new AuditDTO();
        final AuditDTO emptyPublishedAudit = new AuditDTO();
        final AuditDTO emptyDepublishedAudit = new AuditDTO();

        documentDTO.setArchived(emptyArchivedAudit);
        documentDTO.setPublished(emptyPublishedAudit);
        documentDTO.setPublicationEnd(emptyDepublishedAudit);

        documentService.save(documentDTO);
        savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(emptyArchivedAudit, savedDocumentDTO.getArchived());
        assertEquals(emptyPublishedAudit, savedDocumentDTO.getPublished());
        assertEquals(emptyDepublishedAudit, savedDocumentDTO.getPublicationEnd());
    }

    @Test
    public void save_When_CustomMissingLanguagePropertySet_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);

        documentService.save(documentDTO);
        DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(savedDocumentDTO.getDisabledLanguageShowMode(), SHOW_IN_DEFAULT_LANGUAGE);

        documentDTO.setDisabledLanguageShowMode(DO_NOT_SHOW);

        documentService.save(documentDTO);
        savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(savedDocumentDTO.getDisabledLanguageShowMode(), DO_NOT_SHOW);
    }

    @Test
    public void save_When_CustomKeywordsSet_Expect_Saved() {
        final Set<String> keywords = new HashSet<>();
        keywords.add("test keyword 1");
        keywords.add("test keyword 2");
        keywords.add("test keyword 3");
        keywords.add("test keyword 4");
        keywords.add("test keyword 5");
        keywords.add("test keyword 6");

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setKeywords(keywords);

        documentService.save(documentDTO);

        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());
        assertEquals(keywords, savedDocumentDTO.getKeywords());

        final int prevSize = keywords.size();
        keywords.remove("test keyword 1");
        assertEquals(keywords.size() + 1, prevSize);

        savedDocumentDTO.setKeywords(keywords);
        documentService.save(savedDocumentDTO);

        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());
        assertEquals(keywords, savedDocumentDTO1.getKeywords());
    }

    @Test
    public void save_When_SearchEnabledAndDisabled_Expect_Saved() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        documentDTO.setSearchDisabled(true);
        documentService.save(documentDTO);

        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());
        assertTrue(savedDocumentDTO.isSearchDisabled());

        savedDocumentDTO.setSearchDisabled(false);
        documentService.save(savedDocumentDTO);

        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());
        assertFalse(savedDocumentDTO1.isSearchDisabled());
    }

    @Test
    public void save_When_CategoriesIsSet_Expect_Saved() {
        categoryDataInitializer.createData(50);

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        final Set<CategoryDTO> categories = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 0)
                .collect(Collectors.toSet());

        documentDTO.setCategories(categories);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(categories, savedDocumentDTO.getCategories());

        final Set<CategoryDTO> categories1 = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 1)
                .collect(Collectors.toSet());

        documentDTO.setCategories(categories1);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());

        assertEquals(categories1, savedDocumentDTO1.getCategories());

    }

    @Test
    public void save_When_CustomAccessRulesSet_Expect_Saved() {
        final Set<RoleDTO> roles = new HashSet<>();

        for (PermissionDTO permissionDTO : PermissionDTO.values()) {
            final RoleDTO roleDTO = roleService.save(new RoleDTO(null, "test_role_" + permissionDTO));
            roleDTO.setPermission(permissionDTO);
            roles.add(roleDTO);
        }

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setRoles(roles);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertTrue(savedDocumentDTO.getRoles().containsAll(roles));

        final Set<RoleDTO> roles1 = new HashSet<>();
        savedDocumentDTO.setRoles(roles1);
        documentService.save(savedDocumentDTO);

        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO1.getRoles(), roles1);
    }

    @Test
    public void save_When_RestrictedPermissionsSet_Expect_Saved() {

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final HashMap<PermissionDTO, RestrictedPermissionDTO> restrictedPermissions = new HashMap<>();

        final RestrictedPermissionDTO restricted1 = new RestrictedPermissionDTO();
        restricted1.setEditDocInfo(true);
        restricted1.setEditImage(false);
        restricted1.setEditLoop(true);
        restricted1.setEditMenu(false);
        restricted1.setEditText(true);

        final RestrictedPermissionDTO restricted2 = new RestrictedPermissionDTO();
        restricted2.setEditDocInfo(false);
        restricted2.setEditImage(true);
        restricted2.setEditLoop(false);
        restricted2.setEditMenu(true);
        restricted2.setEditText(false);

        restrictedPermissions.put(PermissionDTO.RESTRICTED_1, restricted1);
        restrictedPermissions.put(PermissionDTO.RESTRICTED_2, restricted2);

        documentDTO.setRestrictedPermissions(restrictedPermissions);

        documentService.save(documentDTO);

        final DocumentDTO documentDTO1 = documentService.get(documentDTO.getId());

        assertEquals(restricted1, documentDTO1.getRestrictedPermissions().get(PermissionDTO.RESTRICTED_1));
        assertEquals(restricted2, documentDTO1.getRestrictedPermissions().get(PermissionDTO.RESTRICTED_2));
        assertEquals(documentDTO1, documentDTO);
    }

    @Test
    public void save_When_CustomTemplateSet_Expect_Saved() throws Exception {
        final String templateName = "test_" + System.currentTimeMillis();
        final int docId = createdDoc.getId();
        final TextDocumentTemplateDTO templateDTO = new TextDocumentTemplateDTO(docId, templateName, 0, templateName);

        final TextDocumentTemplateDTO savedTemplate = templateService.save(templateDTO);
        assertNotNull(savedTemplate);

        final DocumentDTO documentDTO = documentService.get(docId);
        documentDTO.setTemplate(templateDTO);

        documentService.save(documentDTO);

        final DocumentDTO savedDoc = documentService.get(documentDTO.getId());
        final TextDocumentTemplateDTO savedDocTemplate = savedDoc.getTemplate();

        assertEquals(savedDocTemplate, savedTemplate);
    }
}
