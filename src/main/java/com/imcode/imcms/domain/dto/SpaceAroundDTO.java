package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.SpaceAround;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SpaceAroundDTO extends SpaceAround implements Serializable {

    private static final long serialVersionUID = -1489089643170770251L;
    private int top;

    private int right;

    private int bottom;

    private int left;

    SpaceAroundDTO(SpaceAround from) {
        super(from);
    }
}
