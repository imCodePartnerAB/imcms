package com.imcode.imcms.mapping.orm;

import javax.persistence.*;

@Entity
@Table(name="imcms_doc_default_version")
public class DefaultDocumentVersion implements Cloneable {

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@Column(name="doc_id", updatable=false)
	private Integer docId;

    @Column(name="no")
	private Integer no;

    @Override
    public DefaultDocumentVersion clone() {
        try {
            return (DefaultDocumentVersion)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);    
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }
}
