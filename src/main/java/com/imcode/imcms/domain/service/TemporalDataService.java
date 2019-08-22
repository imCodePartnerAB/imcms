package com.imcode.imcms.domain.service;

import java.io.IOException;

public interface TemporalDataService {

    String invalidatePublicDocumentCache() throws IOException;

    String invalidateStaticContentCache() throws IOException;

    String invalidateOtherContentCache() throws IOException;

    String rebuildDocumentIndex() throws IOException;

}
