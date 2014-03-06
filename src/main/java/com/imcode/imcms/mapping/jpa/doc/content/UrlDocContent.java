package com.imcode.imcms.mapping.jpa.doc.content;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * URL target and frame name are legacy fields and never used.
 */
@Entity
@Table(name = "imcms_url_docs")
public class UrlDocContent extends VersionedContent {

    @Column(name = "frame_name")
    private String urlFrameName;

    @Column(name = "target")
    private String urlTarget;

    @Column(name = "url_ref")
    private String url;

    @Column(name = "url_txt")
    private String urlText;

    @Column(name = "lang_prefix")
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