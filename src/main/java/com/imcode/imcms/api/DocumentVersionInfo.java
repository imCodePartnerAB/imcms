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
	 * Document's meta id. 
	 */
	private Integer metaId;
	
	/**
	 * Latest version;
	 */
	private DocumentVersion latestVersion;

	/**
	 * Working version (version no 0).
	 */
	private DocumentVersion workingVersion;
	
	/**
	 * Active version.
	 */
	private DocumentVersion activeVersion;
	
	/**
	 * Version list sorted ascending.
	 */
	private List<DocumentVersion> versions;
	
	/**
	 * Versions map.
	 */
	private Map<Integer, DocumentVersion> versionsMap;

    /**
	 * Creates new instance of DocumentVersionSupport.
	 * 
	 * @param metaId documentId.
	 * 
	 * @param versions document versions list.
	 */
	public DocumentVersionInfo(Integer metaId, List<DocumentVersion> versions) {
		versionsMap = new TreeMap<Integer, DocumentVersion>();
		
		for (DocumentVersion  version: versions) {
			versionsMap.put(version.getNo(), version);
		}

        activeVersion = versions.get(0);
        workingVersion = versions.get(0);
        latestVersion = versions.get(0);        

		this.metaId = metaId;
		this.versions = Collections.unmodifiableList(versions);
		this.versionsMap = Collections.unmodifiableMap(versionsMap);			
	}

    /**
	 * @return document id.
	 */
	public Integer getMetaId() {
		return metaId;
	}
	
	/** 
	 * @param no version number.
	 * 
	 * @return version or null if there is no version with such version number.
	 */				
	public DocumentVersion getVersion(Integer no) {
		return versionsMap.get(no);
	}
	
	/**
	 * @returns if given version number belongs to active version.
	 */
	public boolean isActiveVersionNo(Integer no) {
		return hasActiveVersion() && getActiveVersion().getNo().equals(no);
	}
	
	/** 
	 * @return latest version.
	 */				
	public DocumentVersion getLatestVersion() {
		return latestVersion;
	}
	
	/** 
	 * @return working version.
	 */		
	public DocumentVersion getWorkingVersion() {
		return workingVersion;
	}	
	
	/** 
	 * @return active version or null there is no active version.
	 */
	public DocumentVersion getActiveVersion() {
		return activeVersion;
	}	
	
	/** 
	 * Return unmodifiable map of document's version 
	 * sorted by number in ascending order.
	 *  
	 * @return unmodifiable list of document's versions.
	 */
	public List<DocumentVersion> getVersions() {
		return versions;
	}
	
	/** 
	 * Return unmodifiable map of document's version 
	 * sorted by number in ascending order.
	 * 
	 * Map key is a document version number.
	 * 
	 * @return unmodifiable map of document's versions.
	 */
	public Map<Integer, DocumentVersion> getVersionsMap() {
		return versionsMap;
	} 
						
	/**
	 * @return if a document has an active version. 
	 */		
	public boolean hasActiveVersion() {
		return activeVersion != null;
	}
	
	/**
	 * @return document's versions count.
	 */			
	public int getVersionsCount() {
		return versions.size(); 
	}
	
	/**
	 * @returns active version number or null if there is no active version.
	 */
	public Integer getActiveVersionNo() {
		return hasActiveVersion()
			? getActiveVersion().getNo()
			: null;
	}

    public static boolean isWorkingVersion(DocumentVersion version) {
        return DocumentVersion.WORKING_VERSION_NO.equals(version.getNo());
    }
}