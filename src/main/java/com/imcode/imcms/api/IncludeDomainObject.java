package com.imcode.imcms.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Include")
@Table(name="includes")
public class IncludeDomainObject {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rid")
	private Long id;
	
	@Column(name="meta_id")
	private Integer metaId;
	
	@Column(name="include_id")
	private Integer index;	
	
	@Column(name="included_meta_id")
	private Integer includedMetaId;

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

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getIncludedMetaId() {
		return includedMetaId;
	}

	public void setIncludedMetaId(Integer includedMetaId) {
		this.includedMetaId = includedMetaId;
	}		
}
