package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="imcms_html_docs")
public class HtmlReference implements Serializable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="doc_id")
	private Integer docId;

    @Column(name="doc_version_no")
    private Integer docVersionNo;

	private String html;

    public String getHtml() {
		return html;
	}

	public void setHtml(String text) {
		this.html = text;
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