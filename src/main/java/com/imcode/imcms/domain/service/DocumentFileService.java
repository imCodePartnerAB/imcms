package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.DocumentFile;

import java.util.Optional;

/**
 * Service for work with Document's files.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.12.17.
 */
public interface DocumentFileService extends DeleterByDocumentId {

    DocumentFile save(DocumentFile saveMe);

    Optional<DocumentFile> getByDocId(int docId);

}
