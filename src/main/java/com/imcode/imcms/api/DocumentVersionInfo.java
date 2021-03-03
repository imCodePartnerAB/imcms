package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Document's version info.
 */
public class DocumentVersionInfo implements Serializable {

    /**
     * Document's doc id.
     */
    private final int docId;

    /**
     * Latest version;
     */
    private final DocumentVersion latestVersion;

    /**
     * Working version (version no 0).
     */
    private final DocumentVersion workingVersion;

    /**
     * Default version.
     */
    private final DocumentVersion defaultVersion;

    /**
     * Version list enums ascending.
     */
    private final List<DocumentVersion> versions;

    /**
     * Versions map.
     * Map key - version no, value - version.
     */
    private final Map<Integer, DocumentVersion> versionsMap;

    /**
     * Creates new instance of DocumentVersionSupport.
     *
     * @param docId    documentId.
     * @param versions document versions list.
     */
    public DocumentVersionInfo(int docId, List<DocumentVersion> versions, DocumentVersion workingVersion, DocumentVersion defaultVersion) {
        Map<Integer, DocumentVersion> versionsMap = new TreeMap<>();

        for (DocumentVersion version : versions) {
            versionsMap.put(version.getNo(), version);
        }

        this.workingVersion = workingVersion;
        this.defaultVersion = defaultVersion;
        this.latestVersion = versions.get(versions.size() - 1);

        this.docId = docId;
        this.versions = Collections.unmodifiableList(versions);
        this.versionsMap = Collections.unmodifiableMap(versionsMap);
    }

    public static boolean isWorkingVersion(DocumentVersion version) {
        return version != null && isWorkingVersionNo(version.getNo());
    }

    public static boolean isWorkingVersionNo(int no) {
        return no == DocumentVersion.WORKING_VERSION_NO;
    }

    /**
     * @return document id.
     */
    public int getDocId() {
        return docId;
    }

    /**
     * @param no version number.
     * @return version or null if there is no version with such version number.
     */
    public DocumentVersion getVersion(Integer no) {
        return versionsMap.get(no);
    }

    /**
     * @return if given version number belongs to active version.
     */
    public boolean isDefaultVersion(DocumentVersion version) {
        return version != null && getDefaultVersion().getNo() == version.getNo();
    }

    /**
     * @return working version.
     */
    public DocumentVersion getWorkingVersion() {
        return workingVersion;
    }

    /**
     * @return default version of a document.
     */
    public DocumentVersion getDefaultVersion() {
        return defaultVersion;
    }

    /**
     * Return unmodifiable map of document's version
     * enums by number in ascending order.
     *
     * @return unmodifiable list of document's versions.
     */
    public List<DocumentVersion> getVersions() {
        return versions;
    }

    /**
     * @return latest version.
     */
    public DocumentVersion getLatestVersion() {
        return latestVersion;
    }

    /**
     * @return document's versions count.
     */
    public int getVersionsCount() {
        return versions.size();
    }

    public boolean getWorkingIsDefault() {
        return getDefaultVersion().getNo() == getWorkingVersion().getNo();
    }
}