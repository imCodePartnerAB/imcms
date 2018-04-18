package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.persistence.entity.VersionedContent;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "imcms_html_docs")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HtmlDocContent extends VersionedContent {

    private static final long serialVersionUID = 2431782537777984020L;

    @Column(columnDefinition = "longtext")
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String text) {
        this.html = text;
    }
}
