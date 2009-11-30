package com.imcode.imcms.mapping.orm;

import javax.persistence.*;

@Entity
@Table(name="imcms_doc_active_version")
public class ActiveDocumentVersion implements Cloneable {

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@Column(name="doc_id", updatable=false)
	private Integer docId;

    @Column(name="version_no")
	private Integer no;

    @Override
    public ActiveDocumentVersion clone() {
        try {
            return (ActiveDocumentVersion)super.clone();
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
