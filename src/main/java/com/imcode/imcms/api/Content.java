package com.imcode.imcms.api;

import javax.persistence.*;

/**
 * Never set orderIndex and sequenceIndex manually.
 * 
 * @see com.imcode.imcms.dao.ContentLoopDao
 */
//@Entity
//@Table(name="imcms_text_doc_contents")
@Embeddable
public class Content implements Cloneable {

	//@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Transient
    private Long id;
	
	//@Column(name="loop_id")
	@Transient
    private Long loopId;

    /**
     * Unuque order index.
     * This value never repeats but may be changed if a content' position is changed (content moved/swapped).
     */
	//@Column(name="order_index")
	@Transient
    private Integer orderIndex;

    @Column(name="no")
    private Integer no;

    /**
     * Unuque sequence index.
     * This value never changes (once assigned) and never repeats.
     */
	//@Column(name="sequence_index", updatable=false)
	@Transient
    private Integer index;
    
	@Column(name="order_no", updatable=false)
	private Integer orderNo;


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

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
}