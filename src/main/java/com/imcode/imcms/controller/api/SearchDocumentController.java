package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.api.SearchDocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dmizem from Ubrainians for imCode on 19.10.17.
 */
@RestController
@RequestMapping("/documents/search")
public class SearchDocumentController {

    private final SearchDocumentService searchDocumentService;

    public SearchDocumentController(SearchDocumentService searchDocumentService) {
        this.searchDocumentService = searchDocumentService;
    }

    @GetMapping
    public List<DocumentDTO> getDocuments(SearchQueryDTO searchQuery) {
        return searchDocumentService.searchDocuments(searchQuery);
    }

}
