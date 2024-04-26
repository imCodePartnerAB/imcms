package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.DocumentPageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import imcode.server.document.index.IndexSearchResult;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static imcode.server.document.LifeCyclePhase.PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SearchDocumentServiceTest {

    private final static Integer DEFAULT_WORKING_VERSION = 0;
    private final static Integer DEFAULT_LATEST_VERSION = 1;

    @Mock
    private ResolvingQueryIndex documentIndex;

    @Mock
    private IndexSearchResult indexSearchResult;

    @InjectMocks
    private DefaultSearchDocumentService searchDocumentService;

    @BeforeEach
    void setUp() {
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);
    }

    @Test
    void getTextDocuments_given_DefaultSearchQueryIsUsing_Expect_AllReturned() {

        final int documentNumber = 5;
        final List<DocumentStoredFieldsDTO> expected = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFieldsDTO docFields = new DocumentStoredFieldsDTO();
            docFields.setId(i);
            docFields.setTitle("test_headline" + i);
            docFields.setType(Meta.DocumentType.TEXT);
            docFields.setDocumentStatus(DocumentStatus.PUBLISHED);
            docFields.setAlias("test_alias" + i);
            docFields.setCurrentVersion(DEFAULT_LATEST_VERSION);
            docFields.setIsShownTitle(false);
	        docFields.setDefaultLanguageAliasEnabled(false);
	        expected.add(docFields);
        }

        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

        final boolean limitSearch = true;
        given(documentIndex.search(eq(searchQueryDTO), eq(limitSearch)))
                .willReturn(indexSearchResult);

        final List<DocumentStoredFields> documentStoredFieldsList = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFields mock = mock(DocumentStoredFields.class);
            documentStoredFieldsList.add(mock);

            given(mock.id()).willReturn(expected.get(i).getId());
            given(mock.headline()).willReturn(expected.get(i).getTitle());
            given(mock.documentType()).willReturn(expected.get(i).getType());
            given(mock.documentStatus()).willReturn(expected.get(i).getDocumentStatus());
            given(mock.alias()).willReturn(expected.get(i).getAlias());
            given(mock.versionNo()).willReturn(expected.get(i).getCurrentVersion());
        }

        given(indexSearchResult.storedFieldsList()).willReturn(documentStoredFieldsList);

        final List<DocumentStoredFieldsDTO> actual = searchDocumentService.searchDocuments(searchQueryDTO, limitSearch);

        assertEquals(actual.size(), documentNumber);
        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));

        then(documentIndex).should().search(eq(searchQueryDTO), eq(limitSearch));
        then(indexSearchResult).should().storedFieldsList();
    }

    @Test
    void getTextDocuments_given_SearchByQuery_Expect_AllReturned() {
        final int documentNumber = 5;
        final List<DocumentStoredFieldsDTO> expected = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFieldsDTO docFields = new DocumentStoredFieldsDTO();
            docFields.setTitle("test_headline" + i);
            docFields.setId(i);
            docFields.setType(Meta.DocumentType.TEXT);
            docFields.setDocumentStatus(DocumentStatus.PUBLISHED);
            docFields.setAlias("test_alias" + i);
            docFields.setCurrentVersion(DEFAULT_LATEST_VERSION);
	        docFields.setIsShownTitle(false);
	        docFields.setDefaultLanguageAliasEnabled(false);
	        expected.add(docFields);
        }

        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED + " " +
                "+text" + DocumentIndex.FIELD__TEXT + ":" + PUBLISHED + " " +
                "+version_no:" + DocumentIndex.FIELD__VERSION_NO +
                "+(category_id:" + DocumentIndex.FIELD__CATEGORY_ID + " category_id:" + DocumentIndex.FIELD__CATEGORY_ID + ")";

        final boolean limitSearch = true;
        given(documentIndex.search(eq(query), eq(limitSearch)))
                .willReturn(indexSearchResult);

        final List<DocumentStoredFields> documentStoredFieldsList = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFields mock = mock(DocumentStoredFields.class);
            documentStoredFieldsList.add(mock);

            given(mock.headline()).willReturn(expected.get(i).getTitle());
            given(mock.id()).willReturn(expected.get(i).getId());
            given(mock.documentType()).willReturn(expected.get(i).getType());
            given(mock.documentStatus()).willReturn(expected.get(i).getDocumentStatus());
            given(mock.alias()).willReturn(expected.get(i).getAlias());
            given(mock.versionNo()).willReturn(expected.get(i).getCurrentVersion());
        }

        given(indexSearchResult.storedFieldsList()).willReturn(documentStoredFieldsList);

        final List<DocumentStoredFieldsDTO> actual = searchDocumentService.searchDocuments(query, limitSearch);

        assertEquals(actual.size(), documentNumber);
        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));

        then(documentIndex).should().search(eq(query), eq(limitSearch));
        then(indexSearchResult).should().storedFieldsList();

    }

    @Test
    void getTextDocuments_given_SearchByQueryAndPage_Expect_AllReturned() {
        final int documentNumber = 5;
        final List<DocumentStoredFieldsDTO> expected = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFieldsDTO docFields = new DocumentStoredFieldsDTO();
            docFields.setTitle("test_headline" + i);
            docFields.setId(i);
            docFields.setType(Meta.DocumentType.TEXT);
            docFields.setDocumentStatus(DocumentStatus.PUBLISHED);
            docFields.setAlias("test_alias" + i);
            docFields.setCurrentVersion(DEFAULT_LATEST_VERSION);
	        docFields.setIsShownTitle(false);
	        docFields.setDefaultLanguageAliasEnabled(false);
	        expected.add(docFields);
        }

        final String query = "+doc_type_id:" + TextDocument.TYPE_ID + " " +
                "+status:" + Document.PublicationStatus.APPROVED + " " +
                "+text" + DocumentIndex.FIELD__TEXT + ":" + PUBLISHED + " " +
                "+version_no:" + DocumentIndex.FIELD__VERSION_NO +
                "+(category_id:" + DocumentIndex.FIELD__CATEGORY_ID + " category_id:" + DocumentIndex.FIELD__CATEGORY_ID + ")";

        final DocumentPageRequestDTO page = new DocumentPageRequestDTO(DocumentIndex.FIELD__META_HEADLINE, Sort.Direction.ASC, 0, 100);

        final boolean limitSearch = true;
        given(documentIndex.search(eq(query), eq(page), eq(limitSearch)))
                .willReturn(indexSearchResult);

        final List<DocumentStoredFields> documentStoredFieldsList = new ArrayList<>();

        for (int i = 0; i < documentNumber; i++) {
            final DocumentStoredFields mock = mock(DocumentStoredFields.class);
            documentStoredFieldsList.add(mock);

            given(mock.headline()).willReturn(expected.get(i).getTitle());
            given(mock.id()).willReturn(expected.get(i).getId());
            given(mock.documentType()).willReturn(expected.get(i).getType());
            given(mock.documentStatus()).willReturn(expected.get(i).getDocumentStatus());
            given(mock.alias()).willReturn(expected.get(i).getAlias());
            given(mock.versionNo()).willReturn(expected.get(i).getCurrentVersion());
        }

        given(indexSearchResult.storedFieldsList()).willReturn(documentStoredFieldsList);

        final List<DocumentStoredFieldsDTO> actual = searchDocumentService.searchDocuments(query, page, limitSearch);

        assertEquals(actual.size(), documentNumber);
        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));

        then(documentIndex).should().search(eq(query), eq(page), eq(limitSearch));
        then(indexSearchResult).should().storedFieldsList();
    }
}
