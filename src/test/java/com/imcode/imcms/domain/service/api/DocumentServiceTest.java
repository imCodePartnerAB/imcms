package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.TextDocumentTemplateService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.image.Format;
import org.junit.Before;
import org.junit.Ignore;
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

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private TextService textService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LoopService loopService;

    @Autowired
    private MetaRepository metaRepository;

    @Before
    public void setUp() throws Exception {
        createdDoc = documentDataInitializer.createData();
    }

    @Test
    public void get() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        assertEquals(documentDTO, createdDoc);
    }

    @Test(expected = DocumentNotExistException.class)
    public void get_When_DocumentNotExist_Expect_CorrectException() {
        documentService.get(((Long) System.currentTimeMillis()).intValue());
    }

    @Test
    public void get_When_IdIsNull_Expect_DefaultEmptyDtoReturned() {
        final DocumentDTO documentDTO = documentService.get(null);

        assertNull(documentDTO.getId());
        assertNotEquals(documentDTO.getCommonContents().size(), 0);
        assertEquals(documentDTO.getCommonContents(), commonContentService.createCommonContents());
        assertEquals(documentDTO.getPublicationStatus(), Meta.PublicationStatus.NEW);
        assertEquals(documentDTO.getTemplate(), TextDocumentTemplateDTO.createDefault());
    }

    @Test
    public void getDocumentTitle() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final String testHeadline = "test_headline";

        for (CommonContentDTO commonContentDTO : documentDTO.getCommonContents()) {
            commonContentDTO.setHeadline(testHeadline);
        }

        documentService.save(documentDTO);

        assertEquals(documentService.getDocumentTitle(createdDoc.getId()), testHeadline);
    }

    @Test
    public void getDocumentTarget() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final String testTarget = "_target";
        documentDTO.setTarget(testTarget);
        documentService.save(documentDTO);

        assertEquals(documentService.getDocumentTarget(createdDoc.getId()), testTarget);
    }

    @Test
    public void getDocumentLink_When_NoAlias_Expect_DocIdInLink() {
        final int docId = createdDoc.getId();

        assertEquals(documentService.getDocumentLink(docId), "/" + createdDoc.getId());
    }

    @Test
    public void getDocumentLink_When_AliasIsSet_Expect_AliasInLink() {
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        final String testAlias = "test_alias";
        documentDTO.setAlias(testAlias);
        documentService.save(documentDTO);

        assertEquals(documentService.getDocumentLink(createdDoc.getId()), "/" + testAlias);
    }

    @Test
    public void save_When_NewEmptyDoc_Expect_NoError() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final DocumentDTO emptyDocumentDTO = documentService.get(null);
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
    public void save_With_Target_Expect_Saved() {
        final String testTarget = "_test";
        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());

        documentDTO.setTarget(testTarget);

        documentService.save(documentDTO);

        final DocumentDTO documentDTO1 = documentService.get(documentDTO.getId());

        assertEquals(documentDTO1, documentDTO);
    }

    @Test
    public void save_When_NewEmptyDocWithTarget_Expect_Saved() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final String testTarget = "_test";

        final DocumentDTO emptyDocumentDTO = documentService.get(null);
        emptyDocumentDTO.setTarget(testTarget);

        final int saveDocId = documentService.save(emptyDocumentDTO);
        emptyDocumentDTO.setId(saveDocId);
        final DocumentDTO documentDTO = documentService.get(saveDocId);

        assertEquals(documentDTO.getTarget(), testTarget);
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
                .map(CategoryDTO::new)
                .collect(Collectors.toSet());

        documentDTO.setCategories(categories);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertEquals(categories, savedDocumentDTO.getCategories());

        final Set<CategoryDTO> categories1 = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 1)
                .map(CategoryDTO::new)
                .collect(Collectors.toSet());

        documentDTO.setCategories(categories1);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());

        assertEquals(categories1, savedDocumentDTO1.getCategories());

    }

    @Test
    public void save_When_CustomAccessRulesSet_Expect_Saved() {
        final Map<Integer, PermissionDTO> roleIdToPermissionDTO = new HashMap<>();

        for (PermissionDTO permissionDTO : PermissionDTO.values()) {
            final Role role = roleService.save(new RoleDTO(null, "test_role_" + permissionDTO));
            roleIdToPermissionDTO.put(role.getId(), permissionDTO);
        }

        final DocumentDTO documentDTO = documentService.get(createdDoc.getId());
        documentDTO.setRoleIdToPermission(roleIdToPermissionDTO);

        documentService.save(documentDTO);
        final DocumentDTO savedDocumentDTO = documentService.get(createdDoc.getId());

        assertTrue(savedDocumentDTO.getRoleIdToPermission().entrySet().containsAll(roleIdToPermissionDTO.entrySet()));

        final Map<Integer, PermissionDTO> roleIdToPermissionDTO1 = new HashMap<>();
        savedDocumentDTO.setRoleIdToPermission(roleIdToPermissionDTO1);
        documentService.save(savedDocumentDTO);

        final DocumentDTO savedDocumentDTO1 = documentService.get(createdDoc.getId());
        assertEquals(savedDocumentDTO1.getRoleIdToPermission(), roleIdToPermissionDTO1);
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
    public void save_When_CustomTemplateSet_Expect_Saved() {
        final String templateName = "test_" + System.currentTimeMillis();
        final int docId = createdDoc.getId();
        final TextDocumentTemplateDTO templateDTO = new TextDocumentTemplateDTO(docId, templateName, templateName);

        final TextDocumentTemplateDTO savedTemplate = templateService.save(templateDTO);
        assertNotNull(savedTemplate);

        final DocumentDTO documentDTO = documentService.get(docId);
        documentDTO.setTemplate(templateDTO);

        documentService.save(documentDTO);

        final DocumentDTO savedDoc = documentService.get(documentDTO.getId());
        final TextDocumentTemplateDTO savedDocTemplate = savedDoc.getTemplate();

        assertEquals(savedDocTemplate, savedTemplate);
    }

    @Test
    @Ignore
    public void delete_When_UserAdminAndDocExistWithContent_Expect_DocumentNotExistExceptionAfterDeletion() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(user); // means current user is admin now

        final Integer createdDocId = createdDoc.getId();
        final DocumentDTO documentDTO = documentService.get(createdDocId);
        assertNotNull(documentDTO);

        final int testIndex = 1;

        final LoopEntryRefDTO[] loopEntryRefDTOS = {
                new LoopEntryRefDTO(testIndex, testIndex),
                null,
        };

        final List<LoopEntryDTO> loopEntryDTOS = new ArrayList<>(Collections.singletonList(
                LoopEntryDTO.createEnabled(testIndex)
        ));
        final LoopDTO loopDTO = new LoopDTO(createdDocId, testIndex, loopEntryDTOS);
        loopService.saveLoop(loopDTO);

        for (LoopEntryRefDTO loopEntryRefDTO : loopEntryRefDTOS) {

            documentDTO.getCommonContents().forEach(commonContentDTO -> {
                final String langCode = commonContentDTO.getLanguage().getCode();
                final TextDTO textDTO = new TextDTO();
                textDTO.setText("test");
                textDTO.setType(Text.Type.PLAIN_TEXT);
                textDTO.setDocId(createdDocId);
                textDTO.setIndex(testIndex);
                textDTO.setLoopEntryRef(loopEntryRefDTO);
                textDTO.setLangCode(langCode);
                textService.save(textDTO);

                final ImageDTO imageDTO = new ImageDTO(testIndex, createdDocId, loopEntryRefDTO);
                imageDTO.setFormat(Format.JPEG);
                imageDTO.setLangCode(langCode);

                imageService.saveImage(imageDTO);
            });
        }

        final MenuDTO menuDTO = new MenuDTO();
        menuDTO.setDocId(createdDocId);
        menuDTO.setMenuIndex(testIndex);
        final MenuItemDTO menuItemDTO = new MenuItemDTO();
        menuItemDTO.setDocumentId(createdDocId);
        menuDTO.setMenuItems(new ArrayList<>(Collections.singletonList(menuItemDTO)));

        menuService.saveFrom(menuDTO);

        documentService.delete(documentDTO);

        try {
            documentService.get(createdDocId);
            fail("Expected exception wasn't thrown!");

        } catch (DocumentNotExistException e) {
            // expected exception
        }
    }
}
