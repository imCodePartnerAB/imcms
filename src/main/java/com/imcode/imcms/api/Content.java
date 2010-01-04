package com.imcode.imcms.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Never set orderIndex and sequenceIndex manually.
 * 
 * @see com.imcode.imcms.dao.ContentLoopDao
 */
@Entity
@Table(name="imcms_text_doc_contents")
public class Content implements Cloneable {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="loop_id")
	private Long loopId;	

    /**
     * Unuque order index.
     * This value never repeats but may be changed if a content' position is changed (content moved/swapped).
     */
	@Column(name="order_index")
	private Integer orderIndex;

    /**
     * Unuque sequence index.
     * This value never changes (once assigned) and never repeats.
     */
	@Column(name="sequence_index", updatable=false)
	private Integer index;	

    // To support history, contents are never deleted physically - they are disabled.
    private Boolean enabled = true;

    @Override
	public Content clone() {
		try {
			return (Content)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setLoopId(Long loopId) {
		this.loopId = loopId;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}