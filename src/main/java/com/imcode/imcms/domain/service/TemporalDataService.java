package com.imcode.imcms.domain.service;

import javax.servlet.http.HttpServletRequest;

public interface TemporalDataService {

    void invalidatePublicDocumentCache();

    void invalidateStaticContentCache();

    void invalidateOtherContentCache();

    long rebuildDocumentIndexAndGetDocumentsAmount();

    long rebuildImageFileIndexAndGetDocumentsAmount();

    long getAmountOfIndexedDocuments();

    long getAmountOfIndexedImageFiles();

    String getDateInvalidateDocumentCache();

    String getDateStaticContentCache();

    String getDateInvalidateContentCache();

    String getDateDocumentReIndex();

    String getDateImageFilesReIndex();

    String getDateAddedInCacheDocuments();

    void addDocumentsInCache(HttpServletRequest request);

    int getTotalAmountTextDocDataForCaching();
}
