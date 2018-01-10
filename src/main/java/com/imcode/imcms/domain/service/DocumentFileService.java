package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.DocumentFile;

import java.util.List;

/**
 * Service for work with Document's files.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.12.17.
 */
public interface DocumentFileService extends DeleterByDocumentId {

    List<DocumentFile> saveAll(List<DocumentFile> saveUs);

    List<DocumentFile> getByDocId(int docId);

    void publishDocumentFiles(int docId);

    DocumentFile getPublicByDocId(int docId);

}
