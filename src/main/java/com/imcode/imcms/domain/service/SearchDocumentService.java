package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;

import java.util.List;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.12.17.
 */
public interface SearchDocumentService {
    List<DocumentDTO> searchDocuments(SearchQueryDTO searchQuery);
}
