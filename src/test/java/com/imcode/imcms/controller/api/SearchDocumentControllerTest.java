package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class SearchDocumentControllerTest extends MockingControllerTest {

    private static final String CONTROLLER_PATH = "/documents/search";

    @Mock
    private SearchDocumentService searchDocumentService;

    @InjectMocks
    private SearchDocumentController searchDocumentController;

    @Override
    protected Object controllerToMock() {
        return searchDocumentController;
    }

    @Test
    void getTextDocument_When_DefaultSearchQuery_Expect_JsonIsReturned() throws Exception {

        final SearchQueryDTO searchQuery = new SearchQueryDTO();

        final DocumentStoredFieldsDTO expected = new DocumentStoredFieldsDTO();
        expected.setId(1);
        expected.setTitle("test_title");
        expected.setType(Meta.DocumentType.TEXT);
        expected.setDocumentStatus(DocumentStatus.PUBLISHED);
        expected.setAlias("test_alias");

        given(searchDocumentService.searchDocuments(searchQuery)).willReturn(Collections.singletonList(expected));

        perform(get(CONTROLLER_PATH)).andExpectAsJson(new DocumentStoredFieldsDTO[]{expected});

        then(searchDocumentService).should().searchDocuments(searchQuery);
    }
}
