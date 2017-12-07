package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.CategoryService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.domain.service.core.CommonContentService;
import com.imcode.imcms.domain.service.core.TextDocumentTemplateService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
public class DocumentControllerTest extends AbstractControllerTest {

    private DocumentDTO createdDoc;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TextDocumentTemplateService templateService;

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private RoleService roleService;

    @Override
    protected String controllerPath() {
        return "/documents";
    }

    @Before
    public void setUp() throws Exception {
        createdDoc = documentDataInitializer.createData();

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now
    }

    @Test
    public void getDocument() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void getDocument_When_NotExist_Expect_Correct_Exception() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + ((Long) System.currentTimeMillis()).intValue());

        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
    }

    @Test
    public void get_When_IdIsNull_Expect_DefaultEmptyDtoReturned() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        final String response = getJsonResponse(requestBuilder);
        final DocumentDTO documentDTO = fromJson(response, DocumentDTO.class);

        assertNull(documentDTO.getId());
        assertNotEquals(documentDTO.getCommonContents().size(), 0);
        assertEquals(documentDTO.getCommonContents(), commonContentService.createCommonContents());
        assertEquals(documentDTO.getPublicationStatus(), Meta.PublicationStatus.NEW);
        assertEquals(documentDTO.getTemplate(), TextDocumentTemplateDTO.createDefault());
    }

    @Test
    public void saveDocument_When_NoChanges_Expect_NoError() throws Exception {
        performPostWithContentExpectOk(createdDoc);
    }

    @Test
    public void save_When_UserNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.USERS);
        Imcms.setUser(user); // means current user is not admin now

        performPostWithContentExpectException(createdDoc, NoPermissionToEditDocumentException.class);
    }

    @Test
    public void save_With_Target_Expected_Saved() throws Exception {
        createdDoc.setTarget("test_target");
        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_CustomCommonContentsSet_Expect_Saved() throws Exception {
        final List<CommonContentDTO> commonContents = createdDoc.getCommonContents();

        for (int i = 0; i < commonContents.size(); i++) {
            CommonContentDTO commonContentDTO = commonContents.get(i);
            commonContentDTO.setHeadline("Test headline " + i);
            commonContentDTO.setMenuText("Test menu text " + i);
            commonContentDTO.setMenuImageURL("Test menu image url " + i);
            commonContentDTO.setEnabled((i % 2) == 0);
        }

        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_TargetAndAliasChanged_Expect_Saved() throws Exception {
        final String newTarget = "_blank";
        final String newAlias = "test-alias";

        createdDoc.setTarget(newTarget);
        createdDoc.setAlias(newAlias);
        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_DifferentPublicationStatusSet_Expect_Saved() throws Exception {
        final Meta.PublicationStatus statusApproved = Meta.PublicationStatus.APPROVED;
        final Meta.PublicationStatus statusDisapproved = Meta.PublicationStatus.DISAPPROVED;
        final Meta.PublicationStatus statusNew = Meta.PublicationStatus.NEW;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        // approved
        createdDoc.setPublicationStatus(statusApproved);
        performPostWithContentExpectOk(createdDoc);


        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        // disapproved
        createdDoc.setPublicationStatus(statusDisapproved);
        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        // new
        createdDoc.setPublicationStatus(statusNew);
        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_CreatedAndModifiedAndArchivedAndPublishedAndDepublishedAttributesSet_Expect_Saved() throws Exception {
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

        createdDoc.setCreated(createdAudit);
        createdDoc.setModified(modifiedAudit);
        createdDoc.setArchived(archivedAudit);
        createdDoc.setPublished(publishedAudit);
        createdDoc.setPublicationEnd(depublishedAudit);

        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        // only for nullable things
        final AuditDTO emptyArchivedAudit = new AuditDTO();
        final AuditDTO emptyPublishedAudit = new AuditDTO();
        final AuditDTO emptyDepublishedAudit = new AuditDTO();

        createdDoc.setArchived(emptyArchivedAudit);
        createdDoc.setPublished(emptyPublishedAudit);
        createdDoc.setPublicationEnd(emptyDepublishedAudit);

        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_CustomMissingLanguagePropertySet_Expect_Saved() throws Exception {
        createdDoc.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);

        performPostWithContentExpectOk(createdDoc);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        createdDoc.setDisabledLanguageShowMode(DO_NOT_SHOW);

        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_CustomKeywordsSet_Expect_Saved() throws Exception {
        final Set<String> keywords = new HashSet<>();
        keywords.add("test keyword 1");
        keywords.add("test keyword 2");
        keywords.add("test keyword 3");
        keywords.add("test keyword 4");
        keywords.add("test keyword 5");
        keywords.add("test keyword 6");

        createdDoc.setKeywords(keywords);

        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        final int prevSize = keywords.size();
        keywords.remove("test keyword 1");
        assertEquals(keywords.size() + 1, prevSize);

        createdDoc.setKeywords(keywords);
        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_SearchEnabledAndDisabled_Expect_Saved() throws Exception {
        createdDoc.setSearchDisabled(true);
        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        createdDoc.setSearchDisabled(false);
        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_CategoriesIsSet_Expect_Saved() throws Exception {
        categoryDataInitializer.createData(50);

        final Set<CategoryDTO> categories = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 0)
                .map(CategoryDTO::new)
                .collect(Collectors.toSet());

        createdDoc.setCategories(categories);

        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        final Set<CategoryDTO> categories1 = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 1)
                .map(CategoryDTO::new)
                .collect(Collectors.toSet());

        createdDoc.setCategories(categories1);

        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_CustomAccessRulesSet_Expect_Saved() throws Exception {
        final Map<Integer, PermissionDTO> roleIdToPermissionDTO = new HashMap<>();

        for (PermissionDTO permissionDTO : PermissionDTO.values()) {
            final Role role = roleService.save(new RoleDTO(null, "test_role_" + permissionDTO));
            roleIdToPermissionDTO.put(role.getId(), permissionDTO);
        }

        createdDoc.setRoleIdToPermission(roleIdToPermissionDTO);

        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));

        final Map<Integer, PermissionDTO> roleIdToPermissionDTO1 = new HashMap<>();
        createdDoc.setRoleIdToPermission(roleIdToPermissionDTO1);

        performPostWithContentExpectOk(createdDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_RestrictedPermissionsSet_Expect_Saved() throws Exception {
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

        createdDoc.setRestrictedPermissions(restrictedPermissions);

        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void save_When_CustomTemplateSet_Expect_Saved() throws Exception {
        final String templateName = "test_" + System.currentTimeMillis();
        final int docId = createdDoc.getId();
        final TextDocumentTemplateDTO templateDTO = new TextDocumentTemplateDTO(docId, templateName, templateName);

        final TextDocumentTemplateDTO savedTemplate = templateService.save(templateDTO);
        assertNotNull(savedTemplate);

        createdDoc.setTemplate(templateDTO);

        performPostWithContentExpectOk(createdDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdDoc));
    }

    @Test
    public void delete_When_UserIsNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.USERS);
        Imcms.setUser(user); // means current user is not admin now

        performDeleteWithContentExpectException(createdDoc, NoPermissionToEditDocumentException.class);
    }

    @Test
    public void delete_When_DocumentExistAndUserIsAdmin_Expect_NoError() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(createdDoc);
        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    public void delete_When_DocumentExistAndUserIsAdmin_Expect_DocumentNotExistExceptionAfterDeletion() throws Exception {
        delete_When_DocumentExistAndUserIsAdmin_Expect_NoError();

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdDoc.getId());

        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
    }
}
