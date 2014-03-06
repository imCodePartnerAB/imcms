package com.imcode.imcms.mapping.jpa.doc.content;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "imcms_html_docs")
public class HtmlDocContent extends VersionedContent {

    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String text) {
        this.html = text;
    }
}