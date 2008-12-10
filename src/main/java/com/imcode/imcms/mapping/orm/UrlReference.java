package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="url_docs")
public class UrlReference {
	
	@Id @Column(name="meta_id")
	private Long metaId;		

	@Column(name="frame_name")
	private String urlFrameName;
	
	@Column(name="target")
	private String urlTarget;
	
	@Column(name="url_ref")
	private String url;
	
	@Column(name="url_txt")
	private String urlText;
	
	@Column(name="lang_prefix")	
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

	public Long getMetaId() {
		return metaId;
	}

	public void setMetaId(Long metaId) {
		this.metaId = metaId;
	}
}