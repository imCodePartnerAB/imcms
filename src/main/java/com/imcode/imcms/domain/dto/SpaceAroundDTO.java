package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.SpaceAround;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
class SpaceAroundDTO extends SpaceAround {

    private int top;

    private int right;

    private int bottom;

    private int left;

    SpaceAroundDTO(SpaceAround from) {
        super(from);
    }
}
