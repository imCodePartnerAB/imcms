package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;

public interface SearchDocumentService {

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(SearchQueryDTO searchQuery, boolean limitSearch);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(SearchQueryDTO searchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, boolean limitSearch);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, PageRequestDTO page, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, PageRequestDTO page);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, PageRequestDTO page, boolean limitSearch);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, PageRequestDTO page);
}
