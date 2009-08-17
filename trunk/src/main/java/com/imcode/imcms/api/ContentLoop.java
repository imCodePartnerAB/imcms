package com.imcode.imcms.api;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="text_doc_content_loops")
public class ContentLoop implements Cloneable {
	
	/**
	 * Loop step.
	 */
	public static final int STEP = 100000;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="base_index")
	private Integer baseIndex;
	
	@Column(name="loop_index")
	private Integer index;
	
	@Column(name="meta_id")
	private Integer metaId;
	
	@Column(name="meta_version")
	private Integer metaVersion;	
	
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
    @JoinColumn(name="loop_id")
    @OrderBy("orderIndex")
	private List<Content> contents = new LinkedList<Content>();
	
	/**
	 * Modified flag.
	 * Object marked as modified is subject to update.
	 */
	@Transient
	private boolean modified = false;
	
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

	public Integer getBaseIndex() {
		return baseIndex;
	}

	public void setBaseIndex(Integer baseIndex) {
		this.baseIndex = baseIndex;
	}

	@Deprecated
	public Integer getNo() {
		return index;
	}

	@Deprecated
	public void setNo(Integer no) {
		this.index = no;
	}
	
	public Integer getIndex() {
		return getNo();
	}

	public void setIndex(Integer index) {
		setNo(index);
	}	

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> loops) {
		this.contents = loops;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public Integer getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(Integer metaVersion) {
		this.metaVersion = metaVersion;
	}
}
