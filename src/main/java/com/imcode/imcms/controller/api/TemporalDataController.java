package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.TemporalDataService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/temporal-data")
public class TemporalDataController {

    private final TemporalDataService temporalDataService;

    TemporalDataController(TemporalDataService temporalDataService) {
        this.temporalDataService = temporalDataService;
    }

    @DeleteMapping("/document-index")
    public String rebuildDocumentIndex() throws IOException {
        return temporalDataService.rebuildDocumentIndex();
    }

    @DeleteMapping("/public-document")
    public String removePublicDocumentCache() throws IOException {
        return temporalDataService.invalidatePublicDocumentCache();
    }

    @DeleteMapping("/static-content")
    public String removeStaticContentCache() throws IOException {
        return temporalDataService.invalidateStaticContentCache();
    }

    @DeleteMapping("/other-content")
    public String removeOtherContentCache() throws IOException {
        return temporalDataService.invalidateOtherContentCache();
    }
}
