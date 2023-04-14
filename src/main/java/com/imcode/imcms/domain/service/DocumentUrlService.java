package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.DocumentURL;

public interface DocumentUrlService extends Copyable, VersionedContentService {

    DocumentURL getByDocId(int docId);

    DocumentURL getByDocIdAndVersionNo(int docId, int versionNo);

    DocumentURL save(DocumentURL documentURL);
}
