package com.imcode.imcms.domain.service;

import java.io.IOException;

public interface TemporalDataService {

    void invalidatePublicDocumentCache();

    void invalidateStaticContentCache();

    void invalidateOtherContentCache();

    void rebuildDocumentIndex();

    String getDateInvalidateDocumentCache() throws IOException;

    String getDateStaticContentCache() throws IOException;

    String getDateInvalidateContentCache() throws IOException;

    String getDateDocumentReIndex() throws IOException;

}
