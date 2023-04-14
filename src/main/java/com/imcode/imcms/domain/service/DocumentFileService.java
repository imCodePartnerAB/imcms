package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.DocumentFile;
import imcode.util.io.InputStreamSource;

import java.util.List;

/**
 * Service for work with Document's files.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.12.17.
 */
public interface DocumentFileService extends DeleterByDocumentId, VersionedContentService, Copyable {

    /**
     * This will save list of files for specified document by id.
     * Note that all other files that are connected to document but not
     * mentioned in list will be deleted.
     * All changes applied for working document version.
     *
     * @param saveUs list of files to save
     * @param docId  id of document
     * @return list of saved files
     */
    <T extends DocumentFile> List<DocumentFile> saveAll(List<T> saveUs, int docId);

    <T extends DocumentFile> DocumentFile save(T saveMe);

    List<DocumentFile> getByDocId(int docId);

    List<DocumentFile> getByDocIdAndVersion(int docId, int versionNo);

    void publishDocumentFiles(int docId);

    List<DocumentFile> getPublicByDocId(int docId);

    InputStreamSource getFileDocumentInputStreamSource(DocumentFile documentFile);
}
