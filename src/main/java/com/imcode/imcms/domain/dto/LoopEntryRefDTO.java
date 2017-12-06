package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.LoopEntryRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoopEntryRefDTO extends LoopEntryRef {

    private int loopIndex;
    private int loopEntryIndex;

    public LoopEntryRefDTO(LoopEntryRef from) {
        super(from);
    }
}
