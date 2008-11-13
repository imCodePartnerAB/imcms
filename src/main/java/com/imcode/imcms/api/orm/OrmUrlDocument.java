package com.imcode.imcms.api.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SecondaryTable;

import com.imcode.imcms.api.Meta;

@Entity
@SecondaryTable(name="url_docs")
public class OrmUrlDocument extends OrmDocument {

	@Column(name="frame_name", table="url_docs")
	private String urlFrameName;
	
	@Column(name="target", table="url_docs")
	private String urlTarget;
	
	@Column(name="url_ref", table="url_docs")
	private String url;
	
	@Column(name="url_txt", table="url_docs")
	private String urlText;
	
	@Column(name="lang_prefix", table="url_docs")	
	private String urlLanguagePrefix;

	public String getUrlFrameName() {
		return urlFrameName;
	}

	public void setUrlFrameName(String urlFrameName) {
		this.urlFrameName = urlFrameName;
	}

	public String getUrlTarget() {
		return urlTarget;
	}

	public void setUrlTarget(String urlTarget) {
		this.urlTarget = urlTarget;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlText() {
		return urlText;
	}

	public void setUrlText(String urlText) {
		this.urlText = urlText;
	}

	public String getUrlLanguagePrefix() {
		return urlLanguagePrefix;
	}

	public void setUrlLanguagePrefix(String urlLanguagePrefix) {
		this.urlLanguagePrefix = urlLanguagePrefix;
	}
}
