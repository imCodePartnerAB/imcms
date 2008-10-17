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
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NamedNativeQueries;

@Entity
@Table(name="contents")
@NamedQueries({
	@NamedQuery(name="ContentLoop.getByMetaAndNo", 
			query="SELECT l FROM ContentLoop l WHERE l.metaId = :metaId AND l.no = :no"),
	@NamedQuery(name="ContentLoop.getNextContentIndexes", 
			query="SELECT max(c.sequenceIndex) + 1 AS NEXT_SEQUENCE_INDEX, "    + 
			             "max(c.orderIndex)    + 1 AS NEXT_HIGHER_ORDER_INDEX, " + 
			             "min(c.orderIndex)    - 1 AS NEXT_LOWER_ORDER_INDEX "   +
  	               "FROM ContentLoop l JOIN l.contents c " +
			       "WHERE l.id = :id")	
})
public class ContentLoop {
	
	/**
	 * Loop step.
	 */
	public static final int STEP = 100000;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="content_id")
	private Long id;

	@Column(name="base_index")
	private Integer baseIndex;
	
	@Column(name="content_no")
	private Integer no;
	
	@Column(name="meta_id")
	private Integer metaId;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="content_id", referencedColumnName="content_id")
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
}
