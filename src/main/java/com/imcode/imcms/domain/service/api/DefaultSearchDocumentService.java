package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.Imcms;
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
    public List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery) {
        return documentIndex.search(searchQuery, Imcms.getUser())
                .documentStoredFieldsList()
                .stream()
                .map(DocumentStoredFieldsDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery) {
        return documentIndex.search(searchQuery, Imcms.getUser())
                .documentStoredFieldsList()
                .stream()
                .map(DocumentStoredFieldsDTO::new)
                .collect(Collectors.toList());
    }
}
