package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.LoopEntryRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class LoopEntryRefDTO extends LoopEntryRef {

    private int loopIndex;
    private int loopEntryIndex;

    public LoopEntryRefDTO(LoopEntryRef from) {
        super(from);
    }
}
