package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.util.NestedServletException;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class DocumentControllerTest extends MockingControllerTest {

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
    void get_When_DocIdIsNull_Expect_NewDocumentCreated() throws Exception {
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
    void get_When_DocIdIsNotNull_Expect_DocumentReceivedById() throws Exception {
        final int docId = 13;
        final FileDocumentDTO existingDoc = new FileDocumentDTO();

        given(documentService.get(docId)).willReturn(existingDoc);

        final RequestBuilder requestBuilder = get(controllerPath())
                .param("docId", "" + docId);

        perform(requestBuilder).andExpectAsJson(existingDoc);
    }

    @Test
    void copy_When_DocIdIsNotNull_Expect_CopyReturned() throws Exception {
        final int docId = 13;
        final TextDocumentDTO copy = new TextDocumentDTO();

        given(documentService.copy(docId)).willReturn(copy);

        final RequestBuilder requestBuilder = post(controllerPath().concat("/copy/" + docId))
                .param("docId", "" + docId);

        perform(requestBuilder).andExpectAsJson(copy);
    }

    @Test
    void save_When_DocIdIsNotNull_Expect_CopyReturned() throws Exception {
        final UberDocumentDTO saveMe = new UberDocumentDTO();

        given(documentService.save(saveMe)).willReturn(saveMe);

        final RequestBuilder requestBuilder = post(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(saveMe));

        perform(requestBuilder).andExpectAsJson(saveMe);
    }

    @Test
    void delete_When_DeletingDisabled_Expect_NotImplementedException() throws Throwable {
        final UberDocumentDTO deleteMe = new UberDocumentDTO();

        final RequestBuilder requestBuilder = delete(controllerPath())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJson(deleteMe));

        Assertions.assertThrows(NotImplementedException.class, () -> {
            try {
                perform(requestBuilder);

            } catch (NestedServletException e) {
                throw e.getCause(); // real cause
            }
        });
    }
}
