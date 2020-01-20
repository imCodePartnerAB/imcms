package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DataAvailableDocumentInfo;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface TemporalDataService {

    void invalidatePublicDocumentCache();

    void invalidateStaticContentCache();

    void invalidateOtherContentCache();

    long rebuildDocumentIndexAndGetDocumentsAmount();

    long getAmountOfIndexedDocuments();

    String getDateInvalidateDocumentCache() throws IOException;

    String getDateStaticContentCache() throws IOException;

    String getDateInvalidateContentCache() throws IOException;

    String getDateDocumentReIndex() throws IOException;

    String getDateAddedInCacheDocuments() throws IOException;

    DataAvailableDocumentInfo addDocumentsInCacheAndGetDocContentCount(HttpServletRequest request);
}
