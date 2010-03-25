package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="url_docs")
public class UrlReference {
	
	@Id @Column(name="meta_id")
	private Integer docId;		

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

    @Column(name="doc_version_no")
    private Integer docVersionNo;

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

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

    public Integer getDocVersionNo() {
        return docVersionNo;
    }

    public void setDocVersionNo(Integer docVersionNo) {
        this.docVersionNo = docVersionNo;
    }
}