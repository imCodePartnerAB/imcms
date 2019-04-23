package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.TemporalDataService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/temporal-data")
public class TemporalDataController {

    private final TemporalDataService temporalDataService;

    TemporalDataController(TemporalDataService temporalDataService) {
        this.temporalDataService = temporalDataService;
    }

    @GetMapping("/document-index")
    public void rebuildDocumentIndex() {
        temporalDataService.rebuildDocumentIndex();
    }

    @DeleteMapping("/public-document")
    public void removePublicDocumentCache() {
        temporalDataService.invalidatePublicDocumentCache();
    }

    @DeleteMapping("/static-content")
    public void removeStaticContentCache() {
        temporalDataService.invalidateStaticContentCache();
    }

    @DeleteMapping("/other-content")
    public void removeOtherContentCache() {
        temporalDataService.invalidateOtherContentCache();
    }
}
