package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "imcms_html_docs")
public class HtmlDocContent extends DocVersionedContent {

    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String text) {
        this.html = text;
    }
}