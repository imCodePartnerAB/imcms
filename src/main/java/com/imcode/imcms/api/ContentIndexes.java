package com.imcode.imcms.api;

import javax.persistence.*;

@Embeddable
public class ContentIndexes {

    @Column(name="content_sequence_index")
    private Integer sequence;

    @Column(name="content_lower_order_index")
    private Integer lowerOrder;

    @Column(name="content_higher_order_index")
    private Integer higherOrder;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getLowerOrder() {
        return lowerOrder;
    }

    public void setLowerOrder(Integer lowerOrder) {
        this.lowerOrder = lowerOrder;
    }

    public Integer getHigherOrder() {
        return higherOrder;
    }

    public void setHigherOrder(Integer higherOrder) {
        this.higherOrder = higherOrder;
    }
}
