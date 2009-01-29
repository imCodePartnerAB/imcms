package com.imcode.imcms.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Document version.
 */
@Entity(name="DocumentVersion")
@Table(name="meta_version")
@NamedQueries({
	@NamedQuery(name="DocumentVersion.getLastVersion", 
			query="SELECT v FROM DocumentVersion v WHERE v.id IN (" +
					"SELECT max(v.id) FROM DocumentVersion v WHERE v.documentId = :documentId)")
})
public class DocumentVersion implements Cloneable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="meta_id")
	private Integer documentId;

	@Column(name="version")	
	private Integer version;

	@Enumerated(EnumType.STRING)
	@Column(name="version_tag")
	private DocumentVersionTag versionTag;
	
	public DocumentVersion() {}
	
	public DocumentVersion(Integer documentId, Integer version, DocumentVersionTag versionTag) {
		this.documentId = documentId;
		this.version = version;
		this.versionTag = versionTag;
	}	
	
	@Override
	public DocumentVersion clone() {
		try {
			return (DocumentVersion)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public DocumentVersionTag getVersionTag() {
		return versionTag;
	}

	public void setVersionTag(DocumentVersionTag versionTag) {
		this.versionTag = versionTag;
	}
}