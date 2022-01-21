package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    SearchDocumentController(SearchDocumentService searchDocumentService) {
        this.searchDocumentService = searchDocumentService;
    }

    @GetMapping
    public List<DocumentStoredFieldsDTO> getDocuments(SearchQueryDTO searchQuery) {
        return searchDocumentService.searchDocuments(searchQuery);
    }

    @GetMapping("/{id}")
    public DocumentStoredFieldsDTO getDocument(@PathVariable Integer id){
        return searchDocumentService.searchDocuments("id:" + id).get(0);
    }
}