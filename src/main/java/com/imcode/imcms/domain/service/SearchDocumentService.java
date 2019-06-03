package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;

import java.util.List;

public interface SearchDocumentService {

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery);

}
