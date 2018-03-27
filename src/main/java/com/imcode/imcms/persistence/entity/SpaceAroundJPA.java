package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.SpaceAround;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
public class SpaceAroundJPA extends SpaceAround {

    @Column(name = "top_space")
    private int top;

    @Column(name = "right_space")
    private int right;

    @Column(name = "bottom_space")
    private int bottom;

    @Column(name = "left_space")
    private int left;

    public SpaceAroundJPA(SpaceAround from) {
        super(from);
    }
}
