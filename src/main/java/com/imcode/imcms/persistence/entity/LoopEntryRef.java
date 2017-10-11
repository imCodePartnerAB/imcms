package com.imcode.imcms.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoopEntryRef implements Serializable, Cloneable {

    @Column(name = "loop_index")
    private int loopIndex;

    @Column(name = "loop_entry_index")
    private int loopEntryIndex;
}
