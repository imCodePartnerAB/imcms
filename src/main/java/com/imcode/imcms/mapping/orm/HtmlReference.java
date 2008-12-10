package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="frameset_docs")
public class HtmlReference {
	
	@Id @Column(name="meta_id")
	private Long metaId;	

	@Column(name="frame_set")
	private String html;

	public String getHtml() {
		return html;
	}

	public void setHtml(String text) {
		this.html = text;
	}

	public Long getMetaId() {
		return metaId;
	}

	public void setMetaId(Long metaId) {
		this.metaId = metaId;
	}	
}