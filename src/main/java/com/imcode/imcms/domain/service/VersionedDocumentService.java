package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.model.Document;

public interface VersionedDocumentService<D extends Document> {

    D get(int docId, int versionNo) throws DocumentNotExistException;

    boolean publishDocument(int docId, int userId);

    void setAsWorkingVersion(int docId, int versionNo);

}
