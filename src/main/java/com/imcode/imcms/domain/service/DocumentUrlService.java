package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.DocumentURL;

import java.util.List;

public interface DocumentUrlService extends Copyable, VersionedContentService {

    DocumentURL getByDocId(int docId);

    List<? extends DocumentURL> getAllContainingInURL(String content);

    DocumentURL getByDocIdAndVersionNo(int docId, int versionNo);

    DocumentURL save(DocumentURL documentURL);
}
