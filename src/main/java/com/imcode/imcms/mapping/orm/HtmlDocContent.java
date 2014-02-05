package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "imcms_html_docs")
public class HtmlDocContent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private DocRef docRef;

    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String text) {
        this.html = text;
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public void setDocRef(DocRef docRef) {
        this.docRef = docRef;
    }
}