package com.imcode.imcms.api;

/**
 * Document version.
 */
public class DocumentVersion {
		
	private Long metaId;
	
	private Integer version;

	private DocumentVersionTag versionTag;
	
	public DocumentVersion(Long metaId, Integer version, DocumentVersionTag versionTag) {
		this.metaId = metaId;
		this.version = version;
		this.versionTag = versionTag;
	}

	public Long getMetaId() {
		return metaId;
	}

	public Integer getVersion() {
		return version;
	}
	
	public DocumentVersionTag getVersionTag() {
		return versionTag;
	}
}