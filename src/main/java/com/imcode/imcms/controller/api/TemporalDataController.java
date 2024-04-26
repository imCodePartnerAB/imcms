package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.component.PublicDocumentsCache;
import com.imcode.imcms.domain.service.TemporalDataService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/temporal-data")
public class TemporalDataController {

    private final TemporalDataService temporalDataService;
    private final PublicDocumentsCache documentsCache;

    TemporalDataController(TemporalDataService temporalDataService, PublicDocumentsCache documentsCache) {
        this.temporalDataService = temporalDataService;
        this.documentsCache = documentsCache;
    }

    @PostMapping("/document-recache")
    public void addDocumentsInCache(HttpServletRequest request) {
        temporalDataService.addDocumentsInCache(request);
    }

    @GetMapping("/count-data")
    public int getAmountOfTextDocDataForCaching() {
        return temporalDataService.getTotalAmountTextDocDataForCaching();
    }

    @GetMapping("/count-cached")
    public long getAmountOfCachedDocuments() {
        return documentsCache.getAmountOfCachedDocuments();
    }

    @DeleteMapping("/document-index")
    public long rebuildDocumentIndex() {
        return temporalDataService.rebuildDocumentIndexAndGetDocumentsAmount();
    }

    @DeleteMapping("/image-file-index")
    public long rebuildImageFileIndex() {
        return temporalDataService.rebuildImageFileIndexAndGetDocumentsAmount();
    }

    @GetMapping("/indexed-documents-amount")
    public long getAmountOfIndexedDocuments() {
        return temporalDataService.getAmountOfIndexedDocuments();
    }

    @GetMapping("/indexed-image-file-metadata-amount")
    public long getAmountOfIndexedImageFiles() {
        return temporalDataService.getAmountOfIndexedImageFiles();
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
    public String getDateDocumentReindex() {
        return temporalDataService.getDateDocumentReIndex();
    }

    @GetMapping(value = "/date-image-files-reindex", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateImageFilesReindex(){
        return temporalDataService.getDateImageFilesReIndex();
    }

    @GetMapping(value = "/date-public-document", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateRemoveDocumentCache() {
        return temporalDataService.getDateInvalidateDocumentCache();
    }

    @GetMapping(value = "/date-static-content", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateRemoveStaticContentCache() {
        return temporalDataService.getDateStaticContentCache();
    }

    @GetMapping(value = "/date-other-content", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateRemoveOtherContentCache() {
        return temporalDataService.getDateInvalidateContentCache();
    }

    @GetMapping(value = "/date-recache", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getDateReCacheDocuments() {
        return temporalDataService.getDateAddedInCacheDocuments();
    }
}
