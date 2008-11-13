package com.imcode.imcms.api.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SecondaryTable;

@Entity
@SecondaryTable(name="frameset_docs")
public class OrmHtmlDocument extends OrmDocument {

	@Column(name="frame_set", table="frameset_docs")
	private String html;

	public String getHtml() {
		return html;
	}

	public void setHtml(String text) {
		this.html = text;
	}	
}