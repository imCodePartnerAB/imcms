package com.imcode.imcms.domain.service;

import javax.servlet.http.HttpServletRequest;

public interface TemporalDataService {

    void invalidatePublicDocumentCache();

    void invalidateStaticContentCache();

    void invalidateOtherContentCache();

    long rebuildDocumentIndexAndGetDocumentsAmount();

    long getAmountOfIndexedDocuments();

    String getDateInvalidateDocumentCache();

    String getDateStaticContentCache();

    String getDateInvalidateContentCache();

    String getDateDocumentReIndex();

    String getDateAddedInCacheDocuments();

    void addDocumentsInCache(HttpServletRequest request);

    int getTotalAmountTextDocDataForCaching();
}
