package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;

import java.util.List;

public interface SearchDocumentService {

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, PageRequestDTO page, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, PageRequestDTO page);
}
