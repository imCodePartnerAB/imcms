package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class SearchDocumentControllerTest {

    private static final String CONTROLLER_PATH = "/documents/search";

    @Mock
    private SearchDocumentService searchDocumentService;

    @InjectMocks
    private SearchDocumentController searchDocumentController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchDocumentController).build();
    }

    @Test
    public void getTextDocument_When_DefaultSearchQuery_Expect_JsonIsReturned() throws Exception {

        final SearchQueryDTO searchQuery = new SearchQueryDTO();

        final DocumentStoredFieldsDTO expected = new DocumentStoredFieldsDTO();
        expected.setId(1);
        expected.setTitle("test_title");
        expected.setType(Meta.DocumentType.TEXT);
        expected.setDocumentStatus(DocumentStatus.PUBLISHED);
        expected.setAlias("test_alias");

        when(searchDocumentService.searchDocuments(searchQuery))
                .thenReturn(Collections.singletonList(expected));

        mockMvc.perform(get(CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expected.getId())))
                .andExpect(jsonPath("$[0].title", is(expected.getTitle())))
                .andExpect(jsonPath("$[0].documentStatus", is(expected.getDocumentStatus().toString())))
                .andExpect(jsonPath("$[0].type", is(expected.getType().toString())))
                .andExpect(jsonPath("$[0].alias", is(expected.getAlias())));
    }
}
