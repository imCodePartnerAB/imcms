package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.DocumentDataDTO;
import com.imcode.imcms.domain.service.DocumentDataService;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
public class DocumentDataController {

    private final DocumentDataService documentDataService;

    public DocumentDataController(DocumentDataService documentDataService) {
        this.documentDataService = documentDataService;
    }

    @GetMapping("/all-data/{id}")
    @CheckAccess(docPermission = AccessContentType.DOC_INFO)
    public DocumentDataDTO getAllData(@PathVariable Integer id) {
        return documentDataService.getDataByDocId(id);
    }


}
