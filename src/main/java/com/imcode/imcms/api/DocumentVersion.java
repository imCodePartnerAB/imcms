package com.imcode.imcms.api;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents document's version.
 */
@Entity(name="DocumentVersion")
@Table(name="meta_version")
/*
@NamedQueries({
	// Unique result
	@NamedQuery(name="DocumentVersion.getLastVersion", 
			query="SELECT v FROM DocumentVersion v WHERE v.id IN (" +
					"SELECT max(v.id) FROM DocumentVersion v WHERE v.documentId = :documentId)"),
    // Unique result					
	@NamedQuery(name="DocumentVersion.getByDocumentIdAndVersionTag", 
			query="SELECT v FROM DocumentVersion v WHERE v.documentId = :documentId " +
					"AND v.versionTag = :versionTag"),
    // Unique result				o
	@NamedQuery(name="DocumentVersion.getByDocumentIdAndVersion", 
			query="SELECT v FROM DocumentVersion v WHERE v.documentId = :documentId " +
					"AND v.version = :version"),
					
    // Collection				
	@NamedQuery(name="DocumentVersion.getByDocumentId", 
			query="SELECT v FROM DocumentVersion v WHERE v.documentId = :documentId")	
})
*/
public class DocumentVersion implements Cloneable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="meta_id", updatable=false)
	private Integer documentId;

	/**
	 * Version number
	 */
	@Column(name="version")	
	private Integer version;
	
	@Column(name="user_id", updatable=false)	
	private Integer userId;
	
	@Column(name="created_dt")	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDt;	

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

	
	/**
	 * Use getNumber instead
	 */
	@Deprecated
	public Integer getVersion() {
		return version;
	}

	
	/**
	 * Use setNumber instead
	 */	
	@Deprecated
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	/**
	 * @return verswion number.
	 */
	public Integer getNumber() {
		return getVersion();
	}

	
	/**
	 * Sets version number.
	 * 
	 * @param number version number
	 */	
	public void setNumber(Integer number) {
		setVersion(number);
	}	

	/**
	 * Use getTag instead
	 */	
	@Deprecated	
	public DocumentVersionTag getVersionTag() {
		return versionTag;
	}

	/**
	 * Use setTag instead
	 */	
	@Deprecated	
	public void setVersionTag(DocumentVersionTag versionTag) {
		this.versionTag = versionTag;
	}
	
	/** 
	 * @return document version tag.
	 */
	public DocumentVersionTag getTag() {
		return versionTag;
	}

	/** 
	 * Sets document version tag.
	 * 
	 * @param tag document version tag.
	 */
	public void setTag(DocumentVersionTag tag) {
		setVersionTag(tag);
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}
}