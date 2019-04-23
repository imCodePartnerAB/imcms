package com.imcode.imcms.domain.service;

public interface TemporalDataService {

    void invalidatePublicDocumentCache();

    void invalidateStaticContentCache();

    void invalidateOtherContentCache();

    void rebuildDocumentIndex();

}
