package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.SpaceAround;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class SpaceAroundJPA extends SpaceAround {

    @Column(name = "top_space")
    private int top;

    @Column(name = "right_space")
    private int right;

    @Column(name = "bottom_space")
    private int bottom;

    @Column(name = "left_space")
    private int left;

    SpaceAroundJPA(SpaceAround from) {
        super(from);
    }
}
