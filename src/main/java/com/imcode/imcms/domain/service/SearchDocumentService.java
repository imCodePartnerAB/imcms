package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;

import java.util.List;

public interface SearchDocumentService {

    List<DocumentDTO> searchDocuments(SearchQueryDTO searchQuery);

}
