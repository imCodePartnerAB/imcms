package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.document.index.IndexSearchResult;
import imcode.server.document.index.ResolvingQueryIndex;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultSearchDocumentService implements SearchDocumentService {

    private final ResolvingQueryIndex documentIndex;

    DefaultSearchDocumentService(ResolvingQueryIndex documentIndex) {
        this.documentIndex = documentIndex;
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery, boolean limitSearch) {
        return mapToDocumentStoredFieldsDTO(documentIndex.search(searchQuery, limitSearch));
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String stringSearchQuery, boolean limitSearch) {
        return mapToDocumentStoredFieldsDTO(documentIndex.search(stringSearchQuery, limitSearch));
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String stringSearchQuery, PageRequestDTO page, boolean limitSearch) {
        return mapToDocumentStoredFieldsDTO(documentIndex.search(stringSearchQuery, page, limitSearch));
    }

    private List<DocumentStoredFieldsDTO> mapToDocumentStoredFieldsDTO(IndexSearchResult result){
        return result.documentStoredFieldsList()
                .stream()
                .map(DocumentStoredFieldsDTO::new)
                .collect(Collectors.toList());
    }
}
