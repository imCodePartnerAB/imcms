package com.imcode.imcms.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * URL target and frame name are legacy fields and never used.
 */
@Data
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
}