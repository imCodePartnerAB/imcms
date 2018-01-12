package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.TextDocumentTemplate;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.NotImplementedException;
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

import java.io.File;
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

    private TextDocumentDTO createdTextDoc;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private CategoryDataInitializer categoryDataInitializer;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    @Autowired
    private TemplateService templateService;

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
        createdTextDoc = documentDataInitializer.createTextDocument();

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now
    }

    @Test
    public void getDocument() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void getDocument_When_NotExist_Expect_Correct_Exception() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + ((Long) System.currentTimeMillis()).intValue());

        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
    }

    @Test
    public void get_When_IdIsNull_Expect_DefaultEmptyTextDocumentDtoReturned() throws Exception {
        final Meta.DocumentType documentType = Meta.DocumentType.TEXT;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("type", "" + documentType);

        final String response = getJsonResponse(requestBuilder);
        final TextDocumentDTO documentDTO = fromJson(response, TextDocumentDTO.class);

        assertNull(documentDTO.getId());
        assertEquals(documentDTO.getType(), documentType);
        assertFalse(documentDTO.getCommonContents().isEmpty());
        assertEquals(documentDTO.getCommonContents(), commonContentService.createCommonContents());
        assertEquals(documentDTO.getPublicationStatus(), Meta.PublicationStatus.NEW);
        assertEquals(documentDTO.getTemplate(), TextDocumentTemplateDTO.createDefault());
    }

    @Test
    public void saveDocument_When_NoChanges_Expect_NoError() throws Exception {
        performPostWithContentExpectOk(createdTextDoc);
    }

    @Test
    public void save_When_UserNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.USERS);
        Imcms.setUser(user); // means current user is not admin now

        performPostWithContentExpectException(createdTextDoc, NoPermissionToEditDocumentException.class);
    }

    @Test
    public void save_With_Target_Expected_Saved() throws Exception {
        createdTextDoc.setTarget("test");
        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_CustomCommonContentsSet_Expect_Saved() throws Exception {
        final List<CommonContentDTO> commonContents = createdTextDoc.getCommonContents();

        for (int i = 0; i < commonContents.size(); i++) {
            CommonContentDTO commonContentDTO = commonContents.get(i);
            commonContentDTO.setHeadline("Test headline " + i);
            commonContentDTO.setMenuText("Test menu text " + i);
            commonContentDTO.setMenuImageURL("Test menu image url " + i);
            commonContentDTO.setEnabled((i % 2) == 0);
        }

        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_TargetAndAliasChanged_Expect_Saved() throws Exception {
        final String newTarget = "_blank";
        final String newAlias = "test-alias";

        createdTextDoc.setTarget(newTarget);
        createdTextDoc.setAlias(newAlias);
        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_DifferentPublicationStatusSet_Expect_Saved() throws Exception {
        final Meta.PublicationStatus statusApproved = Meta.PublicationStatus.APPROVED;
        final Meta.PublicationStatus statusDisapproved = Meta.PublicationStatus.DISAPPROVED;
        final Meta.PublicationStatus statusNew = Meta.PublicationStatus.NEW;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        // approved
        createdTextDoc.setPublicationStatus(statusApproved);
        performPostWithContentExpectOk(createdTextDoc);


        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        // disapproved
        createdTextDoc.setPublicationStatus(statusDisapproved);
        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        // new
        createdTextDoc.setPublicationStatus(statusNew);
        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
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

        createdTextDoc.setCreated(createdAudit);
        createdTextDoc.setModified(modifiedAudit);
        createdTextDoc.setArchived(archivedAudit);
        createdTextDoc.setPublished(publishedAudit);
        createdTextDoc.setPublicationEnd(depublishedAudit);

        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        // only for nullable things
        final AuditDTO emptyArchivedAudit = new AuditDTO();
        final AuditDTO emptyPublishedAudit = new AuditDTO();
        final AuditDTO emptyDepublishedAudit = new AuditDTO();

        createdTextDoc.setArchived(emptyArchivedAudit);
        createdTextDoc.setPublished(emptyPublishedAudit);
        createdTextDoc.setPublicationEnd(emptyDepublishedAudit);

        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_CustomMissingLanguagePropertySet_Expect_Saved() throws Exception {
        createdTextDoc.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);

        performPostWithContentExpectOk(createdTextDoc);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        createdTextDoc.setDisabledLanguageShowMode(DO_NOT_SHOW);

        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
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

        createdTextDoc.setKeywords(keywords);

        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        final int prevSize = keywords.size();
        keywords.remove("test keyword 1");
        assertEquals(keywords.size() + 1, prevSize);

        createdTextDoc.setKeywords(keywords);
        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_SearchEnabledAndDisabled_Expect_Saved() throws Exception {
        createdTextDoc.setSearchDisabled(true);
        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        createdTextDoc.setSearchDisabled(false);
        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_CategoriesIsSet_Expect_Saved() throws Exception {
        categoryDataInitializer.createData(50);

        final Set<CategoryDTO> categories = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 0)
                .map(CategoryDTO::new)
                .collect(Collectors.toSet());

        createdTextDoc.setCategories(categories);

        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        final Set<CategoryDTO> categories1 = categoryService.getAll().stream()
                .filter(categoryDTO -> categoryDTO.getId() % 2 == 1)
                .map(CategoryDTO::new)
                .collect(Collectors.toSet());

        createdTextDoc.setCategories(categories1);

        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_CustomAccessRulesSet_Expect_Saved() throws Exception {
        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();

        for (Permission permission : Permission.values()) {
            final Role role = roleService.save(new RoleDTO(null, "test_role_" + permission));
            roleIdToPermission.put(role.getId(), permission);
        }

        createdTextDoc.setRoleIdToPermission(roleIdToPermission);

        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        final Map<Integer, Permission> roleIdToPermission1 = new HashMap<>();
        createdTextDoc.setRoleIdToPermission(roleIdToPermission1);

        performPostWithContentExpectOk(createdTextDoc);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_RestrictedPermissionsSet_Expect_Saved() throws Exception {
        final Set<RestrictedPermissionDTO> restrictedPermissions = new HashSet<>();

        final RestrictedPermissionDTO restricted1 = new RestrictedPermissionDTO();
        restricted1.setPermission(Permission.RESTRICTED_1);
        restricted1.setEditDocInfo(true);
        restricted1.setEditImage(false);
        restricted1.setEditLoop(true);
        restricted1.setEditMenu(false);
        restricted1.setEditText(true);

        final RestrictedPermissionDTO restricted2 = new RestrictedPermissionDTO();
        restricted2.setPermission(Permission.RESTRICTED_2);
        restricted2.setEditDocInfo(false);
        restricted2.setEditImage(true);
        restricted2.setEditLoop(false);
        restricted2.setEditMenu(true);
        restricted2.setEditText(false);

        restrictedPermissions.add(restricted1);
        restrictedPermissions.add(restricted2);

        createdTextDoc.setRestrictedPermissions(restrictedPermissions);

        performPostWithContentExpectOk(createdTextDoc);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("docId", "" + createdTextDoc.getId());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
    }

    @Test
    public void save_When_CustomTemplateSet_Expect_Saved() throws Exception {

        final String templateName = "test_" + System.currentTimeMillis();
        final int docId = createdTextDoc.getId();

        final File templateDirectory = templateService.getTemplateDirectory();
        final File templateFile = new File(templateDirectory, templateName + ".jsp");

        try {
            assertTrue(templateFile.createNewFile());

            final TemplateDTO template = new TemplateDTO(templateName, false);
            templateService.save(template);

            final TextDocumentTemplateDTO templateDTO = new TextDocumentTemplateDTO(docId, templateName, templateName);

            final TextDocumentTemplate savedTemplate = textDocumentTemplateService.save(templateDTO);
            assertNotNull(savedTemplate);

            createdTextDoc.setTemplate(templateDTO);

            performPostWithContentExpectOk(createdTextDoc);

            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                    .param("docId", "" + createdTextDoc.getId());

            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void delete_When_UserIsNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.USERS);
        Imcms.setUser(user); // means current user is not admin now

        performDeleteWithContentExpectException(createdTextDoc, NotImplementedException.class);
        // todo: change when will be implemented to this: NoPermissionToEditDocumentException.class);
    }

    @Test
    public void delete_When_DocumentExistAndUserIsAdmin_Expect_NoError() throws Exception {

        performDeleteWithContentExpectException(createdTextDoc, NotImplementedException.class);
        // todo: change when will be implemented to this:
//        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(createdDoc);
//        performRequestBuilderExpectedOk(requestBuilder);
    }

    // todo: uncomment when docs deletion will be needed
//    @Test
//    public void delete_When_DocumentExistAndUserIsAdmin_Expect_DocumentNotExistExceptionAfterDeletion() throws Exception {
//        delete_When_DocumentExistAndUserIsAdmin_Expect_NoError();
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdDoc.getId());
//
//        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
//    }

    @Test
    public void createEmpty_When_FileDocTypeSet_Expect_EmptyFileDoc() throws Exception {
        final Meta.DocumentType documentType = Meta.DocumentType.FILE;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
                .param("type", "" + documentType);

        final String response = getJsonResponse(requestBuilder);
        final FileDocumentDTO documentDTO = fromJson(response, FileDocumentDTO.class);

        assertNull(documentDTO.getId());
        assertEquals(documentDTO.getType(), documentType);
        assertFalse(documentDTO.getCommonContents().isEmpty());
        assertEquals(documentDTO.getCommonContents(), commonContentService.createCommonContents());
        assertEquals(documentDTO.getPublicationStatus(), Meta.PublicationStatus.NEW);
        assertTrue(documentDTO.getFiles().isEmpty());
    }
}
