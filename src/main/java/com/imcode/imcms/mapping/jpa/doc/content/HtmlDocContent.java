package com.imcode.imcms.mapping.jpa.doc.content;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "imcms_html_docs")
public class HtmlDocContent extends VersionedContent {

    @Column(columnDefinition = "longtext")
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String text) {
        this.html = text;
    }
}
