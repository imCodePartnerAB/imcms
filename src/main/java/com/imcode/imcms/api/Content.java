package com.imcode.imcms.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="content_loops")
public class Content {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="loop_id")
	private Long id;
	
	@Column(name="content_id")
	private Long loopId;	
	
	@Column(name="loop_index")
	private Integer sequenceIndex;
	
	@Column(name="order_index")
	private Integer orderIndex;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSequenceIndex() {
		return sequenceIndex;
	}

	public void setSequenceIndex(Integer index) {
		this.sequenceIndex = index;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public Long getLoopId() {
		return loopId;
	}

	public void setLoopId(Long contentId) {
		this.loopId = contentId;
	}		
}
