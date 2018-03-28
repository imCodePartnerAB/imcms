package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.LoopEntryRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LoopEntryRefJPA extends LoopEntryRef {

    @Column(name = "loop_index")
    private int loopIndex;

    @Column(name = "loop_entry_index")
    private int loopEntryIndex;

    public LoopEntryRefJPA(LoopEntryRef from) {
        super(from);
    }
}
