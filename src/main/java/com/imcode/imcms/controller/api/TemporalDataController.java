package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.service.TemporalDataService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/temporal-data")
public class TemporalDataController {

    private final TemporalDataService temporalDataService;
    private final DocumentsCache documentsCache;

    TemporalDataController(TemporalDataService temporalDataService, DocumentsCache documentsCache) {
        this.temporalDataService = temporalDataService;
        this.documentsCache = documentsCache;
    }

    @PostMapping("/document-recache")
    public long addDocumentsInCache() {
        return temporalDataService.addDocumentsInCacheAndGetDocumentsCount();
    }

    @GetMapping("/count-cached")
    public long getAmountOfCachedDocuments() {
        return documentsCache.getAmountOfCachedDocuments();
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

    @GetMapping(value = "/date-reindex", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateDocumentReindex() throws IOException {
        return temporalDataService.getDateDocumentReIndex();
    }

    @GetMapping(value = "/date-public-document", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateRemoveDocumentCache() throws IOException {
        return temporalDataService.getDateInvalidateDocumentCache();
    }

    @GetMapping(value = "/date-static-content", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateRemoveStaticContentCache() throws IOException {
        return temporalDataService.getDateStaticContentCache();
    }

    @GetMapping(value = "/date-other-content", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateRemoveOtherContentCache() throws IOException {
        return temporalDataService.getDateInvalidateContentCache();
    }

    @GetMapping(value = "/date-recache", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateReCacheDocuments() throws IOException {
        return temporalDataService.getDateAddedInCacheDocuments();
    }
}
