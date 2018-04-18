package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.util.NestedServletException;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

//import com.imcode.imcms.components.datainitializer.CategoryDataInitializer;
//import com.imcode.imcms.components.datainitializer.FileDocumentDataInitializer;
//import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
//import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
//import com.imcode.imcms.components.datainitializer.UserDataInitializer;
//import com.imcode.imcms.controller.AbstractControllerTest;
//import com.imcode.imcms.domain.dto.AuditDTO;
//import com.imcode.imcms.domain.dto.DocumentFileDTO;
//import com.imcode.imcms.domain.dto.DocumentUrlDTO;
//import com.imcode.imcms.domain.dto.FileDocumentDTO;
//import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
//import com.imcode.imcms.domain.dto.RoleDTO;
//import com.imcode.imcms.domain.dto.TemplateDTO;
//import com.imcode.imcms.domain.dto.TextDocumentDTO;
//import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
//import com.imcode.imcms.domain.dto.UrlDocumentDTO;
//import com.imcode.imcms.domain.exception.DocumentNotExistException;
//import com.imcode.imcms.domain.factory.DocumentDtoFactory;
//import com.imcode.imcms.domain.service.CategoryService;
//import com.imcode.imcms.domain.service.DocumentService;
//import com.imcode.imcms.domain.service.RoleService;
//import com.imcode.imcms.domain.service.TemplateService;
//import com.imcode.imcms.domain.service.TextDocumentTemplateService;
//import com.imcode.imcms.model.Category;
//import com.imcode.imcms.model.CommonContent;
//import com.imcode.imcms.model.RestrictedPermission;
//import com.imcode.imcms.model.Role;
//import com.imcode.imcms.model.TextDocumentTemplate;
//import com.imcode.imcms.persistence.entity.Meta;
//import com.imcode.imcms.persistence.entity.Meta.Permission;
//import com.imcode.imcms.persistence.entity.User;
//import com.imcode.imcms.persistence.entity.Version;
//import com.imcode.imcms.persistence.repository.DocumentFileRepository;
//import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
//import com.imcode.imcms.persistence.repository.MetaRepository;
//import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
//import imcode.server.Imcms;
//import imcode.server.document.NoPermissionToEditDocumentException;
//import imcode.server.user.RoleId;
//import imcode.server.user.UserDomainObject;
//import org.apache.commons.lang3.NotImplementedException;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.BDDMockito;
//import static org.mockito.BDDMockito.then;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.File;
//import java.time.Instant;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.DO_NOT_SHOW;
//import static com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
//import static org.hamcrest.Matchers.is;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertThat;
//import static org.junit.Assert.assertTrue;

public class DocumentControllerTest extends MockingControllerTest {

//    private TextDocumentDTO createdTextDoc;
//    private FileDocumentDTO createdFileDoc;
//    private UrlDocumentDTO createdUrlDoc;
//
//    @Autowired
//    private DocumentDtoFactory documentDtoFactory;
//
//    @Autowired
//    private TextDocumentDataInitializer textDocumentDataInitializer;
//
//    @Autowired
//    private FileDocumentDataInitializer fileDocumentDataInitializer;
//
//    @Autowired
//    private UserDataInitializer userDataInitializer;
//
//    @Autowired
//    private CategoryDataInitializer categoryDataInitializer;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Autowired
//    private TextDocumentTemplateService textDocumentTemplateService;
//
//    @Autowired
//    private TemplateService templateService;
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private DocumentService<UrlDocumentDTO> urlDocumentService;
//
//    @Autowired
//    private MetaRepository metaRepository;
//
//    @Autowired
//    private UrlDocumentDataInitializer urlDocumentDataInitializer;
//
//    @Autowired
//    private TextDocumentTemplateRepository textDocumentTemplateRepository;
//
//    @Autowired
//    private DocumentFileRepository documentFileRepository;
//
//    @Autowired
//    private DocumentUrlRepository documentUrlRepository;

    @Mock
    private DelegatingByTypeDocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @Override
    protected String controllerPath() {
        return "/documents";
    }

    @Override
    protected Object controllerToMock() {
        return documentController;
    }

    @Test
    public void get_When_DocIdIsNull_Expect_NewDocumentCreated() throws Exception {
        final Meta.DocumentType documentType = Meta.DocumentType.TEXT;
        final int parentDocId = 13;
        final DocumentDTO newDoc = new DocumentDTO();

        given(documentService.createNewDocument(documentType, parentDocId)).willReturn(newDoc);

        final RequestBuilder requestBuilder = get(controllerPath())
                .param("type", documentType.toString())
                .param("parentDocId", "" + parentDocId);

        perform(requestBuilder).andExpectAsJson(newDoc);
    }

    @Test
    public void get_When_DocIdIsNotNull_Expect_DocumentReceivedById() throws Exception {
        final int docId = 13;
        final FileDocumentDTO existingDoc = new FileDocumentDTO();

        given(documentService.get(docId)).willReturn(existingDoc);

        final RequestBuilder requestBuilder = get(controllerPath())
                .param("docId", "" + docId);

        perform(requestBuilder).andExpectAsJson(existingDoc);
    }

    @Test
    public void copy_When_DocIdIsNotNull_Expect_CopyReturned() throws Exception {
        final int docId = 13;
        final TextDocumentDTO copy = new TextDocumentDTO();

        given(documentService.copy(docId)).willReturn(copy);

        final RequestBuilder requestBuilder = post(controllerPath().concat("/copy/" + docId))
                .param("docId", "" + docId);

        perform(requestBuilder).andExpectAsJson(copy);
    }

    @Test
    public void save_When_DocIdIsNotNull_Expect_CopyReturned() throws Exception {
        final UberDocumentDTO saveMe = new UberDocumentDTO();

        given(documentService.save(saveMe)).willReturn(saveMe);

        final RequestBuilder requestBuilder = post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(saveMe));

        perform(requestBuilder).andExpectAsJson(saveMe);
    }

    @Test(expected = NotImplementedException.class)
    public void delete_When_DeletingDisabled_Expect_NotImplementedException() throws Throwable {
        final UberDocumentDTO deleteMe = new UberDocumentDTO();

        final RequestBuilder requestBuilder = delete(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(deleteMe));

        try {
            perform(requestBuilder);

        } catch (NestedServletException e) {
            throw e.getCause(); // real cause
        }
    }

    //    @Before
//    public void setUp() throws Exception {
//        createdTextDoc = textDocumentDataInitializer.createTextDocument();
//        createdFileDoc = fileDocumentDataInitializer.createFileDocument();
//        createdUrlDoc = urlDocumentDataInitializer.createUrlDocument();
//
//        final UserDomainObject user = new UserDomainObject(1);
//        user.addRoleId(RoleId.SUPERADMIN);
//        user.setLanguageIso639_2("eng");
//        Imcms.setUser(user); // means current user is admin now
//    }
//
//    @Test
//    public void getUrlDocument_When_DocIdIsNullAndParentDocIdSet_Expect_NewUrlDocumentReturned() throws Exception {
//        final Meta.DocumentType documentType = Meta.DocumentType.URL;
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("type", documentType.toString())
//                .param("parentDocId", "" + createdUrlDoc.getId());
//
//        final String response = getJsonResponse(requestBuilder);
//
//        final UrlDocumentDTO urlDocumentDTO = fromJson(response, UrlDocumentDTO.class);
//
//        final DocumentUrlDTO actualDocumentUrlDTO = urlDocumentDTO.getDocumentURL();
//        final DocumentUrlDTO expectedDocumentUrlDTO = DocumentUrlDTO.createDefault();
//
//        assertNull(urlDocumentDTO.getId());
//        assertEquals(urlDocumentDTO.getType(), documentType);
//        assertFalse(urlDocumentDTO.getCommonContents().isEmpty());
//        assertEquals(urlDocumentDTO.getPublicationStatus(), Meta.PublicationStatus.NEW);
//        assertEquals(expectedDocumentUrlDTO, actualDocumentUrlDTO);
//    }
//
//    @Test
//    public void getUrlDocument_When_DocumentExists_Expect_Returned() throws Exception {
//        final UrlDocumentDTO empty = documentDtoFactory.createEmptyUrlDocument();
//        final int savedId = urlDocumentService.save(empty).getId();
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", savedId + "");
//
//        final UrlDocumentDTO expectedUrlDocument = urlDocumentService.get(savedId);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(expectedUrlDocument));
//
//    }
//
//    @Test
//    public void getUrlDocument_When_DocumentDoesNotExist_Expect_DocumentNotExistException() throws Exception {
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + ((Long) Instant.now().toEpochMilli()).intValue());
//
//        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
//    }
//
//    @Test
//    public void saveUrlDocument_Expect_Saved() throws Exception {
//        final UrlDocumentDTO empty = documentDtoFactory.createEmptyUrlDocument();
//        final int beforeSavingSize = metaRepository.findAll().size();
//
//        performPostWithContentExpectOk(empty);
//
//        final int afterSavingSize = metaRepository.findAll().size();
//
//        assertEquals(beforeSavingSize + 1, afterSavingSize);
//    }
//
//    @Test
//    public void getDocument() throws Exception {
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void getDocument_When_NotExist_Expect_Correct_Exception() throws Exception {
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + ((Long) System.currentTimeMillis()).intValue());
//
//        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
//    }
//
//    @Test
//    public void get_When_IdIsNullAndParentDocIdIsSet_Expect_NewTextDocumentDtoReturned() throws Exception {
//        final Meta.DocumentType documentType = Meta.DocumentType.TEXT;
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("type", "" + documentType)
//                .param("parentDocId", "" + createdTextDoc.getId());
//
//        final String response = getJsonResponse(requestBuilder);
//        final TextDocumentDTO documentDTO = fromJson(response, TextDocumentDTO.class);
//
//        assertNull(documentDTO.getId());
//        assertEquals(documentDTO.getType(), documentType);
//        assertEquals(documentDTO.getCommonContents().size(), createdTextDoc.getCommonContents().size());
//
//        for (int i = 0; i < documentDTO.getCommonContents().size(); i++) {
//            final CommonContent newCommonContent = documentDTO.getCommonContents().get(i);
//            final CommonContent oldCommonContent = createdTextDoc.getCommonContents().get(i);
//
//            assertEquals(newCommonContent.getHeadline(), oldCommonContent.getHeadline());
//            assertEquals(newCommonContent.getMenuText(), oldCommonContent.getMenuText());
//            assertEquals(newCommonContent.getMenuImageURL(), oldCommonContent.getMenuImageURL());
//            assertEquals(newCommonContent.getLanguage(), oldCommonContent.getLanguage());
//
//            assertEquals(newCommonContent.getVersionNo(), Integer.valueOf(Version.WORKING_VERSION_INDEX));
//            assertNull(newCommonContent.getId());
//            assertNull(newCommonContent.getDocId());
//        }
//
//        assertEquals(documentDTO.getPublicationStatus(), Meta.PublicationStatus.NEW);
//        assertEquals(documentDTO.getTemplate().getTemplateName(), createdTextDoc.getTemplate().getChildrenTemplateName());
//    }
//
//    @Test
//    public void saveDocument_When_NoChanges_Expect_NoError() throws Exception {
//        performPostWithContentExpectOk(createdTextDoc);
//    }
//
//    @Test
//    public void save_When_UserNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
//        final UserDomainObject user = new UserDomainObject(1);
//        user.addRoleId(RoleId.USERS);
//        Imcms.setUser(user); // means current user is not admin now
//
//        performPostWithContentExpectException(createdTextDoc, NoPermissionToEditDocumentException.class);
//    }
//
//    @Test
//    public void save_With_Target_Expected_Saved() throws Exception {
//        createdTextDoc.setTarget("test");
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_CustomCommonContentsSet_Expect_Saved() throws Exception {
//        final List<CommonContent> commonContents = createdTextDoc.getCommonContents();
//
//        for (int i = 0; i < commonContents.size(); i++) {
//            CommonContent commonContentDTO = commonContents.get(i);
//            commonContentDTO.setHeadline("Test headline " + i);
//            commonContentDTO.setMenuText("Test menu text " + i);
//            commonContentDTO.setMenuImageURL("Test menu image url " + i);
//            commonContentDTO.setEnabled((i % 2) == 0);
//        }
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_TargetAndAliasChanged_Expect_Saved() throws Exception {
//        final String newTarget = "_blank";
//        final String newAlias = "test-alias";
//
//        createdTextDoc.setTarget(newTarget);
//        createdTextDoc.setAlias(newAlias);
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_DifferentPublicationStatusSet_Expect_Saved() throws Exception {
//        final Meta.PublicationStatus statusApproved = Meta.PublicationStatus.APPROVED;
//        final Meta.PublicationStatus statusDisapproved = Meta.PublicationStatus.DISAPPROVED;
//        final Meta.PublicationStatus statusNew = Meta.PublicationStatus.NEW;
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        // approved
//        createdTextDoc.setPublicationStatus(statusApproved);
//        performPostWithContentExpectOk(createdTextDoc);
//
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        // disapproved
//        createdTextDoc.setPublicationStatus(statusDisapproved);
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        // new
//        createdTextDoc.setPublicationStatus(statusNew);
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_CreatedAndModifiedAndArchivedAndPublishedAndDepublishedAttributesSet_Expect_Saved() throws Exception {
//        final User user = userDataInitializer.createData("testUser");
//
//        final Supplier<AuditDTO> auditCreator = () -> {
//            final AuditDTO auditDTO = new AuditDTO();
//            auditDTO.setDateTime(new Date());
//            auditDTO.setId(user.getId());
//            auditDTO.setBy(user.getLogin());
//            return auditDTO;
//        };
//
//        final AuditDTO createdAudit = auditCreator.get();
//        final AuditDTO modifiedAudit = auditCreator.get();
//        final AuditDTO archivedAudit = auditCreator.get();
//        final AuditDTO publishedAudit = auditCreator.get();
//        final AuditDTO depublishedAudit = auditCreator.get();
//
//        createdTextDoc.setCreated(createdAudit);
//        createdTextDoc.setModified(modifiedAudit);
//        createdTextDoc.setArchived(archivedAudit);
//        createdTextDoc.setPublished(publishedAudit);
//        createdTextDoc.setPublicationEnd(depublishedAudit);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        // only for nullable things
//        final AuditDTO emptyArchivedAudit = new AuditDTO();
//        final AuditDTO emptyPublishedAudit = new AuditDTO();
//        final AuditDTO emptyDepublishedAudit = new AuditDTO();
//
//        createdTextDoc.setArchived(emptyArchivedAudit);
//        createdTextDoc.setPublished(emptyPublishedAudit);
//        createdTextDoc.setPublicationEnd(emptyDepublishedAudit);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_CustomMissingLanguagePropertySet_Expect_Saved() throws Exception {
//        createdTextDoc.setDisabledLanguageShowMode(SHOW_IN_DEFAULT_LANGUAGE);
//
//        performPostWithContentExpectOk(createdTextDoc);
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        createdTextDoc.setDisabledLanguageShowMode(DO_NOT_SHOW);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_CustomKeywordsSet_Expect_Saved() throws Exception {
//        final Set<String> keywords = new HashSet<>();
//        keywords.add("test keyword 1");
//        keywords.add("test keyword 2");
//        keywords.add("test keyword 3");
//        keywords.add("test keyword 4");
//        keywords.add("test keyword 5");
//        keywords.add("test keyword 6");
//
//        createdTextDoc.setKeywords(keywords);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        final int prevSize = keywords.size();
//        keywords.remove("test keyword 1");
//        assertEquals(keywords.size() + 1, prevSize);
//
//        createdTextDoc.setKeywords(keywords);
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_SearchEnabledAndDisabled_Expect_Saved() throws Exception {
//        createdTextDoc.setSearchDisabled(true);
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        createdTextDoc.setSearchDisabled(false);
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_CategoriesIsSet_Expect_Saved() throws Exception {
//        categoryDataInitializer.createData(50);
//
//        final Set<Category> categories = categoryService.getAll().stream()
//                .filter(categoryDTO -> categoryDTO.getId() % 2 == 0)
//                .collect(Collectors.toSet());
//
//        createdTextDoc.setCategories(categories);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        final Set<Category> categories1 = categoryService.getAll().stream()
//                .filter(categoryDTO -> categoryDTO.getId() % 2 == 1)
//                .collect(Collectors.toSet());
//
//        createdTextDoc.setCategories(categories1);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_CustomAccessRulesSet_Expect_Saved() throws Exception {
//        final Map<Integer, Permission> roleIdToPermission = new HashMap<>();
//
//        for (Permission permission : Permission.values()) {
//            final Role role = roleService.save(new RoleDTO(null, "test_role_" + permission));
//            roleIdToPermission.put(role.getId(), permission);
//        }
//
//        createdTextDoc.setRoleIdToPermission(roleIdToPermission);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        final Map<Integer, Permission> roleIdToPermission1 = new HashMap<>();
//        createdTextDoc.setRoleIdToPermission(roleIdToPermission1);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_RestrictedPermissionsSet_Expect_Saved() throws Exception {
//        final Set<RestrictedPermission> restrictedPermissions = new HashSet<>();
//
//        final RestrictedPermissionDTO restricted1 = new RestrictedPermissionDTO();
//        restricted1.setPermission(Permission.RESTRICTED_1);
//        restricted1.setEditDocInfo(true);
//        restricted1.setEditImage(false);
//        restricted1.setEditLoop(true);
//        restricted1.setEditMenu(false);
//        restricted1.setEditText(true);
//
//        final RestrictedPermissionDTO restricted2 = new RestrictedPermissionDTO();
//        restricted2.setPermission(Permission.RESTRICTED_2);
//        restricted2.setEditDocInfo(false);
//        restricted2.setEditImage(true);
//        restricted2.setEditLoop(false);
//        restricted2.setEditMenu(true);
//        restricted2.setEditText(false);
//
//        restrictedPermissions.add(restricted1);
//        restrictedPermissions.add(restricted2);
//
//        createdTextDoc.setRestrictedPermissions(restrictedPermissions);
//
//        performPostWithContentExpectOk(createdTextDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdTextDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//    }
//
//    @Test
//    public void save_When_CustomTemplateSet_Expect_Saved() throws Exception {
//
//        final String templateName = "test_" + System.currentTimeMillis();
//        final int docId = createdTextDoc.getId();
//
//        final File templateDirectory = templateService.getTemplateDirectory();
//        final File templateFile = new File(templateDirectory, templateName + ".jsp");
//
//        try {
//            assertTrue(templateFile.createNewFile());
//
//            final TemplateDTO template = new TemplateDTO(templateName, false);
//            templateService.save(template);
//
//            final TextDocumentTemplateDTO templateDTO = new TextDocumentTemplateDTO(docId, templateName, templateName);
//
//            final TextDocumentTemplate savedTemplate = textDocumentTemplateService.save(templateDTO);
//            assertNotNull(savedTemplate);
//
//            createdTextDoc.setTemplate(templateDTO);
//
//            performPostWithContentExpectOk(createdTextDoc);
//
//            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                    .param("docId", "" + createdTextDoc.getId());
//
//            performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdTextDoc));
//
//        } finally {
//            assertTrue(templateFile.delete());
//        }
//    }
//
//    @Test
//    public void delete_When_UserIsNotAdmin_Expect_NoPermissionToEditDocumentException() throws Exception {
//        final UserDomainObject user = new UserDomainObject(1);
//        user.addRoleId(RoleId.USERS);
//        Imcms.setUser(user); // means current user is not admin now
//
//        performDeleteWithContentExpectException(createdTextDoc, NotImplementedException.class);
//        // todo: change when will be implemented to this: NoPermissionToEditDocumentException.class);
//    }
//
//    @Test
//    public void delete_When_DocumentExistAndUserIsAdmin_Expect_NoError() throws Exception {
//
//        performDeleteWithContentExpectException(createdTextDoc, NotImplementedException.class);
//        // todo: change when will be implemented to this:
////        final MockHttpServletRequestBuilder requestBuilder = getDeleteRequestBuilderWithContent(createdDoc);
////        performRequestBuilderExpectedOk(requestBuilder);
//    }
//
//    // todo: uncomment when docs deletion will be needed
////    @Test
////    public void delete_When_DocumentExistAndUserIsAdmin_Expect_DocumentNotExistExceptionAfterDeletion() throws Exception {
////        delete_When_DocumentExistAndUserIsAdmin_Expect_NoError();
////
////        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
////                .param("docId", "" + createdDoc.getId());
////
////        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);
////    }
//
//    @Test
//    public void createNew_When_FileDocTypeAndParentDocIdSet_Expect_NewFileDoc() throws Exception {
//        final Meta.DocumentType documentType = Meta.DocumentType.FILE;
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("type", "" + documentType)
//                .param("parentDocId", "" + createdFileDoc.getId());
//
//        final String response = getJsonResponse(requestBuilder);
//        final FileDocumentDTO documentDTO = fromJson(response, FileDocumentDTO.class);
//
//        assertNull(documentDTO.getId());
//        assertEquals(documentDTO.getType(), documentType);
//        assertFalse(documentDTO.getCommonContents().isEmpty());
//        assertEquals(documentDTO.getPublicationStatus(), Meta.PublicationStatus.NEW);
//        assertTrue(documentDTO.getFiles().isEmpty());
//    }
//
//    @Test
//    public void getFileDocument_When_Exist_Expect_Found() throws Exception {
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdFileDoc.getId());
//
//        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(createdFileDoc));
//    }
//
//    @Test
//    public void save_When_CustomFileSet_Expect_Saved() throws Exception {
//        final String testName = "test_name";
//
//        final List<DocumentFileDTO> files = createdFileDoc.getFiles();
//
//        final List<DocumentFileDTO> newFiles = IntStream.range(0, 5).mapToObj(value -> {
//            final DocumentFileDTO documentFileDTO = new DocumentFileDTO();
//            documentFileDTO.setDocId(createdFileDoc.getId());
//            documentFileDTO.setFilename(testName + value);
//            documentFileDTO.setFileId(testName + value);
//            documentFileDTO.setDefaultFile(value == 0);
//            documentFileDTO.setMimeType("test");
//
//            return documentFileDTO;
//        }).collect(Collectors.toList());
//
//        files.addAll(newFiles);
//
//        performPostWithContentExpectOk(createdFileDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdFileDoc.getId());
//
//        final String response = getJsonResponse(requestBuilder);
//        final FileDocumentDTO documentDTO = fromJson(response, FileDocumentDTO.class);
//
//        assertNotNull(documentDTO);
//        assertNotNull(documentDTO.getFiles());
//        assertFalse(documentDTO.getFiles().isEmpty());
//        assertEquals(documentDTO.getFiles().size(), files.size());
//
//        for (DocumentFileDTO fileDTO : documentDTO.getFiles()) {
//            assertEquals(fileDTO.getDocId(), createdFileDoc.getId());
//            assertTrue(fileDTO.getFilename().contains(testName));
//        }
//    }
//
//    @Test
//    public void save_When_DocumentHaveFilesAndNewFilesSet_Expect_NewSavedAndOldRemoved() throws Exception {
//        final String testName = "test_name";
//
//        final List<DocumentFileDTO> oldFiles = createdFileDoc.getFiles();
//
//        final List<DocumentFileDTO> newFiles = IntStream.range(0, 5)
//                .mapToObj(value -> {
//                    final DocumentFileDTO documentFileDTO = new DocumentFileDTO();
//                    documentFileDTO.setDocId(createdFileDoc.getId());
//                    documentFileDTO.setFilename(testName + value);
//                    documentFileDTO.setFileId(testName + value);
//                    documentFileDTO.setDefaultFile(value == 0);
//                    documentFileDTO.setMimeType("test");
//
//                    return documentFileDTO;
//                })
//                .collect(Collectors.toList());
//
//        createdFileDoc.setFiles(newFiles);
//
//        performPostWithContentExpectOk(createdFileDoc);
//
//        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdFileDoc.getId());
//
//        final String response = getJsonResponse(requestBuilder);
//        final FileDocumentDTO documentDTO = fromJson(response, FileDocumentDTO.class);
//
//        assertNotNull(documentDTO);
//        assertNotNull(documentDTO.getFiles());
//        assertFalse(documentDTO.getFiles().isEmpty());
//        assertFalse(documentDTO.getFiles().containsAll(oldFiles));
//        assertEquals(documentDTO.getFiles().size(), newFiles.size());
//
//        for (DocumentFileDTO fileDTO : documentDTO.getFiles()) {
//            assertEquals(fileDTO.getDocId(), createdFileDoc.getId());
//            assertTrue(fileDTO.getFilename().contains(testName));
//        }
//    }
//
//    @Test
//    public void saveFileDoc_When_FilesAlreadyExistAndDefaultFileChanged_Expect_Saved() throws Exception {
//        List<DocumentFileDTO> oldFiles = createdFileDoc.getFiles();
//
//        assertFalse(oldFiles.isEmpty());
//
//        oldFiles.get(0).setDefaultFile(true); // first is set as default
//
//        performPostWithContentExpectOk(createdFileDoc);
//
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + createdFileDoc.getId());
//
//        String response = getJsonResponse(requestBuilder);
//        FileDocumentDTO documentDTO = fromJson(response, FileDocumentDTO.class);
//        final String newDefaultFileName = "default_file";
//
//        oldFiles = documentDTO.getFiles();
//
//        for (DocumentFileDTO oldFile : oldFiles) {
//            oldFile.setDefaultFile(false); // all old files set as not default now
//        }
//
//        final DocumentFileDTO newFile = new DocumentFileDTO();
//        newFile.setDocId(documentDTO.getId());
//        newFile.setFilename(newDefaultFileName);
//        newFile.setDefaultFile(true); // new file is default
//        newFile.setMimeType("test");
//
//        oldFiles.add(newFile);
//
//        performPostWithContentExpectOk(documentDTO);
//
//        requestBuilder = MockMvcRequestBuilders.get(controllerPath())
//                .param("docId", "" + documentDTO.getId());
//
//        response = getJsonResponse(requestBuilder);
//        documentDTO = fromJson(response, FileDocumentDTO.class);
//
//        assertFalse(documentDTO.getFiles().isEmpty());
//
//        final List<DocumentFileDTO> defaultFiles = documentDTO.getFiles()
//                .stream()
//                .filter(DocumentFileDTO::isDefaultFile)
//                .collect(Collectors.toList());
//
//        assertFalse(defaultFiles.isEmpty());
//        assertEquals(defaultFiles.size(), 1);
//
//        final DocumentFileDTO defaultFile = defaultFiles.get(0);
//
//        assertNotNull(defaultFile);
//        assertTrue(defaultFile.isDefaultFile());
//        assertEquals(newDefaultFileName, defaultFile.getFilename());
//    }
//
//    @Test
//    public void copyTextDocument_Expect_Copied() throws Exception {
//        final int documentTemplateSizeBeforeSaving = textDocumentTemplateRepository.findAll().size();
//
//        copyChecking(createdTextDoc.getId());
//
//        final int documentTemplateSizeAfterSaving = textDocumentTemplateRepository.findAll().size();
//
//        assertThat(documentTemplateSizeAfterSaving, is(documentTemplateSizeBeforeSaving + 1));
//    }
//
//    @Test
//    public void copyFileDocument_Expect_Copied() throws Exception {
//        final int documentFileSizeBeforeSaving = documentFileRepository.findAll().size();
//
//        copyChecking(createdFileDoc.getId());
//
//        final int documentFileSizeAfterSaving = documentFileRepository.findAll().size();
//
//        assertThat(documentFileSizeAfterSaving,
//                is(documentFileSizeBeforeSaving + createdFileDoc.getFiles().size()));
//    }
//
//    @Test
//    public void copyUrlDocument_Expect_Copied() throws Exception {
//        final int documentUrlSizeBeforeSaving = documentUrlRepository.findAll().size();
//
//        copyChecking(createdUrlDoc.getId());
//
//        final int documentUrlSizeAfterSaving = documentUrlRepository.findAll().size();
//
//        assertThat(documentUrlSizeAfterSaving, is(documentUrlSizeBeforeSaving + 1));
//    }
//
//    private void copyChecking(int docId) throws Exception {
//        final int documentNumberBeforeSaving = metaRepository.findAll().size();
//
//        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
//                .post(controllerPath() + "/copy/" + docId);
//
//        performRequestBuilderExpectedOk(builder);
//
//        final int documentNumberAfterSaving = metaRepository.findAll().size();
//
//        assertThat(documentNumberAfterSaving, is(documentNumberBeforeSaving + 1));
//    }
}
