package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.index.DocumentStoredFields;
import imcode.server.document.index.IndexSearchResult;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.user.UserDomainObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchDocumentServiceTest {

    @Mock
    private ResolvingQueryIndex documentIndex;

    @Mock
    private IndexSearchResult indexSearchResult;

    @InjectMocks
    private DefaultSearchDocumentService searchDocumentService;

    @Test
    public void getTextDocuments_When_DefaultSearchQueryIsUsing_Expect_AllReturned() {

        final int documentNumber = 5;

        final List<DocumentStoredFieldsDTO> expected = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFieldsDTO docFields = new DocumentStoredFieldsDTO();
            docFields.setId(i);
            docFields.setTitle("test_headline" + i);
            docFields.setType(Meta.DocumentType.TEXT);
            docFields.setDocumentStatus(DocumentStatus.PUBLISHED);
            docFields.setAlias("test_alias" + i);
            expected.add(docFields);
        }

        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

        when(documentIndex.search(eq(searchQueryDTO), Mockito.any(UserDomainObject.class)))
                .thenReturn(indexSearchResult);

        final List<DocumentStoredFields> documentStoredFieldsList = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFields mock = mock(DocumentStoredFields.class);
            documentStoredFieldsList.add(mock);

            when(mock.id()).thenReturn(expected.get(i).getId());
            when(mock.headline()).thenReturn(expected.get(i).getTitle());
            when(mock.documentType()).thenReturn(expected.get(i).getType());
            when(mock.documentStatus()).thenReturn(expected.get(i).getDocumentStatus());
            when(mock.alias()).thenReturn(expected.get(i).getAlias());
        }

        when(indexSearchResult.documentStoredFieldsList())
                .thenReturn(documentStoredFieldsList);

        final List<DocumentStoredFieldsDTO> actual = searchDocumentService.searchDocuments(searchQueryDTO);

        assertThat(actual, hasSize(documentNumber));
        assertThat(actual, containsInAnyOrder(expected.toArray()));

        verify(documentIndex, times(1))
                .search(eq(searchQueryDTO), Mockito.any(UserDomainObject.class));

        verify(indexSearchResult, times(1))
                .documentStoredFieldsList();
    }
}
