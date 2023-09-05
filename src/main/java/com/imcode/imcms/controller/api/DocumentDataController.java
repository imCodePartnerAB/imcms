package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.domain.dto.DocumentDataDTO;
import com.imcode.imcms.domain.service.DocumentDataService;
import com.imcode.imcms.mapping.DocumentVersionMapper;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.CheckAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentDataController {

    private final DocumentDataService documentDataService;
    private final DocumentVersionMapper documentVersionMapper;

    public DocumentDataController(DocumentDataService documentDataService,
                                  DocumentVersionMapper documentVersionMapper) {
        this.documentDataService = documentDataService;
        this.documentVersionMapper = documentVersionMapper;
    }

    @GetMapping("/all-data/{id}")
    @CheckAccess(docPermission = AccessContentType.DOC_INFO)
    public DocumentDataDTO getAllData(@PathVariable Integer id) {
        return documentDataService.getDataByDocIdAndAvailableLangs(id);
    }

    @GetMapping("/versions/{id}")
    @CheckAccess
    public List<DocumentVersion> getAllVersions(@PathVariable Integer id) {
        return documentVersionMapper.getAll(id);
    }

}
