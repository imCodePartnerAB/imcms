package com.imcode.imcms.mapping.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="frameset_docs")
public class HtmlReference implements Serializable {
	
	@Id @Column(name="meta_id")
	private Integer docId;

    @Column(name="doc_version_no")
    private Integer docVersionNo;

	@Column(name="frame_set")
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