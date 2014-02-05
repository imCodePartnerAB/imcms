package com.imcode.imcms.mapping;

import com.imcode.imcms.mapping.orm.DocVersion;

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
	private int docId;
	
	/**
	 * Latest version;
	 */
	private DocVersion latestVersion;

	/**
	 * Working version (version no 0).
	 */
	private DocVersion workingVersion;
	
	/**
	 * Default version.
	 */
	private DocVersion defaultVersion;
	
	/**
	 * Version list sorted ascending.
	 */
	private List<DocVersion> versions;
	
	/**
	 * Versions map.
	 */
	private Map<Integer, DocVersion> versionsMap;

    /**
	 * Creates new instance of DocumentVersionSupport.
	 * 
	 * @param docId documentId.
	 * 
	 * @param versions document versions list.
	 */
	public DocumentVersionInfo(int docId, List<DocVersion> versions, DocVersion workingVersion, DocVersion defaultVersion) {
		versionsMap = new TreeMap<>();
		
		for (DocVersion version: versions) {
			versionsMap.put(version.getNo(), version);
		}

        this.workingVersion = workingVersion;
        this.defaultVersion = defaultVersion;
        this.latestVersion = versions.get(versions.size() - 1);

		this.docId = docId;
		this.versions = Collections.unmodifiableList(versions);
		this.versionsMap = Collections.unmodifiableMap(versionsMap);			
	}

    /**
	 * @return document id.
	 */
	public int getDocId() {
		return docId;
	}
	
	/** 
	 * @param no version number.
	 * 
	 * @return version or null if there is no version with such version number.
	 */				
	public DocVersion getVersion(Integer no) {
		return versionsMap.get(no);
	}

    
    public static boolean isWorkingVersion(DocVersion version) {
        return version != null && isWorkingVersionNo(version.getNo());
    }

    public static boolean isWorkingVersionNo(int no) {
        return no == DocVersion.WORKING_VERSION_NO;
    }
	
	/**
	 * @returns if given version number belongs to active version.
	 */
	public boolean isDefaultVersion(DocVersion version) {
		return version != null && getDefaultVersion().getNo() == version.getNo();
	}
	
	/** 
	 * @return latest version.
	 */				
	public DocVersion getLatestVersion() {
		return latestVersion;
	}
	
	/** 
	 * @return working version.
	 */		
	public DocVersion getWorkingVersion() {
		return workingVersion;
	}	
	
	/** 
	 * @return default version of a document.
	 */
	public DocVersion getDefaultVersion() {
		return defaultVersion;
	}	
	
	/** 
	 * Return unmodifiable map of document's version 
	 * sorted by number in ascending order.
	 *  
	 * @return unmodifiable list of document's versions.
	 */
	public List<DocVersion> getVersions() {
		return versions;
	}
	
	/**
	 * @return document's versions count.
	 */			
	public int getVersionsCount() {
		return versions.size(); 
	}

    public boolean workingIsActive() {
        return getDefaultVersion().getId().equals(getWorkingVersion().getId());
    }
}