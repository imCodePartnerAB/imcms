package com.imcode.imcms.mapping.orm;

import imcode.server.document.textdocument.DocRef;

import javax.persistence.*;

/**
 * URL target and frame name are legacy fields and never used.
 */
@Entity
@Table(name="imcms_url_docs")
public class UrlReference {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;    
	
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

    private DocRef docRef;

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

    public DocRef getDocRef() {
        return docRef;
    }

    public void setDocRef(DocRef docRef) {
        this.docRef = docRef;
    }
}