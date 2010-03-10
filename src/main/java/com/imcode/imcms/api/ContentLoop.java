package com.imcode.imcms.api;

import imcode.server.document.textdocument.DocItem;
import org.hibernate.annotations.CollectionOfElements;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

/**
 * Do not set content indexes values manually.
 * Do not modify contents. 
 *
 * @see com.imcode.imcms.dao.ContentLoopDao
 */
@Entity
@Table(name="imcms_text_doc_content_loops")
public class ContentLoop implements Serializable, Cloneable, DocItem {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Integer no;
	
	@Column(name="doc_id")
	private Integer docId;
	
	@Column(name="doc_version_no")
	private Integer docVersionNo;

//	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
//    @JoinColumn(name="loop_id")
//    @OrderBy("orderIndex")
//	private List<Content> contents = new LinkedList<Content>();

    @CollectionOfElements
	@JoinTable(
	    name = "imcms_text_doc_contents",
	    joinColumns = {@JoinColumn(name="doc_id", referencedColumnName="doc_id"),
                       @JoinColumn(name="doc_version_no", referencedColumnName="doc_version_no"),
                       @JoinColumn(name="loop_no", referencedColumnName="no")})
    private List<Content> contents = new LinkedList<Content>();

    @Override
	public ContentLoop clone() {
		ContentLoop clone;
		
		try {
			clone = (ContentLoop)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		
		List<Content> contentsClone = new LinkedList<Content>();
		
		for (Content content: contents) {
			contentsClone.add(content.clone());
		}
		
		clone.setContents(contentsClone);
								
		return clone;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNo() {
		return no;
	}


	public void setNo(Integer no) {
		this.no = no;
	}

    @Deprecated
	public Integer getIndex() {
		return getNo();
	}

    @Deprecated
	public void setIndex(Integer index) {
		setNo(index);
	}	

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> loops) {
		this.contents = loops;
	}

	public Integer getDocVersionNo() {
		return docVersionNo;
	}

	public void setDocVersionNo(Integer docVersionNo) {
		this.docVersionNo = docVersionNo;
	}
}
