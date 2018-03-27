package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.SpaceAround;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
public class SpaceAroundDTO extends SpaceAround {

    private int top;

    private int right;

    private int bottom;

    private int left;

    public SpaceAroundDTO(SpaceAround from) {
        super(from);
    }
}
