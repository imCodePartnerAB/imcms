package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.DocumentPageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.document.index.DocumentStoredFields;
import imcode.server.document.index.IndexSearchResult;
import imcode.server.document.index.ResolvingQueryIndex;
import org.apache.solr.common.SolrDocumentList;
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
    public List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery) {
        return searchDocuments(searchQuery, true);
    }

    @Override
    public SolrDocumentList searchDocumentsReturnSolrDocumentList(SearchQueryDTO searchQuery, boolean limitSearch) {
        return documentIndex.search(searchQuery, limitSearch).solrDocumentList();
    }

    @Override
    public SolrDocumentList searchDocumentsReturnSolrDocumentList(SearchQueryDTO searchQuery) {
        return searchDocumentsReturnSolrDocumentList(searchQuery, true);
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String stringSearchQuery, boolean limitSearch) {
        return mapToDocumentStoredFieldsDTO(documentIndex.search(stringSearchQuery, limitSearch));
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String stringSearchQuery) {
        return searchDocuments(stringSearchQuery, true);
    }

    @Override
    public SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, boolean limitSearch) {
        return documentIndex.search(stringSearchQuery, limitSearch).solrDocumentList();
    }

    @Override
    public SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery) {
        return searchDocumentsReturnSolrDocumentList(stringSearchQuery, true);
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String stringSearchQuery, DocumentPageRequestDTO page, boolean limitSearch) {
        return mapToDocumentStoredFieldsDTO(documentIndex.search(stringSearchQuery, page, limitSearch));
    }

    @Override
    public List<DocumentStoredFieldsDTO> searchDocuments(String stringSearchQuery, DocumentPageRequestDTO page) {
        return searchDocuments(stringSearchQuery, page, true);
    }

    @Override
    public SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, DocumentPageRequestDTO page, boolean limitSearch) {
        return documentIndex.search(stringSearchQuery, page, limitSearch).solrDocumentList();
    }

    @Override
    public SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, DocumentPageRequestDTO page) {
        return searchDocumentsReturnSolrDocumentList(stringSearchQuery, page, true);
    }

    private List<DocumentStoredFieldsDTO> mapToDocumentStoredFieldsDTO(IndexSearchResult<DocumentStoredFields> result){
        return result.storedFieldsList()
                .stream()
                .map(DocumentStoredFieldsDTO::new)
                .collect(Collectors.toList());
    }
}
