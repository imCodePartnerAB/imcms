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

    public Integer setSequence(Integer sequence) {
        return this.sequence = sequence;
    }

    public Integer getLowerOrder() {
        return lowerOrder;
    }

    public Integer setLowerOrder(Integer lowerOrder) {
        return this.lowerOrder = lowerOrder;
    }

    public Integer getHigherOrder() {
        return higherOrder;
    }

    public Integer setHigherOrder(Integer higherOrder) {
        return this.higherOrder = higherOrder;
    }
}
