package com.imcode.imcms.api;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A document version.
 */
@Entity
@Table(name="imcms_doc_versions")
public class DocumentVersion implements Cloneable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="meta_id", updatable=false)
	private Integer metaId;

	/**  Version number */
	private Integer number;
	
	@Column(name="created_by", updatable=false)	
	private Integer createdBy;
	
	@Column(name="created_dt")	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDt;	

    public DocumentVersion() {}
	
	public DocumentVersion(Integer metaId, Integer number, Integer createBy) {
		this.metaId = metaId;
		this.number = number;
        this.createdBy = createdBy;
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

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}


	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}
}