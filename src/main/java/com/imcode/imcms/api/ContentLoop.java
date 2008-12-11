package com.imcode.imcms.api;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="text_doc_content_loops")
@NamedQueries({
	@NamedQuery(name="ContentLoop.getByMetaIdAndIndex", 
			query="SELECT l FROM ContentLoop l WHERE l.metaId = :metaId AND l.no = :index")
})
public class ContentLoop {
	
	/**
	 * Loop step.
	 */
	public static final int STEP = 100000;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="base_index")
	private Integer baseIndex;
	
	@Column(name="loop_index")
	private Integer no;
	
	@Column(name="meta_id")
	private Long metaId;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="loop_id")
    @OrderBy("orderIndex")
	private List<Content> contents;
	
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

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public Long getMetaId() {
		return metaId;
	}

	public void setMetaId(Long metaId) {
		this.metaId = metaId;
	}

	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> loops) {
		this.contents = loops;
	}
}
