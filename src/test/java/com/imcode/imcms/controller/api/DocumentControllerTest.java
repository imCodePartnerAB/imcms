package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest extends MockingControllerTest {

    private static final String CONTROLLER_PATH = "/documents";

    @Mock
    private DelegatingByTypeDocumentService documentService;
	@Mock
	private DocumentMapper documentMapper;
    @Mock
    private List<Integer> deleteProtectedMetaIds;

    @InjectMocks
    private DocumentController documentController;

    @Override
    protected Object controllerToMock() {
        return documentController;
    }

    @Test
    void get_When_DocIdIsNullAndParentDocIdAvailable_Expect_NewDocumentCreated() {
        final Meta.DocumentType documentType = Meta.DocumentType.TEXT;
        final int parentDocId = 13;
        final DocumentDTO newDoc = new DocumentDTO();

	    given(documentMapper.toDocumentId(String.valueOf(parentDocId))).willReturn(parentDocId);
	    given(documentService.createNewDocument(documentType, parentDocId)).willReturn(newDoc);

        final RequestBuilder requestBuilder = get(CONTROLLER_PATH)
                .param("type", documentType.toString())
		        .param("parentDocIdentity", "" + parentDocId);

        perform(requestBuilder).andExpectAsJson(newDoc);
    }

	@Test
	void get_When_DocIdIsNullAndParentDocAliasExists_Expect_NewDocumentCreated(){
		final Meta.DocumentType documentType = Meta.DocumentType.TEXT;
		final int parentDocId = 13;
		final String parentDocAlias = "ALIAS";
		final DocumentDTO newDoc = new DocumentDTO();

		given(documentMapper.toDocumentId(parentDocAlias)).willReturn(parentDocId);
		given(documentService.createNewDocument(documentType, parentDocId)).willReturn(newDoc);

		final RequestBuilder requestBuilder = get(CONTROLLER_PATH)
				.param("type", documentType.toString())
				.param("parentDocIdentity", parentDocAlias);

		perform(requestBuilder).andExpectAsJson(newDoc);
	}

    @Test
    void get_When_DocIdIsNotNull_Expect_DocumentReceivedById() {
        final int docId = 13;
        final FileDocumentDTO existingDoc = new FileDocumentDTO();

        given(documentService.get(docId)).willReturn(existingDoc);

        final RequestBuilder requestBuilder = get(CONTROLLER_PATH)
                .param("docId", "" + docId);

        perform(requestBuilder).andExpectAsJson(existingDoc);
    }

    @Test
    void copy_When_DocIdIsNotNull_Expect_CopyReturned() {
        final int docId = 13;
        final TextDocumentDTO copy = new TextDocumentDTO();

        given(documentService.copy(docId)).willReturn(copy);

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH.concat("/copy/" + docId))
                .param("docId", "" + docId);

        perform(requestBuilder).andExpectAsJson(copy);
    }

    @Test
    void create_When_DocIdIsNotNull_Expect_CopyReturned() {
        final UberDocumentDTO saveMe = new UberDocumentDTO();

        given(documentService.save(saveMe)).willReturn(saveMe);

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(saveMe));

        perform(requestBuilder).andExpectAsJson(saveMe);
    }

    @Test
    void update_When_DocumentHasNotAnyPermissions_Expect_NecessaryDataIsSetAutomatically() {
        Imcms.setUser(new UserDomainObject());

        final int documentId = 1;
        final UberDocumentDTO updateMe = new UberDocumentDTO();
        updateMe.setId(documentId);

        final UberDocumentDTO savedDocument = new UberDocumentDTO();
        savedDocument.setId(documentId);
        savedDocument.setRoleIdToPermission(Collections.emptyMap());

        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("key", "value");
        savedDocument.setProperties(propertiesMap);

        given(documentService.get(documentId)).willReturn(savedDocument);

        final RequestBuilder requestBuilder = put(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(updateMe));

        updateMe.setProperties(savedDocument.getProperties());
        given(documentService.save(updateMe)).willReturn(updateMe);

        perform(requestBuilder).andExpectAsJson(updateMe);
    }

    @Test
    void update_When_UserIsSuperAdmin_Expect_SaveDocument() {
        final UserDomainObject user = new UserDomainObject();
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        final int documentId = 1;
        final UberDocumentDTO updateMe = new UberDocumentDTO();
        updateMe.setId(documentId);

        final UberDocumentDTO savedDocument = new UberDocumentDTO();
        savedDocument.setId(documentId);
        savedDocument.setRoleIdToPermission(Collections.emptyMap());

        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("key", "value");
        savedDocument.setProperties(propertiesMap);

        given(documentService.get(documentId)).willReturn(savedDocument);

        final RequestBuilder requestBuilder = put(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(updateMe));

        given(documentService.save(updateMe)).willReturn(updateMe);

        perform(requestBuilder).andExpectAsJson(updateMe);
    }

    @Test
    void update_When_DocumentAndUserHaveEditPermission_Expect_SaveDocument() {
        final UserDomainObject user = new UserDomainObject();
        user.addRoleId(2);
        Imcms.setUser(user);

        final int documentId = 1;
        final UberDocumentDTO updateMe = new UberDocumentDTO();
        updateMe.setId(documentId);

        final UberDocumentDTO savedDocument = new UberDocumentDTO();
        savedDocument.setId(documentId);

        Map<Integer, Meta.Permission> permissionsMap = new HashMap<>();
        permissionsMap.put(2, Meta.Permission.EDIT);
        savedDocument.setRoleIdToPermission(permissionsMap);

        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("key", "value");
        savedDocument.setProperties(propertiesMap);

        given(documentService.get(documentId)).willReturn(savedDocument);

        final RequestBuilder requestBuilder = put(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(updateMe));

        given(documentService.save(updateMe)).willReturn(updateMe);

        perform(requestBuilder).andExpectAsJson(updateMe);
    }

    @Test
    void delete_Expect_DeleteMethodCalledWithCorrectDocId() {
        final int docId = 1234;
        when(deleteProtectedMetaIds.contains(docId)).thenReturn(false);

        perform(delete(CONTROLLER_PATH + '/' + docId)).andExpect(status().isOk());

        then(documentService).should().deleteByDocId(docId);
    }

    @Test
    void delete_When_deleteProtectedMetaIdsContainDocId_Expect_BadRequest_And_DeleteMethodNotCalled() throws Exception {
        final int docId = 1111;
        when(deleteProtectedMetaIds.contains(docId)).thenReturn(true);

        perform(delete(CONTROLLER_PATH + '/' + docId)).andExpect(status().isBadRequest())
                .andExpect(content().string(String.valueOf(docId)));

        verify(documentService, never()).deleteByDocId(docId);
    }

    @Test
    void deleteAll_Expect_DeleteMethodCalledWithCorrectDocIds() {
        final List<Integer> docIds = List.of(1234, 1235);
        when(deleteProtectedMetaIds.contains(docIds.get(0))).thenReturn(false);
        when(deleteProtectedMetaIds.contains(docIds.get(1))).thenReturn(false);

        perform(delete(CONTROLLER_PATH + "/deleteAll"), docIds)
                .andExpect(status().isOk());

        then(documentService).should().deleteByIds(docIds);
    }

    @Test
    void deleteAll_When_deleteProtectedMetaIdsContainDocId_Expect_BadRequest_And_DeleteMethodNotCalled() throws Exception {
        final List<Integer> docIds = List.of(1112, 1234, 1111, 1235);
        when(deleteProtectedMetaIds.contains(docIds.get(0))).thenReturn(true);
        when(deleteProtectedMetaIds.contains(docIds.get(1))).thenReturn(false);
        when(deleteProtectedMetaIds.contains(docIds.get(2))).thenReturn(true);
        when(deleteProtectedMetaIds.contains(docIds.get(3))).thenReturn(false);

        final List<Integer> protectedDocIds = List.of(1111, 1112);

        perform(delete(CONTROLLER_PATH + "/deleteAll"), docIds)
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJson(protectedDocIds)));

        verifyNoInteractions(documentService);
    }
}
