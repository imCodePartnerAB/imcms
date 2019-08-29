package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.TemporalDataService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public long rebuildDocumentIndex() {
        return temporalDataService.rebuildDocumentIndexAndGetDocumentsAmount();
    }

    @GetMapping("/indexed-documents-amount")
    public long getAmountOfIndexedDocuments() {
        return temporalDataService.getAmountOfIndexedDocuments();
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

    @GetMapping("/date-reindex")
    public String getDateDocumentReindex() throws IOException {
        return temporalDataService.getDateDocumentReIndex();
    }

    @GetMapping("/date-public-document")
    public String getDateRemoveDocumentCache() throws IOException {
        return temporalDataService.getDateInvalidateDocumentCache();
    }

    @GetMapping("/date-static-content")
    public String getDateRemoveStaticContentCache() throws IOException {
        return temporalDataService.getDateStaticContentCache();
    }

    @GetMapping("/date-other-content")
    public String getDateRemoveOtherContentCache() throws IOException {
        return temporalDataService.getDateInvalidateContentCache();
    }
}
