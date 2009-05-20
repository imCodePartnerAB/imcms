package com.imcode.imcms.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides document version support.
 */
public class DocumentVersionSupport {
	
	/**
	 * Document id. 
	 */
	private Integer documentId;
	
	/**
	 * Latest document version;
	 */
	private DocumentVersion latestVersion;
	
	/**
	 * Working document version.
	 */
	private DocumentVersion workingVersion;
	
	/**
	 * Published document version.
	 */
	private DocumentVersion publishedVersion;
	
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
	 * @param documentId documentId.
	 * 
	 * @param versions document versions list.
	 */
	public DocumentVersionSupport(Integer documentId, List<DocumentVersion> versions) {
		versionsMap = new TreeMap<Integer, DocumentVersion>();
		
		for (DocumentVersion  version: versions) {
			versionsMap.put(version.getNumber(), version);
			
			switch (version.getTag()) {
			case PUBLISHED:
				publishedVersion = version;
				break;
				
			case WORKING:
				workingVersion = version;
				break;
			}
			
			latestVersion = version;
		}

		this.documentId = documentId;
		this.versions = Collections.unmodifiableList(versions);
		this.versionsMap = Collections.unmodifiableMap(versionsMap);			
	} 
	
	/** 
	 * @return document id.
	 */
	public Integer getDocumentId() {
		return documentId;
	}
	
	/** 
	 * @param versionNumber version number.
	 * 
	 * @return document version or null if there is no such version number.
	 */				
	public DocumentVersion getVersion(Integer versionNumber) {
		return versionsMap.get(versionNumber);
	}
	
	/**
	 * @returns if given version number belongs to published version.
	 */
	public boolean isPublishedVersionNumber(Integer versionNumber) {
		return hasPublishedVersion() && getPublishedVersion().getNumber().equals(versionNumber);
	}
	
	/**
	 * @returns if given version number belongs to working version.
	 */
	public boolean isWorkingVersionNumber(Integer versionNumber) {
		return hasWorkingVersion() && getWorkingVersion().getNumber().equals(versionNumber);
	}	
	
	/** 
	 * @return latest document version.
	 */				
	public DocumentVersion getLatestVersion() {
		return latestVersion;
	}
	
	/** 
	 * @return working document version or null there is no working version.
	 */		
	public DocumentVersion getWorkingVersion() {
		return workingVersion;
	}	
	
	/** 
	 * @return published document version or null there is no published version.
	 */
	public DocumentVersion getPublishedVersion() {
		return publishedVersion;
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
	 * @return if a document has working version. 
	 */
	public boolean hasWorkingVersion() {
		return workingVersion != null;
	}
	
	/**
	 * @return if a document has published version. 
	 */		
	public boolean hasPublishedVersion() {
		return publishedVersion != null;
	}
	
	/**
	 * @return document's versions count.
	 */			
	public int getVersionsCount() {
		return versions.size(); 
	}
	
	/**
	 * @returns published version number or null if there is no published version.
	 */
	public Integer getPuplishedVersionNumber() {
		return hasPublishedVersion() 
			? getPublishedVersion().getNumber()
			: null;
	}
	
	/**
	 * @returns working version number or null if there is no published version.
	 */
	public Integer getWorkingVersionNumber() {
		return hasWorkingVersion() 
			? getWorkingVersion().getNumber()
			: null;
	}	
}